/*
 * KeyPressTick.java
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
package com.parachute.client;

import com.parachute.common.AscendKeyPressMessage;
import com.parachute.common.PacketHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

// intercept the ascend key to make the parachute go up
// the ascend key defaults to the space bar or jump key.
public class KeyPressTick {

    private final int ascendKey;

    public KeyPressTick(int key) {
        ascendKey = key;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            if (Keyboard.getEventKey() == ascendKey) { // only send if it's the ascend key
                PacketHandler.network.sendToServer(new AscendKeyPressMessage(Keyboard.getEventKeyState()));
            }
        }
    }

}
