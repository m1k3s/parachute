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
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientAADStateMessage implements IMessage {
    private boolean aadState;

    @SuppressWarnings("unused")
    public ClientAADStateMessage() {}

    public ClientAADStateMessage(boolean aadState) {
        this.aadState = aadState;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        aadState = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeBoolean(aadState);
    }

    public static class Handler implements IMessageHandler<ClientAADStateMessage, IMessage> {
        @Override
        public IMessage onMessage(final ClientAADStateMessage msg, final MessageContext cts) {
            Minecraft client = Minecraft.getMinecraft();
            client.addScheduledTask(() -> ClientConfiguration.setAADState(msg.aadState));
            return null;
        }
    }
}
