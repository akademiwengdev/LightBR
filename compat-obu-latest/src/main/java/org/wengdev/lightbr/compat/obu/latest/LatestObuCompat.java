package org.wengdev.lightbr.compat.obu.latest;

import dev.o7moon.openboatutils.OpenBoatUtils;
import net.minecraft.util.Identifier;
import org.wengdev.lightbr.compat.obu.ObuCompat;

public final class LatestObuCompat implements ObuCompat {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isBlockSlippery(String blockId) {
        Identifier id = Identifier.tryParse(blockId);
        if (id == null) {
            return false;
        }

        Float slipperiness = OpenBoatUtils.instance.getBlockSlipperiness(id);
        return slipperiness != null && Float.compare(slipperiness, 0.6f) != 0;
    }
}
