package org.wengdev.lightbr.mixin.obu;

import dev.o7moon.openboatutils.OpenBoatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.obu.OBUFetcher;

@Mixin(OpenBoatUtils.class)
public class OBUMixin {
    @Inject(method = "resetSettings", at = @At("HEAD"), remap = false)
    private static void onResetSettings(CallbackInfo ci) {
        OBUFetcher.setIsDefaultSlipperinessSet(false);
    }

    @Inject(method = "setAllBlocksSlipperiness", at = @At("HEAD"), remap = false)
    private static void onSetAllBlocksSlipperiness(CallbackInfo ci) {
        OBUFetcher.setIsDefaultSlipperinessSet(true);
    }
}
