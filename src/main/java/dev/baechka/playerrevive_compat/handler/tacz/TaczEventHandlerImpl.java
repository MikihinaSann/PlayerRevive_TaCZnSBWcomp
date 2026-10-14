package dev.baechka.playerrevive_compat.handler.tacz;

import cn.sh1rocu.tacz.api.event.ICancellableEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Реализация обработчика событий TACZ
 * Подписывается на Fabric CALLBACK события GunShootEvent / GunFireEvent
 */
class TaczEventHandlerImpl {

    static void doRegister() {
        GunShootEvent.CALLBACK.register(event -> onGunEvent(event.getShooter(), event));
        GunFireEvent.CALLBACK.register(event -> onGunEvent(event.getShooter(), event));
    }

    private static void onGunEvent(LivingEntity shooter, ICancellableEvent event) {
        if (shooter instanceof Player player && BleedingHelper.isShootingBlocked(player)) {
            event.setCanceled(true);
        }
    }
}
