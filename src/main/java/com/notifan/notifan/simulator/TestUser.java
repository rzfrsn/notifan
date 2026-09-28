package com.notifan.notifan.simulator;

import java.util.UUID;

/**
 * A fake user used only by the event simulator to stand in as actor/recipient
 * when publishing simulated events. Not backed by any real user data.
 */
public record TestUser(UUID id, String username) {
}
