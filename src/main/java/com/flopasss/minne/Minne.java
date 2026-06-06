package com.flopasss.minne;

import com.flopasss.minne.command.Commands;
import com.flopasss.minne.event.TickEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minne implements ModInitializer {

    public static final String MOD_ID = "minne";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) ->
                Commands.register(dispatcher)
        );

        TickEvent.init();

        LOGGER.info("Minne initialized");
    }
}
