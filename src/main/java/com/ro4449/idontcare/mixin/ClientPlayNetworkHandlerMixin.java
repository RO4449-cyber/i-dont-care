package com.ro4449.idontcare.mixin;

import com.ro4449.idontcare.WorldLoadContext;
import com.ro4449.idontcare.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void idontcare$showBypassNotification(GameJoinS2CPacket packet, CallbackInfo ci) {
        WorldLoadContext.PendingBypassNotification notif = WorldLoadContext.pollPendingNotification();
        if (notif != null && ModConfig.getInstance().showToast) {
            Text title = Text.translatable("idontcare.toast.title");
            Text message = Text.translatable(
                notif.backupCreated() ? "idontcare.toast.bypassed_with_backup" : "idontcare.toast.bypassed",
                notif.worldName()
            );

            // Pop up the native vanilla toast in the top right
            this.client.getToastManager().add(
                new SystemToast(SystemToast.Type.WORLD_BACKUP, title, message)
            );

            // Also show subtle in-game action bar text right above the hotbar so it cannot be missed
            if (this.client.player != null) {
                this.client.player.sendMessage(message, true);
            }
        }
    }
}
