package dev.baechka.playerrevive_compat.mixin.sbw.v089;

import com.atsuishio.superbwarfare.network.message.send.ShootMessage;
import dev.baechka.playerrevive_compat.util.BleedingHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = ShootMessage.class, remap = false)
public class ShootMessageMixin089 {

    @Inject(method = "handler", at = @At("HEAD"), cancellable = true)
    private void onHandler(Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();

        if (player != null && BleedingHelper.isShootingBlocked(player)) {
            context.setPacketHandled(true);
            ci.cancel();
        }
    }
}
