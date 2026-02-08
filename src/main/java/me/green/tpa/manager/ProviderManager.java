package me.green.tpa.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ProviderManager {

    public String detectProvider() {
        if (isPluginEnabled("Essentials")) return "EssentialsX";
        if (isPluginEnabled("CMI")) return "CMI";
        if (isPluginEnabled("TheNewEconomy")) return "TheNewEconomy";
        return "Unknown";
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
