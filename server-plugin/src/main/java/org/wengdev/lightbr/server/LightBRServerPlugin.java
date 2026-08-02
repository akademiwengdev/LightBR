package org.wengdev.lightbr.server;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LightBRServerPlugin extends JavaPlugin implements PluginMessageListener {
    private static final String SETTINGS_CHANNEL = "lightbr:settings";
    private static final String CONFIG_CHANNEL = "lightbr:config";
    private static final double DEFAULT_REGION_RADIUS = 16.0;
    private static final double DEFAULT_REGION_HEIGHT = 16.0;

    private RenderContextData currentContext;

    @Override
    public void onEnable() {
        currentContext = RenderContextData.demoDefault();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, SETTINGS_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, SETTINGS_CHANNEL, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CONFIG_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CONFIG_CHANNEL, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, SETTINGS_CHANNEL);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, SETTINGS_CHANNEL, this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, CONFIG_CHANNEL);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, CONFIG_CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (player == null || message == null || message.length == 0) {
            return;
        }

        this.getLogger().info("Received plugin message on channel " + channel + " from player " + player.getName());

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            int packetType = readVarInt(in);
            if (CONFIG_CHANNEL.equals(channel) && packetType == LightBRSettingsCodec.CONFIG_PACKET_ACK) {
                this.getLogger().info("Received config ACK from " + player.getName());
                readVarInt(in);
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    sendConfigAck(player);
                    sendContextUpdates(player, currentContext);
                }, 1L);
            }
        } catch (IOException e) {
            getLogger().warning("Failed to decode LightBR settings packet: " + e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("lightbrsettings")) {
            return false;
        }

        if (!sender.hasPermission("lightbrsettings.use")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /lightbrsettings <setcontext|resetcache|addregion|setregion|removeregion> ...");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "setcontext" -> {
                Player target = resolveTargetPlayer(sender, args);
                if (target == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                RenderContextData updated = buildContextFromArgs(args);
                currentContext = updated;
                sendContextUpdates(target, updated);
                sender.sendMessage("Sent context updates to " + target.getName());
            }
            case "resetcache" -> {
                Player target = resolveTargetPlayer(sender, args);
                if (target == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                sendResetCache(target);
                sender.sendMessage("Sent RESET_CACHE to " + target.getName());
            }
            case "setregion" -> {
                if (args.length < 3) {
                    sender.sendMessage("Usage: /lightbrsettings setregion <id> <ax> <ay> <az> <bx> <by> <bz> | /lightbrsettings setregion <id> [player] [radius] [height]");
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid region ID: " + args[1]);
                    return true;
                }
                String[] regionArgs = new String[args.length - 1];
                System.arraycopy(args, 1, regionArgs, 0, regionArgs.length);
                RenderContextData.Region region = resolveRegionFromArgs(sender, regionArgs);
                if (region == null) {
                    sender.sendMessage("Usage: /lightbrsettings setregion <id> <ax> <ay> <az> <bx> <by> <bz> | /lightbrsettings setregion <id> [player] [radius] [height]");
                    return true;
                }
                currentContext = withSetRegion(currentContext, id, region);
                sender.sendMessage("Set always-render region list " + id + " in context.");
            }
            case "addregion" -> {
                if (args.length < 3) {
                    sender.sendMessage("Usage: /lightbrsettings addregion <id> <ax> <ay> <az> <bx> <by> <bz> | /lightbrsettings addregion <id> [player] [radius] [height]");
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid region ID: " + args[1]);
                    return true;
                }
                String[] regionArgs = new String[args.length - 1];
                System.arraycopy(args, 1, regionArgs, 0, regionArgs.length);
                RenderContextData.Region region = resolveRegionFromArgs(sender, regionArgs);
                if (region == null) {
                    sender.sendMessage("Usage: /lightbrsettings addregion <id> <ax> <ay> <az> <bx> <by> <bz> | /lightbrsettings addregion <id> [player] [radius] [height]");
                    return true;
                }
                currentContext = withAddedRegion(currentContext, id, region);
                sender.sendMessage("Added always-render region to list " + id + " in context.");
            }
            case "removeregion" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /lightbrsettings removeregion <id>");
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid region ID: " + args[1]);
                    return true;
                }
                currentContext = withRemovedRegion(currentContext, id);
                sender.sendMessage("Removed always-render region list " + id + " from context.");
            }
            default -> sender.sendMessage("Unknown subcommand. Use setcontext, resetcache, setregion, addregion, or removeregion.");
        }

        return true;
    }

    private RenderContextData buildContextFromArgs(String[] args) {
        RenderContextData base = currentContext != null ? currentContext : RenderContextData.demoDefault();
        Boolean enabled = base.enabled;
        Integer chunkXZ = base.chunkXZRadius;
        Integer chunkY = base.chunkYRadius;

        if (args.length >= 3) {
            enabled = parseBooleanOrNull(args[2], enabled);
        }
        if (args.length >= 4) {
            chunkXZ = parseIntOrNull(args[3], chunkXZ);
        }
        if (args.length >= 5) {
            chunkY = parseIntOrNull(args[4], chunkY);
        }

        return new RenderContextData(
                enabled,
                chunkXZ,
                chunkY,
                base.renderAllWater,
                base.renderAllLava,
                base.alwaysRenderRegions
        );
    }

    private Integer parseIntOrNull(String value, Integer fallback) {
        if (value == null) {
            return fallback;
        }
        if (value.equalsIgnoreCase("blank") || value.equalsIgnoreCase("default")) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private Boolean parseBooleanOrNull(String value, Boolean fallback) {
        if (value == null) {
            return fallback;
        }
        if (value.equalsIgnoreCase("blank") || value.equalsIgnoreCase("default")) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    private void sendResetCache(Player player) {
        byte[] payload = LightBRSettingsCodec.encodeResetCachePacket();
        player.sendPluginMessage(this, SETTINGS_CHANNEL, payload);
    }

    private void sendResetSettings(Player player) {
        byte[] payload = LightBRSettingsCodec.encodeResetSettingsPacket();
        player.sendPluginMessage(this, SETTINGS_CHANNEL, payload);
    }

    private void sendConfigAck(Player player) {
        byte[] payload = LightBRSettingsCodec.encodeConfigAckPacket();
        player.sendPluginMessage(this, CONFIG_CHANNEL, payload);

        this.getLogger().info("Sent config ACK to " + player.getName());
    }

    private void sendContextUpdates(Player player, RenderContextData context) {
        if (context == null) {
            return;
        }
        List<byte[]> subPackets = new ArrayList<>();
        if (context.enabled != null) {
            subPackets.add(LightBRSettingsCodec.encodeBooleanPacket(LightBRSettingsCodec.PACKET_SET_ENABLED, context.enabled));
        }
        if (context.chunkXZRadius != null) {
            subPackets.add(LightBRSettingsCodec.encodeVarIntPacket(LightBRSettingsCodec.PACKET_SET_CHUNK_XZ, context.chunkXZRadius));
        }
        if (context.chunkYRadius != null) {
            subPackets.add(LightBRSettingsCodec.encodeVarIntPacket(LightBRSettingsCodec.PACKET_SET_CHUNK_Y, context.chunkYRadius));
        }
        if (context.renderAllWater != null) {
            subPackets.add(LightBRSettingsCodec.encodeBooleanPacket(LightBRSettingsCodec.PACKET_SET_RENDER_ALL_WATER, context.renderAllWater));
        }
        if (context.renderAllLava != null) {
            subPackets.add(LightBRSettingsCodec.encodeBooleanPacket(LightBRSettingsCodec.PACKET_SET_RENDER_ALL_LAVA, context.renderAllLava));
        }
        if (context.alwaysRenderRegions != null) {
            for (Map.Entry<Integer, List<RenderContextData.Region>> entry : context.alwaysRenderRegions.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    subPackets.add(LightBRSettingsCodec.encodeRegionListPacket(entry.getKey(), entry.getValue()));
                }
            }
        }
        if (!subPackets.isEmpty()) {
            byte[] payload = LightBRSettingsCodec.encodeBulkPacket(subPackets);
            player.sendPluginMessage(this, SETTINGS_CHANNEL, payload);
        }
    }

    private Player resolveTargetPlayer(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            return Bukkit.getPlayerExact(args[1]);
        }
        return sender instanceof Player ? (Player) sender : null;
    }

    private RenderContextData.Region resolveRegionFromArgs(CommandSender sender, String[] args) {
        if (args.length >= 7) {
            double ax = parseDoubleOrDefault(args[1], Double.NaN);
            double ay = parseDoubleOrDefault(args[2], Double.NaN);
            double az = parseDoubleOrDefault(args[3], Double.NaN);
            double bx = parseDoubleOrDefault(args[4], Double.NaN);
            double by = parseDoubleOrDefault(args[5], Double.NaN);
            double bz = parseDoubleOrDefault(args[6], Double.NaN);
            if (Double.isNaN(ax) || Double.isNaN(ay) || Double.isNaN(az) || Double.isNaN(bx) || Double.isNaN(by) || Double.isNaN(bz)) {
                return null;
            }
            return new RenderContextData.Region(ax, ay, az, bx, by, bz);
        }

        Player centerPlayer = null;
        int radiusIndex = -1;
        int heightIndex = -1;

        if (args.length >= 2) {
            Player named = Bukkit.getPlayerExact(args[1]);
            if (named != null) {
                centerPlayer = named;
                radiusIndex = 2;
                heightIndex = 3;
            } else if (sender instanceof Player) {
                centerPlayer = (Player) sender;
                radiusIndex = 1;
                heightIndex = 2;
            }
        } else if (sender instanceof Player) {
            centerPlayer = (Player) sender;
        }

        if (centerPlayer == null) {
            return null;
        }

        double radius = DEFAULT_REGION_RADIUS;
        double height = DEFAULT_REGION_HEIGHT;
        if (radiusIndex >= 0 && args.length > radiusIndex) {
            radius = parseDoubleOrDefault(args[radiusIndex], radius);
        }
        if (heightIndex >= 0 && args.length > heightIndex) {
            height = parseDoubleOrDefault(args[heightIndex], height);
        }

        Location location = centerPlayer.getLocation();
        double halfHeight = height / 2.0;
        double ax = location.getX() - radius;
        double ay = location.getY() - halfHeight;
        double az = location.getZ() - radius;
        double bx = location.getX() + radius;
        double by = location.getY() + halfHeight;
        double bz = location.getZ() + radius;
        return new RenderContextData.Region(ax, ay, az, bx, by, bz);
    }

    private RenderContextData withSetRegion(RenderContextData base, int id, RenderContextData.Region region) {
        RenderContextData resolved = base != null ? base : RenderContextData.demoDefault();
        Map<Integer, List<RenderContextData.Region>> regions = resolved.alwaysRenderRegions != null
                ? new HashMap<>(resolved.alwaysRenderRegions)
                : new HashMap<>();
        List<RenderContextData.Region> list = new ArrayList<>();
        list.add(region);
        regions.put(id, list);
        return new RenderContextData(
                resolved.enabled,
                resolved.chunkXZRadius,
                resolved.chunkYRadius,
                resolved.renderAllWater,
                resolved.renderAllLava,
                regions
        );
    }

    private RenderContextData withAddedRegion(RenderContextData base, int id, RenderContextData.Region region) {
        RenderContextData resolved = base != null ? base : RenderContextData.demoDefault();
        Map<Integer, List<RenderContextData.Region>> regions = resolved.alwaysRenderRegions != null
                ? new HashMap<>(resolved.alwaysRenderRegions)
                : new HashMap<>();
        List<RenderContextData.Region> list = regions.computeIfAbsent(id, k -> new ArrayList<>());
        list = new ArrayList<>(list);
        list.add(region);
        regions.put(id, list);
        return new RenderContextData(
                resolved.enabled,
                resolved.chunkXZRadius,
                resolved.chunkYRadius,
                resolved.renderAllWater,
                resolved.renderAllLava,
                regions
        );
    }

    private RenderContextData withRemovedRegion(RenderContextData base, int id) {
        RenderContextData resolved = base != null ? base : RenderContextData.demoDefault();
        Map<Integer, List<RenderContextData.Region>> regions = resolved.alwaysRenderRegions != null
                ? new HashMap<>(resolved.alwaysRenderRegions)
                : new HashMap<>();
        regions.remove(id);
        return new RenderContextData(
                resolved.enabled,
                resolved.chunkXZRadius,
                resolved.chunkYRadius,
                resolved.renderAllWater,
                resolved.renderAllLava,
                regions
        );
    }

    private double parseDoubleOrDefault(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private int readVarInt(DataInputStream in) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = in.readByte();
            int value = (read & 0x7F);
            result |= (value << (7 * numRead));
            numRead++;
            if (numRead > 5) {
                throw new IOException("VarInt too large");
            }
        } while ((read & 0x80) != 0);
        return result;
    }
}
