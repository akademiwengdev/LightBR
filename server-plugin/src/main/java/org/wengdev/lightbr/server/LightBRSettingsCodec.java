package org.wengdev.lightbr.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class LightBRSettingsCodec {
    public static final int CONFIG_PACKET_ACK = 0;

    public static final int PACKET_SET_ENABLED = 1;
    public static final int PACKET_SET_RENDER_ALL_WATER = 2;
    public static final int PACKET_SET_CHUNK_XZ = 3;
    public static final int PACKET_SET_CHUNK_Y = 4;
    public static final int PACKET_SET_RENDER_ALL_LAVA = 5;
    public static final int PACKET_SET_ALWAYS_RENDER_REGIONS = 6;
    public static final int PACKET_RESET_CACHE = 7;
    public static final int PACKET_RESET_SETTINGS = 8;

    private LightBRSettingsCodec() {
    }

    public static byte[] encodeBooleanPacket(int packetType, boolean value) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeVarInt(out, packetType);
            out.writeBoolean(value);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode LightBR settings packet", e);
        }
        return bytes.toByteArray();
    }

    public static byte[] encodeVarIntPacket(int packetType, int value) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeVarInt(out, packetType);
            writeVarInt(out, value);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode LightBR settings packet", e);
        }
        return bytes.toByteArray();
    }

    public static byte[] encodeRegionListPacket(List<RenderContextData.Region> regions) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeVarInt(out, PACKET_SET_ALWAYS_RENDER_REGIONS);
            writeVarInt(out, regions.size());
            for (RenderContextData.Region region : regions) {
                out.writeDouble(region.ax());
                out.writeDouble(region.ay());
                out.writeDouble(region.az());
                out.writeDouble(region.bx());
                out.writeDouble(region.by());
                out.writeDouble(region.bz());
            }
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

    public static byte[] encodeConfigAckPacket() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeVarInt(out, CONFIG_PACKET_ACK);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode LightBR config packet", e);
        }
        return bytes.toByteArray();
    }

    public static byte[] encodeResetSettingsPacket() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeVarInt(out, PACKET_RESET_SETTINGS);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode LightBR reset settings packet", e);
        }
        return bytes.toByteArray();
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
