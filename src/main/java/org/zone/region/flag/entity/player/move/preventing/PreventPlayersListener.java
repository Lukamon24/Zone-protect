package org.zone.region.flag.entity.player.move.preventing;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.math.vector.Vector3i;
import org.zone.ZonePlugin;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;

import java.util.Optional;

public class PreventPlayersListener {
    @Listener
    public void onPlayerMove(MoveEntityEvent event, @Getter("entity") Player player) {
        if (event.originalPosition().toInt().equals(event.destinationPosition().toInt())) {
            //ignores this event if the player didn't move, but instead rotated
            return;
        }

        if (player instanceof Subject subject) {
            if (ZonePermissions.BYPASS_ENTRY.hasPermission(subject)) {
                return;
            }
        }

        Optional<Zone> opPreviousZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(event.entity().world(), event.originalPosition());

        if (opPreviousZone.isPresent()) {
            /*
             Player is already in a zone. No need to prevent from them entering unless they are
             coming from one zone to another, that is out of scope of this tutorial
             */
            return;
        }

        Optional<Zone> opNextZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(event.entity().world(), event.destinationPosition());

        if (opNextZone.isEmpty()) {
            //player is not moving into a zone, ignore this flag
            return;
        }

        Zone zone = opNextZone.get();
        Optional<PreventPlayersFlag> opFlag = zone.getFlag(FlagTypes.PREVENT_PLAYERS);
        if (opFlag.isEmpty()) {
            return;
        }

        if (opFlag.get().hasPermission(zone, player.uniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @Listener
    public void onPlayerLoginEvent(ServerSideConnectionEvent.Join joinevent) {
        ServerPlayer serverPlayer = joinevent.player();
        Optional<Zone> opZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(joinevent.player().world(), joinevent.player().position());

        if (opZone.isEmpty()) {
            return;
        }

        Optional<PreventPlayersFlag> flag = opZone.get().getFlag(FlagTypes.PREVENT_PLAYERS);
        if (flag.isEmpty()) {
            return;
        }
        if (!flag.get().hasPermission(opZone.get(), serverPlayer.uniqueId())) {
            return;
        }
        Optional<Vector3i> opPos = this.tpPos(opZone.get(), serverPlayer);
        if (opPos.isPresent()) {
            serverPlayer.setPosition(opPos.get().toDouble());
            return;
        }
        serverPlayer.setPosition(serverPlayer.world().properties().spawnPosition().toDouble());
    }

    private Optional<Vector3i> tpPos(@NotNull Zone zoneOne, @NotNull Locatable player) {
        return zoneOne.getRegion().getTrueChildren().stream().filter(boundedRegion -> {
            Vector3i position = boundedRegion.getMin().add(-1, 0, -1);
            position = player.world().highestPositionAt(position);
            final Vector3i finalPosition = position;
            //check if in any zone
            return ZonePlugin
                    .getZonesPlugin()
                    .getZoneManager()
                    .getZones()
                    .stream()
                    .noneMatch(zone -> zone.inRegion(player.world(), finalPosition.toDouble()));
        }).findAny().map(boundedRegion -> {
            Vector3i position = boundedRegion.getMin().add(-1, 0, -1);
            return player.world().highestPositionAt(position);
        });
    }

    @Listener
    public void onEntityMoveEvent(MoveEntityEvent event, @Getter("entity") Player player) {
        Optional<Zone> opPreviousZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(event.entity().world(), event.originalPosition());
        if (opPreviousZone.isEmpty()) {
            return;
        }

        Optional<Zone> opNextZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(event.entity().world(), event.destinationPosition());
        if (opNextZone.isEmpty()) {
            return;
        }

        Zone zone = opNextZone.get();
        Optional<PreventPlayersFlag> opFlag = zone.getFlag(FlagTypes.PREVENT_PLAYERS);
        if (opFlag.isEmpty()) {
            return;
        }

        if (opFlag.get().hasPermission(zone, player.uniqueId())) {
            return;
        }

        Zone previousZone = opPreviousZone.get();
        Optional<PreventPlayersFlag> opPreviousFlag = previousZone.getFlag(FlagTypes.PREVENT_PLAYERS);
        if (opPreviousFlag.isEmpty()) {
            return;
        }

        if (opPreviousFlag.get().hasPermission(previousZone, player.uniqueId())) {
            return;
        }

        Optional<Vector3i> opTpPos = this.tpPos(zone, player);

        if (opTpPos.isPresent()) {
            event.setDestinationPosition(opTpPos.get().toDouble());
            return;
        }
        event.setDestinationPosition(player.world().properties().spawnPosition().toDouble());
    }

}