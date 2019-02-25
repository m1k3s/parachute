/*
 * PlayerManager.java
 *
 *  Copyright (c) 2019 Michael Sheppard
 *
 * =====GPL=============================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 * =====================================================================
 */

package com.parachute.common;

//import net.minecraft.entity.player.EntityPlayer;

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
//    public PlayerInfo getPlayerInfoFromPlayer(EntityPlayer player) {
//        for (PlayerInfo pi : Players) {
//            if (pi.getName().equals(player.getDisplayNameString()))
//                return pi;
//        }
//        return null;
//    }
}
