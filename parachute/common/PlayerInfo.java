package com.parachute.common;

public class PlayerInfo {
    private String name;
    private boolean mode; // true = ascend, false = drift

    public PlayerInfo(String name) {
        this.name = name;
    }

    public void setAscendMode(boolean m) {
        mode = m;
    }

    public boolean getAscendMode() {
        return mode;
    }

    public String getName() {
        return name;
    }
}