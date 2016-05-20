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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.world.World;


// this item is eye candy only. The parachute pack is placed as armor
// on the player when the parachute item is selected in the hot bar.
public class ItemParachutePack extends ItemArmor {

    public ItemParachutePack(ItemArmor.ArmorMaterial armorMaterial, int renderIndex, EntityEquipmentSlot armorType) {
        super(armorMaterial, renderIndex, armorType);
        setMaxDamage(armorMaterial.getDurability(armorType));
        maxStackSize = 1;
    }

    // if the player has tried to move the parachute pack item to another inventory slot
    // delete the stack unless the slot is the armor plate slot, the item is only for display
    // if the pack item is dropped getEntityLifespan takes care of that.
    // Todo: ideally it would be better if the pack item was not selectable at all.
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (stack.getItem() instanceof ItemParachutePack) {
            if (!worldIn.isRemote && entityIn instanceof EntityPlayer) {
                if (ParachuteCommonProxy.armorType.getIndex() != itemSlot) {
                    ((EntityPlayer) entityIn).inventory.deleteStack(stack);
                }
            }
        }
    }

    @Override
    public String getArmorTexture(ItemStack itemstack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (itemstack.getItem() == Parachute.packItem) {
            return Parachute.modid.toLowerCase() + ":textures/models/armor/pack.png";
        }
        return Parachute.modid.toLowerCase() + ":textures/models/armor/pack.png";
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    // kill the dropped item quickly, remember it's only eye candy
    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 1;
    }

}
