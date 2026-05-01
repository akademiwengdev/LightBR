package org.wengdev.lightbr.compat.obu.legacy.mixin;

import dev.o7moon.openboatutils.OpenBoatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.compat.obu.ObuSlipperinessState;

@Mixin(OpenBoatUtils.class)
public class OBULegacyMixin {
    @Inject(method = "resetSettings", at = @At("HEAD"), remap = false, require = 0)
    private static void onResetSettings(CallbackInfo ci) {
        ObuSlipperinessState.setDefaultSlipperinessSet(false);
    }

    @Inject(method = "setAllBlocksSlipperiness", at = @At("HEAD"), remap = false, require = 0)
    private static void onSetAllBlocksSlipperiness(CallbackInfo ci) {
        ObuSlipperinessState.setDefaultSlipperinessSet(true);
    }
}
