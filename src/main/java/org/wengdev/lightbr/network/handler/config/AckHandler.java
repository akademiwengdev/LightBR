package org.wengdev.lightbr.network.handler.config;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.wengdev.lightbr.LightBR;
import org.wengdev.lightbr.network.PayloadHandler;

public class AckHandler implements PayloadHandler {
    @Override
    public void handle(FriendlyByteBuf buf, ClientPlayNetworking.Context context) {
        LightBR.LOGGER.info("{}", Component.translatable("lightbr.log.acknowledge_received").getString());
        // TODO: maybe handle ack?
    }
}
