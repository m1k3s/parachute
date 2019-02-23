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
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ClientConfigMessage extends SimpleChannel.MessageBuilder<ClientConfigMessage> {
    private String chuteColor;
    private double burnVolume;
    private String hudPosition;
    private String steeringControl;
    private boolean aadState;
    private boolean useFlyingSound;

    @SuppressWarnings("unused")
    public ClientConfigMessage() {}

    public ClientConfigMessage(String chuteColor, double burnVolume, String hudPosition, String steeringControl, boolean aadState, boolean useFlyingSound) {
        this.chuteColor = chuteColor;
        this.burnVolume = burnVolume;
        this.hudPosition = hudPosition;
        this.steeringControl = steeringControl;
        this.aadState = aadState;
        this.useFlyingSound = useFlyingSound;
    }

    public static ClientConfigMessage decode(PacketBuffer buffer) {
        return new ClientConfigMessage(buffer.readString(8), buffer.readDouble(), buffer.readString(8), buffer.readString(8), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void encode(ClientConfigMessage msg, PacketBuffer buffer) {
        buffer.writeString(msg.chuteColor);
        buffer.writeDouble(msg.burnVolume);
        buffer.writeString(msg.hudPosition);
        buffer.writeString(msg.steeringControl);
        buffer.writeBoolean(msg.aadState);
        buffer.writeBoolean(msg.useFlyingSound);
    }

    public static class Handler {
        public static void handle(final ClientConfigMessage pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ClientConfiguration.setChuteColor(pkt.chuteColor);
                ClientConfiguration.setBurnVolume(pkt.burnVolume);
                ClientConfiguration.setHudPosition(pkt.hudPosition);
                ClientConfiguration.setSteeringControl(pkt.steeringControl);
                ClientConfiguration.setAADState(pkt.aadState);
                ClientConfiguration.setUseFlyingSound(pkt.useFlyingSound);
                RenderParachute.setParachuteColor(pkt.chuteColor);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
