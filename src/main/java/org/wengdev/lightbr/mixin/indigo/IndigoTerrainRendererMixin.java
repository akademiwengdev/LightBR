package org.wengdev.lightbr.mixin.indigo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
//? if 1.21.11
//import net.minecraft.client.renderer.block.model.BlockStateModel;
//? if 1.21.4
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.RenderChecker;

@Mixin(TerrainRenderContext.class)
public class IndigoTerrainRendererMixin {
    //? if 1.21.4 {
    @Inject(method = "tessellateBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void onTessellateBlock(BlockState blockState, BlockPos blockPos, BakedModel model, PoseStack matrixStack, CallbackInfo ci) {
        cancelIfFiltered(ci, blockState, blockPos);
    }
    //? } elif 1.21.11 {
    /*@Inject(method = "bufferModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void onBufferModel(BlockStateModel model, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
        cancelIfFiltered(ci, blockState, blockPos);
    }
    *///? }

    private static void cancelIfFiltered(CallbackInfo ci, BlockState state, BlockPos pos) {
        if (!RenderChecker.shouldRenderBlock(state, pos)) {
            ci.cancel();
        }
    }
}
