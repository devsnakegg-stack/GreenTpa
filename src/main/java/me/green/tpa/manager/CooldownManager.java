package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final GreenTPA plugin;
    private final Map<UUID, Map<String, Long>> systemCooldowns = new ConcurrentHashMap<>();

    public CooldownManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void setCooldown(UUID uuid, String system) {
        int seconds = getCooldownTime(system);
        if (seconds <= 0) return;
        systemCooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .put(system.toLowerCase(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean hasCooldown(UUID uuid, String system) {
        Map<String, Long> cooldowns = systemCooldowns.get(uuid);
        if (cooldowns == null) return false;

        Long expire = cooldowns.get(system.toLowerCase());
        if (expire == null) return false;

        if (expire < System.currentTimeMillis()) {
            cooldowns.remove(system.toLowerCase());
            return false;
        }
        return true;
    }

    public long getRemainingTime(UUID uuid, String system) {
        Map<String, Long> cooldowns = systemCooldowns.get(uuid);
        if (cooldowns == null) return 0;

        Long expire = cooldowns.get(system.toLowerCase());
        if (expire == null) return 0;

        long remaining = (expire - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    private int getCooldownTime(String system) {
        String path = "cooldown.per-command." + system.toLowerCase();
        String val = plugin.getConfig().getString(path, "default");
        if (val.equalsIgnoreCase("default")) {
            return plugin.getConfig().getInt("cooldown.default", 5);
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return plugin.getConfig().getInt("cooldown.default", 5);
        }
    }
}
