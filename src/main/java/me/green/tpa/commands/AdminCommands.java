package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminCommands implements CommandExecutor {

    private final GreenTPA plugin;

    public AdminCommands(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("tpareload")) {
            if (!sender.hasPermission("greentpa.admin.reload")) {
                plugin.getChatUtil().sendMessage(sender, "no-permission");
                return true;
            }
            plugin.reloadConfig();
            plugin.reloadMessagesConfig();
            plugin.reloadCommandsConfig();
            plugin.getPriceManager().load();
            plugin.getCooldownManager().setCooldownTime(plugin.getConfig().getInt("settings.cooldown-time", 30));
            plugin.getToggleManager().setDefaultAutoAccept(plugin.getConfig().getBoolean("settings.auto-accept-default", false));
            plugin.getChatUtil().sendMessage(sender, "reload-success");
            return true;
        }

        if (!(sender instanceof Player player)) {
            plugin.getChatUtil().sendMessage(sender, "player-only");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "tpahereall" -> {
                if (!player.hasPermission("greentpa.admin.tpahereall")) {
                    plugin.getChatUtil().sendMessage(player, "no-permission");
                    return true;
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(player)) continue;
                    plugin.getRequestManager().addRequest(player.getUniqueId(), p.getUniqueId(), me.green.tpa.manager.RequestManager.RequestType.TPAHERE, 0.0);
                    plugin.getChatUtil().sendMessage(p, "tpahere-received", "%player%", player.getName());
                }
                plugin.getChatUtil().sendMessage(player, "tpahereall-sent");
            }
            case "tpo" -> {
                if (!player.hasPermission("greentpa.admin.tpo")) {
                    plugin.getChatUtil().sendMessage(player, "no-permission");
                    return true;
                }
                if (args.length < 1) {
                    plugin.getChatUtil().sendMessage(player, "usage-tpo");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
                    return true;
                }
                plugin.getTeleportManager().teleport(player, target.getLocation(), true);
            }
            case "tpohere" -> {
                if (!player.hasPermission("greentpa.admin.tpohere")) {
                    plugin.getChatUtil().sendMessage(player, "no-permission");
                    return true;
                }
                if (args.length < 1) {
                    plugin.getChatUtil().sendMessage(player, "usage-tpohere");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
                    return true;
                }
                plugin.getTeleportManager().teleport(target, player.getLocation(), true);
            }
        }

        return true;
    }
}
