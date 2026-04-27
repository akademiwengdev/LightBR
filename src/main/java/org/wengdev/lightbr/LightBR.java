package org.wengdev.lightbr;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import org.wengdev.lightbr.config.LightBRConfig;
import org.wengdev.lightbr.obu.OBUManager;

import java.util.HashMap;

public class LightBR implements ClientModInitializer {
    public static LightBRConfig config;
    public static HashMap<String, Float> defaultSlipperinessMap = null;

    private static final float DEFAULT_BLOCK_SLIPPERINESS = 0.6f;

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

    public static HashMap<String, Float> getSlipperinessMap() {
        if (OBUManager.isOBUEnabled()) {
            return OBUManager.getSlipperinessMap();
        } else {
            return loadDefaultSlipperinessMap();
        }
    }

    @Override
    public void onInitializeClient() {
        config = LightBRConfig.load();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            TrackCache.clear();
            System.out.println("[LightBR] Disconnected from world. Track cache cleared.");
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            TrackCache.clear();
        });
    }
}
