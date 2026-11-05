package com.ro4449.idontcare.mixin;

import com.mojang.serialization.Dynamic;
import com.ro4449.idontcare.WorldLoadContext;
import com.ro4449.idontcare.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServerLoader.class)
public abstract class IntegratedServerLoaderMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow
    protected abstract void start(LevelStorage.Session session, Dynamic<?> levelProperties, boolean safeMode, Runnable onCancel);

    @Unique
    private static final SystemToast.Type IDONTCARE_TOAST_TYPE = new SystemToast.Type(15000L);

    @Unique
    private void idontcare$handleBypassActions(LevelStorage.Session session) {
        boolean doBackup = ModConfig.getInstance().shouldCreateBackup(session.getDirectoryName());
        if (doBackup && !WorldLoadContext.wasBackedUp()) {
            EditWorldScreen.backupLevel(session);
            WorldLoadContext.markBackedUp();
        }
        if (ModConfig.getInstance().showToast && !WorldLoadContext.wasToastShown()) {
            WorldLoadContext.markToastShown();
            this.client.getToastManager().add(
                new SystemToast(
                    IDONTCARE_TOAST_TYPE,
                    Text.translatable("idontcare.toast.title"),
                    Text.translatable(doBackup ? "idontcare.toast.bypassed_with_backup" : "idontcare.toast.bypassed", session.getDirectoryName())
                )
            );
        }
    }

    @Inject(method = "start(Ljava/lang/String;Ljava/lang/Runnable;)V", at = @At("HEAD"))
    private void idontcare$onStartByName(String name, Runnable onCancel, CallbackInfo ci) {
        WorldLoadContext.setCurrentWorld(name);
    }

    @Inject(
        method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/storage/LevelSummary;Lcom/mojang/serialization/Dynamic;Ljava/lang/Runnable;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void idontcare$bypassConversionWarning(
        LevelStorage.Session session,
        LevelSummary summary,
        Dynamic<?> levelProperties,
        Runnable onCancel,
        CallbackInfo ci
    ) {
        WorldLoadContext.setCurrentWorld(session.getDirectoryName());
        if (!summary.isVersionAvailable()) {
            return;
        }

        LevelSummary.ConversionWarning conversionWarning = summary.getConversionWarning();
        if (conversionWarning.promptsBackup() && ModConfig.getInstance().isWorldBypassed(session.getDirectoryName())) {
            this.idontcare$handleBypassActions(session);
            this.start(session, levelProperties, false, onCancel);
            ci.cancel();
        }
    }

    @Inject(
        method = "showBackupPromptScreen",
        at = @At("HEAD"),
        cancellable = true
    )
    private void idontcare$bypassBackupPrompt(
        LevelStorage.Session session,
        boolean customized,
        Runnable callback,
        Runnable onCancel,
        CallbackInfo ci
    ) {
        WorldLoadContext.setCurrentWorld(session.getDirectoryName());
        if (ModConfig.getInstance().isWorldBypassed(session.getDirectoryName())) {
            this.idontcare$handleBypassActions(session);
            callback.run();
            ci.cancel();
        }
    }
}
