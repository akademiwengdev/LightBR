package org.wengdev.lightbr.obu;

import net.fabricmc.loader.api.FabricLoader;

public class OBUManager {
    private static boolean isOBULoaded() {
        return FabricLoader.getInstance().isModLoaded("openboatutils");
    }

    private static boolean hasObuCompatibilityBeenScanned = false;
    private static boolean isOBUInstanceExists = false;

    private static boolean isOBUCompatible() {
        if (!hasObuCompatibilityBeenScanned) {
            isOBUInstanceExists = OBUFetcher.doesOBUInstanceExist();
            hasObuCompatibilityBeenScanned = true;
        }
        return isOBUInstanceExists;
    }

    /** OBU mod loaded but incompatible */
    public static boolean isOBUIncompatible() {
        return isOBULoaded() && !isOBUCompatible();
    }

    public static boolean isOBUEnabled() {
        return isOBULoaded() && isOBUCompatible();
    }

    public static boolean isDefaultSlipperinessSet() {
        if (!isOBULoaded() || !isOBUCompatible()) {
            return false;
        }

        return OBUFetcher.isIsDefaultSlipperinessSet();
    }

    public static boolean isBlockSlippery(String blockId) {
        if (!isOBULoaded() || !isOBUCompatible()) {
            return false;
        }

        return OBUFetcher.isBlockSlippery(blockId);
    }
}
