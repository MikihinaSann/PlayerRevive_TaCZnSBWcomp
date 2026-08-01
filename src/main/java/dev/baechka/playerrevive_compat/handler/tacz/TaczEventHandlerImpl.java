package dev.baechka.playerrevive_compat.handler.tacz;

import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Реализация обработчика событий TaCZ
 * Использует рефлексию для избежания прямых ссылок на классы TaCZ
 */
class TaczEventHandlerImpl {

    @SuppressWarnings("unchecked")
    static void doRegister() {
        try {
            // Загружаем классы событий через рефлексию
            Class<? extends Event> gunShootEventClass = (Class<? extends Event>) Class.forName("com.tacz.guns.api.event.common.GunShootEvent");
            Class<? extends Event> gunFireEventClass = (Class<? extends Event>) Class.forName("com.tacz.guns.api.event.common.GunFireEvent");

            IEventBus bus = MinecraftForge.EVENT_BUS;

            // Регистрируем обработчики
            registerHandler(bus, gunShootEventClass);
            registerHandler(bus, gunFireEventClass);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load TaCZ event classes", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> void registerHandler(IEventBus bus, Class<T> eventClass) {
        Consumer<T> handler = event -> handleGunEvent(event);
        bus.addListener(EventPriority.HIGHEST, false, eventClass, handler);
    }

    /**
     * Обрабатывает события стрельбы через рефлексию
     */
    private static void handleGunEvent(Event event) {
        try {
            // Получаем shooter через рефлексию
            Method getShooterMethod = event.getClass().getMethod("getShooter");
            Object shooter = getShooterMethod.invoke(event);

            if (shooter instanceof Player player) {
                if (BleedingHelper.isShootingBlocked(player)) {
                    event.setCanceled(true);
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки рефлексии
        }
    }
}
