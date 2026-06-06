package com.flopasss.minne.command;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class Commands {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
            literal("partner")
                .executes(context -> {
                    context
                        .getSource()
                        .sendSuccess(
                            () ->
                                Component.literal(
                                    "Ask anyone online to become your partner"
                                ),
                            false
                        );

                    return 1;
                })
                // Ask any online player to be your partner
                .then(
                    literal("ask").then(
                        argument("player", EntityArgument.player()).executes(
                            context -> {
                                ServerPlayer target = EntityArgument.getPlayer(
                                    context,
                                    "player"
                                );

                                // TODO: Request system

                                context
                                    .getSource()
                                    .sendSuccess(
                                        () ->
                                            Component.literal(
                                                "You have asked " +
                                                    target
                                                        .getName()
                                                        .getString() +
                                                    " to be your partner"
                                            ),
                                        false
                                    );

                                return 1;
                            }
                        )
                    )
                )
                // Accept a partner request from any online player
                .then(
                    literal("accept").then(
                        // TODO: Only show players that have sent you a request
                        argument("player", EntityArgument.player()).executes(
                            context -> {
                                ServerPlayer target = EntityArgument.getPlayer(
                                    context,
                                    "player"
                                );

                                // TODO: Partner saving system

                                context
                                    .getSource()
                                    .sendSuccess(
                                        () ->
                                            Component.literal(
                                                "You and " +
                                                    target
                                                        .getName()
                                                        .getString() +
                                                    " are now partners"
                                            ),
                                        false
                                    );

                                return 1;
                            }
                        )
                    )
                )
                // Deny a partner request from any online player
                .then(
                    literal("deny").then(
                        // TODO: Only show players that have sent you a request
                        argument("player", EntityArgument.player()).executes(
                            context -> {
                                ServerPlayer target = EntityArgument.getPlayer(
                                    context,
                                    "player"
                                );

                                // TODO: Deny system

                                context
                                    .getSource()
                                    .sendSuccess(
                                        () ->
                                            Component.literal(
                                                "You denied " +
                                                    target
                                                        .getName()
                                                        .getString() +
                                                    "'s request to be your partner"
                                            ),
                                        false
                                    );

                                return 1;
                            }
                        )
                    )
                )
                // Show your current partner
                .then(
                    literal("show").executes(context -> {
                        // TODO: Get current partner from partner saving system

                        context
                            .getSource()
                            .sendSuccess(
                                () ->
                                    Component.literal(
                                        "You do not have a partner yet"
                                    ),
                                false
                            );

                        return 1;
                    })
                )
                // Remove your current partner
                .then(
                    literal("remove").executes(context -> {
                        // TODO: "Are you sure?" question system
                        // TODO: Remove current partner from partner saving system

                        context
                            .getSource()
                            .sendSuccess(
                                () ->
                                    Component.literal(
                                        "You removed your current partner"
                                    ),
                                false
                            );

                        return 1;
                    })
                )
        );
    }
}
