package com.ro4449.idontcare.compat.modmenu;

import com.ro4449.idontcare.gui.IDontCareConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return IDontCareConfigScreen::new;
    }
}
