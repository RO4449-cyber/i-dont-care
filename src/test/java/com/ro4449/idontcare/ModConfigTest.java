package com.ro4449.idontcare;

import com.ro4449.idontcare.config.BypassAction;
import com.ro4449.idontcare.config.ModConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModConfigTest {

    @Test
    public void testDefaultConfig() {
        ModConfig config = new ModConfig();
        Assertions.assertFalse(config.globalBypass);
        Assertions.assertEquals(BypassAction.SKIP, config.defaultBypassAction);
        Assertions.assertTrue(config.showToast);
        Assertions.assertFalse(config.isWorldBypassed("test_world"));
        Assertions.assertFalse(config.shouldCreateBackup("test_world"));
    }

    @Test
    public void testPerWorldBypass() {
        ModConfig config = new ModConfig();
        
        config.setWorldBypassed("world_alpha", true, BypassAction.SKIP);
        Assertions.assertTrue(config.isWorldBypassed("world_alpha"));
        Assertions.assertFalse(config.shouldCreateBackup("world_alpha"));

        config.setWorldBypassed("world_beta", true, BypassAction.BACKUP);
        Assertions.assertTrue(config.isWorldBypassed("world_beta"));
        Assertions.assertTrue(config.shouldCreateBackup("world_beta"));

        config.setWorldBypassed("world_alpha", false);
        Assertions.assertFalse(config.isWorldBypassed("world_alpha"));
    }

    @Test
    public void testGlobalBypass() {
        ModConfig config = new ModConfig();
        config.globalBypass = true;
        Assertions.assertTrue(config.isWorldBypassed("any_random_world"));
        Assertions.assertFalse(config.shouldCreateBackup("any_random_world"));

        config.defaultBypassAction = BypassAction.BACKUP;
        Assertions.assertTrue(config.shouldCreateBackup("any_random_world"));
    }

    @Test
    public void testResetAllWorlds() {
        ModConfig config = new ModConfig();
        config.setWorldBypassed("w1", true);
        config.setWorldBypassed("w2", true);
        Assertions.assertEquals(2, config.rememberedWorlds.size());

        config.resetAllWorlds();
        Assertions.assertEquals(0, config.rememberedWorlds.size());
        Assertions.assertFalse(config.isWorldBypassed("w1"));
    }

    @Test
    public void testResetAllToDefaults() {
        ModConfig config = new ModConfig();
        config.globalBypass = true;
        config.defaultBypassAction = BypassAction.BACKUP;
        config.showToast = false;
        config.setWorldBypassed("w1", true);

        config.resetAllToDefaults();
        Assertions.assertFalse(config.globalBypass);
        Assertions.assertEquals(BypassAction.SKIP, config.defaultBypassAction);
        Assertions.assertTrue(config.showToast);
        Assertions.assertEquals(0, config.rememberedWorlds.size());
    }

    @Test
    public void testWorldLoadContext() {
        WorldLoadContext.setCurrentWorld("my_survival_world");
        Assertions.assertEquals("my_survival_world", WorldLoadContext.getCurrentWorld());
        WorldLoadContext.clear();
        Assertions.assertNull(WorldLoadContext.getCurrentWorld());

        WorldLoadContext.setPendingNotification("world1", true);
        WorldLoadContext.PendingBypassNotification notif = WorldLoadContext.pollPendingNotification();
        Assertions.assertNotNull(notif);
        Assertions.assertEquals("world1", notif.worldName());
        Assertions.assertTrue(notif.backupCreated());
        Assertions.assertNull(WorldLoadContext.pollPendingNotification());
    }
}
