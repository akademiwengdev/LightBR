package org.wengdev.lightbr.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
//? if 1.21.11
//import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.core.BlockPos;
//? if 1.21.4
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.RenderChecker;

//? if 1.21.11
//import java.util.List;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderManagerMixin {
    //? if 1.21.11 {
    /*@Inject(method = "renderBatched", at = @At("HEAD"), cancellable = true)
    private void onRenderBatched(BlockState state, BlockPos pos, BlockAndTintGetter tintGetter, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, List<BlockModelPart> list, CallbackInfo ci) {
        if (!RenderChecker.shouldRenderBlock(state, pos)) {
            ci.cancel();
        }
    }
    *///? } elif 1.21.4 {
    @Inject(method="renderBatched", at = @At("HEAD"), cancellable = true)
    private void onRenderBatched(BlockState state, BlockPos pos, BlockAndTintGetter tintGetter, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, RandomSource randomSource, CallbackInfo ci) {
        if (!RenderChecker.shouldRenderBlock(state, pos)) {
            ci.cancel();
        }
    }
    //? }
}
