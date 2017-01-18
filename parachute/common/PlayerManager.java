package com.parachute.common;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class PlayerManager {

    public ArrayList<PlayerInfo> Players;

    private static final PlayerManager instance = new PlayerManager();

    public static PlayerManager getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    private PlayerManager() {
        Players = new ArrayList();
    }

    // must test for null EntityPlayer before calling this method
    public PlayerInfo getPlayerInfoFromPlayer(EntityPlayer player) {
        for(PlayerInfo pi : Players) {
            if(pi.getName().equals(player.getDisplayNameString()))
                return pi;
        }
        return null;
    }


}
