//
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.common;


import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class AscendKeyPressMessage implements IMessage {

    private boolean keyPressed;

    @SuppressWarnings("unused")
    public AscendKeyPressMessage() {

    }

    public AscendKeyPressMessage(boolean keyPressed) {
        this.keyPressed = keyPressed;
    }

    // the server does not respond with any messages so this isn't being used;
    @Override
    public void fromBytes(ByteBuf bb) {
        keyPressed = bb.readBoolean();
    }

    // write the data to the stream
    @Override
    public void toBytes(ByteBuf bb) {
        bb.writeBoolean(keyPressed);
    }

    public static class Handler implements IMessageHandler<AscendKeyPressMessage, IMessage> {
        @Override
        public IMessage onMessage(final AscendKeyPressMessage msg, final MessageContext ctx) {
            IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.world;
            mainThread.addScheduledTask(() -> {
                EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;
                if (entityPlayer != null && entityPlayer.getRidingEntity() instanceof EntityParachute) {
//                    EntityParachute.setAscendMode(msg.keyPressed);
                    PlayerInfo pi = PlayerManager.getInstance().getPlayerInfoFromPlayer(entityPlayer);
                    if (pi != null) {
                        pi.setAscendMode(msg.keyPressed);
                    }
                }
            });

            return null;
        }
    }

}
