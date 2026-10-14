package dev.baechka.playerrevive_compat.util;

import dev.baechka.playerrevive_compat.config.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import team.creative.playerrevive.server.PlayerReviveServer;

/**
 * Утилитарный класс для проверки состояния bleeding игрока
 */
public class BleedingHelper {

    private static final boolean PLAYER_REVIVE_LOADED = FabricLoader.getInstance().isModLoaded("playerrevive");

    /**
     * Проверяет, находится ли игрок в состоянии bleeding
     */
    public static boolean isBleeding(Player player) {
        if (!PLAYER_REVIVE_LOADED || player == null) {
            return false;
        }
        try {
            // Основной источник – состояние PlayerRevive
            return PlayerReviveServer.isBleeding(player);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isPlayerReviveLoaded() {
        return PLAYER_REVIVE_LOADED;
    }

    public static boolean isShootingBlocked(Player player) {
        return !ModConfig.GENERAL.canShootWhenDowned.get() && isBleeding(player);
    }
}
