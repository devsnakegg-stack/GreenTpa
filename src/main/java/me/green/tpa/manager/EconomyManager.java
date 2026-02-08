package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class EconomyManager {

    private final GreenTPA plugin;
    private Economy econ = null;
    private boolean enabled = false;

    public EconomyManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void init() {
        if (plugin.getConfig().getBoolean("economy.enabled", false)) {
            if (setupEconomy()) {
                enabled = true;
                plugin.getLogger().info("Economy linked with Vault!");
            } else {
                plugin.getLogger().warning("Economy enabled in config but Vault or an economy provider was not found!");
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getBalance(UUID uuid) {
        if (!enabled) return 0;
        return econ.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    public boolean has(UUID uuid, double amount) {
        if (!enabled) return true;
        return econ.has(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (!enabled || amount <= 0) return true;
        EconomyResponse r = econ.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        return r.transactionSuccess();
    }

    public boolean deposit(UUID uuid, double amount) {
        if (!enabled || amount <= 0) return true;
        EconomyResponse r = econ.depositPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        return r.transactionSuccess();
    }

    public String format(double amount) {
        if (econ == null) return String.format("%.2f", amount);
        return econ.format(amount);
    }
}
