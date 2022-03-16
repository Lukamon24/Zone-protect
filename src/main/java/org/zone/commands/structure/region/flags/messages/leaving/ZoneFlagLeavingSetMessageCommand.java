package org.zone.commands.structure.region.flags.messages.leaving;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.configurate.ConfigurateException;
import org.zone.ZonePlugin;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.AnyMatchArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.operation.OptionalArgument;
import org.zone.commands.system.arguments.sponge.ComponentRemainingArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.entity.player.move.message.display.MessageDisplayType;
import org.zone.region.flag.entity.player.move.message.leaving.LeavingFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagLeavingSetMessageCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_LEAVING_SET));
    public static final ComponentRemainingArgument MESSAGE = new ComponentRemainingArgument(
            "message_value");
    public static final OptionalArgument<MessageDisplayType<?>> DISPLAY_MODE =
            new OptionalArgument<>(new AnyMatchArgument<>("displayMode",
                    MessageDisplayType::getId
                    , ZonePlugin
                    .getZonesPlugin()
                    .getMessageDisplayManager()
                    .getDisplayTypes()),
                    (MessageDisplayType<?>) null);

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                             new ExactArgument("flag"),
                             ZONE,
                             new ExactArgument("leaving"),
                             new ExactArgument("message"),
                             new ExactArgument("set"),
                             MESSAGE,
                             DISPLAY_MODE);
    }

    @Override
    public @NotNull Component getDescription() {
        return Messages.getLeavingSetMessageCommandDescription();
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_LEAVING_SET);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        Component message = commandContext.getArgument(this, MESSAGE);
        MessageDisplayType<?> displayMode = commandContext.getArgument(this, DISPLAY_MODE);

        LeavingFlag flag = zone
                .getFlag(FlagTypes.LEAVING)
                .orElse(new LeavingFlag(Messages.getEnterLeavingMessage(), displayMode.createCopyOfDefault()));

        flag.setLeavingMessage(message);
        zone.setFlag(flag);
        try {
            zone.save();
            commandContext.sendMessage(Messages.getZoneFlagLeavingMessageSet(message));
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return CommandResult.error(Messages.getZoneSavingError(e));
        }
        return CommandResult.success();
    }
}
