/*
 * PlayerTickEventHandler.java
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

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Hand;
import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerTickEventHandler {

    private static boolean displayArmorBar;

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
    private void togglePlayerParachutePack(PlayerEntity player) {
        if (player != null) {
            ItemStack armor = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            ItemStack heldItemMainhand = player.getHeldItemMainhand();
            ItemStack heldItem = !heldItemMainhand.isEmpty() ? heldItemMainhand : player.getHeldItem(Hand.OFF_HAND);
            boolean deployed = player.getRidingEntity() instanceof ParachuteEntity;

            if (!deployed && armor.getItem() instanceof ParachutePackItem && (heldItem.isEmpty() || !(heldItem.getItem() instanceof ParachuteItem))) {
                player.inventory.armorInventory.set(EquipmentSlotType.CHEST.getIndex(), ItemStack.EMPTY);
            } else if (heldItem.getItem() instanceof ParachuteItem && armor.isEmpty()) {
                player.inventory.armorInventory.set(EquipmentSlotType.CHEST.getIndex(), new ItemStack(Parachute.RegistryEvents.ITEM_PARACHUTE_PACK));
            }
        }
    }

    // do not display the armorbar if the parachute is selected in the hot bar
    // and no other armor is being worn
    private void armorBarRenderingHandler(PlayerEntity player) {
        if (player != null) {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                if (player.getItemStackFromSlot(slot).getItem() instanceof ArmorItem) {
                    displayArmorBar = !(player.getItemStackFromSlot(slot).getItem() instanceof ParachutePackItem);
                }
            }
            if (Parachute.isClientSide(player.world)) {
                ForgeIngameGui.renderArmor = displayArmorBar;
            }
        }
    }

    // Handles the Automatic Activation Device, if the AAD is active
    // and the player is actually wearing the parachute, and the immediate
    // AAD option is active, deploy after minFallDistance is reached.
    private void autoActivateDevice(PlayerEntity player) {
        boolean aadState = Parachute.getAADState();

        if (aadState && !(player.getRidingEntity() instanceof ParachuteEntity)) {
            ItemStack heldItem = null;
            Iterable<ItemStack> heldEquipment = player.getHeldEquipment();
            for (ItemStack itemStack : heldEquipment) {
                if (itemStack != null && itemStack.getItem() instanceof ParachuteItem) {
                    heldItem = itemStack;
                }
            }
            double AAD_FALL_DISTANCE = 5.0;
            if (player.fallDistance > AAD_FALL_DISTANCE) {
                if (heldItem != null && heldItem.getItem() instanceof ParachuteItem) {
                    ((ParachuteItem) heldItem.getItem()).deployParachute(player.world, player);
                }
            }
        }
    }
}
