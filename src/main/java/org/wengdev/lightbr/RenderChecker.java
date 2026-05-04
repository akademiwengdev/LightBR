package org.wengdev.lightbr;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.wengdev.lightbr.obu.OBUManager;

import java.util.List;

public class RenderChecker {
    public static boolean shouldRenderBlock(BlockState state, BlockPos pos, BlockRenderView world) {
        RenderContext context = LightBR.getRenderContext();
        if (!context.isEnabled || OBUManager.isDefaultSlipperinessSet()) {
            return true;
        }

        if (isInAlwaysRenderRegion(context.alwaysRenderRegions, pos)) {
            return true;
        }

        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        if (LightBR.isSlipperyBlock(blockId)) {
            long chunkKey = TrackCache.toChunkKey(pos);
            int sectionY = TrackCache.toSectionY(pos.getY());

            TrackCache.markTrackChunk(chunkKey, sectionY);
            return true;
        }

        if (context.renderAllWater && state.isOf(Blocks.WATER)) return true;
        if (context.renderAllLava && state.isOf(Blocks.LAVA)) return true;

        long chunkKey = TrackCache.toChunkKey(pos);
        int sectionY = TrackCache.toSectionY(pos.getY());
        return TrackCache.shouldRenderChunk(chunkKey, sectionY);
    }

    private static boolean isInAlwaysRenderRegion(List<Pair<Vec3d, Vec3d>> regions, BlockPos pos) {
        if (regions == null || regions.isEmpty()) {
            return false;
        }

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        for (Pair<Vec3d, Vec3d> region : regions) {
            Vec3d a = region.getLeft();
            Vec3d b = region.getRight();
            double minX = Math.min(a.x, b.x);
            double maxX = Math.max(a.x, b.x);
            double minY = Math.min(a.y, b.y);
            double maxY = Math.max(a.y, b.y);
            double minZ = Math.min(a.z, b.z);
            double maxZ = Math.max(a.z, b.z);

            if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
                return true;
            }
        }

        return false;
    }
}
