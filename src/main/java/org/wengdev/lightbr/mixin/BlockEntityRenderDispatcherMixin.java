package org.wengdev.lightbr.mixin;

import net.minecraft.block.entity.*;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.LightBR;

import java.util.List;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Unique
    private static final List<Class<? extends BlockEntity>> blockEntityWhitelists = List.of(
            ChestBlockEntity.class,
            EnchantingTableBlockEntity.class,
            SkullBlockEntity.class,
            BrewingStandBlockEntity.class,
            ShulkerBoxBlockEntity.class,
            AbstractFurnaceBlockEntity.class,
            BedBlockEntity.class
    );

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void onRenderBlockEntity(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {

        if (!LightBR.config.isEnabled) {
            return;
        }

        if (LightBR.config.unrenderNoCollisionBlocks) {
            for (Class<? extends BlockEntity> clazz : blockEntityWhitelists) {
                if (clazz.isInstance(blockEntity)) {
                    return;
                }
            }

            ci.cancel();
        }
    }
}
