package me.green.tpa.listeners;

import java.util.UUID;
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
        UUID uuid = event.getPlayer().getUniqueId();

        // Handle restrict-movement for pending requests
        if (plugin.getRequestManager().getIncomingRequests(uuid).size() > 0 ||
            plugin.getRequestManager().getLatestRequest(uuid) != null) {
            // This is a bit complex since we need to know if the player is a requester or receiver
            // and check config. For now, let's focus on the teleport warmup which is the most critical.
        }

        if (!plugin.getTeleportManager().isInWarmup(uuid)) return;

        String system = plugin.getTeleportManager().getWarmupSystem(event.getPlayer().getUniqueId());
        if (system == null) return;

        // Check movement
        boolean cancelOnMove = plugin.getConfig().getBoolean("warmup.cancel-on-move", true);
        if (cancelOnMove) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                plugin.getTeleportManager().cancelWarmup(event.getPlayer().getUniqueId());
                return;
            }
        }

        // Check rotation
        boolean cancelOnRotate = plugin.getConfig().getBoolean("warmup.cancel-on-rotate", true);
        if (cancelOnRotate) {
            if (event.getFrom().getYaw() != event.getTo().getYaw() ||
                event.getFrom().getPitch() != event.getTo().getPitch()) {
                plugin.getTeleportManager().cancelWarmup(event.getPlayer().getUniqueId());
            }
        }
    }
}
