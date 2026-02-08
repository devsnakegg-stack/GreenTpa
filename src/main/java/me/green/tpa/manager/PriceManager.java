package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class PriceManager {

    private final GreenTPA plugin;
    private final Map<String, Double> globalPrices = new HashMap<>();
    private final Map<String, Map<String, Double>> worldPrices = new HashMap<>();

    public PriceManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void load() {
        globalPrices.clear();
        worldPrices.clear();

        // Load global prices
        ConfigurationSection global = plugin.getConfig().getConfigurationSection("pricing.global");
        if (global != null) {
            for (String key : global.getKeys(false)) {
                globalPrices.put(key.toLowerCase(), global.getDouble(key));
            }
        }

        // Load per-world prices
        ConfigurationSection perWorld = plugin.getConfig().getConfigurationSection("pricing.per-world");
        if (perWorld != null) {
            for (String worldName : perWorld.getKeys(false)) {
                ConfigurationSection worldSection = perWorld.getConfigurationSection(worldName);
                if (worldSection != null) {
                    Map<String, Double> prices = new HashMap<>();
                    for (String cmd : worldSection.getKeys(false)) {
                        prices.put(cmd.toLowerCase(), worldSection.getDouble(cmd));
                    }
                    worldPrices.put(worldName, prices);
                }
            }
        }
    }

    public double getPrice(String command, String worldName) {
        command = command.toLowerCase();
        if (worldPrices.containsKey(worldName)) {
            Map<String, Double> prices = worldPrices.get(worldName);
            if (prices.containsKey(command)) {
                return prices.get(command);
            }
        }
        return globalPrices.getOrDefault(command, 0.0);
    }
}
