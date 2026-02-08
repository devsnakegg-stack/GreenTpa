package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class PriceManager {

    private final GreenTPA plugin;
    private final Map<String, Double> basePrices = new HashMap<>();
    private final Map<String, Map<String, Double>> worldPrices = new HashMap<>();

    public PriceManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void load() {
        basePrices.clear();
        worldPrices.clear();

        ConfigurationSection pricing = plugin.getConfig().getConfigurationSection("pricing");
        if (pricing != null) {
            for (String key : pricing.getKeys(false)) {
                if (pricing.isConfigurationSection(key)) {
                    // It's a world-specific section
                    ConfigurationSection worldSection = pricing.getConfigurationSection(key);
                    Map<String, Double> prices = new HashMap<>();
                    for (String cmd : worldSection.getKeys(false)) {
                        prices.put(cmd.toLowerCase(), worldSection.getDouble(cmd));
                    }
                    worldPrices.put(key, prices);
                } else {
                    basePrices.put(key.toLowerCase(), pricing.getDouble(key));
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
        return basePrices.getOrDefault(command, 0.0);
    }
}
