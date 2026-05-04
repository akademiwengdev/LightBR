package org.wengdev.lightbr.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class LightBRSettingsCodec {
    public static final int PACKET_ACK = 0;
    public static final int PACKET_SET_CONTEXT = 1;
    public static final int PACKET_RESET_CACHE = 2;

    private LightBRSettingsCodec() {
    }

    public static byte[] encodeContextPacket(int packetType, RenderContextData context) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeVarInt(out, packetType);
            writeContext(out, context);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode LightBR settings packet", e);
        }
        return bytes.toByteArray();
    }

    public static byte[] encodeResetCachePacket() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeVarInt(out, PACKET_RESET_CACHE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode LightBR reset packet", e);
        }
        return bytes.toByteArray();
    }

    private static void writeContext(DataOutputStream out, RenderContextData context) throws IOException {
        out.writeBoolean(context.enabled);
        writeVarInt(out, context.chunkXZRadius);
        writeVarInt(out, context.chunkYRadius);
        out.writeBoolean(context.renderAllWater);
        out.writeBoolean(context.renderAllLava);
        out.writeBoolean(context.unrenderBlockEntities);

        List<String> blockEntities = context.alwaysRenderBlockEntities;
        writeVarInt(out, blockEntities.size());
        for (String blockEntityId : blockEntities) {
            writeString(out, blockEntityId);
        }

        List<RenderContextData.Region> regions = context.alwaysRenderRegions;
        writeVarInt(out, regions.size());
        for (RenderContextData.Region region : regions) {
            out.writeDouble(region.ax());
            out.writeDouble(region.ay());
            out.writeDouble(region.az());
            out.writeDouble(region.bx());
            out.writeDouble(region.by());
            out.writeDouble(region.bz());
        }
    }

    private static void writeString(DataOutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        int i = value;
        while ((i & -128) != 0) {
            out.writeByte(i & 127 | 128);
            i >>>= 7;
        }
        out.writeByte(i);
    }
}
