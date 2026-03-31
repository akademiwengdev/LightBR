package org.wengdev.lightbr;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.wengdev.lightbr.obu.OBUManager;

import java.util.HashMap;

public class RenderChecker {
    public static boolean shouldRenderBlock(BlockState state, BlockPos pos, BlockRenderView world) {
        if (!LightBR.config.isEnabled || OBUManager.isDefaultSlipperinessSet()) {
            return true;
        }

        long chunkKey = TrackCache.toChunkKey(pos);
        int sectionY = TrackCache.toSectionY(pos.getY());

        HashMap<String, Float> slipperinessMap = LightBR.getSlipperinessMap();
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        if (slipperinessMap.containsKey(blockId)) {
            TrackCache.markTrackChunk(chunkKey, sectionY);
            return true;
        }

        if (LightBR.config.renderAllWater && state.isOf(Blocks.WATER)) return true;
        if (LightBR.config.renderAllLava && state.isOf(Blocks.LAVA)) return true;

        if (LightBR.config.unrenderNoCollisionBlocks && state.getCollisionShape(world, pos).isEmpty()) {
            return false;
        }

        return TrackCache.shouldRenderChunk(chunkKey, sectionY);
    }
}
