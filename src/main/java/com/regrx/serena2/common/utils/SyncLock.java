package com.regrx.serena2.common.utils;

public class SyncLock {
    private boolean locked;
    public SyncLock() {
        this.locked = false;
    }

    public void lockOn() {
        this.locked = true;
    }

    public void lockOff() {
        this.locked = false;
    }

    public boolean isLocked() {
        return locked;
    }
}
