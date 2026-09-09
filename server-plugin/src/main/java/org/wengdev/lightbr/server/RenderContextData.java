package org.wengdev.lightbr.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderContextData {
    public final Boolean enabled;
    public final Integer chunkXZRadius;
    public final Integer chunkYRadius;
    public final Boolean renderAllWater;
    public final Boolean renderAllLava;
    public final Boolean autoFixIncompleteChunks;
    public final Map<Integer, List<Region>> alwaysRenderRegions;

    public RenderContextData(Boolean enabled, Integer chunkXZRadius, Integer chunkYRadius, Boolean renderAllWater, Boolean renderAllLava, Boolean autoFixIncompleteChunks, Map<Integer, List<Region>> alwaysRenderRegions) {
        this.enabled = enabled;
        this.chunkXZRadius = chunkXZRadius;
        this.chunkYRadius = chunkYRadius;
        this.renderAllWater = renderAllWater;
        this.renderAllLava = renderAllLava;
        this.autoFixIncompleteChunks = autoFixIncompleteChunks;
        this.alwaysRenderRegions = alwaysRenderRegions;
    }

    public static RenderContextData demoDefault() {
        return new RenderContextData(
                true,
                1,
                1,
                false,
                false,
                false,
                new HashMap<>()
        );
    }

    public record Region(double ax, double ay, double az, double bx, double by, double bz) {
    }
}
