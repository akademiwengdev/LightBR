package org.wengdev.lightbr.server;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LightBRServerPlugin extends JavaPlugin implements Listener {
    private static final String CHANNEL = "lightbr:settings";
    private static final double DEFAULT_REGION_RADIUS = 16.0;
    private static final double DEFAULT_REGION_HEIGHT = 16.0;

    private RenderContextData currentContext;

    @Override
    public void onEnable() {
        currentContext = RenderContextData.demoDefault();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendAckWithContext(event.getPlayer(), currentContext);
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
            sender.sendMessage("Usage: /lightbrsettings <ack|setcontext|resetcache|addregion> ...");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "ack" -> {
                Player target = resolveTargetPlayer(sender, args);
                if (target == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                sendAckWithContext(target, currentContext);
                sender.sendMessage("Sent ACK + context to " + target.getName());
            }
            case "setcontext" -> {
                Player target = resolveTargetPlayer(sender, args);
                if (target == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                RenderContextData updated = buildContextFromArgs(args);
                currentContext = updated;
                sendSetContext(target, updated);
                sender.sendMessage("Sent SET_CONTEXT to " + target.getName());
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
            case "addregion" -> {
                RenderContextData.Region region = resolveRegionFromArgs(sender, args);
                if (region == null) {
                    sender.sendMessage("Usage: /lightbrsettings addregion [player] [radius] [height] | /lightbrsettings addregion <ax> <ay> <az> <bx> <by> <bz>");
                    return true;
                }
                currentContext = withAddedRegion(currentContext, region);
                sender.sendMessage("Added always-render region to context.");
            }
            default -> sender.sendMessage("Unknown subcommand. Use ack, setcontext, resetcache, or addregion.");
        }

        return true;
    }

    private RenderContextData buildContextFromArgs(String[] args) {
        RenderContextData base = currentContext != null ? currentContext : RenderContextData.demoDefault();
        boolean enabled = base.enabled;
        int chunkXZ = base.chunkXZRadius;
        int chunkY = base.chunkYRadius;

        if (args.length >= 3) {
            enabled = Boolean.parseBoolean(args[2]);
        }
        if (args.length >= 4) {
            chunkXZ = parseIntOrDefault(args[3], chunkXZ);
        }
        if (args.length >= 5) {
            chunkY = parseIntOrDefault(args[4], chunkY);
        }

        return new RenderContextData(
                enabled,
                chunkXZ,
                chunkY,
                base.renderAllWater,
                base.renderAllLava,
                base.unrenderBlockEntities,
                base.alwaysRenderBlockEntities,
                base.alwaysRenderRegions
        );
    }

    private int parseIntOrDefault(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void sendAckWithContext(Player player, RenderContextData context) {
        byte[] payload = LightBRSettingsCodec.encodeContextPacket(LightBRSettingsCodec.PACKET_ACK, context);
        player.sendPluginMessage(this, CHANNEL, payload);
    }

    private void sendSetContext(Player player, RenderContextData context) {
        byte[] payload = LightBRSettingsCodec.encodeContextPacket(LightBRSettingsCodec.PACKET_SET_CONTEXT, context);
        player.sendPluginMessage(this, CHANNEL, payload);
    }

    private void sendResetCache(Player player) {
        byte[] payload = LightBRSettingsCodec.encodeResetCachePacket();
        player.sendPluginMessage(this, CHANNEL, payload);
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

    private RenderContextData withAddedRegion(RenderContextData base, RenderContextData.Region region) {
        RenderContextData resolved = base != null ? base : RenderContextData.demoDefault();
        List<String> blockEntities = new ArrayList<>(resolved.alwaysRenderBlockEntities);
        List<RenderContextData.Region> regions = new ArrayList<>(resolved.alwaysRenderRegions);
        regions.add(region);
        return new RenderContextData(
                resolved.enabled,
                resolved.chunkXZRadius,
                resolved.chunkYRadius,
                resolved.renderAllWater,
                resolved.renderAllLava,
                resolved.unrenderBlockEntities,
                blockEntities,
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
}
