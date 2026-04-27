package org.wengdev.lightbr.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.LightBR;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void onRenderBlockEntity(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (!LightBR.config.isEnabled) {
            return;
        }

        if (LightBR.config.shouldUnrenderBlockEntities) {
            String blockId = Registries.BLOCK.getId(blockEntity.getCachedState().getBlock()).toString();
            if (LightBR.config.renderableBlockEntities.contains(blockId)) {
                return;
            }

            ci.cancel();
        }
    }
}
