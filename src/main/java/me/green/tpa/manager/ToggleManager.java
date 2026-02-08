package me.green.tpa.manager;

import java.util.*;

public class ToggleManager {

    private final Set<UUID> tpaDisabled = new HashSet<>();
    private final Map<UUID, Set<UUID>> blockedPlayers = new HashMap<>();
    private final Map<UUID, Set<UUID>> ignoredPlayers = new HashMap<>();
    private final Set<UUID> ignoreAll = new HashSet<>();
    private final Map<UUID, Boolean> autoAccept = new HashMap<>();
    private boolean defaultAutoAccept = false;

    public void setDefaultAutoAccept(boolean val) {
        this.defaultAutoAccept = val;
    }

    public void toggleTpa(UUID uuid) {
        if (tpaDisabled.contains(uuid)) {
            tpaDisabled.remove(uuid);
        } else {
            tpaDisabled.add(uuid);
        }
    }

    public boolean isTpaDisabled(UUID uuid) {
        return tpaDisabled.contains(uuid);
    }

    public void blockPlayer(UUID uuid, UUID target) {
        blockedPlayers.computeIfAbsent(uuid, k -> new HashSet<>()).add(target);
    }

    public void unblockPlayer(UUID uuid, UUID target) {
        if (blockedPlayers.containsKey(uuid)) {
            blockedPlayers.get(uuid).remove(target);
        }
    }

    public boolean isBlocked(UUID uuid, UUID target) {
        return blockedPlayers.getOrDefault(uuid, Collections.emptySet()).contains(target);
    }

    public void ignorePlayer(UUID uuid, UUID target) {
        ignoredPlayers.computeIfAbsent(uuid, k -> new HashSet<>()).add(target);
    }

    public void unignorePlayer(UUID uuid, UUID target) {
        if (ignoredPlayers.containsKey(uuid)) {
            ignoredPlayers.get(uuid).remove(target);
        }
    }

    public boolean isIgnoring(UUID uuid, UUID target) {
        return ignoredPlayers.getOrDefault(uuid, Collections.emptySet()).contains(target) || ignoreAll.contains(uuid);
    }

    public void toggleIgnoreAll(UUID uuid) {
        if (ignoreAll.contains(uuid)) {
            ignoreAll.remove(uuid);
        } else {
            ignoreAll.add(uuid);
        }
    }

    public boolean isIgnoringAll(UUID uuid) {
        return ignoreAll.contains(uuid);
    }

    public void toggleAutoAccept(UUID uuid) {
        autoAccept.put(uuid, !isAutoAccept(uuid));
    }

    public boolean isAutoAccept(UUID uuid) {
        return autoAccept.getOrDefault(uuid, defaultAutoAccept);
    }

    public Set<UUID> getTpaDisabled() { return tpaDisabled; }
    public Set<UUID> getIgnoreAll() { return ignoreAll; }
    public Map<UUID, Set<UUID>> getBlockedPlayers() { return blockedPlayers; }
    public Map<UUID, Set<UUID>> getIgnoredPlayers() { return ignoredPlayers; }
    public Map<UUID, Boolean> getAutoAccept() { return autoAccept; }
}
