package me.green.tpa.listeners;

import me.green.tpa.GreenTPA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MoveListener implements Listener {

    private final GreenTPA plugin;

    public MoveListener(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getConfig().getBoolean("settings.cancel-on-move")) return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (plugin.getTeleportManager().isInWarmup(event.getPlayer().getUniqueId())) {
            plugin.getTeleportManager().cancelWarmup(event.getPlayer().getUniqueId());
        }
    }
}
