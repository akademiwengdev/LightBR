package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ConfigPayload(byte[] data) implements CustomPacketPayload {
    public static final Type<ConfigPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("lightbr", "config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ConfigPayload decode(RegistryFriendlyByteBuf buf) {
            return fromBuf(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ConfigPayload payload) {
            buf.writeBytes(payload.data());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static ConfigPayload fromBuf(FriendlyByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new ConfigPayload(data);
    }

    public FriendlyByteBuf toPacketByteBuf() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBytes(data);
        return buf;
    }
}

