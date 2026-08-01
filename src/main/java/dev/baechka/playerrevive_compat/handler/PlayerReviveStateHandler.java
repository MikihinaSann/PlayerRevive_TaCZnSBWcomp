package dev.baechka.playerrevive_compat.handler;

import dev.baechka.playerrevive_compat.PlayerReviveTaCZnSBW;
import dev.baechka.playerrevive_compat.config.ModConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.packet.HelperPacket;
import team.creative.playerrevive.server.PlayerReviveServer;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = PlayerReviveTaCZnSBW.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerReviveStateHandler {
    private PlayerReviveStateHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        killIfTeamHasNoOtherOnlinePlayers(player);

        int syncInterval = ModConfig.GENERAL.stateSyncIntervalTicks.get();
        if (player.tickCount % syncInterval == 0) {
            PlayerReviveServer.sendUpdatePacket(player);
            syncHelperState(player);
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

        boolean hasOnlineTeammate = player.server.getPlayerList().getPlayers().stream()
                .anyMatch(other -> other != player && other.getTeam() == team);

        if (!hasOnlineTeammate) {
            PlayerReviveServer.kill(player);
        }
    }

    private static void syncHelperState(ServerPlayer helper) {
        UUID targetId = null;

        for (ServerPlayer target : helper.server.getPlayerList().getPlayers()) {
            IBleeding bleeding = PlayerReviveServer.getBleeding(target);
            if (bleeding.isBleeding() && bleeding.revivingPlayers().contains(helper)) {
                targetId = target.getUUID();
                break;
            }
        }

        PlayerRevive.NETWORK.sendToClient(new HelperPacket(targetId, targetId != null), helper);
    }
}
