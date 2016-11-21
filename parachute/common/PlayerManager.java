package com.parachute.common;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    public List<PlayerInfo> Players;

    private static final PlayerManager instance = new PlayerManager();

    public static final PlayerManager getInstance() {
        return instance;
    }

    private PlayerManager() {
        Players = new ArrayList();
    }

    // must test for null EntityPlayer before calling this method
    public PlayerInfo getPlayerInfoFromPlayer(EntityPlayer player) {
        for(PlayerInfo pi : Players) {
            if(pi.Name.equals(player.getDisplayNameString()))
                return pi;
        }
        return null;
    }


}
