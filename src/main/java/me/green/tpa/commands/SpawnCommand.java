package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Spawn;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    private final GreenTPA plugin;

    public SpawnCommand(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getChatUtil().sendMessage(sender, "player-only");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "spawn" -> handleSpawn(player, args);
            case "setspawn" -> handleSetSpawn(player);
            case "delspawn" -> handleDeleteSpawn(player, args);
        }

        return true;
    }

    private void handleSpawn(Player player, String[] args) {
        World world = player.getWorld();
        if (args.length > 1 && args[0].equalsIgnoreCase("world")) {
            world = Bukkit.getWorld(args[1]);
        }

        if (world == null) {
            plugin.getChatUtil().sendMessage(player, "world-not-found");
            return;
        }

        Spawn spawn = plugin.getSpawnManager().getSpawn(world.getName());
        if (spawn == null) {
            plugin.getChatUtil().sendMessage(player, "spawn-not-found");
            return;
        }

        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "spawn") && !player.hasPermission("greentpa.admin.nocooldown")) {
            plugin.getChatUtil().sendMessage(player, "cooldown-active", "%time%", String.valueOf(plugin.getCooldownManager().getRemainingTime(player.getUniqueId(), "spawn")));
            return;
        }

        double price = plugin.getPriceManager().getPrice("spawn", world.getName());
        if (!player.hasPermission("greentpa.free") && !plugin.getEconomyManager().has(player.getUniqueId(), price)) {
            plugin.getChatUtil().sendMessage(player, "economy-no-money", "%price%", plugin.getEconomyManager().format(price));
            return;
        }

        if (plugin.getEconomyManager().withdraw(player.getUniqueId(), price)) {
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), "spawn", plugin.getConfig().getInt("spawn.cooldown", 0));
            plugin.getTeleportManager().teleport(player, spawn.getLocation(), false, "spawn");
        } else {
            plugin.getChatUtil().sendMessage(player, "economy-error");
        }
    }

    private void handleSetSpawn(Player player) {
        if (!player.hasPermission("greentpa.admin.setspawn")) {
            plugin.getChatUtil().sendMessage(player, "no-permission");
            return;
        }
        plugin.getSpawnManager().setSpawn(player.getWorld().getName(), player.getLocation());
        plugin.getChatUtil().sendMessage(player, "spawn-set", "%world%", player.getWorld().getName());
    }

    private void handleDeleteSpawn(Player player, String[] args) {
        if (!player.hasPermission("greentpa.admin.delspawn")) {
            plugin.getChatUtil().sendMessage(player, "no-permission");
            return;
        }
        String worldName = args.length > 0 ? args[0] : player.getWorld().getName();
        plugin.getSpawnManager().deleteSpawn(worldName);
        plugin.getChatUtil().sendMessage(player, "spawn-deleted", "%world%", worldName);
    }
}
