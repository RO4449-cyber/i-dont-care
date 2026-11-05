package com.ro4449.idontcare.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("idontcare");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig INSTANCE;

    public boolean globalBypass = false;
    public BypassAction defaultBypassAction = BypassAction.SKIP;
    public boolean showToast = true;
    public Map<String, WorldPreference> rememberedWorlds = new HashMap<>();

    public static Path getConfigPath() {
        try {
            if (FabricLoader.getInstance() != null) {
                return FabricLoader.getInstance().getConfigDir().resolve("idontcare.json");
            }
        } catch (Throwable ignored) {
        }
        return Path.of("build", "test-config", "idontcare.json");
    }

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static ModConfig load() {
        Path path = getConfigPath();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    if (config.rememberedWorlds == null) {
                        config.rememberedWorlds = new HashMap<>();
                    }
                    if (config.defaultBypassAction == null) {
                        config.defaultBypassAction = BypassAction.SKIP;
                    }
                    return config;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load idontcare.json config, falling back to defaults", e);
            }
        }
        ModConfig config = new ModConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Path path = getConfigPath();
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save idontcare.json config", e);
        }
    }

    public boolean isWorldBypassed(String worldDirectory) {
        if (worldDirectory == null) {
            return false;
        }
        if (this.globalBypass) {
            return true;
        }
        WorldPreference pref = this.rememberedWorlds.get(worldDirectory);
        return pref != null && pref.bypassed;
    }

    public boolean shouldCreateBackup(String worldDirectory) {
        if (worldDirectory != null) {
            WorldPreference pref = this.rememberedWorlds.get(worldDirectory);
            if (pref != null && pref.bypassed) {
                return pref.action == BypassAction.BACKUP;
            }
        }
        return this.defaultBypassAction == BypassAction.BACKUP;
    }

    public void setWorldBypassed(String worldDirectory, boolean bypassed) {
        if (worldDirectory == null) return;
        WorldPreference pref = this.rememberedWorlds.computeIfAbsent(worldDirectory, k -> new WorldPreference());
        pref.bypassed = bypassed;
    }

    public void setWorldBypassed(String worldDirectory, boolean bypassed, BypassAction action) {
        if (worldDirectory == null) return;
        WorldPreference pref = this.rememberedWorlds.computeIfAbsent(worldDirectory, k -> new WorldPreference());
        pref.bypassed = bypassed;
        pref.action = action;
    }

    public void resetAllWorlds() {
        this.rememberedWorlds.clear();
    }

    public void resetAllToDefaults() {
        this.globalBypass = false;
        this.defaultBypassAction = BypassAction.SKIP;
        this.showToast = true;
        this.rememberedWorlds.clear();
    }
}
