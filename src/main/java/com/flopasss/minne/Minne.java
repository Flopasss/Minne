package com.flopasss.minne;

import com.flopasss.minne.command.Commands;
import com.flopasss.minne.config.Config;
import com.flopasss.minne.event.EndServerTickEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minne implements ModInitializer {

    public static final String MOD_ID = "minne";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Config CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = Config.load();

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) ->
                Commands.register(dispatcher)
        );

        EndServerTickEvent.init();

        LOGGER.info("Minne initialized");
    }
}
