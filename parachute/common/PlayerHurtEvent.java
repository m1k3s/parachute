/*
 * PlayerHurtEvent.java
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

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerHurtEvent {
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void SkydiverHurtEvent(LivingHurtEvent event) {
        if (event.getEntityLiving().isRiding() && event.getEntityLiving().getRidingEntity() instanceof EntityParachute) {//Parachute.isDeployed()) {
            event.setCanceled(true);
            event.setAmount(0.0f);
        }
    }
}