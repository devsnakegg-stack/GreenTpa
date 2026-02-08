package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class HomeCommand implements CommandExecutor {

    private final GreenTPA plugin;

    public HomeCommand(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getChatUtil().sendMessage(sender, "player-only");
            return true;
        }

        if (!plugin.getConfig().getBoolean("features.home", true)) {
            plugin.getChatUtil().sendMessage(player, "no-permission");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "home" -> handleHome(player, args);
            case "homes" -> handleHomes(player);
            case "sethome" -> handleSetHome(player, args);
            case "delhome" -> handleDeleteHome(player, args);
        }

        return true;
    }

    private void handleHome(Player player, String[] args) {
        String name = args.length > 0 ? args[0] : "home";

        if (name.equalsIgnoreCase("bed") && plugin.getConfig().getBoolean("home.bed-home", true)) {
            if (player.getBedSpawnLocation() == null) {
                plugin.getChatUtil().sendMessage(player, "home-bed-not-found");
                return;
            }
            teleportToLocation(player, player.getBedSpawnLocation(), "home");
            return;
        }

        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), name);

        if (home == null) {
            plugin.getChatUtil().sendMessage(player, "home-not-found", "%name%", name);
            return;
        }

        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "home") && !player.hasPermission("greentpa.admin.nocooldown")) {
            plugin.getChatUtil().sendMessage(player, "cooldown-active", "%time%", String.valueOf(plugin.getCooldownManager().getRemainingTime(player.getUniqueId(), "home")));
            return;
        }

        if (!plugin.getTeleportRulesManager().canTeleport(player, player.getLocation(), home.getLocation(), "home")) {
            return;
        }

        teleportToLocation(player, home.getLocation(), "home");
    }

    private void teleportToLocation(Player player, org.bukkit.Location location, String system) {
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), system) && !player.hasPermission("greentpa.admin.nocooldown")) {
            plugin.getChatUtil().sendMessage(player, "cooldown-active", "%time%", String.valueOf(plugin.getCooldownManager().getRemainingTime(player.getUniqueId(), system)));
            return;
        }

        if (!plugin.getTeleportRulesManager().canTeleport(player, player.getLocation(), location, system)) {
            return;
        }

        double price = plugin.getPriceManager().getPrice(system, location.getWorld().getName());
        if (!player.hasPermission("greentpa.free") && !plugin.getEconomyManager().has(player.getUniqueId(), price)) {
            plugin.getChatUtil().sendMessage(player, "economy-no-money", "%price%", plugin.getEconomyManager().format(price));
            return;
        }

        if (plugin.getEconomyManager().withdraw(player.getUniqueId(), price)) {
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), system);
            plugin.getTeleportManager().teleport(player, location, false, system, price);
        } else {
            plugin.getChatUtil().sendMessage(player, "economy-error");
        }
    }

    private void handleHomes(Player player) {
        Map<String, Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        if (homes.isEmpty()) {
            plugin.getChatUtil().sendMessage(player, "home-none");
            return;
        }

        String list = homes.keySet().stream().collect(Collectors.joining(", "));
        plugin.getChatUtil().sendMessage(player, "home-list", "%homes%", list);
    }

    private void handleSetHome(Player player, String[] args) {
        String name = args.length > 0 ? args[0] : "home";
        int limit = getHomeLimit(player);

        if (plugin.getHomeManager().getHomeCount(player.getUniqueId()) >= limit && plugin.getHomeManager().getHome(player.getUniqueId(), name) == null) {
            plugin.getChatUtil().sendMessage(player, "home-limit-reached", "%limit%", String.valueOf(limit));
            return;
        }

        plugin.getHomeManager().setHome(player.getUniqueId(), name, player.getLocation());
        plugin.getChatUtil().sendMessage(player, "home-set", "%name%", name);
    }

    private void handleDeleteHome(Player player, String[] args) {
        String name = args.length > 0 ? args[0] : "home";
        if (plugin.getHomeManager().getHome(player.getUniqueId(), name) == null) {
            plugin.getChatUtil().sendMessage(player, "home-not-found", "%name%", name);
            return;
        }

        plugin.getHomeManager().deleteHome(player.getUniqueId(), name);
        plugin.getChatUtil().sendMessage(player, "home-deleted", "%name%", name);
    }

    private int getHomeLimit(Player player) {
        if (player.hasPermission("greentpa.homes.unlimited")) return Integer.MAX_VALUE;
        for (int i = 100; i >= 1; i--) {
            if (player.hasPermission("greentpa.homes." + i)) return i;
        }
        return plugin.getConfig().getInt("home.default-limit", 1);
    }
}
