package org.wengdev.lightbr;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import org.wengdev.lightbr.config.LightBRConfig;
import org.wengdev.lightbr.obu.OBUManager;
import org.wengdev.lightbr.network.ConfigPayload;
import org.wengdev.lightbr.network.SettingsPayload;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class LightBR implements ClientModInitializer {
    public static LightBRConfig config;
    public static final int PROTOCOL_VERSION = loadProtocolVersion();

    private static final int DEFAULT_PROTOCOL_VERSION = 3;

    public static HashMap<String, Float> defaultSlipperinessMap = null;

    private static final String SETTINGS_CHANNEL = "lightbr:settings";
    private static final String CONFIG_CHANNEL = "lightbr:config";

    private static final int DEFAULT_CHUNK_XZ_RADIUS = 1;
    private static final int DEFAULT_CHUNK_Y_RADIUS = 1;

    private static final int CONFIG_PACKET_ACK = 0;

    private static final int PACKET_SET_ENABLED = 1;
    private static final int PACKET_SET_RENDER_ALL_WATER = 2;
    private static final int PACKET_SET_CHUNK_XZ = 3;
    private static final int PACKET_SET_CHUNK_Y = 4;
    private static final int PACKET_SET_RENDER_ALL_LAVA = 5;
    private static final int PACKET_SET_UNRENDER_BLOCK_ENTITIES = 6;
    private static final int PACKET_SET_ALWAYS_RENDER_BLOCK_ENTITIES = 7;
    private static final int PACKET_SET_ALWAYS_RENDER_REGIONS = 8;
    private static final int PACKET_RESET_CACHE = 9;
    private static final int PACKET_RESET_SETTINGS = 10;

    private static volatile RenderContext renderContext;
    private static volatile RenderContextPatch serverContextPatch;
    private static volatile boolean serverControlled = false;
    private static int pendingAckTicks = -1;

    private static final float DEFAULT_BLOCK_SLIPPERINESS = 0.6f;
    //? if 1.21.11 {
    /*private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("category", "lightbr"));
    *///? } elif 1.21.4 {
    private static final String KEY_CATEGORY = "category.lightbr";
    //? }
    private static KeyMapping toggleKey;

    private static int loadProtocolVersion() {
        Integer version = loadProtocolVersionFromProperties();
        return version != null ? version : DEFAULT_PROTOCOL_VERSION;
    }

    private static Integer loadProtocolVersionFromProperties() {
        try (InputStream in = LightBR.class.getClassLoader().getResourceAsStream("lightbr.properties")) {
            if (in == null) {
                return null;
            }
            Properties props = new Properties();
            props.load(in);
            String value = props.getProperty("protocol_version");
            if (value == null || value.isBlank()) {
                return null;
            }
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            System.err.println("Failed to read LightBR protocol_version from properties: " + e.getMessage());
            return null;
        }
    }

    public static HashMap<String, Float> loadDefaultSlipperinessMap() {
        if (defaultSlipperinessMap == null) {
            defaultSlipperinessMap = new HashMap<>();

            for (Block b : BuiltInRegistries.BLOCK.stream().toList()) {
                if (b.getFriction() != DEFAULT_BLOCK_SLIPPERINESS) {
                    defaultSlipperinessMap.put(BuiltInRegistries.BLOCK.getKey(b).toString(), b.getFriction());
                }
            }
        }

        return defaultSlipperinessMap;
    }

    public static boolean isSlipperyBlock(String blockId) {
        if (OBUManager.isOBUEnabled()) {
            return OBUManager.isBlockSlippery(blockId);
        } else {
            HashMap<String, Float> slipperinessMap = loadDefaultSlipperinessMap();
            return slipperinessMap.containsKey(blockId);
        }
    }

    private static void toggleEnabled() {
        if (config == null) {
            return;
        }
        config.isEnabled = !config.isEnabled;
        updateRenderContextFromConfig();
        config.saveAndReloadWorld();
    }

    public static boolean isServerControlled() {
        return serverControlled;
    }

    public static boolean isEnabledServerControlled() {
        return serverControlled && serverContextPatch != null && serverContextPatch.isEnabled != null;
    }

    public static boolean isRenderAllWaterServerControlled() {
        return serverControlled && serverContextPatch != null && serverContextPatch.renderAllWater != null;
    }

    public static boolean isRenderAllLavaServerControlled() {
        return serverControlled && serverContextPatch != null && serverContextPatch.renderAllLava != null;
    }

    public static boolean isUnrenderBlockEntitiesServerControlled() {
        return serverControlled && serverContextPatch != null && serverContextPatch.unrenderBlockEntities != null;
    }

    public static boolean isRenderableBlockEntitiesServerControlled() {
        return serverControlled && serverContextPatch != null && serverContextPatch.alwaysRenderBlockEntities != null;
    }

    public static boolean isAlwaysRenderRegionsServerControlled() {
        return serverControlled && serverContextPatch != null && serverContextPatch.alwaysRenderRegions != null;
    }

    public static RenderContext getRenderContext() {
        RenderContext context = renderContext;
        if (context == null) {
            LightBRConfig fallback = config != null ? config : new LightBRConfig();
            RenderContext defaults = buildRenderContextFromConfig(fallback);
            if (serverControlled && serverContextPatch != null) {
                context = serverContextPatch.merge(defaults);
            } else {
                context = defaults;
            }
            renderContext = context;
        }
        return context;
    }

    public static void updateRenderContextFromConfig() {
        if (config == null) {
            return;
        }
        RenderContext defaults = buildRenderContextFromConfig(config);
        if (serverControlled && serverContextPatch != null) {
            renderContext = serverContextPatch.merge(defaults);
        } else {
            renderContext = defaults;
        }
        TrackCache.clear();
    }

    public static void clearCacheAndReload() {
        TrackCache.clear();

        if (getRenderContext().isEnabled) {
            reloadWorldRenderer();
        }
    }

    public static void applyServerRenderContext(RenderContextPatch patch) {
        serverContextPatch = patch;
        serverControlled = true;
        LightBRConfig fallback = config != null ? config : new LightBRConfig();
        renderContext = patch.merge(buildRenderContextFromConfig(fallback));
        clearCacheAndReload();
    }

    public static void clearServerControl() {
        serverControlled = false;
        serverContextPatch = null;
        updateRenderContextFromConfig();
    }

    private static void applyServerOverride(java.util.function.Function<RenderContextPatch, RenderContextPatch> updater) {
        RenderContextPatch base = serverContextPatch != null ? serverContextPatch : RenderContextPatch.empty();
        serverContextPatch = updater.apply(base);
        serverControlled = true;
        LightBRConfig fallback = config != null ? config : new LightBRConfig();
        renderContext = serverContextPatch.merge(buildRenderContextFromConfig(fallback));
        clearCacheAndReload();
    }

    private static void applyServerControlAck() {
        serverControlled = true;
        RenderContextPatch patch = serverContextPatch != null ? serverContextPatch : RenderContextPatch.empty();
        LightBRConfig fallback = config != null ? config : new LightBRConfig();
        renderContext = patch.merge(buildRenderContextFromConfig(fallback));
    }

    private static void applyServerResetSettings() {
        serverControlled = true;
        serverContextPatch = RenderContextPatch.empty();
        LightBRConfig fallback = config != null ? config : new LightBRConfig();
        renderContext = serverContextPatch.merge(buildRenderContextFromConfig(fallback));
        clearCacheAndReload();
    }

    private static void reloadWorldRenderer() {
        Minecraft client = Minecraft.getInstance();
        client.levelRenderer.allChanged();
    }

    private static void sendAcknowledgement() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(CONFIG_PACKET_ACK);
        buf.writeVarInt(PROTOCOL_VERSION);
        ClientPlayNetworking.send(ConfigPayload.fromBuf(buf));
    }

    private static RenderContext buildRenderContextFromConfig(LightBRConfig config) {
        return new RenderContext(
                config.isEnabled,
                DEFAULT_CHUNK_XZ_RADIUS,
                DEFAULT_CHUNK_Y_RADIUS,
                List.of(),
                config.renderAllWater,
                config.renderAllLava,
                config.shouldUnrenderBlockEntities,
                config.renderableBlockEntities == null ? List.of() : List.copyOf(config.renderableBlockEntities)
        );
    }

    @Override
    public void onInitializeClient() {
        config = LightBRConfig.load();
        updateRenderContextFromConfig();

        PayloadTypeRegistry.playC2S().register(SettingsPayload.ID, SettingsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SettingsPayload.ID, SettingsPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ConfigPayload.ID, ConfigPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.CODEC);

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lightbr.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pendingAckTicks >= 0) {
                if (pendingAckTicks == 0) {
                    if (client.getConnection() != null) {
                        sendAcknowledgement();
                    }
                    pendingAckTicks = -1;
                } else {
                    pendingAckTicks--;
                }
            }
            while (toggleKey.consumeClick()) {
                toggleEnabled();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(SettingsPayload.ID, (payload, context) -> {

            FriendlyByteBuf dataBuf = payload.toPacketByteBuf();
            int packetType = dataBuf.readVarInt();
            switch (packetType) {
                case PACKET_SET_ENABLED -> {
                    boolean value = dataBuf.readBoolean();
                    context.client().execute(() -> applyServerOverride(patch -> patch.withEnabled(value)));
                }
                case PACKET_SET_RENDER_ALL_WATER -> {
                    boolean value = dataBuf.readBoolean();
                    context.client().execute(() -> applyServerOverride(patch -> patch.withRenderAllWater(value)));
                }
                case PACKET_SET_CHUNK_XZ -> {
                    int value = dataBuf.readVarInt();
                    context.client().execute(() -> applyServerOverride(patch -> patch.withChunkXZRadius(value)));
                }
                case PACKET_SET_CHUNK_Y -> {
                    int value = dataBuf.readVarInt();
                    context.client().execute(() -> applyServerOverride(patch -> patch.withChunkYRadius(value)));
                }
                case PACKET_SET_RENDER_ALL_LAVA -> {
                    boolean value = dataBuf.readBoolean();
                    context.client().execute(() -> applyServerOverride(patch -> patch.withRenderAllLava(value)));
                }
                case PACKET_SET_UNRENDER_BLOCK_ENTITIES -> {
                    boolean value = dataBuf.readBoolean();
                    context.client().execute(() -> applyServerOverride(patch -> patch.withUnrenderBlockEntities(value)));
                }
                case PACKET_SET_ALWAYS_RENDER_BLOCK_ENTITIES -> {
                    int count = dataBuf.readVarInt();
                    List<String> blockEntities = new java.util.ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        blockEntities.add(dataBuf.readUtf());
                    }
                    List<String> resolved = List.copyOf(blockEntities);
                    context.client().execute(() -> applyServerOverride(patch -> patch.withAlwaysRenderBlockEntities(resolved)));
                }
                case PACKET_SET_ALWAYS_RENDER_REGIONS -> {
                    int count = dataBuf.readVarInt();
                    List<Tuple<Vec3, Vec3>> regions = new java.util.ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        Vec3 a = new Vec3(dataBuf.readDouble(), dataBuf.readDouble(), dataBuf.readDouble());
                        Vec3 b = new Vec3(dataBuf.readDouble(), dataBuf.readDouble(), dataBuf.readDouble());
                        regions.add(new Tuple<>(a, b));
                    }
                    List<Tuple<Vec3, Vec3>> resolved = List.copyOf(regions);
                    context.client().execute(() -> applyServerOverride(patch -> patch.withAlwaysRenderRegions(resolved)));
                }
                case PACKET_RESET_CACHE -> context.client().execute(LightBR::clearCacheAndReload);
                case PACKET_RESET_SETTINGS -> context.client().execute(LightBR::applyServerResetSettings);
                default -> {
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            FriendlyByteBuf dataBuf = payload.toPacketByteBuf();
            int packetType = dataBuf.readVarInt();
            if (packetType == CONFIG_PACKET_ACK) {
                System.out.println(Component.translatable("lightbr.log.acknowledge_received").getString());
                context.client().execute(LightBR::applyServerControlAck);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            clearServerControl();
            TrackCache.clear();
            pendingAckTicks = -1;
            System.out.println(Component.translatable("lightbr.log.disconnect_cleared").getString());
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            clearServerControl();
            TrackCache.clear();
            pendingAckTicks = 1;
        });
    }
}
