package com.ro4449.idontcare.config;

public class WorldPreference {
    public boolean bypassed = true;
    public BypassAction action = BypassAction.SKIP;

    public WorldPreference() {
    }

    public WorldPreference(boolean bypassed, BypassAction action) {
        this.bypassed = bypassed;
        this.action = action;
    }
}
