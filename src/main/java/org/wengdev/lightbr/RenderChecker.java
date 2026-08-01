package org.wengdev.lightbr;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.wengdev.lightbr.obu.OBUManager;

import java.util.List;

public class RenderChecker {
    public static boolean shouldRenderBlock(BlockState state, BlockPos pos) {
        RenderContext context = RenderContextManager.get();
        if (!LightBR.config.isEnabled || !context.isEnabled || OBUManager.isDefaultSlipperinessSet()) {
            return true;
        }

        if (isInAlwaysRenderRegion(context.alwaysRenderRegions, pos)) {
            return true;
        }

        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();

        if (LightBR.isSlipperyBlock(blockId)) {
            long chunkKey = TrackCache.toChunkKey(pos);
            int sectionY = TrackCache.toSectionY(pos.getY());

            TrackCache.markTrackChunk(chunkKey, sectionY);
            return true;
        }

        if (context.renderAllWater && state.is(Blocks.WATER)) return true;
        if (context.renderAllLava && state.is(Blocks.LAVA)) return true;

        long chunkKey = TrackCache.toChunkKey(pos);
        int sectionY = TrackCache.toSectionY(pos.getY());
        return TrackCache.shouldRenderChunk(chunkKey, sectionY);
    }

    private static boolean isInAlwaysRenderRegion(List<Tuple<Vec3, Vec3>> regions, BlockPos pos) {
        if (regions == null || regions.isEmpty()) {
            return false;
        }

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        for (Tuple<Vec3, Vec3> region : regions) {
            Vec3 a = region.getA();
            Vec3 b = region.getB();
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
