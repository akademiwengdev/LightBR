package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import org.wengdev.lightbr.network.handler.settings.*;

import java.util.HashMap;
import java.util.Map;

public class SettingsPayloadHandlers {
    public static final int SET_ENABLED = 1;
    public static final int SET_RENDER_ALL_WATER = 2;
    public static final int SET_CHUNK_XZ = 3;
    public static final int SET_CHUNK_Y = 4;
    public static final int SET_RENDER_ALL_LAVA = 5;
    public static final int SET_ALWAYS_RENDER_REGIONS = 6;
    public static final int RESET_CACHE = 7;
    public static final int RESET_SETTINGS = 8;
    public static final int BULK_SET_CONTEXT = 9;
    public static final int ADD_ALWAYS_RENDER_REGIONS = 10;
    public static final int REMOVE_ALWAYS_RENDER_REGIONS = 11;

    private final Map<Integer, PayloadHandler> handlers;

    public SettingsPayloadHandlers() {
        handlers = new HashMap<>();
        handlers.put(SET_ENABLED, new SetEnabledHandler());
        handlers.put(SET_RENDER_ALL_WATER, new SetRenderAllWaterHandler());
        handlers.put(SET_CHUNK_XZ, new SetChunkXZHandler());
        handlers.put(SET_CHUNK_Y, new SetChunkYHandler());
        handlers.put(SET_RENDER_ALL_LAVA, new SetRenderAllLavaHandler());
        handlers.put(SET_ALWAYS_RENDER_REGIONS, new SetAlwaysRenderRegionsHandler());
        handlers.put(RESET_CACHE, new ResetCacheHandler());
        handlers.put(RESET_SETTINGS, new ResetSettingsHandler());
        handlers.put(BULK_SET_CONTEXT, new BulkSetContextHandler(handlers));
        handlers.put(ADD_ALWAYS_RENDER_REGIONS, new AddAlwaysRenderRegionsHandler());
        handlers.put(REMOVE_ALWAYS_RENDER_REGIONS, new RemoveAlwaysRenderRegionsHandler());
    }

    public void registerChannels() {
        PayloadTypeRegistry.playC2S().register(SettingsPayload.ID, SettingsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SettingsPayload.ID, SettingsPayload.CODEC);
    }

    public void registerHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(SettingsPayload.ID, (payload, context) -> {
            FriendlyByteBuf dataBuf = payload.toPacketByteBuf();
            int packetType = dataBuf.readVarInt();

            PayloadHandler handler = handlers.get(packetType);
            if (handler != null) {
                handler.handle(dataBuf, context);
            }
        });
    }
}
