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

import java.util.function.Predicate;

public class PlayerLoginHandler {

    public PlayerLoginHandler() {
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerManager.getInstance().Players.add(new PlayerInfo(event.player.getDisplayNameString())); // add player name

        // send the client controlled config variables
        String color = ConfigHandler.getChuteColor();
        double burnVolume = ConfigHandler.getBurnVolume();
        String hudPosition = ConfigHandler.getHudPosition();
        String steeringControl = ConfigHandler.getSteeringControl();
        boolean aadState = ConfigHandler.getAADState();
        boolean useFlyingSound = ConfigHandler.getUseFlyingSound();
        PacketHandler.NETWORK.sendTo(new ClientConfigMessage(color, burnVolume, hudPosition, steeringControl, aadState, useFlyingSound),
                (EntityPlayerMP)event.player);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Predicate<PlayerInfo> player = p -> event.player.getDisplayNameString().equals(p.getName());
        PlayerManager.getInstance().Players.removeIf(player);
    }
}
