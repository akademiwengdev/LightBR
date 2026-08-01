package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import org.wengdev.lightbr.network.handler.settings.*;

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

    private final Map<Integer, PayloadHandler> handlers = Map.of(
            SET_ENABLED, new SetEnabledHandler(),
            SET_RENDER_ALL_WATER, new SetRenderAllWaterHandler(),
            SET_CHUNK_XZ, new SetChunkXZHandler(),
            SET_CHUNK_Y, new SetChunkYHandler(),
            SET_RENDER_ALL_LAVA, new SetRenderAllLavaHandler(),
            SET_ALWAYS_RENDER_REGIONS, new SetAlwaysRenderRegionsHandler(),
            RESET_CACHE, new ResetCacheHandler(),
            RESET_SETTINGS, new ResetSettingsHandler()
    );

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
