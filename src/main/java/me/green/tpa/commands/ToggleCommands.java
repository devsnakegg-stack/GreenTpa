package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleCommands implements CommandExecutor {

    private final GreenTPA plugin;

    public ToggleCommands(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getChatUtil().sendMessage(sender, "player-only");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "tptoggle" -> {
                plugin.getToggleManager().toggleTpa(player.getUniqueId());
                plugin.getChatUtil().sendMessage(player, plugin.getToggleManager().isTpaDisabled(player.getUniqueId()) ? "tptoggle-off" : "tptoggle-on");
            }
            case "tpblock" -> {
                if (args.length < 1) {
                    plugin.getChatUtil().sendMessage(player, "usage-tpblock");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
                    return true;
                }
                plugin.getToggleManager().blockPlayer(player.getUniqueId(), target.getUniqueId());
                plugin.getChatUtil().sendMessage(player, "tpblock-success", "%player%", target.getName());
            }
            case "tpunblock" -> {
                if (args.length < 1) {
                    plugin.getChatUtil().sendMessage(player, "usage-tpunblock");
                    return true;
                }
                // We might want to allow unblocking offline players too if we had UUIDs,
                // but for simplicity we'll stick to online for now or just name matching.
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
                    return true;
                }
                plugin.getToggleManager().unblockPlayer(player.getUniqueId(), target.getUniqueId());
                plugin.getChatUtil().sendMessage(player, "tpunblock-success", "%player%", target.getName());
            }
            case "tpaignore" -> {
                if (args.length < 1) {
                    plugin.getChatUtil().sendMessage(player, "usage-tpaignore");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
                    return true;
                }
                plugin.getToggleManager().ignorePlayer(player.getUniqueId(), target.getUniqueId());
                plugin.getChatUtil().sendMessage(player, "tpaignore-on", "%player%", target.getName());
            }
            case "tpaignoreall" -> {
                plugin.getToggleManager().toggleIgnoreAll(player.getUniqueId());
                plugin.getChatUtil().sendMessage(player, plugin.getToggleManager().isIgnoringAll(player.getUniqueId()) ? "tpaignoreall-on" : "tpaignoreall-off");
            }
            case "tpaauto" -> {
                plugin.getToggleManager().toggleAutoAccept(player.getUniqueId());
                plugin.getChatUtil().sendMessage(player, plugin.getToggleManager().isAutoAccept(player.getUniqueId()) ? "tpaauto-on" : "tpaauto-off");
            }
        }

        return true;
    }
}
