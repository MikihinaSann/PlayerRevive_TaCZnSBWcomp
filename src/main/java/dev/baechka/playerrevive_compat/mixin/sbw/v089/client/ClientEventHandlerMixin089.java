package dev.baechka.playerrevive_compat.mixin.sbw.v089.client;

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

@Mixin(value = ClientEventHandler.class, remap = false)
public class ClientEventHandlerMixin089 {

    @Inject(method = "shootClient", at = @At("HEAD"), cancellable = true)
    private void onShootClient(Player player, CallbackInfo ci) {
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            resetFireState();
            ci.cancel();
        }
    }

    @Inject(method = "handleClientShoot", at = @At("HEAD"), cancellable = true)
    private void onHandleClientShoot(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            resetFireState();
            ClientEventHandler.drawTime = 1;
            ci.cancel();
        }
    }

    @Inject(method = "playGunClientSounds", at = @At("HEAD"), cancellable = true)
    private void onPlayGunClientSounds(Player player, CallbackInfo ci) {
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleClientTick(Lnet/minecraftforge/event/TickEvent$ClientTickEvent;)V", at = @At("HEAD"))
    private void onHandleClientTick(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            resetFireState();
            ClientEventHandler.zoom = false;
            ClientEventHandler.fireSpread = 0;
            ClientEventHandler.fireCooldown = 0;

            if (ClientEventHandler.drawTime < 1) {
                ClientEventHandler.drawTime = Math.min(ClientEventHandler.drawTime + 0.1, 1);
            }
        }
    }

    private static void resetFireState() {
        ClientEventHandler.holdingFireKey = false;
        ClientEventHandler.holdingFireKeyTicks = 0;
        ClientEventHandler.fireRecoilTime = 0;
        ClientEventHandler.burstFireAmount = 0;
    }
}
