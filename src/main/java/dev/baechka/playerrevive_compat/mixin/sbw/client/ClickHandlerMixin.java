package dev.baechka.playerrevive_compat.mixin.sbw.client;

import com.atsuishio.superbwarfare.client.ClickHandler;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Клиентский миксин для блокировки обработки нажатия кнопки стрельбы SuperbWarfare
 * когда игрок находится в состоянии bleeding
 */
@Mixin(value = ClickHandler.class, remap = false)
public class ClickHandlerMixin {

    /**
     * Блокируем handleWeaponFirePress если игрок в состоянии bleeding
     * Это предотвращает установку holdingFireKey = true
     */
    @Inject(method = "handleWeaponFirePress", at = @At("HEAD"), cancellable = true)
    private static void onHandleWeaponFirePress(Player player, ItemStack stack, CallbackInfo ci) {
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            // Сбрасываем все флаги стрельбы
            ClientEventHandler.holdingFireKey = false;
            ClientEventHandler.holdingFireKeyTicks = 0;
            ClientEventHandler.burstFireAmount = 0;
            ClientEventHandler.zoom = false;

            ci.cancel();
        }
    }

    /**
     * Перехватываем onButtonPressed для блокировки обработки SuperbWarfare
     * Не отменяем сам event - это позволяет PlayerRevive обработать ЛКМ для ускорения смерти
     */
    @Inject(method = "onButtonPressed", at = @At("HEAD"), cancellable = true)
    private static void onButtonPressedHead(InputEvent.MouseButton.Pre event, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            // Блокируем левый клик мыши (стрельба) - обычно button 0
            if (event.getButton() == 0) {
                ci.cancel();
            }
        }
    }

    /**
     * Перехватываем onKeyPressed для сброса флагов стрельбы при нажатии клавиатуры
     */
    @Inject(method = "onKeyPressed", at = @At("HEAD"))
    private static void onKeyPressedHead(InputEvent.Key event, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            // Сбрасываем флаги при любом нажатии клавиш
            ClientEventHandler.holdingFireKey = false;
            ClientEventHandler.holdingFireKeyTicks = 0;
            ClientEventHandler.burstFireAmount = 0;
        }
    }
}
