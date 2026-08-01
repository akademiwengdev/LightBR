package org.wengdev.lightbr;

import org.wengdev.lightbr.network.ConfigPayloadHandlers;
import org.wengdev.lightbr.network.SettingsPayloadHandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ServerControlManager {
    public static volatile RenderContextPatch serverContextPatch;

    private static final ConfigPayloadHandlers configPayloadHandlers = new ConfigPayloadHandlers();
    private static final SettingsPayloadHandlers settingsPayloadHandlers = new SettingsPayloadHandlers();

    private static final List<Function<RenderContextPatch, RenderContextPatch>> pendingUpdaters = Collections.synchronizedList(new ArrayList<>());
    private static boolean pendingCacheReset = false;

    public static void init() {
        configPayloadHandlers.registerChannels();
        settingsPayloadHandlers.registerChannels();

        configPayloadHandlers.registerHandlers();
        settingsPayloadHandlers.registerHandlers();
    }

    public static void sendAcknowledgement() {
        configPayloadHandlers.sendAcknowledgement(LightBR.PROTOCOL_VERSION);
    }

    public static void queueServerOverride(Function<RenderContextPatch, RenderContextPatch> updater) {
        pendingUpdaters.add(updater);
    }

    public static void queueCacheReset() {
        pendingCacheReset = true;
    }

    public static void flushPendingOverrides() {
        if (pendingUpdaters.isEmpty() && !pendingCacheReset) {
            return;
        }

        if (!pendingUpdaters.isEmpty()) {
            RenderContextPatch base = serverContextPatch != null ? serverContextPatch : RenderContextPatch.EMPTY;
            for (Function<RenderContextPatch, RenderContextPatch> updater : pendingUpdaters) {
                base = updater.apply(base);
            }
            serverContextPatch = base;
        }

        pendingUpdaters.clear();
        pendingCacheReset = false;

        RenderContextManager.reloadRenderContext();
        LightBR.clearCacheAndReload();
    }

    public static void clearServerControl() {
        serverContextPatch = null;
        RenderContextManager.reloadRenderContext();
    }

    public static boolean isServerControlled() {
        return serverContextPatch != null && serverContextPatch != RenderContextPatch.EMPTY;
    }

    public static boolean isIsEnabledServerControlled() {
        return serverContextPatch != null && serverContextPatch.isEnabled() != null;
    }

    public static boolean isRenderAllWaterServerControlled() {
        return serverContextPatch != null && serverContextPatch.renderAllWater() != null;
    }

    public static boolean isChunkXZServerControlled() {
        return serverContextPatch != null && serverContextPatch.chunkXZRadius() != null;
    }

    public static boolean isChunkYServerControlled() {
        return serverContextPatch != null && serverContextPatch.chunkYRadius() != null;
    }

    public static boolean isRenderAllLavaServerControlled() {
        return serverContextPatch != null && serverContextPatch.renderAllLava() != null;
    }

    public static boolean isAlwaysRenderRegionsServerControlled() {
        return serverContextPatch != null && serverContextPatch.alwaysRenderRegions() != null;
    }
}
