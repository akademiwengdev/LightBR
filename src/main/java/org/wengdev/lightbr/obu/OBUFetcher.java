package org.wengdev.lightbr.obu;

import dev.o7moon.openboatutils.ISettingContext;
import dev.o7moon.openboatutils.OpenBoatUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class OBUFetcher {
    public static boolean isIsDefaultSlipperinessSet() {
        return ObuSlipperinessState.isDefaultSlipperinessSet();
    }

    public static void setIsDefaultSlipperinessSet(boolean value) {
        ObuSlipperinessState.setDefaultSlipperinessSet(value);
    }

    public static boolean isBlockSlippery(String blockId) {
        ResourceLocation id = ResourceLocation.tryParse(blockId);
        if (id == null) {
            return false;
        }

        ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null) {
            if (context.getBlocksWithSettings().contains(id)) {
                return true;
            }

            Float slipperiness = context.getBlockSlipperiness(id);

            return slipperiness != null && slipperiness != 0.6;
        }

        Float slipperiness = OpenBoatUtils.instance.getBlockSlipperiness(id);
        if (slipperiness != null && Float.compare(slipperiness, 0.6f) != 0)
            return true;

        return BuiltInRegistries.BLOCK.getValue(id).getFriction() != 0.6f;
    }
}
