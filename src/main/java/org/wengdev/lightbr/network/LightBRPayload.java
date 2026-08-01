package org.wengdev.lightbr.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface LightBRPayload extends CustomPacketPayload {
    FriendlyByteBuf toPacketByteBuf();
}
