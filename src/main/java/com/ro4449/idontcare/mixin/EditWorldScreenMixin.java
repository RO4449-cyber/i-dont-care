package com.ro4449.idontcare.mixin;

import com.ro4449.idontcare.config.ModConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditWorldScreen.class)
public abstract class EditWorldScreenMixin extends Screen {
    @Shadow @Final
    private DirectionalLayoutWidget layout;

    @Shadow @Final
    private LevelStorage.Session storageSession;

    @Unique
    private boolean idontcare$pendingBypassState;

    protected EditWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/world/level/storage/LevelStorage$Session;Ljava/lang/String;Lit/unimi/dsi/fastutil/booleans/BooleanConsumer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;forEachChild(Ljava/util/function/Consumer;)V"
        )
    )
    private void idontcare$addWorldCheckbox(
        MinecraftClient client,
        LevelStorage.Session session,
        String levelName,
        BooleanConsumer callback,
        CallbackInfo ci
    ) {
        String worldId = this.storageSession.getDirectoryName();
        this.idontcare$pendingBypassState = ModConfig.getInstance().isWorldBypassed(worldId);

        Text text = Text.translatable("idontcare.checkbox.dont_show_edit");
        // Calculate the exact visual width of the checkbox icon + padding + label
        int contentWidth = CheckboxWidget.getCheckboxSize(client.textRenderer) + 4 + client.textRenderer.getWidth(text);

        CheckboxWidget checkbox = CheckboxWidget.builder(text, client.textRenderer)
            .checked(this.idontcare$pendingBypassState)
            .maxWidth(contentWidth)
            .callback((cb, checked) -> {
                // Only update the pending state; do not write to disk yet!
                this.idontcare$pendingBypassState = checked;
            })
            .build();

        // Center the checkbox horizontally within the layout column
        this.layout.add(checkbox, positioner -> positioner.alignHorizontalCenter());
    }

    @Inject(method = "commit", at = @At("HEAD"))
    private void idontcare$onSave(String levelName, CallbackInfo ci) {
        // Only save changes to config when the user explicitly clicks Save
        String worldId = this.storageSession.getDirectoryName();
        ModConfig.getInstance().setWorldBypassed(worldId, this.idontcare$pendingBypassState);
        ModConfig.getInstance().save();
    }
}
