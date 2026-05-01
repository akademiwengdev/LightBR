package org.wengdev.lightbr.compat.obu.latest.mixin;

import dev.o7moon.openboatutils.MutableContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.wengdev.lightbr.compat.obu.ObuSlipperinessState;

@Mixin(MutableContext.class)
public class MutableContextMixin {
    @Inject(method = "setDefaultSlipperiness", at = @At("HEAD"), remap = false, require = 0)
    private void onSetDefaultSlipperiness(float v, CallbackInfoReturnable<MutableContext> cir) {
        ObuSlipperinessState.setDefaultSlipperinessSet(true);
    }
}
