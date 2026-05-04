package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SettingsPayload(byte[] data) implements CustomPayload {
    public static final Id<SettingsPayload> ID = new Id<>(Identifier.of("lightbr", "settings"));
    public static final PacketCodec<RegistryByteBuf, SettingsPayload> CODEC = new PacketCodec<>() {
        @Override
        public SettingsPayload decode(RegistryByteBuf buf) {
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            return new SettingsPayload(data);
        }

        @Override
        public void encode(RegistryByteBuf buf, SettingsPayload payload) {
            buf.writeBytes(payload.data());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static SettingsPayload fromBuf(PacketByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new SettingsPayload(data);
    }

    public PacketByteBuf toPacketByteBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBytes(data);
        return buf;
    }
}
