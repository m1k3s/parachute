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

import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;


// this item is eye candy only. The parachute pack is placed as armor
// on the player when the parachute item is selected in the hot bar.
public class ItemParachutePack extends ItemArmor {

	public ItemParachutePack(ItemArmor.ArmorMaterial armorMaterial, int renderIndex, int armorType)
	{
		super(armorMaterial, renderIndex, armorType);
		setMaxDamage(armorMaterial.getDurability(armorType));
		maxStackSize = 1;
	}

	@Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type)
	{
		if (itemstack.getItem() == Parachute.packItem) {
			return Parachute.modid.toLowerCase() + ":textures/models/armor/pack.png";
		}
		return Parachute.modid.toLowerCase() + ":textures/models/armor/pack.png";
	}

}
