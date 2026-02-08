package me.green.tpa.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ProviderManager {

    public String detectProvider() {
        if (isPluginEnabled("Essentials")) return "EssentialsX";
        if (isPluginEnabled("CMI")) return "CMI";
        if (isPluginEnabled("TheNewEconomy")) return "TheNewEconomy";
        if (isPluginEnabled("Xconomy")) return "Xconomy";
        if (isPluginEnabled("UltraEconomy")) return "UltraEconomy";
        if (isPluginEnabled("RedisEconomy")) return "RedisEconomy";
        if (isPluginEnabled("CraftConomy3")) return "CraftConomy3";
        if (isPluginEnabled("RealEconomy")) return "RealEconomy";
        if (isPluginEnabled("Gringotts")) return "Gringotts";
        if (isPluginEnabled("AdvancedEconomy")) return "AdvancedEconomy";
        if (isPluginEnabled("QueenEconomy")) return "QueenEconomy";
        if (isPluginEnabled("Treasury")) return "Treasury";
        if (isPluginEnabled("EconomyShopGUI")) return "EconomyShopGUI (Bridge)";
        if (isPluginEnabled("Vault")) return "Vault (Generic)";
        return "Unknown";
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
