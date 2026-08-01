package dev.baechka.playerrevive_compat.client;

import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Клиентский обработчик ввода: гасит клики/состояние стрельбы при bleeding.
 */
@Mod.EventBusSubscriber(modid = "playerrevive_compat", value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            resetSbwFireState();
        }
    }

    /**
     * Жёстко сбрасываем основные клиентские стейты стрельбы SBW через reflection.
     */
    private static void resetSbwFireState() {
        try {
            Class<?> handler = Class.forName("com.atsuishio.superbwarfare.event.ClientEventHandler");

            setStatic(handler, "holdingFireKey", false);
            setStatic(handler, "holdingFireKeyTicks", 0);
            setStatic(handler, "burstFireAmount", 0);
        } catch (ClassNotFoundException ignored) {
            // Мод не установлен — ничего не делаем
        } catch (Exception ignored) {
            // Безопасный best-effort сброс
        }
    }

    private static void setStatic(Class<?> owner, String fieldName, Object value) throws Exception {
        var field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
