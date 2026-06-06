package com.flopasss.minne.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.jetbrains.annotations.Nullable;

public class PendingRequests {

    // requester UUID → target UUID
    private static final Map<UUID, UUID> requests = new HashMap<>();

    private PendingRequests() {}

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Adds a partner request from requester to target.
     * If the requester already had a pending request to someone else, it is
     * replaced. Returns the previous target UUID so the caller can notify them
     * of the withdrawal, or null if there was no previous request.
     */
    public static @Nullable UUID add(UUID requester, UUID target) {
        return requests.put(requester, target);
    }

    /**
     * Returns who the given player has sent a request to, or null if none.
     */
    public static @Nullable UUID getTarget(UUID requester) {
        return requests.get(requester);
    }

    /**
     * Returns true if requester has an active request specifically to target.
     * Use this to validate /partner accept and /partner deny.
     */
    public static boolean hasRequest(UUID requester, UUID target) {
        return target.equals(requests.get(requester));
    }

    /**
     * Removes the request sent by the given player.
     * Returns the target they had asked, or null if there was no request.
     */
    public static @Nullable UUID removeByRequester(UUID requester) {
        return requests.remove(requester);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Registers the disconnect cleanup hook. Call this from Minne.onInitialize().
     * Cleans up both directions when a player leaves:
     *   - Any request they sent (requester side)
     *   - Any request sent to them (target side)
     */
    public static void init() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID disconnecting = handler.player.getUUID();

            // Remove any request this player sent
            requests.remove(disconnecting);

            // Remove any request sent TO this player
            // removeIf on values() correctly removes the whole entry from the map
            requests.values().removeIf(target -> target.equals(disconnecting));
        });
    }
}
