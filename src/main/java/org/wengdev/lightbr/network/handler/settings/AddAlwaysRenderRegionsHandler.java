package org.wengdev.lightbr.network.handler.settings;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import org.wengdev.lightbr.ServerControlManager;
import org.wengdev.lightbr.network.PayloadHandler;

import java.util.ArrayList;
import java.util.List;

public class AddAlwaysRenderRegionsHandler implements PayloadHandler {
    @Override
    public void handle(FriendlyByteBuf buf, ClientPlayNetworking.Context context) {
        int id = buf.readVarInt();
        int count = buf.readVarInt();
        List<Tuple<Vec3, Vec3>> regions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Vec3 a = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            Vec3 b = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            regions.add(new Tuple<>(a, b));
        }
        List<Tuple<Vec3, Vec3>> resolved = List.copyOf(regions);
        ServerControlManager.queueServerOverride(patch -> patch.withAddAlwaysRenderRegions(id, resolved));
    }
}
