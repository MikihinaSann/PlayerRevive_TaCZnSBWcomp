package dev.baechka.playerrevive_compat.mixin.tacz.client;

import com.tacz.guns.client.input.ShootKey;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Клиентский миксин для блокировки автоматической стрельбы TaCZ
 * на каждом клиентском тике когда игрок находится в состоянии bleeding
 */
@Mixin(value = ShootKey.class, remap = false)
public class ShootKeyMixin {

    /**
     * Блокируем autoShoot если игрок в состоянии bleeding
     * Это предотвращает автоматическую стрельбу в full-auto/burst режимах
     */
    @Inject(method = "autoShoot(Lnet/minecraftforge/event/TickEvent$ClientTickEvent;)V", at = @At("HEAD"), cancellable = true)
    private static void onAutoShoot(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            ci.cancel();
        }
    }

    /**
     * Блокируем semiShoot если игрок в состоянии bleeding
     * Это предотвращает полуавтоматическую стрельбу
     */
    @Inject(method = "semiShoot(Lnet/minecraftforge/client/event/InputEvent$MouseButton$Post;)V", at = @At("HEAD"), cancellable = true)
    private static void onSemiShoot(InputEvent.MouseButton.Post event, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            ci.cancel();
        }
    }

    /**
     * Блокируем autoShootController если игрок в состоянии bleeding
     */
    @Inject(method = "autoShootController", at = @At("HEAD"), cancellable = true)
    private static void onAutoShootController(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Блокируем semiShootController если игрок в состоянии bleeding
     */
    @Inject(method = "semiShootController", at = @At("HEAD"), cancellable = true)
    private static void onSemiShootController(boolean isPress, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            cir.setReturnValue(false);
        }
    }
}
