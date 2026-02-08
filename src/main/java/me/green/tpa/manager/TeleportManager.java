package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final GreenTPA plugin;
    private final Map<UUID, BukkitTask> warmups = new HashMap<>();
    private final Map<UUID, String> warmupSystems = new HashMap<>();
    private final Map<UUID, Location> backLocations = new HashMap<>();

    public TeleportManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void teleport(Player player, Location target, boolean force, String system) {
        setBackLocation(player, player.getLocation());

        // Cancel existing warmup if any
        if (warmups.containsKey(player.getUniqueId())) {
            warmups.get(player.getUniqueId()).cancel();
            warmups.remove(player.getUniqueId());
            warmupSystems.remove(player.getUniqueId());
        }

        int warmupTime = getWarmupTime(system);

        if (force || warmupTime <= 0 || (player.hasPermission("greentpa.bypass.warmup") && plugin.getConfig().getBoolean("admin.bypass.warmup", true))) {
            player.teleport(target);
            plugin.getChatUtil().sendMessage(player, "teleport-success");
            return;
        }

        warmupSystems.put(player.getUniqueId(), system.toLowerCase());

        String warmupType = plugin.getConfig().getString("warmup.type", "TITLE").toUpperCase();

        BukkitTask task = new BukkitRunnable() {
            int count = warmupTime;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    warmups.remove(player.getUniqueId());
                    warmupSystems.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                if (count <= 0) {
                    warmups.remove(player.getUniqueId());
                    warmupSystems.remove(player.getUniqueId());
                    player.teleport(target);
                    plugin.getChatUtil().sendMessage(player, "teleport-success");
                    this.cancel();
                    return;
                }

                // Handle visual/chat warmup
                if (warmupType.equals("TITLE")) {
                    String titleText = plugin.getMessagesConfig().getString("messages.warmup-title", "<green><bold>Teleporting in %time%...</bold></green>");
                    player.showTitle(Title.title(
                            plugin.getChatUtil().parse(titleText, "%time%", String.valueOf(count), "{seconds}", String.valueOf(count)),
                            Component.empty(),
                            Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(800), Duration.ofMillis(100))
                    ));
                } else {
                    String chatText = plugin.getMessagesConfig().getString("messages.warmup-chat", "<green>Teleporting in %time% seconds...</green>");
                    plugin.getChatUtil().sendRawMessage(player, plugin.getChatUtil().replacePlaceholders(chatText, "%time%", String.valueOf(count), "{seconds}", String.valueOf(count)));
                }

                // Play sound
                player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);

                count--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        warmups.put(player.getUniqueId(), task);
    }

    private int getWarmupTime(String system) {
        String path = "warmup.per-command." + system.toLowerCase();
        String val = plugin.getConfig().getString(path, "default");
        if (val.equalsIgnoreCase("default")) {
            return plugin.getConfig().getInt("warmup.default", 3);
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return plugin.getConfig().getInt("warmup.default", 3);
        }
    }

    public void cancelWarmup(UUID uuid) {
        BukkitTask task = warmups.remove(uuid);
        warmupSystems.remove(uuid);
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

    public String getWarmupSystem(UUID uuid) {
        return warmupSystems.get(uuid);
    }

    public void setBackLocation(Player player, Location location) {
        backLocations.put(player.getUniqueId(), location);
    }

    public Location getBackLocation(UUID uuid) {
        return backLocations.get(uuid);
    }
}
