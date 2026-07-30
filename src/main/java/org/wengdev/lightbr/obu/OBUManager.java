package org.wengdev.lightbr.obu;

import dev.o7moon.openboatutils.OpenBoatUtils;
import net.fabricmc.loader.api.FabricLoader;

public class OBUManager {
    private static boolean isOBULoaded() {
        return FabricLoader.getInstance().isModLoaded("openboatutils");
    }

    private static boolean hasObuCompatibilityBeenScanned = false;
    private static boolean obuCompatible = false;

    private static boolean isOBUCompatible() {
        if (!hasObuCompatibilityBeenScanned) {
            try {
                OpenBoatUtils.class.getDeclaredField("instance");
                obuCompatible = true;
            } catch (NoSuchFieldException e) {
                obuCompatible = false;
            }
            hasObuCompatibilityBeenScanned = true;
        }
        return obuCompatible;
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
