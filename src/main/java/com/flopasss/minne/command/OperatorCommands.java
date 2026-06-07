package com.flopasss.minne.command;

import static net.minecraft.commands.Commands.literal;

import com.flopasss.minne.Minne;
import com.flopasss.minne.config.Config;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class OperatorCommands {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
            literal("minne")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                // Reload the config during runtime
                .then(
                    literal("reload").executes(context -> {
                        Minne.CONFIG = Config.load();

                        context
                            .getSource()
                            .sendSuccess(
                                () -> Component.literal("Config reloaded"),
                                false
                            );

                        return 1;
                    })
                )
                // Change config options
                .then(
                    literal("options")
                        // booleans
                        .then(
                            CommandHelper.booleanConfig(
                                "enabled",
                                () -> Minne.CONFIG.enabled,
                                v -> Minne.CONFIG.enabled = v
                            )
                        )
                        .then(
                            CommandHelper.booleanConfig(
                                "requireSneaking",
                                () -> Minne.CONFIG.requireSneaking,
                                v -> Minne.CONFIG.requireSneaking = v
                            )
                        )
                        .then(
                            CommandHelper.booleanConfig(
                                "requireVisible",
                                () -> Minne.CONFIG.requireVisible,
                                v -> Minne.CONFIG.requireVisible = v
                            )
                        )
                        .then(
                            CommandHelper.booleanConfig(
                                "requireLooking",
                                () -> Minne.CONFIG.requireLooking,
                                v -> Minne.CONFIG.requireLooking = v
                            )
                        )
                        // integers
                        .then(
                            CommandHelper.intConfig(
                                "interval",
                                " ticks",
                                1,
                                () -> Minne.CONFIG.interval,
                                v -> Minne.CONFIG.interval = v
                            )
                        )
                        .then(
                            CommandHelper.intConfig(
                                "count",
                                " particle(s)",
                                1,
                                () -> Minne.CONFIG.count,
                                v -> Minne.CONFIG.count = v
                            )
                        )
                        // floats
                        .then(
                            CommandHelper.floatConfig(
                                "distance",
                                " blocks",
                                0.0f,
                                () -> Minne.CONFIG.distance,
                                v -> Minne.CONFIG.distance = v
                            )
                        )
                        .then(
                            CommandHelper.floatConfig(
                                "toleranceDegrees",
                                "°",
                                0.0f,
                                () -> Minne.CONFIG.toleranceDegrees,
                                v -> Minne.CONFIG.toleranceDegrees = v
                            )
                        )
                        .then(
                            CommandHelper.floatConfig(
                                "spread",
                                "",
                                0.0f,
                                () -> Minne.CONFIG.spread,
                                v -> Minne.CONFIG.spread = v
                            )
                        )
                        .then(
                            CommandHelper.floatConfig(
                                "speed",
                                "",
                                0.0f,
                                () -> Minne.CONFIG.speed,
                                v -> Minne.CONFIG.speed = v
                            )
                        )
                )
        );
    }
}
