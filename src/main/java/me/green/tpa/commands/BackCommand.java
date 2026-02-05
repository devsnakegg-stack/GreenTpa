package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BackCommand implements CommandExecutor {

    private final GreenTPA plugin;

    public BackCommand(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getChatUtil().sendMessage(sender, "player-only");
            return true;
        }

        Location backLoc = plugin.getTeleportManager().getBackLocation(player.getUniqueId());
        if (backLoc == null) {
            plugin.getChatUtil().sendMessage(player, "back-no-location");
            return true;
        }

        plugin.getTeleportManager().teleport(player, backLoc, false);
        return true;
    }
}
