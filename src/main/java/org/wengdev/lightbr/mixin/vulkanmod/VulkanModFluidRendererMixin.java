package org.wengdev.lightbr.mixin.vulkanmod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.vulkanmod.render.chunk.build.renderer.FluidRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.RenderChecker;

@Mixin(FluidRenderer.class)
public class VulkanModFluidRendererMixin {
    @Inject(method = "renderLiquid", at = @At("HEAD"), cancellable = true, remap = false)
    public void onRenderLiquid(BlockState blockState, FluidState fluidState, BlockPos blockPos, CallbackInfo ci) {
        if (!RenderChecker.shouldRenderBlock(blockState, blockPos)) {
            ci.cancel();
        }
    }
}
