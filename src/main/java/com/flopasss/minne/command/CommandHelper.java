package com.flopasss.minne.command;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.flopasss.minne.Minne;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class CommandHelper {

    // Boolean config with standard §a/§c color formatting
    public static LiteralArgumentBuilder<CommandSourceStack> booleanConfig(
        String commandName,
        Supplier<Boolean> getter,
        Consumer<Boolean> setter
    ) {
        return literal(commandName)
            .executes(context -> {
                boolean currentValue = getter.get();

                context
                    .getSource()
                    .sendSuccess(
                        () ->
                            Component.literal(
                                commandName +
                                    " is: " +
                                    (currentValue ? "§a" : "§c") +
                                    currentValue
                            ),
                        false
                    );

                return 1;
            })
            .then(
                argument("boolean", BoolArgumentType.bool()).executes(
                    context -> {
                        boolean value = BoolArgumentType.getBool(
                            context,
                            "boolean"
                        );

                        setter.accept(value);
                        Minne.CONFIG.save();

                        context
                            .getSource()
                            .sendSuccess(
                                () ->
                                    Component.literal(
                                        commandName +
                                            " set: " +
                                            (value ? "§a" : "§c") +
                                            value
                                    ),
                                true
                            );

                        return 1;
                    }
                )
            );
    }

    // Integer config with min bound
    public static LiteralArgumentBuilder<CommandSourceStack> intConfig(
        String commandName,
        String suffix,
        int min,
        Supplier<Integer> getter,
        Consumer<Integer> setter
    ) {
        return literal(commandName)
            .executes(context -> {
                int currentValue = getter.get();

                context
                    .getSource()
                    .sendSuccess(
                        () ->
                            Component.literal(
                                commandName +
                                    " is: §9" +
                                    currentValue +
                                    "§r" +
                                    suffix
                            ),
                        false
                    );

                return 1;
            })
            .then(
                argument("int", IntegerArgumentType.integer(min)).executes(
                    context -> {
                        int value = IntegerArgumentType.getInteger(
                            context,
                            "int"
                        );

                        setter.accept(value);
                        Minne.CONFIG.save();

                        context
                            .getSource()
                            .sendSuccess(
                                () ->
                                    Component.literal(
                                        commandName +
                                            " set: §9" +
                                            value +
                                            "§r" +
                                            suffix
                                    ),
                                true
                            );

                        return 1;
                    }
                )
            );
    }

    // Float config with min bound and optional max bound
    public static LiteralArgumentBuilder<CommandSourceStack> floatConfig(
        String commandName,
        String suffix,
        float min,
        Supplier<Float> getter,
        Consumer<Float> setter
    ) {
        return literal(commandName)
            .executes(context -> {
                float currentValue = getter.get();

                context
                    .getSource()
                    .sendSuccess(
                        () ->
                            Component.literal(
                                commandName +
                                    " is: §9" +
                                    currentValue +
                                    "§r" +
                                    suffix
                            ),
                        false
                    );

                return 1;
            })
            .then(
                argument("float", FloatArgumentType.floatArg(min)).executes(
                    context -> {
                        float value = FloatArgumentType.getFloat(
                            context,
                            "float"
                        );

                        setter.accept(value);
                        Minne.CONFIG.save();

                        context
                            .getSource()
                            .sendSuccess(
                                () ->
                                    Component.literal(
                                        commandName +
                                            " set: §9" +
                                            value +
                                            "§r" +
                                            suffix
                                    ),
                                true
                            );

                        return 1;
                    }
                )
            );
    }
}
