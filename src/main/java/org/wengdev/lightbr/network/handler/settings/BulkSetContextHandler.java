package org.wengdev.lightbr.network.handler.settings;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import org.wengdev.lightbr.network.PayloadHandler;

import java.util.Map;

public class BulkSetContextHandler implements PayloadHandler {
    private final Map<Integer, PayloadHandler> handlers;

    public BulkSetContextHandler(Map<Integer, PayloadHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handle(FriendlyByteBuf buf, ClientPlayNetworking.Context context) {
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {
            int subType = buf.readVarInt();
            PayloadHandler handler = handlers.get(subType);
            if (handler != null) {
                handler.handle(buf, context);
            }
        }
    }
}
