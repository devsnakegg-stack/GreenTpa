package me.green.tpa.listeners;

import me.green.tpa.GreenTPA;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WarmupListener implements Listener {

    private final GreenTPA plugin;

    public WarmupListener(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getTeleportManager().isInWarmup(player.getUniqueId())) return;

        if (plugin.getConfig().getBoolean("warmup.cancel-on-damage", true)) {
            plugin.getTeleportManager().cancelWarmup(player.getUniqueId());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getTeleportManager().isInWarmup(player.getUniqueId())) return;

        if (plugin.getConfig().getBoolean("warmup.cancel-on-teleport", true)) {
            // Check if the teleport was caused by our plugin to avoid infinite loop
            // But usually we cancel the task before teleporting, so this should be fine.
            plugin.getTeleportManager().cancelWarmup(player.getUniqueId());
        }
    }
}
