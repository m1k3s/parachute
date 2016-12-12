package com.parachute.client;

import com.parachute.common.ControlKeyPressMessage;
import com.parachute.common.PacketHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ControlKeyPressTick {
    private final int leftKey;
    private final int rightKey;
    private final int forwardKey;
    private final int backKey;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean forwardPressed;
    private boolean backPressed;

    public ControlKeyPressTick(int leftKey, int rightKey, int forwardKey, int backKey) {
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.forwardKey = forwardKey;
        this.backKey = backKey;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            if (Keyboard.getEventKey() == leftKey) {
                leftPressed = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == rightKey) {
                rightPressed = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == forwardKey) {
                forwardPressed = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == backKey) {
                backPressed = Keyboard.getEventKeyState();
            }
            PacketHandler.network.sendToServer(new ControlKeyPressMessage(leftPressed, rightPressed, forwardPressed, backPressed));
        }
    }

}
