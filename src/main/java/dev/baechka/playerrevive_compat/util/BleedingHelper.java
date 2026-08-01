package dev.baechka.playerrevive_compat.util;

import dev.baechka.playerrevive_compat.config.ModConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import team.creative.playerrevive.server.PlayerReviveServer;

/**
 * Утилитарный класс для проверки состояния bleeding игрока
 */
public class BleedingHelper {

    private static final boolean PLAYER_REVIVE_LOADED = ModList.get().isLoaded("playerrevive");
    private static final String BLEEDING_TAG = "playerrevive:bleeding";

    /**
     * Проверяет, находится ли игрок в состоянии bleeding
     */
    public static boolean isBleeding(Player player) {
        if (!PLAYER_REVIVE_LOADED || player == null) {
            return false;
        }
        try {
            // Основной источник – capability PlayerRevive
            return PlayerReviveServer.isBleeding(player);
        } catch (Throwable ignored) {
            // падаем к NBT ниже
        }
        // Fallback: тег NBT, который PlayerRevive дублирует на игрока
        return player.getPersistentData().getBoolean(BLEEDING_TAG);
    }

    public static boolean isPlayerReviveLoaded() {
        return PLAYER_REVIVE_LOADED;
    }

    public static boolean isShootingBlocked(Player player) {
        return !ModConfig.GENERAL.canShootWhenDowned.get() && isBleeding(player);
    }
}
