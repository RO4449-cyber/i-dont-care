package com.ro4449.idontcare;

import com.ro4449.idontcare.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDontCareClient implements ClientModInitializer {
    public static final String MOD_ID = "idontcare";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[I Don't Care] Initializing client mod...");
        ModConfig.getInstance(); // Load config on startup
    }
}
