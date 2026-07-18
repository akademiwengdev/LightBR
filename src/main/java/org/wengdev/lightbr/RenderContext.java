package org.wengdev.lightbr;

import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RenderContext {
    public final boolean isEnabled;

    public final int chunkXZRadius;
    public final int chunkYRadius;

    public final List<Tuple<Vec3, Vec3>> alwaysRenderRegions;

    public final boolean renderAllWater;
    public final boolean renderAllLava;

    public final boolean unrenderBlockEntities;

    public final List<String> alwaysRenderBlockEntities;

    public RenderContext(boolean isEnabled, int chunkXZRadius, int chunkYRadius, List<Tuple<Vec3, Vec3>> alwaysRenderRegions, boolean renderAllWater, boolean renderAllLava, boolean unrenderBlockEntities, List<String> alwaysRenderBlockEntities) {
        this.isEnabled = isEnabled;
        this.chunkXZRadius = chunkXZRadius;
        this.chunkYRadius = chunkYRadius;
        this.alwaysRenderRegions = alwaysRenderRegions;
        this.renderAllWater = renderAllWater;
        this.renderAllLava = renderAllLava;
        this.unrenderBlockEntities = unrenderBlockEntities;
        this.alwaysRenderBlockEntities = alwaysRenderBlockEntities;
    }
}
