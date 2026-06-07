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
        );
    }
}
