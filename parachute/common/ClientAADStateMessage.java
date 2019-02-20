/*
 * ClientAADStateMessage.java
 *
 *  Copyright (c) 2018 Michael Sheppard
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

import com.parachute.client.ClientConfiguration;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientAADStateMessage {
    private final boolean aadState;

    @SuppressWarnings("unused")
    public ClientAADStateMessage(boolean value) {
        aadState = value;
    }

    public static ClientAADStateMessage decode(PacketBuffer buffer) {
        return new ClientAADStateMessage(buffer.readBoolean());
    }

    public static void encode(ClientAADStateMessage msg, PacketBuffer buffer) {
        buffer.writeBoolean(msg.aadState);
    }

    public static class Handler {
        public static void handle(final ClientAADStateMessage pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> ClientConfiguration.setAADState(pkt.aadState));
        }
    }
}
