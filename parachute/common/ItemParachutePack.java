/*
 * ItemParachutePack.java
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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.world.World;


// this item is eye candy only. The parachute pack is placed as armor
// on the player when the parachute item is selected in the hot bar.
public class ItemParachutePack extends ItemArmor {

    public ItemParachutePack(Properties props) {
        super(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, props);
//        props.defaultMaxDamage(ArmorMaterial.LEATHER.getDurability(armorType));
//        props.maxStackSize(0);
//        setUnlocalizedName(Parachute.MODID + ":" + itemName);
    }

    // if the player has tried to move the parachute pack item to another inventory slot
    // delete the stack unless the slot is the armor plate slot, the item is only for display
    // if the pack item is dropped getEntityLifespan takes care of that.
    // Todo: ideally it would be better if the pack item was not selectable at all.
    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (stack.getItem() instanceof ItemParachutePack) {
            if (Parachute.isServerSide(worldIn) && entityIn instanceof EntityPlayer) {
                if (EntityEquipmentSlot.CHEST.getIndex() != itemSlot) {
                    ((EntityPlayer) entityIn).inventory.deleteStack(stack);
                }
            }
        }
    }

    @Override
    public String getArmorTexture(ItemStack itemstack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (itemstack.getItem() == Parachute.RegistryEvents.ITEM_PARACHUTE_PACK) {
            return Parachute.MODID.toLowerCase() + ":textures/models/armor/pack.png";
        }
        return Parachute.MODID.toLowerCase() + ":textures/models/armor/pack.png";
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
