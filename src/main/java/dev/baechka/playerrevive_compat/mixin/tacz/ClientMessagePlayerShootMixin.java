package dev.baechka.playerrevive_compat.mixin.tacz;

import com.tacz.guns.network.message.ClientMessagePlayerShoot;
import dev.baechka.playerrevive_compat.config.ModConfig;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Миксин для блокировки стрельбы TACZ на сервере
 * когда игрок находится в состоянии bleeding
 */
@Mixin(value = ClientMessagePlayerShoot.class, remap = false)
public class ClientMessagePlayerShootMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private static void onHandle(ClientMessagePlayerShoot message, ServerPlayNetworking.Context context, CallbackInfo ci) {
        if (ModConfig.GENERAL.canShootWhenDowned.get()) {
            return;
        }

        ServerPlayer player = context.player();

        if (player != null && BleedingHelper.isBleeding(player)) {
            ci.cancel();
        }
    }
}
