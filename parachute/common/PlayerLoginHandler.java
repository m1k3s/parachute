package com.parachute.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerLoginHandler {

    public PlayerLoginHandler() {}

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerManager.getInstance().Players.add(new PlayerInfo(event.player.getDisplayNameString()));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerInfo PI = new PlayerInfo("");
        for(int i = 0; i < PlayerManager.getInstance().Players.size(); i++) {
            if(PlayerManager.getInstance().Players.get(i).getName().equals(event.player.getDisplayNameString())) {
                PlayerManager.getInstance().Players.remove(i);
            }
        }
    }
}
