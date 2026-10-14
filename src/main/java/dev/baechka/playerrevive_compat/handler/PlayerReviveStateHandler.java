package dev.baechka.playerrevive_compat.handler;

import dev.baechka.playerrevive_compat.config.ModConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Team;
import team.creative.playerrevive.PlayerReviveFabric;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.packet.HelperPacket;
import team.creative.playerrevive.server.PlayerReviveServer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerReviveStateHandler {
    // Helpers that currently have an active revive target, so a single
    // stop packet can be sent when the revive ends instead of spamming
    // HelperPacket every sync interval
    private static final Set<UUID> ACTIVE_HELPERS = ConcurrentHashMap.newKeySet();

    private PlayerReviveStateHandler() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(PlayerReviveStateHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            killIfTeamHasNoOtherOnlinePlayers(player);

            int syncInterval = ModConfig.GENERAL.stateSyncIntervalTicks.get();
            if (player.tickCount % syncInterval == 0) {
                if (PlayerReviveServer.isBleeding(player)) {
                    PlayerReviveServer.sendUpdatePacket(player);
                }
                syncHelperState(player);
            }
        }
    }

    private static void killIfTeamHasNoOtherOnlinePlayers(ServerPlayer player) {
        if (!ModConfig.GENERAL.killDownedPlayerWithoutOnlineTeammates.get()) {
            return;
        }

        Team team = player.getTeam();
        if (team == null || !PlayerReviveServer.isBleeding(player)) {
            return;
        }

        boolean hasOnlineTeammate = player.getServer().getPlayerList().getPlayers().stream()
                .anyMatch(other -> other != player && other.getTeam() == team);

        if (!hasOnlineTeammate) {
            PlayerReviveServer.kill(player);
        }
    }

    private static void syncHelperState(ServerPlayer helper) {
        UUID targetId = null;

        for (ServerPlayer target : helper.getServer().getPlayerList().getPlayers()) {
            IBleeding bleeding = PlayerReviveServer.getBleeding(target);
            if (bleeding.isBleeding() && bleeding.revivingPlayers().contains(helper)) {
                targetId = target.getUUID();
                break;
            }
        }

        if (targetId != null) {
            ACTIVE_HELPERS.add(helper.getUUID());
            PlayerReviveFabric.NETWORK.sendToClient(new HelperPacket(targetId, true), helper);
        } else if (ACTIVE_HELPERS.remove(helper.getUUID())) {
            PlayerReviveFabric.NETWORK.sendToClient(new HelperPacket(null, false), helper);
        }
    }
}
