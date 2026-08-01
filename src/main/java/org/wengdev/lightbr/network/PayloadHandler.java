package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

public interface PayloadHandler {
    void handle(FriendlyByteBuf buf, ClientPlayNetworking.Context context);
}
