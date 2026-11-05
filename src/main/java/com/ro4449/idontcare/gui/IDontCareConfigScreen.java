package com.ro4449.idontcare.gui;

import com.ro4449.idontcare.config.BypassAction;
import com.ro4449.idontcare.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class IDontCareConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;
    private ButtonWidget resetWorldsButton;

    public IDontCareConfigScreen(Screen parent) {
        super(Text.translatable("idontcare.config.title"));
        this.parent = parent;
        this.config = ModConfig.getInstance();
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = 45;
        int spacing = 26;

        // 1. Global Bypass Toggle
        this.addDrawableChild(
            CyclingButtonWidget.onOffBuilder(this.config.globalBypass)
                .build(
                    centerX - 125,
                    startY,
                    250,
                    20,
                    Text.translatable("idontcare.config.global_bypass"),
                    (button, value) -> {
                        this.config.globalBypass = value;
                        this.config.save();
                    }
                )
        );

        // 2. Default Bypass Action
        this.addDrawableChild(
            CyclingButtonWidget.builder(BypassAction::getText, this.config.defaultBypassAction)
                .values(BypassAction.values())
                .build(
                    centerX - 125,
                    startY + spacing,
                    250,
                    20,
                    Text.translatable("idontcare.config.default_action"),
                    (button, value) -> {
                        this.config.defaultBypassAction = value;
                        this.config.save();
                    }
                )
        );

        // 3. Show Toast Toggle
        this.addDrawableChild(
            CyclingButtonWidget.onOffBuilder(this.config.showToast)
                .build(
                    centerX - 125,
                    startY + spacing * 2,
                    250,
                    20,
                    Text.translatable("idontcare.config.show_toast"),
                    (button, value) -> {
                        this.config.showToast = value;
                        this.config.save();
                    }
                )
        );

        // 4. Reset Remembered Worlds
        this.resetWorldsButton = ButtonWidget.builder(
            this.getResetButtonText(),
            button -> {
                this.config.resetAllWorlds();
                this.config.save();
                button.setMessage(this.getResetButtonText());
                if (this.client != null) {
                    this.client.getToastManager().add(
                        new SystemToast(
                            SystemToast.Type.WORLD_BACKUP,
                            Text.translatable("idontcare.toast.title"),
                            Text.translatable("idontcare.config.reset_worlds.success")
                        )
                    );
                }
            }
        )
        .dimensions(centerX - 125, startY + spacing * 3, 250, 20)
        .build();
        this.addDrawableChild(this.resetWorldsButton);

        // 5. Done Button
        this.addDrawableChild(
            ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .dimensions(centerX - 100, Math.min(this.height - 35, startY + spacing * 5), 200, 20)
                .build()
        );
    }

    private Text getResetButtonText() {
        int count = this.config.rememberedWorlds.size();
        return Text.translatable("idontcare.config.reset_worlds").append(" (" + count + ")");
    }

    @Override
    public void close() {
        this.config.save();
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, Colors.WHITE);
    }
}
