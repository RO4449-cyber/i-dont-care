package com.ro4449.idontcare;

import org.jspecify.annotations.Nullable;

public class WorldLoadContext {
    @Nullable
    private static String currentWorld;
    private static boolean backedUpThisSession;
    private static boolean toastShownThisSession;

    public static void setCurrentWorld(@Nullable String world) {
        currentWorld = world;
        backedUpThisSession = false;
        toastShownThisSession = false;
    }

    @Nullable
    public static String getCurrentWorld() {
        return currentWorld;
    }

    public static boolean wasBackedUp() {
        return backedUpThisSession;
    }

    public static void markBackedUp() {
        backedUpThisSession = true;
    }

    public static boolean wasToastShown() {
        return toastShownThisSession;
    }

    public static void markToastShown() {
        toastShownThisSession = true;
    }

    public static void clear() {
        currentWorld = null;
        backedUpThisSession = false;
        toastShownThisSession = false;
    }
}
