/*
 * ClientConfigMessage.java
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
import com.parachute.client.RenderParachute;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientConfigMessage implements IMessage {
    private String chuteColor;
    private boolean noHUD;
    private double burnVolume;
    private String hudPosition;
    private boolean altitudeMSL;
    private String steeringControl;
    private boolean frontBubble;

    @SuppressWarnings("unused")
    public ClientConfigMessage() {}

    public ClientConfigMessage(
            String chuteColor, boolean noHUD, double burnVolume, String hudPosition,
            boolean altitudeMSL, String steeringControl, boolean frontBubble) {
        this.chuteColor = chuteColor;
        this.noHUD = noHUD;
        this.burnVolume = burnVolume;
        this.hudPosition = hudPosition;
        this.altitudeMSL = altitudeMSL;
        this.steeringControl = steeringControl;
        this.frontBubble = frontBubble;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {  // server ==> client
        chuteColor = ByteBufUtils.readUTF8String(byteBuf);
        noHUD = byteBuf.readBoolean();
        burnVolume = byteBuf.readDouble();
        hudPosition = ByteBufUtils.readUTF8String(byteBuf);
        altitudeMSL = byteBuf.readBoolean();
        steeringControl = ByteBufUtils.readUTF8String(byteBuf);
        frontBubble = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) { // client ==> server - not used
        ByteBufUtils.writeUTF8String(byteBuf, chuteColor);
        byteBuf.writeBoolean(noHUD);
        byteBuf.writeDouble(burnVolume);
        ByteBufUtils.writeUTF8String(byteBuf, hudPosition);
        byteBuf.writeBoolean(altitudeMSL);
        ByteBufUtils.writeUTF8String(byteBuf, steeringControl);
        byteBuf.writeBoolean(frontBubble);
    }

    public static class Handler implements IMessageHandler<ClientConfigMessage, IMessage> {
        @Override
        public IMessage onMessage(final ClientConfigMessage msg, final MessageContext ctx) {
            Minecraft client = Minecraft.getMinecraft();
            client.addScheduledTask(() -> {
                ClientConfiguration.setChuteColor(msg.chuteColor);
                ClientConfiguration.setNoHUD(msg.noHUD);
                ClientConfiguration.setBurnVolume(msg.burnVolume);
                ClientConfiguration.setHudPosition(msg.hudPosition);
                ClientConfiguration.setAltitudeMSL(msg.altitudeMSL);
                ClientConfiguration.setSteeringControl(msg.steeringControl);
                ClientConfiguration.setFrontBubble(msg.frontBubble);
                RenderParachute.setParachuteColor(msg.chuteColor);
            });
            return null;
        }
    }
}
