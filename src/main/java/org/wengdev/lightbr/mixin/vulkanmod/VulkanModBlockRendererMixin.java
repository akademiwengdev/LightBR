package org.wengdev.lightbr.mixin.vulkanmod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.vulkanmod.render.chunk.build.renderer.BlockRenderer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.RenderChecker;

@Mixin(BlockRenderer.class)
public class VulkanModBlockRendererMixin {
    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true, remap = false)
    public void onRenderBlock(BlockState blockState, BlockPos blockPos, Vector3f pos, CallbackInfo ci) {
        if (!RenderChecker.shouldRenderBlock(blockState, blockPos)) {
            ci.cancel();
        }
    }
}
