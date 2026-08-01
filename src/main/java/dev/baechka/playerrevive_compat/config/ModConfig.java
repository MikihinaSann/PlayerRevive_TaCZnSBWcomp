package dev.baechka.playerrevive_compat.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static final GeneralConfig GENERAL;
    public static final ForgeConfigSpec SPEC;
    
    static {
        GENERAL = new GeneralConfig(BUILDER);
        SPEC = BUILDER.build();
    }
    
    public static class GeneralConfig {
        public final ForgeConfigSpec.BooleanValue canShootWhenDowned;
        public final ForgeConfigSpec.BooleanValue killDownedPlayerWithoutOnlineTeammates;
        public final ForgeConfigSpec.IntValue stateSyncIntervalTicks;

        GeneralConfig(ForgeConfigSpec.Builder builder) {
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
