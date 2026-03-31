package org.wengdev.lightbr.obu;

import dev.o7moon.openboatutils.OpenBoatUtils;

import java.util.HashMap;

public class OBUFetcher {
    private static boolean isDefaultSlipperinessSet = false;

    public static boolean isOBUEnabled() {
        return OpenBoatUtils.enabled;
    }

    public static boolean isIsDefaultSlipperinessSet() {
        return isDefaultSlipperinessSet;
    }

    public static void setIsDefaultSlipperinessSet(boolean value) {
        isDefaultSlipperinessSet = value;
    }

    public static HashMap<String, Float> getSlipperinessMap() {
        return OpenBoatUtils.getSlipperinessMap();
    }
}
