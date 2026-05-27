package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfigPayload(byte[] data) implements CustomPayload {
    public static final Id<ConfigPayload> ID = new Id<>(Identifier.of("lightbr", "config"));
    public static final PacketCodec<RegistryByteBuf, ConfigPayload> CODEC = new PacketCodec<>() {
        @Override
        public ConfigPayload decode(RegistryByteBuf buf) {
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            return new ConfigPayload(data);
        }

        @Override
        public void encode(RegistryByteBuf buf, ConfigPayload payload) {
            buf.writeBytes(payload.data());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static ConfigPayload fromBuf(PacketByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new ConfigPayload(data);
    }

    public PacketByteBuf toPacketByteBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBytes(data);
        return buf;
    }
}

