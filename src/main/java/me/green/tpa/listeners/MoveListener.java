package me.green.tpa.listeners;

import me.green.tpa.GreenTPA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private final GreenTPA plugin;

    public MoveListener(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getTeleportManager().isInWarmup(event.getPlayer().getUniqueId())) return;

        String system = plugin.getTeleportManager().getWarmupSystem(event.getPlayer().getUniqueId());
        if (system == null) return;

        if (!plugin.getConfig().getBoolean(system + ".cancel-on-move", true)) return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        plugin.getTeleportManager().cancelWarmup(event.getPlayer().getUniqueId());
    }
}
