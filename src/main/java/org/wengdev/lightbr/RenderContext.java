package org.wengdev.lightbr;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class RenderContext {
    public final boolean isEnabled;

    public final int chunkXZRadius;
    public final int chunkYRadius;

    public final List<Pair<Vec3d, Vec3d>> alwaysRenderRegions;

    public final boolean renderAllWater;
    public final boolean renderAllLava;

    public final boolean unrenderBlockEntities;

    public final List<String> alwaysRenderBlockEntities;

    public RenderContext(boolean isEnabled, int chunkXZRadius, int chunkYRadius, List<Pair<Vec3d, Vec3d>> alwaysRenderRegions, boolean renderAllWater, boolean renderAllLava, boolean unrenderBlockEntities, List<String> alwaysRenderBlockEntities) {
        this.isEnabled = isEnabled;
        this.chunkXZRadius = chunkXZRadius;
        this.chunkYRadius = chunkYRadius;
        this.alwaysRenderRegions = alwaysRenderRegions;
        this.renderAllWater = renderAllWater;
        this.renderAllLava = renderAllLava;
        this.unrenderBlockEntities = unrenderBlockEntities;
        this.alwaysRenderBlockEntities = alwaysRenderBlockEntities;
    }

    public void writeToBuf(PacketByteBuf buf) {
        buf.writeBoolean(isEnabled);
        buf.writeVarInt(chunkXZRadius);
        buf.writeVarInt(chunkYRadius);
        buf.writeBoolean(renderAllWater);
        buf.writeBoolean(renderAllLava);
        buf.writeBoolean(unrenderBlockEntities);

        List<String> blockEntities = alwaysRenderBlockEntities == null ? List.of() : alwaysRenderBlockEntities;
        buf.writeVarInt(blockEntities.size());
        for (String blockEntityId : blockEntities) {
            buf.writeString(blockEntityId);
        }

        List<Pair<Vec3d, Vec3d>> regions = alwaysRenderRegions == null ? List.of() : alwaysRenderRegions;
        buf.writeVarInt(regions.size());
        for (Pair<Vec3d, Vec3d> region : regions) {
            Vec3d a = region.getLeft();
            Vec3d b = region.getRight();
            buf.writeDouble(a.x);
            buf.writeDouble(a.y);
            buf.writeDouble(a.z);
            buf.writeDouble(b.x);
            buf.writeDouble(b.y);
            buf.writeDouble(b.z);
        }
    }

    public static RenderContext readFromBuf(PacketByteBuf buf) {
        boolean isEnabled = buf.readBoolean();
        int chunkXZRadius = buf.readVarInt();
        int chunkYRadius = buf.readVarInt();
        boolean renderAllWater = buf.readBoolean();
        boolean renderAllLava = buf.readBoolean();
        boolean unrenderBlockEntities = buf.readBoolean();

        int blockEntityCount = buf.readVarInt();
        List<String> blockEntities = new ArrayList<>(blockEntityCount);
        for (int i = 0; i < blockEntityCount; i++) {
            blockEntities.add(buf.readString());
        }

        int regionCount = buf.readVarInt();
        List<Pair<Vec3d, Vec3d>> regions = new ArrayList<>(regionCount);
        for (int i = 0; i < regionCount; i++) {
            Vec3d a = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            Vec3d b = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            regions.add(new Pair<>(a, b));
        }

        return new RenderContext(
                isEnabled,
                chunkXZRadius,
                chunkYRadius,
                regions,
                renderAllWater,
                renderAllLava,
                unrenderBlockEntities,
                blockEntities
        );
    }
}
