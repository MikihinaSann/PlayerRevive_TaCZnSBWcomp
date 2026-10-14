package dev.baechka.playerrevive_compat.mixin.tacz;

import com.tacz.guns.util.HitboxHelper;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Миксин для исправления хитбокса игрока в состоянии bleeding
 * TACZ использует кешированный хитбокс для компенсации латентности,
 * но не учитывает что игрок может лежать (Pose.SWIMMING от PlayerRevive)
 */
@Mixin(value = HitboxHelper.class, remap = false)
public class HitboxHelperMixin {

    /**
     * Исправляем получение хитбокса для bleeding игроков
     * Принудительно возвращаем хитбокс для позы SWIMMING если игрок bleeding
     */
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private static void fixBleedingHitbox(Player entity, int ping, CallbackInfoReturnable<AABB> cir) {
        if (BleedingHelper.isBleeding(entity)) {
            // Игрок в состоянии bleeding - должен использовать хитбокс лежащего игрока
            // PlayerRevive использует Pose.SWIMMING для лежачей позы
            AABB currentBox = cir.getReturnValue();

            // Получаем размеры для позы SWIMMING
            EntityDimensions swimmingDimensions = entity.getDimensions(Pose.SWIMMING);

            // Пересчитываем AABB на основе правильных размеров
            Vec3 pos = new Vec3(
                (currentBox.minX + currentBox.maxX) / 2.0,
                currentBox.minY,
                (currentBox.minZ + currentBox.maxZ) / 2.0
            );

            AABB fixedBox = swimmingDimensions.makeBoundingBox(pos);
            cir.setReturnValue(fixedBox);
        }
    }

    /**
     * Также исправляем getFixedBoundingBox для случаев когда latency fix отключен
     */
    @Inject(method = "getFixedBoundingBox", at = @At("RETURN"), cancellable = true)
    private static void fixBleedingFixedHitbox(Entity entity, Entity owner, CallbackInfoReturnable<AABB> cir) {
        if (entity instanceof Player player && BleedingHelper.isBleeding(player)) {
            AABB currentBox = cir.getReturnValue();

            // Получаем размеры для позы SWIMMING
            EntityDimensions swimmingDimensions = player.getDimensions(Pose.SWIMMING);

            // Пересчитываем AABB на основе правильных размеров
            Vec3 pos = new Vec3(
                (currentBox.minX + currentBox.maxX) / 2.0,
                currentBox.minY,
                (currentBox.minZ + currentBox.maxZ) / 2.0
            );

            AABB fixedBox = swimmingDimensions.makeBoundingBox(pos);
            cir.setReturnValue(fixedBox);
        }
    }
}
