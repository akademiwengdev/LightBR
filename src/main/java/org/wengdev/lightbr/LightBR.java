package org.wengdev.lightbr;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.wengdev.lightbr.config.LightBRConfig;
import org.wengdev.lightbr.obu.OBUManager;
import org.wengdev.lightbr.network.SettingsPayload;

import java.util.HashMap;
import java.util.List;

public class LightBR implements ClientModInitializer {
    public static LightBRConfig config;
    public static final int PROTOCOL_VERSION = loadProtocolVersion();

    private static final int DEFAULT_PROTOCOL_VERSION = 1;

    public static HashMap<String, Float> defaultSlipperinessMap = null;

    private static final int DEFAULT_CHUNK_XZ_RADIUS = 1;
    private static final int DEFAULT_CHUNK_Y_RADIUS = 1;

    private static final int PACKET_ACK = 0;
    private static final int PACKET_SET_CONTEXT = 1;
    private static final int PACKET_RESET_CACHE = 2;

    private static volatile RenderContext renderContext;
    private static volatile boolean serverControlled = false;

    private static final float DEFAULT_BLOCK_SLIPPERINESS = 0.6f;
    private static final String KEY_CATEGORY = "category.lightbr";
    private static KeyBinding toggleKey;

    private static int loadProtocolVersion() {
        int version = DEFAULT_PROTOCOL_VERSION;
        try {
            var container = FabricLoader.getInstance().getModContainer("lightbr");
            if (container.isPresent()) {
                CustomValue custom = container.get().getMetadata().getCustomValue("lightbr");
                if (custom != null && custom.getType() == CustomValue.CvType.OBJECT) {
                    CustomValue entry = custom.getAsObject().get("protocol_version");
                    if (entry != null) {
                        version = Integer.parseInt(entry.getAsString());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read LightBR protocol_version, using default: " + e.getMessage());
        }
        return version;
    }

    public static HashMap<String, Float> loadDefaultSlipperinessMap() {
        if (defaultSlipperinessMap == null) {
            defaultSlipperinessMap = new HashMap<>();

            for (Block b : Registries.BLOCK.stream().toList()) {
                if (b.getSlipperiness() != DEFAULT_BLOCK_SLIPPERINESS) {
                    defaultSlipperinessMap.put(Registries.BLOCK.getId(b).toString(), b.getSlipperiness());
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

    public static RenderContext getRenderContext() {
        RenderContext context = renderContext;
        if (context == null) {
            LightBRConfig fallback = config != null ? config : new LightBRConfig();
            context = buildRenderContextFromConfig(fallback);
            renderContext = context;
        }
        return context;
    }

    public static void updateRenderContextFromConfig() {
        if (config == null || serverControlled) {
            return;
        }
        renderContext = buildRenderContextFromConfig(config);
        TrackCache.clear();
    }

    public static void applyServerRenderContext(RenderContext context) {
        renderContext = context;
        serverControlled = true;
        TrackCache.clear();
        reloadWorldRenderer();
    }

    public static void clearServerControl() {
        serverControlled = false;
        updateRenderContextFromConfig();
    }

    private static void reloadWorldRenderer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.worldRenderer != null) {
            client.worldRenderer.reload();
        }
    }

    private static void sendAcknowledgement() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(PACKET_ACK);
        buf.writeVarInt(PROTOCOL_VERSION);
        ClientPlayNetworking.send(SettingsPayload.fromBuf(buf));
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

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lightbr.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                toggleEnabled();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(SettingsPayload.ID, (payload, context) -> {
            PacketByteBuf dataBuf = payload.toPacketByteBuf();
            int packetType = dataBuf.readVarInt();
            if (packetType == PACKET_ACK || packetType == PACKET_SET_CONTEXT) {
                RenderContext contextPayload = RenderContext.readFromBuf(dataBuf);
                context.client().execute(() -> applyServerRenderContext(contextPayload));
                return;
            }

            if (packetType == PACKET_RESET_CACHE) {
                context.client().execute(() -> {
                    TrackCache.clear();
                    reloadWorldRenderer();
                });
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            clearServerControl();
            TrackCache.clear();
            System.out.println(Text.translatable("lightbr.log.disconnect_cleared").getString());
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            clearServerControl();
            TrackCache.clear();
            sendAcknowledgement();
        });
    }
}
