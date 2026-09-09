//? if 1.21.4 {
package org.wengdev.lightbr.mixin.indigo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wengdev.lightbr.RenderChecker;

@Mixin(TerrainRenderContext.class)
public class IndigoTerrainRendererMixin {
    @Inject(method = "tessellateBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void onTessellateBlock(
        BlockState blockState,
        BlockPos blockPos,
        BakedModel model,
        PoseStack matrixStack,
        CallbackInfo ci
    ) {
        if (!RenderChecker.shouldRenderBlock(blockState, blockPos)) {
            ci.cancel();
        }
    }
}
//? }
