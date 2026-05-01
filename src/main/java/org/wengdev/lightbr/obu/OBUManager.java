package org.wengdev.lightbr.obu;

import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;

public class OBUManager {
    private static boolean isOBULoaded() {
        return FabricLoader.getInstance().isModLoaded("openboatutils");
    }

    public static boolean isOBUEnabled() {
        if (!isOBULoaded()) {
            return false;
        }

        return OBUFetcher.isOBUEnabled();
    }

    public static boolean isDefaultSlipperinessSet() {
        if (!isOBULoaded()) {
            return false;
        }

        return OBUFetcher.isIsDefaultSlipperinessSet();
    }

    public static boolean isBlockSlippery(String blockId) {
        if (!isOBULoaded()) {
            return false;
        }

        return OBUFetcher.isBlockSlippery(blockId);
    }
}
