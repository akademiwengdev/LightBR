package org.wengdev.lightbr.compat.obu.legacy;

import dev.o7moon.openboatutils.OpenBoatUtils;
import org.wengdev.lightbr.compat.obu.ObuCompat;

import java.util.HashMap;
import java.util.Map;

public final class LegacyObuCompat implements ObuCompat {
    @Override
    public boolean isEnabled() {
        return OpenBoatUtils.enabled;
    }

    @Override
    public boolean isBlockSlippery(String blockId) {
        return OpenBoatUtils.getSlipperinessMap().containsKey(blockId);
    }
}

