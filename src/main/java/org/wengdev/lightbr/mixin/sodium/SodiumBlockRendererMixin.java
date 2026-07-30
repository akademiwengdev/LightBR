package org.wengdev.lightbr.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
//? if 1.21.11 {
/*import net.minecraft.client.renderer.block.model.BlockStateModel;
*///? } elif 1.21.4 {
import net.minecraft.client.resources.model.BakedModel;
//? }

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.RenderChecker;

@Mixin(BlockRenderer.class)
public class SodiumBlockRendererMixin {

    //? if 1.21.11 {
    /*@Inject(method = "renderModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (!RenderChecker.shouldRenderBlock(state, pos)) {
            ci.cancel();
        }
    }
    *///? } elif 1.21.4 {
    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (!RenderChecker.shouldRenderBlock(state, pos)) {
            ci.cancel();
        }
    }
    //? }
}
