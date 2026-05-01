package org.wengdev.lightbr.compat.obu;

public final class ObuSlipperinessState {
    private static volatile boolean defaultSlipperinessSet;

    private ObuSlipperinessState() {
    }

    public static boolean isDefaultSlipperinessSet() {
        return defaultSlipperinessSet;
    }

    public static void setDefaultSlipperinessSet(boolean value) {
        defaultSlipperinessSet = value;
    }
}

