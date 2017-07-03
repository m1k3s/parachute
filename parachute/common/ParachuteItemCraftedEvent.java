/*
 * ParachuteItemCraftedEvent.java
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

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

// if a parachute is crafted, add the achievement
// if the wool blocks are the same color change the parachute
// color to match, otherwise set the parachute color to white.
public class ParachuteItemCraftedEvent {

    @SubscribeEvent
    public void event(PlayerEvent.ItemCraftedEvent craftedEvent) {
        if (craftedEvent.crafting.getItem() instanceof ItemParachute) {
//            craftedEvent.player.addStat(Parachute.buildParachute, 1);
            EnumDyeColor[] canopy = new EnumDyeColor[3];
            for (int k = 0; k < 3; k++) { // scan the top three slots for same colored wool blocks
                ItemStack stack = craftedEvent.craftMatrix.getStackInSlot(k);
//                if (stack != null) {
                    Item item = stack.getItem();
                    if (item instanceof ItemCloth) {
                        canopy[k] = EnumDyeColor.byMetadata(item.getMetadata(stack));
                        if (craftedEvent.player.world.isRemote) {
                            if (canopy[0].equals(canopy[1]) && canopy[1].equals(canopy[2])) {
                                ConfigHandler.setChuteColor(canopy[0].toString());
                            } else {
                                ConfigHandler.setChuteColor("white");
                            }
                        }
                    }
//                }
            }
        }
    }
}
