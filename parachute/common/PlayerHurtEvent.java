package com.parachute.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerHurtEvent {
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void SkydiverHurtEvent(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && ParachuteCommonProxy.isDeployed()) {
            event.setCanceled(true);
            event.setAmount(0.0f);
        }
    }
}
