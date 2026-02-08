package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RTPCommand implements CommandExecutor {

    private final GreenTPA plugin;

    public RTPCommand(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getChatUtil().sendMessage(sender, "player-only");
            return true;
        }

        World world = player.getWorld();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("world") && args.length > 1) {
                world = Bukkit.getWorld(args[1]);
            } else if (args[0].equalsIgnoreCase("nether")) {
                world = Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NETHER).findFirst().orElse(null);
            } else if (args[0].equalsIgnoreCase("end")) {
                world = Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.THE_END).findFirst().orElse(null);
            }
        }

        if (world == null) {
            plugin.getChatUtil().sendMessage(player, "world-not-found");
            return true;
        }

        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "rtp") && !player.hasPermission("greentpa.admin.nocooldown")) {
            plugin.getChatUtil().sendMessage(player, "cooldown-active", "%time%", String.valueOf(plugin.getCooldownManager().getRemainingTime(player.getUniqueId(), "rtp")));
            return true;
        }

        double price = plugin.getPriceManager().getPrice("rtp", world.getName());
        if (!player.hasPermission("greentpa.free") && !plugin.getEconomyManager().has(player.getUniqueId(), price)) {
            plugin.getChatUtil().sendMessage(player, "economy-no-money", "%price%", plugin.getEconomyManager().format(price));
            return true;
        }

        plugin.getChatUtil().sendMessage(player, "rtp-searching");
        plugin.getRtpManager().findSafeLocation(world).thenAccept(location -> {
            if (location == null) {
                plugin.getChatUtil().sendMessage(player, "rtp-no-safe-location");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (plugin.getEconomyManager().withdraw(player.getUniqueId(), price)) {
                    plugin.getCooldownManager().setCooldown(player.getUniqueId(), "rtp", plugin.getConfig().getInt("rtp.cooldown", 0));
                    plugin.getTeleportManager().teleport(player, location, false, "rtp");
                } else {
                    plugin.getChatUtil().sendMessage(player, "economy-error");
                }
            });
        });

        return true;
    }
}
