package dev.baechka.playerrevive_compat.mixin.sbw.client;

import com.atsuishio.superbwarfare.event.ClientEventHandler;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Клиентский миксин для блокировки стрельбы SuperbWarfare
 * когда игрок находится в состоянии bleeding
 */
@Mixin(value = ClientEventHandler.class, remap = false)
public class ClientEventHandlerMixin {

    /**
     * Блокируем shootClient - основной метод который вызывает анимацию и звуки выстрела
     * Это ключевой метод для блокировки!
     */
    @Inject(method = "shootClient", at = @At("HEAD"), cancellable = true)
    private static void onShootClient(Player player, CallbackInfo ci) {
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            // Сбрасываем переменные связанные со стрельбой
            ClientEventHandler.holdingFireKey = false;
            ClientEventHandler.holdingFireKeyTicks = 0;
            ClientEventHandler.fireRecoilTime = 0;
            ClientEventHandler.burstFireAmount = 0;

            ci.cancel();
        }
    }

    /**
     * Блокируем handleClientShoot если игрок в состоянии bleeding
     * Это предотвращает отправку пакета стрельбы на сервер
     */
    @Inject(method = "handleClientShoot", at = @At("HEAD"), cancellable = true)
    private static void onHandleClientShoot(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            // Сбрасываем переменные связанные со стрельбой
            ClientEventHandler.holdingFireKey = false;
            ClientEventHandler.holdingFireKeyTicks = 0;
            ClientEventHandler.fireRecoilTime = 0;
            ClientEventHandler.burstFireAmount = 0;

            // Устанавливаем drawTime = 1 для анимации убирания оружия
            ClientEventHandler.drawTime = 1;

            ci.cancel();
        }
    }

    /**
     * Блокируем playGunClientSounds - звуки выстрела
     */
    @Inject(method = "playGunClientSounds", at = @At("HEAD"), cancellable = true)
    private static void onPlayGunClientSounds(Player player, CallbackInfo ci) {
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            ci.cancel();
        }
    }

    /**
     * Сбрасываем holdingFireKey на каждом тике если игрок bleeding
     * Это предотвращает автоматическую стрельбу
     */
    @Inject(method = "handleClientTick(Lnet/minecraftforge/event/TickEvent$ClientTickEvent;)V", at = @At("HEAD"))
    private static void onHandleClientTick(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            // Принудительно сбрасываем все флаги стрельбы
            ClientEventHandler.holdingFireKey = false;
            ClientEventHandler.holdingFireKeyTicks = 0;
            ClientEventHandler.burstFireAmount = 0;
            ClientEventHandler.zoom = false;
            ClientEventHandler.fireSpread = 0;
            ClientEventHandler.fireCooldown = 0;

            // Анимация убранного оружия
            if (ClientEventHandler.drawTime < 1) {
                ClientEventHandler.drawTime = Math.min(ClientEventHandler.drawTime + 0.1, 1);
            }
        }
    }
}
