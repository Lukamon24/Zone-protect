package org.zone.commands.structure.create;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;
import org.zone.Permissions;
import org.zone.ZonePlugin;
import org.zone.commands.structure.misc.Messages;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.operation.RemainingArgument;
import org.zone.commands.system.arguments.simple.StringArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.region.Zone;
import org.zone.region.ZoneBuilder;
import org.zone.region.bounds.BoundedRegion;
import org.zone.region.bounds.ChildRegion;
import org.zone.region.group.key.GroupKeys;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The command for zone bound creation start of sub regions. This command activates when in a valid zone.
 * <p>Command: "/zone create bounds start 'name'"</p>
 */
public class ZoneCreateSubStartCommand implements ArgumentCommand {

    private static final ZoneArgument ZONE = new ZoneArgument("zone",
                                                              new ZoneArgument.ZoneArgumentPropertiesBuilder()
                                                                      .setLevel(GroupKeys.OWNER)
                                                                      .setOnlyMainZones(true));

    private static final RemainingArgument<String> NAME = new RemainingArgument<>(new StringArgument(
            "name"));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("create"),
                             new ExactArgument("sub"),
                             new ExactArgument("region"),
                             ZONE,
                             NAME);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Creates a sub region by walking end to end");
    }

    @Override
    public @NotNull Optional<String> getPermissionNode() {
        return Optional.of(Permissions.REGION_CREATE_BOUNDS.getPermission());
    }

    @Override
    public @NotNull CommandResult run(CommandContext context, String... args) {
        Subject subject = context.getSource();
        if (!(subject instanceof ServerPlayer player)) {
            return CommandResult.error(Messages.getPlayerCommandError());
        }
        Zone zone = context.getArgument(this, ZONE);
        String name = String.join(" ", context.getArgument(this, NAME));

        BoundedRegion region = new BoundedRegion(player.blockPosition().add(0, -1, 0),
                                                 player.blockPosition());

        ChildRegion childRegion = new ChildRegion(Collections.singleton(region));

        ZoneBuilder builder = new ZoneBuilder()
                .setParent(zone)
                .setKey(zone.getKey() + "_" + name.toLowerCase().replaceAll(" ", "_"))
                .setName(name)
                .setRegion(childRegion)
                .setContainer(ZonePlugin.getZonesPlugin().getPluginContainer());
        if (ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getZone(builder.getContainer(), builder.getKey())
                .isPresent()) {
            return CommandResult.error(Messages.getDuplicateNameError());
        }
        ZonePlugin
                .getZonesPlugin()
                .getMemoryHolder()
                .registerZoneBuilder(player.uniqueId(), builder);
        player.sendMessage(Messages.getZoneRegionBuilderEnabled());
        return CommandResult.success();
    }
}
