package org.wengdev.lightbr.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
//? if 1.21.11 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
*///? } elif 1.21.4 {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
//? }
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.LightBR;
import org.wengdev.lightbr.RenderContext;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    //? if 1.21.11 {
    /*@Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void onRenderBlockEntity(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        RenderContext context = LightBR.getRenderContext();
        if (!context.isEnabled || !context.unrenderBlockEntities) return;

        String blockId = BuiltInRegistries.BLOCK.getKey(renderState.blockState.getBlock()).toString();
        if (context.alwaysRenderBlockEntities.contains(blockId)) {
            return;
        }

        ci.cancel();
    }
    *///? } elif 1.21.4 {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public <E extends BlockEntity> void onRenderBlockEntity(E blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
        RenderContext context = LightBR.getRenderContext();
        if (!context.isEnabled || !context.unrenderBlockEntities) return;

        String blockId = BuiltInRegistries.BLOCK.getKey(blockEntity.getBlockState().getBlock()).toString();
        if (context.alwaysRenderBlockEntities.contains(blockId)) {
            return;
        }

        ci.cancel();
    }
    //? }
}
