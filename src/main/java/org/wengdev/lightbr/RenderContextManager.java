package org.wengdev.lightbr;

import org.wengdev.lightbr.config.LightBRConfig;

import java.util.List;

public class RenderContextManager {
    private static volatile RenderContext renderContext;

    public static RenderContext get() {
        RenderContext context = renderContext;

        if (context == null) {
            context = reloadRenderContext();
        }

        return context;
    }

    public static void reloadContextAndClearCache() {
        reloadRenderContext();
        TrackCache.clear();
    }

    public static RenderContext reloadRenderContext() {
        LightBRConfig fallback = LightBR.config != null ? LightBR.config : new LightBRConfig();
        RenderContext defaults = buildRenderContextFromConfig(fallback);

        if (ServerControlManager.serverContextPatch != null) {
            renderContext = ServerControlManager.serverContextPatch.merge(defaults);
        } else {
            renderContext = defaults;
        }

        return renderContext;
    }

    private static RenderContext buildRenderContextFromConfig(LightBRConfig config) {
        return new RenderContext(
                config.isEnabled,
                config.chunkXZRadius,
                config.chunkYRadius,
                List.of(),
                config.renderAllWater,
                config.renderAllLava
        );
    }
}
