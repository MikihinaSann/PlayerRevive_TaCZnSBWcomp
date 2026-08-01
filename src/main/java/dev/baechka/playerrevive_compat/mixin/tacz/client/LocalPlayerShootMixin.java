package dev.baechka.playerrevive_compat.mixin.tacz.client;

import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Клиентский миксин для блокировки стрельбы TaCZ
 * когда игрок находится в состоянии bleeding
 */
@Mixin(value = LocalPlayerShoot.class, remap = false)
public class LocalPlayerShootMixin {

    @Shadow
    @Final
    private LocalPlayerDataHolder data;

    @Inject(method = "shoot", at = @At("HEAD"), cancellable = true)
    private void onShoot(CallbackInfoReturnable<ShootResult> cir) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            // Сбрасываем состояние записи стрельбы
            data.isShootRecorded = true;

            // Сбрасываем прицеливание
            data.clientIsAiming = false;

            cir.setReturnValue(ShootResult.UNKNOWN_FAIL);
        }
    }
}
