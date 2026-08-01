package org.wengdev.lightbr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import org.wengdev.lightbr.network.handler.config.AckHandler;

import java.util.Map;

public class ConfigPayloadHandlers {
    public static final int ACK = 0;

    private final Map<Integer, PayloadHandler> handlers = Map.of(
        ACK, new AckHandler()
    );

    public void registerChannels() {
        PayloadTypeRegistry.playC2S().register(ConfigPayload.ID, ConfigPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.CODEC);
    }

    public void registerHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            FriendlyByteBuf dataBuf = payload.toPacketByteBuf();
            int packetType = dataBuf.readVarInt();

            PayloadHandler handler = handlers.get(packetType);
            if (handler != null) {
                handler.handle(dataBuf, context);
            }
        });
    }

    public void sendAcknowledgement(int protocolVersion) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(ACK);
        buf.writeVarInt(protocolVersion);
        ClientPlayNetworking.send(ConfigPayload.fromBuf(buf));
    }
}
