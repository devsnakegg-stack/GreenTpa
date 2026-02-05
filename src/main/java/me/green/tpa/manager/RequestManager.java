package me.green.tpa.manager;

import java.util.*;

public class RequestManager {

    public enum RequestType {
        TPA, TPAHERE
    }

    public static class TPARequest {
        private final UUID sender;
        private final UUID receiver;
        private final RequestType type;
        private final long timestamp;

        public TPARequest(UUID sender, UUID receiver, RequestType type) {
            this.sender = sender;
            this.receiver = receiver;
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }

        public UUID getSender() { return sender; }
        public UUID getReceiver() { return receiver; }
        public RequestType getType() { return type; }
        public long getTimestamp() { return timestamp; }
    }

    private final Map<UUID, List<TPARequest>> incomingRequests = new HashMap<>();
    private final Map<UUID, List<TPARequest>> outgoingRequests = new HashMap<>();
    private final long timeout = 60 * 1000; // 60 seconds

    public void addRequest(UUID sender, UUID receiver, RequestType type) {
        TPARequest request = new TPARequest(sender, receiver, type);
        incomingRequests.computeIfAbsent(receiver, k -> new ArrayList<>()).add(request);
        outgoingRequests.computeIfAbsent(sender, k -> new ArrayList<>()).add(request);
    }

    public TPARequest getLatestRequest(UUID receiver) {
        cleanExpired();
        List<TPARequest> requests = incomingRequests.get(receiver);
        if (requests == null || requests.isEmpty()) return null;
        return requests.get(requests.size() - 1);
    }

    public TPARequest getRequestFrom(UUID receiver, UUID sender) {
        cleanExpired();
        List<TPARequest> requests = incomingRequests.get(receiver);
        if (requests == null) return null;
        return requests.stream().filter(r -> r.getSender().equals(sender)).findFirst().orElse(null);
    }

    public TPARequest getRequestTo(UUID sender, UUID receiver) {
        cleanExpired();
        List<TPARequest> requests = outgoingRequests.get(sender);
        if (requests == null) return null;
        return requests.stream().filter(r -> r.getReceiver().equals(receiver)).findFirst().orElse(null);
    }

    public void removeRequest(TPARequest request) {
        List<TPARequest> incoming = incomingRequests.get(request.getReceiver());
        if (incoming != null) incoming.remove(request);
        List<TPARequest> outgoing = outgoingRequests.get(request.getSender());
        if (outgoing != null) outgoing.remove(request);
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();
        incomingRequests.values().forEach(list -> list.removeIf(r -> now - r.getTimestamp() > timeout));
        outgoingRequests.values().forEach(list -> list.removeIf(r -> now - r.getTimestamp() > timeout));
    }

    public List<TPARequest> getIncomingRequests(UUID receiver) {
        cleanExpired();
        return incomingRequests.getOrDefault(receiver, Collections.emptyList());
    }
}
