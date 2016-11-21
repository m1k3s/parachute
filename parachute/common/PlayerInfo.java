package com.parachute.common;

public class PlayerInfo {
    public String Name;
    public boolean mode; // true = ascend, false = drift
//        public INetworkManager networkManager;

    public PlayerInfo(String name/*, INetworkManager nm*/) {
        Name = name;
//            networkManager = nm;
    }

    public void setAscendMode(boolean m) {
        mode = m;
    }

    public boolean getAscendMode() {
        return mode;
    }
}