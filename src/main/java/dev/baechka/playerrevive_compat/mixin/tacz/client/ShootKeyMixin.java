package dev.baechka.playerrevive_compat.mixin.tacz.client;

import com.tacz.guns.client.input.ShootKey;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Клиентский миксин для блокировки автоматической стрельбы TACZ
 * на каждом клиентском тике когда игрок находится в состоянии bleeding
 */
@Environment(EnvType.CLIENT)
@Mixin(value = ShootKey.class, remap = false)
public class ShootKeyMixin {

    @Shadow
    private static boolean controllerShootDown;

    /**
     * Блокируем autoShoot если игрок в состоянии bleeding
     * Это предотвращает автоматическую стрельбу в full-auto/burst режимах
     */
    @Inject(method = "autoShoot", at = @At("HEAD"), cancellable = true)
    private static void onAutoShoot(Minecraft mc, boolean isPhaseEnd, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            ci.cancel();
        }
    }

    /**
     * Блокируем ввод стрельбы с контроллера если игрок в состоянии bleeding
     */
    @Inject(method = "shootControllerTick", at = @At("HEAD"), cancellable = true)
    private static void onShootControllerTick(boolean isShootDown, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            controllerShootDown = false;
            cir.setReturnValue(false);
        }
    }
}
