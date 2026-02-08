package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeTabCompleter implements TabCompleter {

    private final GreenTPA plugin;

    public HomeTabCompleter(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        if (args.length != 1) return new ArrayList<>();

        Map<String, Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        List<String> completions = new ArrayList<>(homes.keySet());

        if (command.getName().equalsIgnoreCase("home")) {
            if (plugin.getConfig().getBoolean("home.bed-home", true)) {
                completions.add("bed");
            }
        }

        String input = args[0].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
    }
}
