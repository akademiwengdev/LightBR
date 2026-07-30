package org.wengdev.lightbr;

import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RenderContextPatch {
    public final Boolean isEnabled;
    public final Integer chunkXZRadius;
    public final Integer chunkYRadius;
    public final Boolean renderAllWater;
    public final Boolean renderAllLava;
    public final List<Tuple<Vec3, Vec3>> alwaysRenderRegions;

    public RenderContextPatch(Boolean isEnabled, Integer chunkXZRadius, Integer chunkYRadius, Boolean renderAllWater, Boolean renderAllLava, List<Tuple<Vec3, Vec3>> alwaysRenderRegions) {
        this.isEnabled = isEnabled;
        this.chunkXZRadius = chunkXZRadius;
        this.chunkYRadius = chunkYRadius;
        this.renderAllWater = renderAllWater;
        this.renderAllLava = renderAllLava;
        this.alwaysRenderRegions = alwaysRenderRegions;
    }

    public static RenderContextPatch empty() {
        return new RenderContextPatch(null, null, null, null, null, null);
    }

    public RenderContextPatch withEnabled(Boolean value) {
        return new RenderContextPatch(value, chunkXZRadius, chunkYRadius, renderAllWater, renderAllLava, alwaysRenderRegions);
    }

    public RenderContextPatch withChunkXZRadius(Integer value) {
        return new RenderContextPatch(isEnabled, value, chunkYRadius, renderAllWater, renderAllLava, alwaysRenderRegions);
    }

    public RenderContextPatch withChunkYRadius(Integer value) {
        return new RenderContextPatch(isEnabled, chunkXZRadius, value, renderAllWater, renderAllLava, alwaysRenderRegions);
    }

    public RenderContextPatch withRenderAllWater(Boolean value) {
        return new RenderContextPatch(isEnabled, chunkXZRadius, chunkYRadius, value, renderAllLava, alwaysRenderRegions);
    }

    public RenderContextPatch withRenderAllLava(Boolean value) {
        return new RenderContextPatch(isEnabled, chunkXZRadius, chunkYRadius, renderAllWater, value, alwaysRenderRegions);
    }

    public RenderContextPatch withAlwaysRenderRegions(List<Tuple<Vec3, Vec3>> value) {
        return new RenderContextPatch(isEnabled, chunkXZRadius, chunkYRadius, renderAllWater, renderAllLava, value);
    }

    public RenderContext merge(RenderContext defaults) {
        return new RenderContext(
                isEnabled != null ? isEnabled : defaults.isEnabled,
                chunkXZRadius != null ? chunkXZRadius : defaults.chunkXZRadius,
                chunkYRadius != null ? chunkYRadius : defaults.chunkYRadius,
                alwaysRenderRegions != null ? List.copyOf(alwaysRenderRegions) : defaults.alwaysRenderRegions,
                renderAllWater != null ? renderAllWater : defaults.renderAllWater,
                renderAllLava != null ? renderAllLava : defaults.renderAllLava
        );
    }
}
