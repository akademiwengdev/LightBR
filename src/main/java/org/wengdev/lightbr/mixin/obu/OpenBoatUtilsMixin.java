package org.wengdev.lightbr.mixin.obu;

import dev.o7moon.openboatutils.OpenBoatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.obu.ObuSlipperinessState;

@Mixin(OpenBoatUtils.class)
public class OpenBoatUtilsMixin {
    @Inject(method = "resetAll", at = @At("HEAD"), remap = false, require = 0)
    private static void onResetAll(CallbackInfo ci) {
        ObuSlipperinessState.setDefaultSlipperinessSet(false);
    }
}
