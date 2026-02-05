package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final GreenTPA plugin;
    private final Map<UUID, BukkitTask> warmups = new HashMap<>();
    private final Map<UUID, Location> backLocations = new HashMap<>();

    public TeleportManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void teleport(Player player, Location target, boolean force) {
        setBackLocation(player, player.getLocation());

        // Cancel existing warmup if any
        if (warmups.containsKey(player.getUniqueId())) {
            warmups.get(player.getUniqueId()).cancel();
            warmups.remove(player.getUniqueId());
        }

        if (force || plugin.getConfig().getInt("settings.warmup-time") <= 0) {
            player.teleport(target);
            plugin.getChatUtil().sendMessage(player, "teleport-success");
            return;
        }

        int warmupTime = plugin.getConfig().getInt("settings.warmup-time");
        plugin.getChatUtil().sendMessage(player, "warmup-start", "%time%", String.valueOf(warmupTime));

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            warmups.remove(player.getUniqueId());
            player.teleport(target);
            plugin.getChatUtil().sendMessage(player, "teleport-success");
        }, warmupTime * 20L);

        warmups.put(player.getUniqueId(), task);
    }

    public void cancelWarmup(UUID uuid) {
        BukkitTask task = warmups.remove(uuid);
        if (task != null) {
            task.cancel();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                plugin.getChatUtil().sendMessage(player, "warmup-cancelled");
            }
        }
    }

    public boolean isInWarmup(UUID uuid) {
        return warmups.containsKey(uuid);
    }

    public void setBackLocation(Player player, Location location) {
        backLocations.put(player.getUniqueId(), location);
    }

    public Location getBackLocation(UUID uuid) {
        return backLocations.get(uuid);
    }
}
