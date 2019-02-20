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
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientConfigMessage { //} implements IMessage {
    private static String chuteColor;
    private static double burnVolume;
    private static String hudPosition;
    private static String steeringControl;
    private static boolean aadState;
    private static boolean useFlyingSound;

    @SuppressWarnings("unused")
    public ClientConfigMessage() {}

    public ClientConfigMessage(String chuteColor, double burnVolume, String hudPosition, String steeringControl, boolean aadState, boolean useFlyingSound) {
        ClientConfigMessage.chuteColor = chuteColor;
        ClientConfigMessage.burnVolume = burnVolume;
        ClientConfigMessage.hudPosition = hudPosition;
        ClientConfigMessage.steeringControl = steeringControl;
        ClientConfigMessage.aadState = aadState;
        ClientConfigMessage.useFlyingSound = useFlyingSound;
    }

    public static void decode (ClientConfigMessage pkt, PacketBuffer buffer) {  // server ==> client
        chuteColor = buffer.readUTF8String(buffer);
        burnVolume = buffer.readDouble();
        hudPosition = ByteBufUtils.readUTF8String(buffer);
        steeringControl = ByteBufUtils.readUTF8String(buffer);
        aadState = buffer.readBoolean();
        useFlyingSound = buffer.readBoolean();
    }

    public static void encode(PacketBuffer buffer) { // client ==> server - not used
        ByteBufUtils.writeUTF8String(buffer, chuteColor);
        buffer.writeDouble(burnVolume);
        ByteBufUtils.writeUTF8String(buffer, hudPosition);
        ByteBufUtils.writeUTF8String(buffer, steeringControl);
        buffer.writeBoolean(aadState);
        buffer.writeBoolean(useFlyingSound);
    }

    public static class Handler {
        public static void handle(final ClientConfigMessage pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {

//        @Override
//        public IMessage onMessage(final ClientConfigMessage msg, final MessageContext ctx) {
//            Minecraft client = Minecraft.getMinecraft();
//            client.addScheduledTask(() -> {
                ClientConfiguration.setChuteColor(msg.chuteColor);
                ClientConfiguration.setBurnVolume(msg.burnVolume);
                ClientConfiguration.setHudPosition(msg.hudPosition);
                ClientConfiguration.setSteeringControl(msg.steeringControl);
                ClientConfiguration.setAADState(msg.aadState);
                ClientConfiguration.setUseFlyingSound(msg.useFlyingSound);
                RenderParachute.setParachuteColor(msg.chuteColor);
            });
//            return null;
        }
    }
}
