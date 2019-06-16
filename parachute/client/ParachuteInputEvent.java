/*
 * ParachuteInputEvent.java
 *
 *  Copyright (c) 2019 Michael Sheppard
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

package com.parachute.client;

import com.parachute.common.ParachuteEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParachuteInputEvent {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void inputEvent(InputUpdateEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity) {
            if (entity.isPassenger() && (entity.getRidingEntity() instanceof ParachuteEntity)) {
                ((ParachuteEntity) entity.getRidingEntity()).updateInputs(event.getMovementInput());
            }
        }
    }
}
