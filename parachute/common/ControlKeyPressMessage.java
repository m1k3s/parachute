package com.parachute.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ControlKeyPressMessage implements IMessage {
    private boolean keyCodeLeft;
    private boolean keyCodeRight;
    private boolean keyCodeForward;
    private boolean keyCodeBack;

    @SuppressWarnings("unused")
    public ControlKeyPressMessage() {}

    public ControlKeyPressMessage(boolean keyCodeLeft, boolean keyCodeRight, boolean keyCodeForward, boolean keyCodeBack) {
        this.keyCodeLeft = keyCodeLeft;
        this.keyCodeRight = keyCodeRight;
        this.keyCodeForward = keyCodeForward;
        this.keyCodeBack = keyCodeBack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        keyCodeLeft = buf.readBoolean();
        keyCodeRight = buf.readBoolean();
        keyCodeForward = buf.readBoolean();
        keyCodeBack = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(keyCodeLeft);
        buf.writeBoolean(keyCodeRight);
        buf.writeBoolean(keyCodeForward);
        buf.writeBoolean(keyCodeBack);
    }

    public static class Handler implements IMessageHandler<ControlKeyPressMessage, IMessage> {

        @Override
        public IMessage onMessage(ControlKeyPressMessage msg, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
                EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;
                if (entityPlayer != null && entityPlayer.getRidingEntity() instanceof EntityParachute) {
//                    EntityParachute.updateInputs(msg.keyCodeLeft, msg.keyCodeRight, msg.keyCodeForward, msg.keyCodeBack);
//                    System.out.println("Inputs: " + msg.keyCodeLeft + ":" + msg.keyCodeRight + ":" + msg.keyCodeForward + ":" + msg.keyCodeBack);
                }
            });
            return null;
        }
    }
}
