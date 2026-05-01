package org.wengdev.lightbr.obu;

import org.wengdev.lightbr.compat.obu.ObuCompatLoader;
import org.wengdev.lightbr.compat.obu.ObuSlipperinessState;

public class OBUFetcher {
    public static boolean isOBUEnabled() {
        return ObuCompatLoader.get().isEnabled();
    }

    public static boolean isIsDefaultSlipperinessSet() {
        return ObuSlipperinessState.isDefaultSlipperinessSet();
    }

    public static void setIsDefaultSlipperinessSet(boolean value) {
        ObuSlipperinessState.setDefaultSlipperinessSet(value);
    }

    public static boolean isBlockSlippery(String blockId) {
        return ObuCompatLoader.get().isBlockSlippery(blockId);
    }
}
