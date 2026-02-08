package me.green.tpa.manager;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private int cooldownTime; // in seconds

    public CooldownManager(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public void setCooldownTime(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis() + (cooldownTime * 1000L));
    }

    public boolean hasCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) return false;
        if (cooldowns.get(uuid) < System.currentTimeMillis()) {
            cooldowns.remove(uuid);
            return false;
        }
        return true;
    }

    public long getRemainingTime(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) return 0;
        return (cooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
    }
}
