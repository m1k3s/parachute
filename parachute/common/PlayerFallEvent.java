/*
 * PlayerFallEvent.java
 *
 * Copyright (c) 2017 Michael Sheppard
 *
 *  =====GPL=============================================================
 * $program is free software: you can redistribute it and/or modify
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
 *
 */
package com.parachute.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class PlayerFallEvent {

    public static boolean isDismounting;

    public PlayerFallEvent() {
        Parachute.instance.info("PlayerFallEvent ctor");
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onFallEvent(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && isDismounting) { //ConfigHandler.isDismounting()) {
			event.setCanceled(true);
			event.setDistance(0.0f);
			event.setDamageMultiplier(0.0f);
//            ConfigHandler.setIsDismounting(false);
            isDismounting = false;
        }
    }
}
