package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RefundManager {

    private final GreenTPA plugin;

    public RefundManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void refund(Player player, double amount, String reason) {
        if (amount <= 0) return;

        boolean success = plugin.getEconomyManager().deposit(player.getUniqueId(), amount);
        if (success) {
            plugin.getChatUtil().sendMessage(player, "economy-refund", "%amount%", plugin.getEconomyManager().format(amount), "%reason%", reason);
        }
    }

    public boolean shouldRefund(String situation) {
        return plugin.getConfig().getBoolean("refund." + situation, false);
    }
}
