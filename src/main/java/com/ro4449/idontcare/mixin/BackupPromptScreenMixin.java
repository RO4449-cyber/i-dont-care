package com.ro4449.idontcare.mixin;

import com.ro4449.idontcare.WorldLoadContext;
import com.ro4449.idontcare.config.BypassAction;
import com.ro4449.idontcare.config.ModConfig;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.BackupPromptScreen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackupPromptScreen.class)
public abstract class BackupPromptScreenMixin extends Screen {
    @Shadow @Mutable
    protected BackupPromptScreen.Callback callback;

    @Shadow @Final
    private boolean showEraseCacheCheckbox;

    @Shadow
    private MultilineText wrappedText;

    @Unique
    private CheckboxWidget idontcare$checkbox;

    @Unique
    private String idontcare$worldDirectory;

    protected BackupPromptScreenMixin(Text title) {
        super(title);
    }

    @Inject(
        method = "<init>(Ljava/lang/Runnable;Lnet/minecraft/client/gui/screen/world/BackupPromptScreen$Callback;Lnet/minecraft/text/Text;Lnet/minecraft/text/Text;Lnet/minecraft/text/Text;Z)V",
        at = @At("TAIL")
    )
    private void idontcare$wrapCallback(
        Runnable onCancel,
        BackupPromptScreen.Callback callback,
        Text title,
        Text subtitle,
        Text firstButtonText,
        boolean showEraseCacheCheckbox,
        CallbackInfo ci
    ) {
        this.idontcare$worldDirectory = WorldLoadContext.getCurrentWorld();
        if (this.idontcare$worldDirectory != null) {
            BackupPromptScreen.Callback original = this.callback;
            this.callback = (backup, eraseCache) -> {
                if (this.idontcare$checkbox != null && this.idontcare$checkbox.isChecked()) {
                    ModConfig.getInstance().setWorldBypassed(
                        this.idontcare$worldDirectory,
                        true,
                        backup ? BypassAction.BACKUP : BypassAction.SKIP
                    );
                    ModConfig.getInstance().save();
                }
                original.proceed(backup, eraseCache);
            };
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void idontcare$addCheckboxWidget(CallbackInfo ci) {
        if (this.idontcare$worldDirectory != null) {
            int i = (this.wrappedText.getLineCount() + 1) * 9;
            int y = this.showEraseCacheCheckbox ? (150 + i) : (76 + i);
            Text text = Text.translatable("idontcare.checkbox.dont_show");
            int width = CheckboxWidget.getCheckboxSize(this.textRenderer) + 4 + this.textRenderer.getWidth(text);
            int x = this.width / 2 - width / 2;

            this.idontcare$checkbox = CheckboxWidget.builder(text, this.textRenderer)
                .pos(x, y)
                .maxWidth(width)
                .checked(false)
                .build();

            this.addDrawableChild(this.idontcare$checkbox);
        }
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void idontcare$onRemoved(CallbackInfo ci) {
        WorldLoadContext.clear();
    }
}
