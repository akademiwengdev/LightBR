package org.wengdev.lightbr;

import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public record RenderContextPatch(
        Boolean isEnabled,
        Integer chunkXZRadius,
        Integer chunkYRadius,
        Boolean renderAllWater,
        Boolean renderAllLava,
        Map<Integer, List<Tuple<Vec3, Vec3>>> alwaysRenderRegions
) {
    public static RenderContextPatch EMPTY = new RenderContextPatch(null, null, null, null, null, null);

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

    public RenderContextPatch withSetAlwaysRenderRegions(int id, List<Tuple<Vec3, Vec3>> regions) {
        Map<Integer, List<Tuple<Vec3, Vec3>>> newMap = alwaysRenderRegions != null
                ? new HashMap<>(alwaysRenderRegions)
                : new HashMap<>();
        newMap.put(id, regions);
        return new RenderContextPatch(isEnabled, chunkXZRadius, chunkYRadius, renderAllWater, renderAllLava, newMap);
    }

    public RenderContextPatch withAddAlwaysRenderRegions(int id, List<Tuple<Vec3, Vec3>> regions) {
        Map<Integer, List<Tuple<Vec3, Vec3>>> newMap = alwaysRenderRegions != null
                ? new HashMap<>(alwaysRenderRegions)
                : new HashMap<>();
        List<Tuple<Vec3, Vec3>> existing = newMap.get(id);
        if (existing != null) {
            List<Tuple<Vec3, Vec3>> combined = new ArrayList<>(existing);
            combined.addAll(regions);
            newMap.put(id, combined);
        } else {
            newMap.put(id, regions);
        }
        return new RenderContextPatch(isEnabled, chunkXZRadius, chunkYRadius, renderAllWater, renderAllLava, newMap);
    }

    public RenderContextPatch withRemoveAlwaysRenderRegions(int id) {
        if (alwaysRenderRegions == null) return this;
        Map<Integer, List<Tuple<Vec3, Vec3>>> newMap = new HashMap<>(alwaysRenderRegions);
        newMap.remove(id);
        return new RenderContextPatch(isEnabled, chunkXZRadius, chunkYRadius, renderAllWater, renderAllLava, newMap);
    }

    public RenderContext merge(RenderContext defaults) {
        List<Tuple<Vec3, Vec3>> regions;
        if (alwaysRenderRegions != null) {
            regions = alwaysRenderRegions.values().stream()
                    .flatMap(List::stream)
                    .toList();
        } else {
            regions = defaults.alwaysRenderRegions;
        }
        return new RenderContext(
                isEnabled != null ? isEnabled : defaults.isEnabled,
                chunkXZRadius != null ? chunkXZRadius : defaults.chunkXZRadius,
                chunkYRadius != null ? chunkYRadius : defaults.chunkYRadius,
                regions,
                renderAllWater != null ? renderAllWater : defaults.renderAllWater,
                renderAllLava != null ? renderAllLava : defaults.renderAllLava
        );
    }
}
