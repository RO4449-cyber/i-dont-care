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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditWorldScreen.class)
public abstract class EditWorldScreenMixin extends Screen {
    @Shadow @Final
    private DirectionalLayoutWidget layout;

    @Shadow @Final
    private LevelStorage.Session storageSession;

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
        boolean bypassed = ModConfig.getInstance().isWorldBypassed(worldId);

        CheckboxWidget checkbox = CheckboxWidget.builder(
            Text.translatable("idontcare.checkbox.dont_show_edit"),
            client.textRenderer
        )
        .checked(bypassed)
        .maxWidth(200)
        .callback((cb, checked) -> {
            ModConfig.getInstance().setWorldBypassed(worldId, checked);
            ModConfig.getInstance().save();
        })
        .build();

        this.layout.add(checkbox);
    }
}
