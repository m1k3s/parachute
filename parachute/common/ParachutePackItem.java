/*
 * ParachutePackItem.java
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
package com.parachute.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.world.World;



// this item is eye candy only. The PARACHUTE pack is placed as armor
// on the player when the PARACHUTE item is selected in the hot bar.
public class ParachutePackItem extends ArmorItem {

    public ParachutePackItem(Properties props) {
        super(ArmorMaterial.LEATHER, EquipmentSlotType.CHEST, props);
    }

    // if the player has tried to move the PARACHUTE pack item to another inventory slot
    // delete the stack unless the slot is the armor plate slot, the item is only for display
    // if the pack item is dropped getEntityLifespan takes care of that.
    // Todo: ideally it would be better if the pack item was not selectable at all.
    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (stack.getItem() instanceof ParachutePackItem) {
            if (!worldIn.isRemote && entityIn instanceof PlayerEntity) {
                if (EquipmentSlotType.CHEST.getIndex() != itemSlot) {
                    ((PlayerEntity) entityIn).inventory.deleteStack(stack);
                }
            }
        }
    }

    @Override
    public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlotType slot, String type) {
        if (itemstack.getItem() instanceof ParachutePackItem) {//== Parachute.RegistryEvents.PARACHUTEPACK_ITEM) {
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
