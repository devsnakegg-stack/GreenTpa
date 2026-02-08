package me.green.tpa.commands;

import me.green.tpa.GreenTPA;
import me.green.tpa.manager.RequestManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TPACommand implements CommandExecutor {

    private final GreenTPA plugin;

    public TPACommand(GreenTPA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getChatUtil().sendMessage(sender, "player-only");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "tpa" -> handleTpa(player, args, RequestManager.RequestType.TPA);
            case "tpahere" -> handleTpa(player, args, RequestManager.RequestType.TPAHERE);
            case "tpaccept" -> handleAccept(player, args);
            case "tpdeny" -> handleDeny(player, args);
            case "tpcancel" -> handleCancel(player, args);
            case "tpalist" -> handleList(player);
        }

        return true;
    }

    private void handleTpa(Player player, String[] args, RequestManager.RequestType type) {
        if (args.length < 1) {
            plugin.getChatUtil().sendMessage(player, type == RequestManager.RequestType.TPA ? "usage-tpa" : "usage-tpahere");
            return;
        }

        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId()) && !player.hasPermission("greentpa.admin.nocooldown")) {
            plugin.getChatUtil().sendMessage(player, "cooldown-active", "%time%", String.valueOf(plugin.getCooldownManager().getRemainingTime(player.getUniqueId())));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
            return;
        }

        if (target.equals(player)) {
            plugin.getChatUtil().sendMessage(player, "tpa-self");
            return;
        }

        if (plugin.getToggleManager().isTpaDisabled(target.getUniqueId())) {
            plugin.getChatUtil().sendMessage(player, "target-disabled-tpa", "%player%", target.getName());
            return;
        }

        if (plugin.getToggleManager().isBlocked(target.getUniqueId(), player.getUniqueId())) {
            plugin.getChatUtil().sendMessage(player, "target-blocked-you");
            return;
        }

        if (plugin.getToggleManager().isIgnoring(target.getUniqueId(), player.getUniqueId())) {
            plugin.getChatUtil().sendMessage(player, type == RequestManager.RequestType.TPA ? "tpa-sent" : "tpahere-sent", "%player%", target.getName());
            return;
        }

        double price = plugin.getPriceManager().getPrice(type == RequestManager.RequestType.TPA ? "tpa" : "tpahere", player.getWorld().getName());
        if (!player.hasPermission("greentpa.free") && !plugin.getEconomyManager().has(player.getUniqueId(), price)) {
            plugin.getChatUtil().sendMessage(player, "economy-no-money", "%price%", plugin.getEconomyManager().format(price));
            return;
        }

        if (plugin.getToggleManager().isAutoAccept(target.getUniqueId())) {
            if (!player.hasPermission("greentpa.free") && !plugin.getEconomyManager().withdraw(player.getUniqueId(), price)) {
                plugin.getChatUtil().sendMessage(player, "economy-error");
                return;
            }
            plugin.getChatUtil().sendMessage(player, type == RequestManager.RequestType.TPA ? "tpa-sent" : "tpahere-sent", "%player%", target.getName());
            plugin.getChatUtil().sendMessage(target, type == RequestManager.RequestType.TPA ? "tpaccept-receiver" : "tpaccept-sender", "%player%", player.getName());
            if (type == RequestManager.RequestType.TPA) {
                plugin.getTeleportManager().teleport(player, target.getLocation(), false);
            } else {
                plugin.getTeleportManager().teleport(target, player.getLocation(), false);
            }
            return;
        }

        if (!player.hasPermission("greentpa.free") && !plugin.getEconomyManager().withdraw(player.getUniqueId(), price)) {
            plugin.getChatUtil().sendMessage(player, "economy-error");
            return;
        }

        plugin.getRequestManager().addRequest(player.getUniqueId(), target.getUniqueId(), type, price);
        plugin.getCooldownManager().setCooldown(player.getUniqueId());

        plugin.getChatUtil().sendMessage(player, type == RequestManager.RequestType.TPA ? "tpa-sent" : "tpahere-sent", "%player%", target.getName());
        plugin.getChatUtil().sendMessage(target, type == RequestManager.RequestType.TPA ? "tpa-received" : "tpahere-received", "%player%", player.getName());
    }

    private void handleAccept(Player player, String[] args) {
        RequestManager.TPARequest request;
        if (args.length > 0) {
            Player sender = Bukkit.getPlayer(args[0]);
            if (sender == null) {
                plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
                return;
            }
            request = plugin.getRequestManager().getRequestFrom(player.getUniqueId(), sender.getUniqueId());
        } else {
            request = plugin.getRequestManager().getLatestRequest(player.getUniqueId());
        }

        if (request == null) {
            plugin.getChatUtil().sendMessage(player, "no-pending-requests", "%player%", args.length > 0 ? args[0] : "anyone");
            return;
        }

        Player sender = Bukkit.getPlayer(request.getSender());
        if (sender == null) {
            plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", "Sender");
            plugin.getRequestManager().removeRequest(request);
            return;
        }

        plugin.getChatUtil().sendMessage(player, "tpaccept-receiver", "%player%", sender.getName());
        plugin.getChatUtil().sendMessage(sender, "tpaccept-sender", "%player%", player.getName());

        if (request.getType() == RequestManager.RequestType.TPA) {
            plugin.getTeleportManager().teleport(sender, player.getLocation(), false);
        } else {
            plugin.getTeleportManager().teleport(player, sender.getLocation(), false);
        }

        plugin.getRequestManager().removeRequest(request);
    }

    private void handleDeny(Player player, String[] args) {
        RequestManager.TPARequest request;
        if (args.length > 0) {
            Player sender = Bukkit.getPlayer(args[0]);
            if (sender == null) {
                plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
                return;
            }
            request = plugin.getRequestManager().getRequestFrom(player.getUniqueId(), sender.getUniqueId());
        } else {
            request = plugin.getRequestManager().getLatestRequest(player.getUniqueId());
        }

        if (request == null) {
            plugin.getChatUtil().sendMessage(player, "no-pending-requests", "%player%", args.length > 0 ? args[0] : "anyone");
            return;
        }

        Player sender = Bukkit.getPlayer(request.getSender());
        if (sender != null) {
            plugin.getChatUtil().sendMessage(sender, "tpdeny-sender", "%player%", player.getName());
            if (plugin.getRefundManager().shouldRefund("deny")) {
                plugin.getRefundManager().refund(sender, request.getCost(), "deny");
            }
        }
        plugin.getChatUtil().sendMessage(player, "tpdeny-receiver", "%player%", sender != null ? sender.getName() : "Unknown");

        plugin.getRequestManager().removeRequest(request);
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getChatUtil().sendMessage(player, "usage-tpcancel");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getChatUtil().sendMessage(player, "player-not-found", "%player%", args[0]);
            return;
        }

        RequestManager.TPARequest request = plugin.getRequestManager().getRequestTo(player.getUniqueId(), target.getUniqueId());
        if (request == null) {
            plugin.getChatUtil().sendMessage(player, "no-pending-to-target", "%player%", target.getName());
            return;
        }

        plugin.getChatUtil().sendMessage(player, "tpcancel-sender", "%player%", target.getName());
        if (plugin.getRefundManager().shouldRefund("cancel")) {
            plugin.getRefundManager().refund(player, request.getCost(), "cancel");
        }
        plugin.getRequestManager().removeRequest(request);
    }

    private void handleList(Player player) {
        List<RequestManager.TPARequest> requests = plugin.getRequestManager().getIncomingRequests(player.getUniqueId());
        if (requests.isEmpty()) {
            plugin.getChatUtil().sendMessage(player, "no-requests");
            return;
        }

        plugin.getChatUtil().sendMessage(player, "tpalist-header");
        for (RequestManager.TPARequest request : requests) {
            Player sender = Bukkit.getPlayer(request.getSender());
            String name = sender != null ? sender.getName() : "Unknown";
            plugin.getChatUtil().sendMessage(player, "tpalist-item", "%player%", name, "%type%", request.getType().name());
        }
    }
}
