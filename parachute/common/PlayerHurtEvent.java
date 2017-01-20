package com.parachute.common;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerHurtEvent {
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void SkydiverHurtEvent(LivingHurtEvent event) {
        if (event.getEntityLiving().isRiding() && ParachuteCommonProxy.isDeployed()) {
            event.setCanceled(true);
            event.setAmount(0.0f);
        }
    }
}