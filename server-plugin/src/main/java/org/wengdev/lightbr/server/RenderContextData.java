package org.wengdev.lightbr.server;

import java.util.ArrayList;
import java.util.List;

public class RenderContextData {
    public final boolean enabled;
    public final int chunkXZRadius;
    public final int chunkYRadius;
    public final boolean renderAllWater;
    public final boolean renderAllLava;
    public final boolean unrenderBlockEntities;
    public final List<String> alwaysRenderBlockEntities;
    public final List<Region> alwaysRenderRegions;

    public RenderContextData(boolean enabled, int chunkXZRadius, int chunkYRadius, boolean renderAllWater, boolean renderAllLava, boolean unrenderBlockEntities, List<String> alwaysRenderBlockEntities, List<Region> alwaysRenderRegions) {
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

