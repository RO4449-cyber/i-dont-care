package com.ro4449.idontcare.gui;

import com.ro4449.idontcare.config.BypassAction;
import com.ro4449.idontcare.config.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;

import java.nio.file.Path;

public class IDontCareConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;
    private ButtonWidget resetWorldsButton;
    private CyclingButtonWidget<Boolean> globalBypassButton;
    private CyclingButtonWidget<BypassAction> defaultActionButton;
    private CyclingButtonWidget<Boolean> showToastButton;

    public IDontCareConfigScreen(Screen parent) {
        super(Text.translatable("idontcare.config.title"));
        this.parent = parent;
        this.config = ModConfig.getInstance();
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = 38;
        int spacing = 24;

        // 1. Global Bypass Toggle
        this.globalBypassButton = this.addDrawableChild(
            CyclingButtonWidget.onOffBuilder(this.config.globalBypass)
                .build(
                    centerX - 130,
                    startY,
                    260,
                    20,
                    Text.translatable("idontcare.config.global_bypass"),
                    (button, value) -> {
                        this.config.globalBypass = value;
                        this.config.save();
                    }
                )
        );

        // 2. Default Bypass Action
        this.defaultActionButton = this.addDrawableChild(
            CyclingButtonWidget.builder(BypassAction::getText, this.config.defaultBypassAction)
                .values(BypassAction.values())
                .build(
                    centerX - 130,
                    startY + spacing,
                    260,
                    20,
                    Text.translatable("idontcare.config.default_action"),
                    (button, value) -> {
                        this.config.defaultBypassAction = value;
                        this.config.save();
                    }
                )
        );

        // 3. Show Toast Toggle
        this.showToastButton = this.addDrawableChild(
            CyclingButtonWidget.onOffBuilder(this.config.showToast)
                .build(
                    centerX - 130,
                    startY + spacing * 2,
                    260,
                    20,
                    Text.translatable("idontcare.config.show_toast"),
                    (button, value) -> {
                        this.config.showToast = value;
                        this.config.save();
                    }
                )
        );

        // 4. Reset Remembered Worlds (with "Are you sure?" confirmation)
        this.resetWorldsButton = ButtonWidget.builder(
            this.getResetButtonText(),
            button -> {
                if (this.client != null) {
                    this.client.setScreen(
                        new ConfirmScreen(
                            confirmed -> {
                                if (confirmed) {
                                    this.config.resetAllWorlds();
                                    this.config.save();
                                    this.resetWorldsButton.setMessage(this.getResetButtonText());
                                    if (this.client != null) {
                                        this.client.getToastManager().add(
                                            new SystemToast(
                                                SystemToast.Type.WORLD_BACKUP,
                                                Text.translatable("idontcare.toast.title"),
                                                Text.translatable("idontcare.config.confirm_reset")
                                            )
                                        );
                                    }
                                }
                                this.client.setScreen(this);
                            },
                            Text.translatable("idontcare.config.reset_worlds.title"),
                            Text.translatable("idontcare.config.reset_worlds.message"),
                            Text.translatable("idontcare.config.confirm_reset"),
                            ScreenTexts.CANCEL
                        )
                    );
                }
            }
        )
        .dimensions(centerX - 130, startY + spacing * 3, 260, 20)
        .build();
        this.addDrawableChild(this.resetWorldsButton);

        // 5. Additional Button: Reset All to Defaults (with "Are you sure?" confirmation)
        this.addDrawableChild(
            ButtonWidget.builder(
                Text.translatable("idontcare.config.reset_defaults"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(
                            new ConfirmScreen(
                                confirmed -> {
                                    if (confirmed) {
                                        this.config.resetAllToDefaults();
                                        this.config.save();
                                        // Refresh all button values on screen
                                        this.globalBypassButton.setValue(this.config.globalBypass);
                                        this.defaultActionButton.setValue(this.config.defaultBypassAction);
                                        this.showToastButton.setValue(this.config.showToast);
                                        this.resetWorldsButton.setMessage(this.getResetButtonText());
                                    }
                                    this.client.setScreen(this);
                                },
                                Text.translatable("idontcare.config.reset_defaults.title"),
                                Text.translatable("idontcare.config.reset_defaults.message"),
                                Text.translatable("idontcare.config.confirm_reset_defaults"),
                                ScreenTexts.CANCEL
                            )
                        );
                    }
                }
            )
            .dimensions(centerX - 130, startY + spacing * 4, 128, 20)
            .build()
        );

        // 6. Additional Button: Open Config Folder
        this.addDrawableChild(
            ButtonWidget.builder(
                Text.translatable("idontcare.config.open_folder"),
                button -> {
                    Path configDir = FabricLoader.getInstance().getConfigDir();
                    Util.getOperatingSystem().open(configDir);
                }
            )
            .dimensions(centerX + 2, startY + spacing * 4, 128, 20)
            .build()
        );

        // 7. Done Button
        this.addDrawableChild(
            ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .dimensions(centerX - 100, Math.min(this.height - 30, startY + spacing * 6), 200, 20)
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
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, Colors.WHITE);
    }
}
