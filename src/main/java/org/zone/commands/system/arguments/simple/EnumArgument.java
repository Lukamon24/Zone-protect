package org.zone.commands.system.arguments.simple;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EnumArgument<T extends Enum<T>> implements CommandArgument<T> {

    private final @NotNull String id;
    private final @NotNull EnumSet<T> set;

    public EnumArgument(@NotNull String id, @NotNull EnumSet<T> set) {
        this.id = id;
        this.set = set;
    }

    public EnumArgument(@NotNull String id, Class<T> enumClass) {
        this(id, EnumSet.allOf(enumClass));
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<T> parse(@NotNull CommandContext context,
                                          @NotNull CommandArgumentContext<T> argument) throws
            IOException {
        String arg = argument.getFocusArgument();
        Optional<T> opValue = this.set
                .parallelStream()
                .filter(t -> t.name().equalsIgnoreCase(arg))
                .findAny();
        if (opValue.isEmpty()) {
            throw new IOException("Unknown value of " + arg);
        }
        return CommandArgumentResult.from(argument, opValue.get());
    }

    @Override
    public Collection<CommandCompletion> suggest(@NotNull CommandContext commandContext,
                                                 @NotNull CommandArgumentContext<T> argument) {
        String arg = argument.getFocusArgument();
        return this.set
                .parallelStream()
                .map(value -> CommandCompletion.of(value.name().toLowerCase()))
                .filter(value -> value.completion().toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        CommandCompletion::completion))));
    }
}