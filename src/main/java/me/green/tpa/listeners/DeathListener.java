package me.green.tpa.listeners;

import me.green.tpa.GreenTPA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final GreenTPA plugin;

    public DeathListener(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        plugin.getTeleportManager().setBackLocation(event.getEntity(), event.getEntity().getLocation());
    }
}
