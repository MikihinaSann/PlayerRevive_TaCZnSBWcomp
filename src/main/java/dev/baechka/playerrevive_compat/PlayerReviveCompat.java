package dev.baechka.playerrevive_compat;

import com.mojang.logging.LogUtils;
import dev.baechka.playerrevive_compat.config.ModConfig;
import dev.baechka.playerrevive_compat.handler.PlayerReviveStateHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public class PlayerReviveCompat implements ModInitializer {
    public static final String MODID = "playerrevive_compat";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("PlayerRevive TACZ Compatibility mod initializing...");

        boolean playerReviveLoaded = FabricLoader.getInstance().isModLoaded("playerrevive");
        boolean taczLoaded = FabricLoader.getInstance().isModLoaded("tacz");

        LOGGER.info("PlayerRevive loaded: {}", playerReviveLoaded);
        LOGGER.info("TACZ loaded: {}", taczLoaded);

        if (!playerReviveLoaded) {
            LOGGER.warn("PlayerRevive not found! This mod requires PlayerRevive to function.");
            return;
        }

        ModConfig.register();
        PlayerReviveStateHandler.register();

        if (taczLoaded) {
            LOGGER.info("Registering TACZ compatibility handler");
            registerTaczHandler();
            LOGGER.info("Compatibility layer active - players in bleeding state will be unable to shoot");
        } else {
            LOGGER.warn("TACZ not found! This mod has no effect.");
        }
    }

    private void registerTaczHandler() {
        try {
            Class<?> handlerClass = Class.forName("dev.baechka.playerrevive_compat.handler.tacz.TaczEventHandler");
            handlerClass.getMethod("register").invoke(null);
            LOGGER.info("TACZ event handlers registered successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Failed to load TACZ handler class", e);
        } catch (Exception e) {
            LOGGER.error("Failed to register TACZ handler", e);
        }
    }
}
