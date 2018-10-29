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

import com.parachute.client.ClientConfiguration;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerTickEventHandler {

    private boolean displayArmorBar;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            autoActivateDevice(event.player);
            togglePlayerParachutePack(event.player);
            armorBarRenderingHandler(event.player);
        }
    }

    // Check the players currently held item and if it is a
    // PARACHUTE_ITEM set a ITEM_PARACHUTE_PACK in the chestplate armor slot.
    // Remove the ITEM_PARACHUTE_PACK if the player is no longer holding the PARACHUTE_ITEM
    // as long as the player is not on the parachute. If there is already an
    // armor item in the armor slot do nothing.
    private void togglePlayerParachutePack(EntityPlayer player) {
        if (player != null) {
            ItemStack armor = player.getItemStackFromSlot(Parachute.ARMOR_TYPE);
            ItemStack heldItemMainhand = player.getHeldItemMainhand();
            ItemStack heldItem = !heldItemMainhand.isEmpty() ? heldItemMainhand : player.getHeldItem(EnumHand.OFF_HAND);
            boolean deployed = player.getRidingEntity() instanceof EntityParachute;

            if (!deployed && armor.getItem() instanceof ItemParachutePack && (heldItem.isEmpty() || !(heldItem.getItem() instanceof ItemParachute))) {
                player.inventory.armorInventory.set(Parachute.ARMOR_TYPE.getIndex(), ItemStack.EMPTY);
            } else if (heldItem.getItem() instanceof ItemParachute && armor.isEmpty()) {
                player.inventory.armorInventory.set(Parachute.ARMOR_TYPE.getIndex(), new ItemStack(Parachute.ITEM_PARACHUTE_PACK));
            }
        }
    }

    // do not display the armorbar if the parachute is selected in the hot bar
    // and no other armor is being worn
    private void armorBarRenderingHandler(EntityPlayer player) {
        if (player != null) {
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if (player.getItemStackFromSlot(slot).getItem() instanceof ItemArmor) {
                    displayArmorBar = !(player.getItemStackFromSlot(slot).getItem() instanceof ItemParachutePack);
                }
            }
            if (player.world.isRemote) {
                GuiIngameForge.renderArmor = displayArmorBar;
            }
        }
    }

    // Handles the Automatic Activation Device, if the AAD is active
    // and the player is actually wearing the parachute, check the
    // altitude, if autoAltitude has been reached, deploy. If the immediate
    // AAD option is active, deploy after minFallDistance is reached.
    private void autoActivateDevice(EntityPlayer player) {
        boolean aadState;
        if (player.world.isRemote) { // client
            aadState = ClientConfiguration.getAADState();
        } else {
            aadState = ConfigHandler.getAADState();
        }

        if (aadState && !(player.getRidingEntity() instanceof EntityParachute)) {
            ItemStack heldItem = null;
            Iterable<ItemStack> heldEquipment = player.getHeldEquipment();
            for (ItemStack itemStack : heldEquipment) {
                if (itemStack != null && itemStack.getItem() instanceof ItemParachute) {
                    heldItem = itemStack;
                }
            }
            double AAD_FALL_DISTANCE = 5.0;
            if (player.fallDistance > AAD_FALL_DISTANCE) {
                if (heldItem != null && heldItem.getItem() instanceof ItemParachute) {
                    ((ItemParachute) heldItem.getItem()).deployParachute(player.world, player);
                }
            }
        }
    }

}
