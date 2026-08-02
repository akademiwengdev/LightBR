package org.wengdev.lightbr.network.handler.settings;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import org.wengdev.lightbr.ServerControlManager;
import org.wengdev.lightbr.network.PayloadHandler;

public class SetChunkYHandler implements PayloadHandler {
    @Override
    public void handle(FriendlyByteBuf buf, ClientPlayNetworking.Context context) {
        int value = buf.readVarInt();
        ServerControlManager.queueServerOverride(patch -> patch.withChunkYRadius(value));
    }
}
