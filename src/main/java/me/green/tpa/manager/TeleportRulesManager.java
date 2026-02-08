package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TeleportRulesManager {

    private final GreenTPA plugin;

    public TeleportRulesManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public boolean canTeleport(Player player, Location from, Location to, String system) {
        if (player.hasPermission("greentpa.admin.bypass.rules") && plugin.getConfig().getBoolean("admin.bypass.restrictions", true)) {
            return true;
        }

        // Check distance limit
        if (plugin.getConfig().getBoolean("teleport-rules.distance-limit.enabled", false)) {
            if (from.getWorld().equals(to.getWorld())) {
                double distance = from.distance(to);
                double maxDistance = getDistanceLimit(system);
                if (distance > maxDistance) {
                    plugin.getChatUtil().sendMessage(player, "teleport-fail-distance", "%distance%", String.valueOf((int)maxDistance));
                    return false;
                }
            }
        }

        // Check world rules
        if (plugin.getConfig().getBoolean("teleport-rules.world-rules.enabled", false)) {
            if (!canLeaveWorld(from.getWorld().getName(), system)) {
                plugin.getChatUtil().sendMessage(player, "teleport-fail-world-out", "%world%", from.getWorld().getName());
                return false;
            }
            if (!canEnterWorld(to.getWorld().getName(), system)) {
                plugin.getChatUtil().sendMessage(player, "teleport-fail-world-in", "%world%", to.getWorld().getName());
                return false;
            }
            if (from.getWorld().equals(to.getWorld()) && !canTeleportWithinWorld(from.getWorld().getName(), system)) {
                plugin.getChatUtil().sendMessage(player, "teleport-fail-world-within", "%world%", from.getWorld().getName());
                return false;
            }
        }

        return true;
    }

    private double getDistanceLimit(String system) {
        String path = "teleport-rules.distance-limit.per-command." + system.toLowerCase();
        String val = plugin.getConfig().getString(path, "default");
        if (val.equalsIgnoreCase("default")) {
            return plugin.getConfig().getDouble("teleport-rules.distance-limit.max-distance", 10000);
        }
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return plugin.getConfig().getDouble("teleport-rules.distance-limit.max-distance", 10000);
        }
    }

    private boolean canLeaveWorld(String worldName, String system) {
        if (plugin.getConfig().getBoolean("teleport-rules.command-rules." + system + ".override-world-rules", false)) {
            return true;
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("teleport-rules.world-rules." + worldName);
        if (section == null) return true;
        return section.getBoolean("allow-out", true);
    }

    private boolean canEnterWorld(String worldName, String system) {
        if (plugin.getConfig().getBoolean("teleport-rules.command-rules." + system + ".override-world-rules", false)) {
            return true;
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("teleport-rules.world-rules." + worldName);
        if (section == null) return true;
        return section.getBoolean("allow-in", true);
    }

    private boolean canTeleportWithinWorld(String worldName, String system) {
        if (plugin.getConfig().getBoolean("teleport-rules.command-rules." + system + ".override-world-rules", false)) {
            return true;
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("teleport-rules.world-rules." + worldName);
        if (section == null) return true;
        return section.getBoolean("allow-within", true);
    }
}
