package org.zone.region.flag;

import org.jetbrains.annotations.NotNull;
import org.zone.region.flag.interact.block.destroy.BlockBreakFlagType;
import org.zone.region.flag.interact.block.place.BlockPlaceFlagType;
import org.zone.region.flag.interact.door.DoorInteractionFlagType;
import org.zone.region.flag.meta.eco.EcoFlagType;
import org.zone.region.flag.meta.edit.EditingFlagType;
import org.zone.region.flag.meta.member.MembersFlagType;
import org.zone.region.flag.meta.tag.TagsFlagType;
import org.zone.region.flag.move.monster.PreventMonsterFlagType;
import org.zone.region.flag.move.player.greetings.GreetingsFlagType;
import org.zone.region.flag.move.player.leaving.LeavingFlagType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * All known default flag types found within the zones plugin
 */
public final class FlagTypes {

    public static final PreventMonsterFlagType PREVENT_MONSTER = new PreventMonsterFlagType();
    public static final MembersFlagType MEMBERS = new MembersFlagType();
    public static final DoorInteractionFlagType DOOR_INTERACTION = new DoorInteractionFlagType();
    public static final BlockBreakFlagType BLOCK_BREAK = new BlockBreakFlagType();
    public static final EcoFlagType ECO = new EcoFlagType();
    public static final EditingFlagType EDITING = new EditingFlagType();
    public static final BlockPlaceFlagType BLOCK_PLACE = new BlockPlaceFlagType();
    public static final GreetingsFlagType GREETINGS = new GreetingsFlagType();
    public static final LeavingFlagType LEAVING = new LeavingFlagType();
    public static final TagsFlagType TAGS = new TagsFlagType();
    
    private FlagTypes() {
        throw new RuntimeException("Should not init");
    }

    public static @NotNull Collection<FlagType<?>> getVanillaFlags() {
        return Arrays
                .stream(FlagTypes.class.getDeclaredFields())
                .parallel()
                .filter(field -> FlagType.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (FlagType<?>) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        //noinspection ReturnOfNull
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
