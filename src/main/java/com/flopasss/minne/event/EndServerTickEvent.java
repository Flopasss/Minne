package com.flopasss.minne.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class EndServerTickEvent {

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Only run the code every 20 ticks (1 second) to reduce performance impact
            if (server.getTickCount() % 20 != 10) return;

            // TODO: Check if any online partners are crouched close to eachother and roughly looking at eachother, if so, send heart particles between both positions
        });
    }
}
