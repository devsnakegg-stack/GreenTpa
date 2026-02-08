package me.green.tpa.models;

import java.util.UUID;

public class EconomyTransaction {
    private final UUID playerUuid;
    private final double amount;
    private final String reason;
    private final long timestamp;

    public EconomyTransaction(UUID playerUuid, double amount, String reason) {
        this.playerUuid = playerUuid;
        this.amount = amount;
        this.reason = reason;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
