package org.wengdev.lightbr;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.wengdev.lightbr.config.LightBRConfig;
import org.wengdev.lightbr.obu.OBUManager;

import java.util.HashMap;

public class LightBR implements ClientModInitializer {
    public static LightBRConfig config;
    public static HashMap<String, Float> defaultSlipperinessMap = null;

    private static final float DEFAULT_BLOCK_SLIPPERINESS = 0.6f;
    private static final String KEY_CATEGORY = "category.lightbr";
    private static KeyBinding toggleKey;

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
        config.saveAndReloadWorld();
    }

    @Override
    public void onInitializeClient() {
        config = LightBRConfig.load();

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

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            TrackCache.clear();
            System.out.println(Text.translatable("lightbr.log.disconnect_cleared").getString());
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            TrackCache.clear();
        });
    }
}
