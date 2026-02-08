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

        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "back") && !player.hasPermission("greentpa.admin.nocooldown")) {
            plugin.getChatUtil().sendMessage(player, "cooldown-active", "%time%", String.valueOf(plugin.getCooldownManager().getRemainingTime(player.getUniqueId(), "back")));
            return true;
        }

        double price = plugin.getPriceManager().getPrice("back", backLoc.getWorld().getName());
        if (!player.hasPermission("greentpa.free") && !plugin.getEconomyManager().has(player.getUniqueId(), price)) {
            plugin.getChatUtil().sendMessage(player, "economy-no-money", "%price%", plugin.getEconomyManager().format(price));
            return true;
        }

        if (plugin.getEconomyManager().withdraw(player.getUniqueId(), price)) {
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), "back", plugin.getConfig().getInt("back.cooldown", 0));
            plugin.getTeleportManager().teleport(player, backLoc, false, "back");
        } else {
            plugin.getChatUtil().sendMessage(player, "economy-error");
        }
        return true;
    }
}
