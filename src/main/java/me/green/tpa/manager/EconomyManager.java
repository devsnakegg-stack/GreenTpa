package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collection;
import java.util.UUID;

public class EconomyManager implements Listener {

    private final GreenTPA plugin;
    private Economy econ = null;
    private boolean enabled = false;
    private String providerName = "None";

    public EconomyManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void init() {
        if (!plugin.getConfig().getBoolean("economy.enabled", true)) {
            plugin.getLogger().info("Economy is disabled in config.");
            return;
        }

        if (setupEconomy()) {
            enabled = true;
            providerName = econ.getName();
            plugin.getLogger().info("Economy linked! Provider: " + providerName);
        } else {
            plugin.getLogger().warning("No economy provider found yet. Waiting for one to register...");
            // We are already registered as a listener in GreenTPA.java (if we add it)
        }
    }

    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault not found! Economy features will not work unless another supported API is added.");
            return false;
        }

        String mode = plugin.getConfig().getString("economy.mode", "auto");
        String manualProvider = plugin.getConfig().getString("economy.manual-provider", "");

        if (mode.equalsIgnoreCase("manual") && !manualProvider.isEmpty()) {
            Collection<RegisteredServiceProvider<Economy>> rsps = Bukkit.getServer().getServicesManager().getRegistrations(Economy.class);
            for (RegisteredServiceProvider<Economy> rsp : rsps) {
                if (rsp.getProvider().getName().equalsIgnoreCase(manualProvider) ||
                    rsp.getPlugin().getName().equalsIgnoreCase(manualProvider)) {
                    econ = rsp.getProvider();
                    return true;
                }
            }
            plugin.getLogger().warning("Manual economy provider '" + manualProvider + "' not found! Falling back to auto-detection.");
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
            return econ != null;
        }

        return false;
    }

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(Economy.class)) {
            plugin.getLogger().info("Economy service registered: " + event.getProvider().getProvider().getClass().getName());
            if (!enabled) {
                init();
            }
        }
    }

    public boolean isEnabled() {
        return enabled && econ != null;
    }

    public double getBalance(UUID uuid) {
        if (!isEnabled()) return 0;
        return econ.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    public boolean has(UUID uuid, double amount) {
        if (!isEnabled() || amount <= 0) return true;
        return econ.has(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (!isEnabled() || amount <= 0) return true;
        EconomyResponse r = econ.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        return r.transactionSuccess();
    }

    public boolean deposit(UUID uuid, double amount) {
        if (!isEnabled() || amount <= 0) return true;
        EconomyResponse r = econ.depositPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        return r.transactionSuccess();
    }

    public String format(double amount) {
        if (econ == null) return String.format("%.2f", amount);
        return econ.format(amount);
    }

    public String getProviderName() {
        return providerName;
    }
}
