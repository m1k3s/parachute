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
            ItemStack armor = player.getItemStackFromSlot(ParachuteCommonProxy.armorType);
            ItemStack heldItemOffhand = player.getHeldItemOffhand(); // offhand needs to be handled separately
            if (armor == null && heldItemOffhand != null && heldItemOffhand.getItem() instanceof ItemParachute) {
                player.inventory.armorInventory.add(ParachuteCommonProxy.armorType.getIndex(), new ItemStack(Parachute.packItem));
                return;
            }
            // need this additional test, armor bar flickers when offhand has parachute and
            // the parachute is not deployed.
            if (heldItemOffhand != null && heldItemOffhand.getItem() instanceof ItemParachute) {
                return;
            }
            ItemStack heldItemMainhand = player.getHeldItemMainhand();
            boolean deployed = ParachuteCommonProxy.onParachute(player);
            if (armor != null && heldItemMainhand == null) { // parachute item has been removed from slot in the hot bar
                if (!deployed && armor.getItem() instanceof ItemParachutePack) {
                    player.inventory.armorInventory.remove(ParachuteCommonProxy.armorType.getIndex());
                }
            } else if (armor != null) { // player has selected another slot in the hot bar || regular armor is present
                if (!deployed && armor.getItem() instanceof ItemParachutePack && !(heldItemMainhand.getItem() instanceof ItemParachute)) {
                    player.inventory.armorInventory.remove(ParachuteCommonProxy.armorType.getIndex());
                }
            } else {
                if (heldItemMainhand != null && heldItemMainhand.getItem() instanceof ItemParachute) {
                    player.inventory.armorInventory.add(ParachuteCommonProxy.armorType.getIndex(), new ItemStack(Parachute.packItem));
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
