package org.zone.commands.structure.region.flags.player.damage.attack;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.configurate.ConfigurateException;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.simple.BooleanArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.player.entitydamage.EntityDamagePlayerFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagEntityDamagePlayerEnableDisable implements ArgumentCommand {
    public static final ExactArgument REGION = new ExactArgument("region");
    public static final ExactArgument FLAG = new ExactArgument("flag");
    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zone_value",
                                                                   new ZoneArgument.ZoneArgumentPropertiesBuilder());
    public static final ExactArgument PLAYER = new ExactArgument("player");
    public static final ExactArgument DAMAGE = new ExactArgument("damage");
    public static final BooleanArgument ENABLE_DISABLE = new BooleanArgument("enableValue",
                                                                            "enable", "disable");
    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(REGION, FLAG, ZONE_VALUE, PLAYER, DAMAGE, ENABLE_DISABLE);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Command to enable/disable the Damage Flag");
    }

    @Override
    public @NotNull Optional<String> getPermissionNode() {
        return Optional.empty();
    }

    @Override
    public @NotNull CommandResult run(@NotNull CommandContext commandContext, @NotNull String... args) {
        boolean enable = commandContext.getArgument(this, ENABLE_DISABLE);
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        EntityDamagePlayerFlag entityDamagePlayerFlag = zone
                .getFlag(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE)
                .orElse(new EntityDamagePlayerFlag());
        if (enable) {
            zone.addFlag(entityDamagePlayerFlag);
        }else {
            zone.removeFlag(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE);
        }
        try {
            zone.save();
            commandContext.sendMessage(Messages.getUpdatedMessage(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE));
        }catch (ConfigurateException ce) {
            ce.printStackTrace();
            return CommandResult.error(Messages.getZoneSavingError(ce));
        }
        return CommandResult.success();
    }
}