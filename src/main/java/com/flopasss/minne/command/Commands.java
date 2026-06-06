package com.flopasss.minne.command;

import static net.minecraft.commands.Commands.LEVEL_GAMEMASTERS;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.hasPermission;
import static net.minecraft.commands.Commands.literal;

import com.flopasss.minne.data.PartnerData;
import com.flopasss.minne.data.PendingRequests;
import com.mojang.brigadier.CommandDispatcher;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
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
                // Set a partner directly without a request, but clear both players' current partners first
                .then(
                    literal("set")
                        // Only allow server operators to use this command, since it can be used to bypass the request
                        .requires(hasPermission(LEVEL_GAMEMASTERS))
                        .then(
                            argument(
                                "player",
                                EntityArgument.player()
                            ).executes(context -> {
                                ServerPlayer target = EntityArgument.getPlayer(
                                    context,
                                    "player"
                                );

                                CommandSourceStack source = context.getSource();
                                ServerPlayer player =
                                    source.getPlayerOrException();
                                MinecraftServer server = source.getServer();

                                if (player.getUUID().equals(target.getUUID())) {
                                    source.sendFailure(
                                        Component.literal(
                                            "You cannot be your own partner"
                                        )
                                    );

                                    return 1;
                                }

                                PartnerData.get(server).removePartner(
                                    player.getUUID()
                                );
                                PartnerData.get(server).removePartner(
                                    target.getUUID()
                                );

                                PartnerData.get(server).setPartner(
                                    player.getUUID(),
                                    target.getUUID()
                                );

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
                            })
                        )
                )
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

                                // TODO: Accept system

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
                        CommandSourceStack source = context.getSource();
                        ServerPlayer player = source.getPlayerOrException();
                        MinecraftServer server = source.getServer();

                        UUID partnerUUID = PartnerData.get(server).getPartner(
                            player.getUUID()
                        );
                        if (partnerUUID == null) {
                            source.sendFailure(
                                Component.literal(
                                    "You do not have a partner yet"
                                )
                            );

                            return 1;
                        }

                        ServerPlayer partner = server
                            .getPlayerList()
                            .getPlayer(partnerUUID);
                        boolean partnerOnline = partner != null;
                        String partnerName = partnerOnline
                            ? partner.getName().getString()
                            : partnerUUID.toString();

                        source.sendSuccess(
                            () ->
                                Component.literal(
                                    "Your partner is " +
                                        (partnerOnline
                                            ? partnerName
                                            : "currently offline (UUID: " +
                                                  partnerName +
                                                  ")")
                                ),
                            false
                        );

                        return 1;
                    })
                )
                // Remove your current partner
                .then(
                    literal("remove").executes(context -> {
                        CommandSourceStack source = context.getSource();
                        ServerPlayer player = source.getPlayerOrException();
                        MinecraftServer server = source.getServer();

                        if (
                            !PartnerData.get(server).hasPartner(
                                player.getUUID()
                            )
                        ) {
                            source.sendFailure(
                                Component.literal(
                                    "You do not have a partner to remove"
                                )
                            );

                            return 1;
                        }

                        PartnerData.get(server).removePartner(player.getUUID());

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
