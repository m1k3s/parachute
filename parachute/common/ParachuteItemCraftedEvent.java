package com.parachute.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ParachuteItemCraftedEvent {

    @SubscribeEvent
    public void event(PlayerEvent.ItemCraftedEvent craftedEvent)
    {
        if (craftedEvent.crafting.getItem() instanceof ItemParachute) {
            craftedEvent.player.addStat(Parachute.buildParachute, 1);
        }
    }
}
