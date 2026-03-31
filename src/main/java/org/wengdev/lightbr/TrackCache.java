package org.wengdev.lightbr;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.concurrent.locks.StampedLock;

public class TrackCache {
    private static final int NEARBY_CHUNK_RADIUS = 1;
    private static final int NEARBY_SECTION_RADIUS = 1;

    private static final LongOpenHashSet TRACK_SECTIONS = new LongOpenHashSet();
    private static final LongOpenHashSet RENDERABLE_SECTIONS = new LongOpenHashSet();
    private static final StampedLock LOCK = new StampedLock();

    public static void markTrackChunk(long chunkKey, int sectionY) {
        long stamp = LOCK.writeLock();
        try {
            long sectionKey = toSectionKey(chunkKey, sectionY);
            if (!TRACK_SECTIONS.add(sectionKey)) {
                return;
            }

            int cx = ChunkPos.getPackedX(chunkKey);
            int cz = ChunkPos.getPackedZ(chunkKey);

            for (int x = cx - NEARBY_CHUNK_RADIUS; x <= cx + NEARBY_CHUNK_RADIUS; x++) {
                for (int y = sectionY - NEARBY_SECTION_RADIUS; y <= sectionY + NEARBY_SECTION_RADIUS; y++) {
                    for (int z = cz - NEARBY_CHUNK_RADIUS; z <= cz + NEARBY_CHUNK_RADIUS; z++) {
                        RENDERABLE_SECTIONS.add(ChunkSectionPos.asLong(x, y, z));
                    }
                }
            }
        } finally {
            LOCK.unlockWrite(stamp);
        }
    }

    public static boolean shouldRenderChunk(long chunkKey, int sectionY) {
        long stamp = LOCK.readLock();
        try {
            return RENDERABLE_SECTIONS.contains(toSectionKey(chunkKey, sectionY));
        } finally {
            LOCK.unlockRead(stamp);
        }
    }

    public static void clear() {
        long stamp = LOCK.writeLock();
        try {
            TRACK_SECTIONS.clear();
            RENDERABLE_SECTIONS.clear();
        } finally {
            LOCK.unlockWrite(stamp);
        }
    }

    public static int toSectionY(int y) {
        return y >> 4;
    }

    public static long toChunkKey(BlockPos pos) {
        return ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static long toChunkKey(int chunkX, int chunkZ) {
        return ChunkPos.toLong(chunkX, chunkZ);
    }

    private static long toSectionKey(long chunkKey, int sectionY) {
        return ChunkSectionPos.asLong(ChunkPos.getPackedX(chunkKey), sectionY, ChunkPos.getPackedZ(chunkKey));
    }
}
