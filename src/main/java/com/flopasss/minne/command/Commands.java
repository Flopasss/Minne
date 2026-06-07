package com.flopasss.minne.command;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.flopasss.minne.data.PartnerData;
import com.flopasss.minne.data.PendingRequests;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
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
                // Ask any online player to be your partner
                .then(
                    literal("ask").then(
                        argument("player", EntityArgument.player()).executes(
                            context -> {
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
                                            "You cannot ask yourself to be your partner"
                                        )
                                    );

                                    return 1;
                                }

                                if (
                                    PartnerData.get(server).hasPartner(
                                        player.getUUID()
                                    )
                                ) {
                                    source.sendFailure(
                                        Component.literal(
                                            "You already have a partner"
                                        )
                                    );

                                    return 1;
                                }

                                if (
                                    PartnerData.get(server).hasPartner(
                                        target.getUUID()
                                    )
                                ) {
                                    source.sendFailure(
                                        Component.literal(
                                            target.getName().getString() +
                                                " already has a partner"
                                        )
                                    );

                                    return 1;
                                }

                                UUID previousTarget = PendingRequests.add(
                                    player.getUUID(),
                                    target.getUUID()
                                );

                                if (previousTarget != null) {
                                    ServerPlayer previousPlayer = server
                                        .getPlayerList()
                                        .getPlayer(previousTarget);
                                    if (
                                        previousPlayer != null
                                    ) previousPlayer.sendSystemMessage(
                                        Component.literal(
                                            player.getName().getString() +
                                                " withdrew their partner request"
                                        )
                                    );
                                }

                                target.sendSystemMessage(
                                    Component.literal(
                                        player.getName().getString() +
                                            " wants to be your partner"
                                    )
                                );

                                source.sendSuccess(
                                    () ->
                                        Component.literal(
                                            "You have asked " +
                                                target.getName().getString() +
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
                        argument("player", EntityArgument.player())
                            .suggests((context, builder) -> {
                                try {
                                    CommandSourceStack source =
                                        context.getSource();
                                    ServerPlayer player =
                                        source.getPlayerOrException();
                                    MinecraftServer server = source.getServer();

                                    PendingRequests.getRequestersFor(
                                        player.getUUID()
                                    )
                                        .stream()
                                        .map(uuid ->
                                            server
                                                .getPlayerList()
                                                .getPlayer(uuid)
                                        )
                                        .filter(Objects::nonNull)
                                        .map(p -> p.getName().getString())
                                        .forEach(builder::suggest);
                                } catch (CommandSyntaxException ignored) {}

                                return builder.buildFuture();
                            })
                            .executes(context -> {
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
                                            "You cannot accept yourself to be your partner"
                                        )
                                    );

                                    return 1;
                                }

                                if (
                                    PartnerData.get(server).hasPartner(
                                        player.getUUID()
                                    )
                                ) {
                                    source.sendFailure(
                                        Component.literal(
                                            "You already have a partner"
                                        )
                                    );

                                    return 1;
                                }

                                if (
                                    !PendingRequests.hasRequest(
                                        target.getUUID(),
                                        player.getUUID()
                                    )
                                ) {
                                    source.sendFailure(
                                        Component.literal(
                                            "You do not have a partner request from " +
                                                target.getName().getString()
                                        )
                                    );

                                    return 1;
                                }

                                if (
                                    PartnerData.get(server).hasPartner(
                                        target.getUUID()
                                    )
                                ) {
                                    source.sendFailure(
                                        Component.literal(
                                            target.getName().getString() +
                                                " already has a partner"
                                        )
                                    );

                                    return 1;
                                }

                                PendingRequests.removeByRequester(
                                    target.getUUID()
                                );
                                PendingRequests.removeByRequester(
                                    player.getUUID()
                                );

                                PartnerData.get(server).setPartner(
                                    player.getUUID(),
                                    target.getUUID()
                                );

                                target.sendSystemMessage(
                                    Component.literal(
                                        player.getName().getString() +
                                            " accepted your partner request"
                                    )
                                );

                                source.sendSuccess(
                                    () ->
                                        Component.literal(
                                            "You and " +
                                                target.getName().getString() +
                                                " are now partners"
                                        ),
                                    false
                                );

                                return 1;
                            })
                    )
                )
                // Deny a partner request from any online player
                .then(
                    literal("deny").then(
                        argument("player", EntityArgument.player())
                            .suggests((context, builder) -> {
                                try {
                                    CommandSourceStack source =
                                        context.getSource();
                                    ServerPlayer player =
                                        source.getPlayerOrException();
                                    MinecraftServer server = source.getServer();

                                    PendingRequests.getRequestersFor(
                                        player.getUUID()
                                    )
                                        .stream()
                                        .map(uuid ->
                                            server
                                                .getPlayerList()
                                                .getPlayer(uuid)
                                        )
                                        .filter(Objects::nonNull)
                                        .map(p -> p.getName().getString())
                                        .forEach(builder::suggest);
                                } catch (CommandSyntaxException ignored) {}

                                return builder.buildFuture();
                            })
                            .executes(context -> {
                                ServerPlayer target = EntityArgument.getPlayer(
                                    context,
                                    "player"
                                );

                                CommandSourceStack source = context.getSource();
                                ServerPlayer player =
                                    source.getPlayerOrException();

                                if (player.getUUID().equals(target.getUUID())) {
                                    source.sendFailure(
                                        Component.literal(
                                            "You cannot deny yourself to be your partner"
                                        )
                                    );

                                    return 1;
                                }

                                if (
                                    !PendingRequests.hasRequest(
                                        target.getUUID(),
                                        player.getUUID()
                                    )
                                ) {
                                    source.sendFailure(
                                        Component.literal(
                                            "You do not have a partner request from " +
                                                target.getName().getString()
                                        )
                                    );

                                    return 1;
                                }

                                PendingRequests.removeByRequester(
                                    target.getUUID()
                                );

                                target.sendSystemMessage(
                                    Component.literal(
                                        player.getName().getString() +
                                            " denied your partner request"
                                    )
                                );

                                source.sendSuccess(
                                    () ->
                                        Component.literal(
                                            "You denied " +
                                                target.getName().getString() +
                                                "'s request to be your partner"
                                        ),
                                    false
                                );

                                return 1;
                            })
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

                        UUID partnerUUID = PartnerData.get(server).getPartner(
                            player.getUUID()
                        );
                        PartnerData.get(server).removePartner(player.getUUID());

                        ServerPlayer partner = server
                            .getPlayerList()
                            .getPlayer(partnerUUID);
                        if (partner != null) {
                            partner.sendSystemMessage(
                                Component.literal(
                                    player.getName().getString() +
                                        " removed you as their partner"
                                )
                            );
                        }

                        source.sendSuccess(
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
