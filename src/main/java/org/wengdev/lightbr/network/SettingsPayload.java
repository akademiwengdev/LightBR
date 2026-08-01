package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SettingsPayload(byte[] data) implements LightBRPayload {
    public static final Type<SettingsPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("lightbr", "settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SettingsPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull SettingsPayload decode(RegistryFriendlyByteBuf buf) {
            return fromBuf(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SettingsPayload payload) {
            buf.writeBytes(payload.data());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static SettingsPayload fromBuf(FriendlyByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new SettingsPayload(data);
    }

    public FriendlyByteBuf toPacketByteBuf() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBytes(data);
        return buf;
    }
}
