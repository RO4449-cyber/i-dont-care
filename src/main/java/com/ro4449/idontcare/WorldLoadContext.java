package com.ro4449.idontcare;

import org.jspecify.annotations.Nullable;

public class WorldLoadContext {
    @Nullable
    private static String currentWorld;

    public static void setCurrentWorld(@Nullable String world) {
        currentWorld = world;
    }

    @Nullable
    public static String getCurrentWorld() {
        return currentWorld;
    }

    public static void clear() {
        currentWorld = null;
    }
}
