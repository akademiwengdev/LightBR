package org.wengdev.lightbr;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
//? if 1.21.11
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.wengdev.lightbr.config.LightBRConfig;
import org.wengdev.lightbr.obu.OBUManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class LightBR implements ClientModInitializer {
    private static final int DEFAULT_PROTOCOL_VERSION = 4;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static LightBRConfig config;
    public static final int PROTOCOL_VERSION = loadProtocolVersion();

    public static HashMap<String, Float> defaultSlipperinessMap = null;

    private static final float DEFAULT_BLOCK_SLIPPERINESS = 0.6f;
    //? if 1.21.11 {
    /*private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("lightbr", "category"));
    *///? } elif 1.21.4 {
    private static final String KEY_CATEGORY = "category.lightbr";
    //? }
    private static KeyMapping toggleKey;
    private static KeyMapping configKey;

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
            LOGGER.error("Failed to read LightBR protocol_version from properties", e);
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

    private static void toggleEnabled(Minecraft client) {
        if (config == null) {
            return;
        }
        config.isEnabled = !config.isEnabled;
        config.saveAndReloadWorldOnly();

        if (client.player != null) {
            Component msg = Component.translatable(config.isEnabled ? "lightbr.actionbar.enabled" : "lightbr.actionbar.disabled");
            client.player.displayClientMessage(msg, false);
            client.player.displayClientMessage(msg, true);
        }
    }

    public static void clearCacheAndReload() {
        TrackCache.clear();

        if (RenderContextManager.get().isEnabled) {
            reloadWorldRenderer();
        }
    }

    private static void reloadWorldRenderer() {
        Minecraft client = Minecraft.getInstance();
        client.levelRenderer.allChanged();
    }

    private static void processPendingSections(Minecraft client) {
        LongList pending = TrackCache.drainPendingSections();
        if (pending.isEmpty()) {
            return;
        }

        for (int i = 0; i < pending.size(); i++) {
            long sectionPos = pending.getLong(i);
            int sx = SectionPos.x(sectionPos);
            int sy = SectionPos.y(sectionPos);
            int sz = SectionPos.z(sectionPos);
            client.levelRenderer.setBlocksDirty(
                sx << 4, sy << 4, sz << 4,
                (sx << 4) + 15, (sy << 4) + 15, (sz << 4) + 15
            );
        }
    }

    @Override
    public void onInitializeClient() {
        config = LightBRConfig.load();
        RenderContextManager.reloadContextAndClearCache();

        ServerControlManager.init();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lightbr.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        ));
        configKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lightbr.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                toggleEnabled(client);
            }
            while (configKey.consumeClick()) {
                client.setScreen(new org.wengdev.lightbr.config.YACLConfigScreen(client.screen));
            }

            if (config != null && config.autoFixIncompleteChunks && client.levelRenderer != null) {
                processPendingSections(client);
            }

            ServerControlManager.flushPendingOverrides();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ServerControlManager.clearServerControl();
            TrackCache.clear();

            LOGGER.info("{}", Component.translatable("lightbr.log.disconnect_cleared").getString());
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerControlManager.clearServerControl();
            TrackCache.clear();

            ServerControlManager.sendAcknowledgement();

            if (OBUManager.isOBUIncompatible()) {
                Component warning = Component.translatable("lightbr.log.obu_incompatible");
                if (client.player != null) {
                    client.player.displayClientMessage(warning, false);
                    client.player.displayClientMessage(warning, true);
                }
            }
        });
    }
}
