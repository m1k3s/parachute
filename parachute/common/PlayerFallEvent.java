//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class PlayerFallEvent {

	public PlayerFallEvent()
	{
		Parachute.proxy.info("PlayerFallEvent ctor");
	}

	@SubscribeEvent
	public void onFallEvent(LivingFallEvent event)
	{
		EntityLivingBase pilot = event.entityLiving;
		if (event.entityLiving instanceof EntityPlayer && pilot.getRidingEntity() instanceof EntityParachute) {
//			Parachute.proxy.info("Caught onFallEvent ***");
			pilot.fallDistance = 0.0f;
			pilot.isCollided = false;
			event.setCanceled(true);
		}
	}
}
