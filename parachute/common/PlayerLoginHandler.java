/*
 * PlayerLoginHandler.java
 *
 * Copyright (c) 2017 Michael Sheppard
 *
 *  =====GPL=============================================================
 * $program is free software: you can redistribute it and/or modify
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
 *
 */

package com.parachute.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerLoginHandler {

    public PlayerLoginHandler() {
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerManager.getInstance().Players.add(new PlayerInfo(event.player.getDisplayNameString()));
        String color = ConfigHandler.getChuteColor();
        boolean noHUD = ConfigHandler.getNoHUD();
        boolean useCompassHUD = ConfigHandler.getUseCompassHUD();
        PacketHandler.network.sendTo(new ClientConfigMessage(color, noHUD, useCompassHUD), (EntityPlayerMP)event.player);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerInfo PI = new PlayerInfo("");
        for (int i = 0; i < PlayerManager.getInstance().Players.size(); i++) {
            if (PlayerManager.getInstance().Players.get(i).getName().equals(event.player.getDisplayNameString())) {
                PlayerManager.getInstance().Players.remove(i);
            }
        }
    }
}
