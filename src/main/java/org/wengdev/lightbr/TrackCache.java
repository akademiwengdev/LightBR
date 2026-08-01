package org.wengdev.lightbr;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

import java.util.concurrent.locks.StampedLock;

public class TrackCache {
    private static final LongOpenHashSet TRACK_SECTIONS = new LongOpenHashSet();
    private static final LongOpenHashSet RENDERABLE_SECTIONS = new LongOpenHashSet();
    private static final LongOpenHashSet PENDING_SECTIONS = new LongOpenHashSet();
    private static final StampedLock LOCK = new StampedLock();

    public static void markTrackChunk(long chunkKey, int sectionY) {
        long stamp = LOCK.writeLock();
        try {
            long sectionKey = toSectionKey(chunkKey, sectionY);
            if (!TRACK_SECTIONS.add(sectionKey)) {
                return;
            }

            int cx = ChunkPos.getX(chunkKey);
            int cz = ChunkPos.getZ(chunkKey);
            int chunkRadius = getChunkRadius();
            int sectionRadius = getSectionRadius();

            for (int x = cx - chunkRadius; x <= cx + chunkRadius; x++) {
                for (int y = sectionY - sectionRadius; y <= sectionY + sectionRadius; y++) {
                    for (int z = cz - chunkRadius; z <= cz + chunkRadius; z++) {
                        long neighborKey = SectionPos.asLong(x, y, z);
                        if (RENDERABLE_SECTIONS.add(neighborKey)) {
                            PENDING_SECTIONS.add(neighborKey);
                        }
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
            PENDING_SECTIONS.clear();
        } finally {
            LOCK.unlockWrite(stamp);
        }
    }

    public static LongList drainPendingSections() {
        long stamp = LOCK.writeLock();
        try {
            if (PENDING_SECTIONS.isEmpty()) {
                return LongList.of();
            }
            LongList result = new LongArrayList(PENDING_SECTIONS);
            PENDING_SECTIONS.clear();
            return result;
        } finally {
            LOCK.unlockWrite(stamp);
        }
    }

    public static int toSectionY(int y) {
        return y >> 4;
    }

    public static long toChunkKey(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static long toChunkKey(int chunkX, int chunkZ) {
        return ChunkPos.asLong(chunkX, chunkZ);
    }

    private static int getChunkRadius() {
        RenderContext context = RenderContextManager.get();
        return Math.max(0, context.chunkXZRadius);
    }

    private static int getSectionRadius() {
        RenderContext context = RenderContextManager.get();
        return Math.max(0, context.chunkYRadius);
    }

    private static long toSectionKey(long chunkKey, int sectionY) {
        return SectionPos.asLong(ChunkPos.getX(chunkKey), sectionY, ChunkPos.getZ(chunkKey));
    }
}
