package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GreenTPACommand implements CommandExecutor {

    private final GreenTPA plugin;

    public GreenTPACommand(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("greentpa.admin")) {
            plugin.getChatUtil().sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§aGreenTPA v" + plugin.getDescription().getVersion());
            sender.sendMessage("§7Usage: /gtp [debug/reload]");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.reloadMessagesConfig();
            plugin.reloadCommandsConfig();
            plugin.getPriceManager().load();
            plugin.getEconomyManager().init();
            plugin.getChatUtil().sendMessage(sender, "reload-success");
            return true;
        }

        if (args[0].equalsIgnoreCase("debug")) {
            sender.sendMessage("§a--- GreenTPA Debug ---");
            sender.sendMessage("§7Economy Enabled: " + (plugin.getEconomyManager().isEnabled() ? "§2Yes" : "§cNo"));
            sender.sendMessage("§7Economy Provider: §f" + plugin.getEconomyManager().getProviderName());
            sender.sendMessage("§7Storage Backend: §f" + plugin.getConfig().getString("storage.type"));
            sender.sendMessage("§7Paper Version: §f" + plugin.getServer().getMinecraftVersion());
            sender.sendMessage("§7API Version: §f" + plugin.getDescription().getAPIVersion());
            sender.sendMessage("§a--------------------");
            return true;
        }

        return true;
    }
}
