package org.wengdev.lightbr.server;

import java.util.ArrayList;
import java.util.List;

public class RenderContextData {
    public final Boolean enabled;
    public final Integer chunkXZRadius;
    public final Integer chunkYRadius;
    public final Boolean renderAllWater;
    public final Boolean renderAllLava;
    public final Boolean unrenderBlockEntities;
    public final List<String> alwaysRenderBlockEntities;
    public final List<Region> alwaysRenderRegions;

    public RenderContextData(Boolean enabled, Integer chunkXZRadius, Integer chunkYRadius, Boolean renderAllWater, Boolean renderAllLava, Boolean unrenderBlockEntities, List<String> alwaysRenderBlockEntities, List<Region> alwaysRenderRegions) {
        this.enabled = enabled;
        this.chunkXZRadius = chunkXZRadius;
        this.chunkYRadius = chunkYRadius;
        this.renderAllWater = renderAllWater;
        this.renderAllLava = renderAllLava;
        this.unrenderBlockEntities = unrenderBlockEntities;
        this.alwaysRenderBlockEntities = alwaysRenderBlockEntities;
        this.alwaysRenderRegions = alwaysRenderRegions;
    }

    public static RenderContextData demoDefault() {
        return new RenderContextData(
                true,
                1,
                1,
                true,
                true,
                true,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public record Region(double ax, double ay, double az, double bx, double by, double bz) {
    }
}
