package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GTPSecurityManager {

    private final GreenTPA plugin;
    private final Map<UUID, Integer> requestsPerMinute = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastReset = new ConcurrentHashMap<>();

    public GTPSecurityManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public boolean canRequest(Player player) {
        if (!plugin.getConfig().getBoolean("security.anti-spam", true)) return true;
        if (player.hasPermission("greentpa.admin.bypass.security")) return true;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (now - lastReset.getOrDefault(uuid, 0L) > 60000) {
            requestsPerMinute.put(uuid, 0);
            lastReset.put(uuid, now);
        }

        int count = requestsPerMinute.getOrDefault(uuid, 0);
        int max = plugin.getConfig().getInt("security.max-requests-per-minute", 5);

        if (count >= max) {
            plugin.getChatUtil().sendMessage(player, "security-spam");
            return false;
        }

        requestsPerMinute.put(uuid, count + 1);
        return true;
    }
}
