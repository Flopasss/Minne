package com.flopasss.minne.config;

import com.flopasss.minne.Minne;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Config {

    // Base Minne options
    public boolean enabled = true; // By default, the mod is enabled
    public boolean requireSneaking = true; // By default, both partners must be sneaking to spawn particles
    public boolean requireVisible = true; // By default, if either partner is invisible, particles will not spawn
    public boolean requireLooking = true; // By default, partners must be looking at each other to spawn particles
    public int interval = 20; // By default, the mod checks for partners every 20 ticks (1 second)
    public int count = 1; // By default, the mod spawns 1 heart particle
    public float distance = 2.0f; // By default, partners must be within 2.0 blocks of each other to spawn particles
    public float spread = 0.0f; // By default, the particles are spread out with a radius of 0.0
    public float speed = 0.0f; // By default, the particles have 0.0 speed, which is the default speed for particles in Minecraft
    public float toleranceDegrees = 45.0f; // By default, partners must be facing each other within a 45 degree angle to spawn particles
    public transient double toleranceCosine;

    // Gson instance for serializing and deserializing the config file, with pretty printing enabled for easier readability
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    // Path to the config file, located in the config directory of the Fabric Loader, with the name "zael-ymladda.json"
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve(Minne.MOD_ID + ".json");

    public static Config load() {
        // If the config file doesn't exist, create it with default values and return a new config instance
        if (!Files.exists(CONFIG_PATH)) {
            Config defaultConfig = new Config();
            defaultConfig.resolve();
            defaultConfig.save();
            return defaultConfig;
        }

        try {
            // Read the config file and deserialize it into a Config instance
            Config config = GSON.fromJson(
                Files.readString(CONFIG_PATH),
                Config.class
            );

            // If deserialization fails and returns null, return a new config instance with default values
            if (config == null) return new Config().resolve();

            // Save the config to ensure that any missing fields are added to the config file with their default values, this is especially useful when new fields are added in future updates, as it will automatically update existing config files without losing user settings
            config.save();

            // If the config file is loaded successfully, return the loaded config instance
            return config.resolve();
        } catch (Exception e) {
            // If there's an error reading or deserializing the config file, log the error
            Minne.LOGGER.warn("Failed to load config, using default values", e);

            // If there's an error, return a new config instance with default values
            return new Config().resolve();
        }
    }

    public void save() {
        try {
            // Serialize the current config instance to JSON and write it to the config file
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (Exception e) {
            // If there's an error writing the config file, log the error
            Minne.LOGGER.error("Failed to save config", e);
        }
    }

    public Config resolve() {
        toleranceCosine = Math.cos(Math.toRadians(toleranceDegrees));
        return this;
    }
}
