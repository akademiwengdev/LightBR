package org.wengdev.lightbr;

import net.minecraft.client.Minecraft;
import org.wengdev.lightbr.config.LightBRConfig;
import org.wengdev.lightbr.config.YACLConfigScreen;

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

        if (Minecraft.getInstance().screen instanceof YACLConfigScreen configScreen) {
            configScreen.refreshAppliedValues();
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
                config.renderAllLava,
                config.autoFixIncompleteChunks
        );
    }
}
