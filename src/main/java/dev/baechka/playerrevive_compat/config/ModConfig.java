package dev.baechka.playerrevive_compat.config;

import dev.baechka.playerrevive_compat.PlayerReviveCompat;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final GeneralConfig GENERAL;
    public static final ModConfigSpec SPEC;

    static {
        GENERAL = new GeneralConfig(BUILDER);
        SPEC = BUILDER.build();
    }

    public static void register() {
        NeoForgeConfigRegistry.INSTANCE.register(PlayerReviveCompat.MODID, Type.COMMON, SPEC);
    }

    public static class GeneralConfig {
        public final ModConfigSpec.BooleanValue canShootWhenDowned;
        public final ModConfigSpec.BooleanValue killDownedPlayerWithoutOnlineTeammates;
        public final ModConfigSpec.IntValue stateSyncIntervalTicks;

        GeneralConfig(ModConfigSpec.Builder builder) {
            builder.push("general");

            canShootWhenDowned = builder
                    .comment("When true, downed (bleeding) players can still shoot their weapons")
                    .define("canShootWhenDowned", false);

            killDownedPlayerWithoutOnlineTeammates = builder
                    .comment("When true, a downed player in a vanilla /team dies immediately if no other team member is online")
                    .define("killDownedPlayerWithoutOnlineTeammates", true);

            stateSyncIntervalTicks = builder
                    .comment("How often the server re-sends authoritative PlayerRevive states (20 ticks = 1 second)")
                    .defineInRange("stateSyncIntervalTicks", 100, 20, 1200);

            builder.pop();
        }
    }
}
