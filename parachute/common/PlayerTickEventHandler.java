/*
 * PlayerTickEventHandler.java
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

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerTickEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START) && event.side.isServer()) {
            autoActivateDevice(event.player);
            togglePlayerParachutePack(event.player);
        }
    }

    // Check the players currently held item and if it is a
    // parachuteItem set a packItem in the chestplate armor slot.
    // Remove the packItem if the player is no longer holding the parachuteItem
    // as long as the player is not on the parachute. If there is already an
    // armor item in the armor slot do nothing.
    private void togglePlayerParachutePack(EntityPlayer player) {
        if (player != null) {
            EntityEquipmentSlot armorSlot = ParachuteModRegistration.armorType;
            ItemStack armor = player.getItemStackFromSlot(ParachuteModRegistration.armorType);
            ItemStack heldItemMainhand = player.getHeldItemMainhand();
            ItemStack heldItem = !heldItemMainhand.isEmpty() ? heldItemMainhand : player.getHeldItem(EnumHand.OFF_HAND);
            boolean deployed = ParachuteCommonProxy.onParachute(player);

            if (!deployed && armor.getItem() instanceof ItemParachutePack && (heldItem.isEmpty() || !(heldItem.getItem() instanceof ItemParachute))) {
                player.inventory.armorInventory.set(armorSlot.getIndex(), ItemStack.EMPTY);
            } else {
                if (heldItem.getItem() instanceof ItemParachute && armor.isEmpty()) {
                    player.inventory.armorInventory.set(armorSlot.getIndex(), new ItemStack(ParachuteModRegistration.packItem));
                }
            }
        }
    }

    // Handles the Automatic Activation Device, if the AAD is active
    // and the player is actually wearing the parachute, check the
    // altitude, if autoAltitude has been reached, deploy. If the immediate
    // AAD option is active, deploy after minFallDistance is reached.
    private void autoActivateDevice(EntityPlayer player) {
        if (ConfigHandler.getIsAADActive() && !ParachuteCommonProxy.onParachute(player)) {
            ItemStack heldItem = null;
            Iterable<ItemStack> heldEquipment = player.getHeldEquipment();
            for (ItemStack itemStack : heldEquipment) {
                if (itemStack != null && itemStack.getItem() instanceof ItemParachute) {
                    heldItem = itemStack;
                }
            }
            if (ConfigHandler.getAADImmediate() && ParachuteCommonProxy.canActivateAADImmediate(player)) {
                if (heldItem != null && heldItem.getItem() instanceof ItemParachute) {
                    ((ItemParachute) heldItem.getItem()).deployParachute(player.world, player);
                }
            } else {
                boolean autoAltitudeReached = ParachuteCommonProxy.getAutoActivateAltitude(player);
                if (autoAltitudeReached && ParachuteCommonProxy.isFalling(player)) {
                    if (heldItem != null && heldItem.getItem() instanceof ItemParachute) {
                        ((ItemParachute) heldItem.getItem()).deployParachute(player.world, player);
                    }
                }
            }
        }
    }

}
