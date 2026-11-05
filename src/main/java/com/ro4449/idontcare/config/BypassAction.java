package com.ro4449.idontcare.config;

import net.minecraft.text.Text;

public enum BypassAction {
    SKIP("idontcare.config.action.skip"),
    BACKUP("idontcare.config.action.backup");

    private final String translationKey;

    BypassAction(String translationKey) {
        this.translationKey = translationKey;
    }

    public Text getText() {
        return Text.translatable(this.translationKey);
    }
}
