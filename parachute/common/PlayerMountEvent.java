/*
 * PlayerMountEvent.java
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

import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// this class allows the player to 'drop' the parachute with precision.
// the vanilla dismount method moves the player away from the mounted object.
// in this mod the player should be able to dismount the parachute and land
// directly at the parachute's X and Z location. This performs the same steps
// as the auto-dismount code. When the player presses the LSHIFT to dismount,
// cancel the dismount and call the EntityParachute dismount method.
// FIXME: this is problematic since forge moved the hook.

public class PlayerMountEvent {

    public PlayerMountEvent() { Parachute.instance.info("PlayerMountEvent ctor"); }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onMount(EntityMountEvent event) {
        if (event.getEntityBeingMounted() instanceof EntityParachute && Parachute.isDeployed() && !event.isMounting()) {
//            if (event.getEntity().world.isRemote) {
//                event.setCanceled(true);
//            }
            ((EntityParachute)event.getEntityBeingMounted()).dismountParachute();
        }
    }
}
