package com.ro4449.idontcare;

import org.jspecify.annotations.Nullable;

public class WorldLoadContext {
    @Nullable
    private static String currentWorld;

    @Nullable
    private static PendingBypassNotification pendingNotification;

    public record PendingBypassNotification(String worldName, boolean backupCreated) {}

    public static void setCurrentWorld(@Nullable String world) {
        currentWorld = world;
    }

    @Nullable
    public static String getCurrentWorld() {
        return currentWorld;
    }

    public static void setPendingNotification(String worldName, boolean backupCreated) {
        pendingNotification = new PendingBypassNotification(worldName, backupCreated);
    }

    @Nullable
    public static PendingBypassNotification pollPendingNotification() {
        PendingBypassNotification notif = pendingNotification;
        pendingNotification = null;
        return notif;
    }

    public static void clear() {
        currentWorld = null;
    }
}
