/*
 * ParachuteModRegistration.java
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class ParachuteModRegistration {

    public static final Item parachuteItem = new ItemParachute("parachute");//.setUnlocalizedName("parachute").setRegistryName(Parachute.modid, "parachute");
    public static final EntityEquipmentSlot armorType = EntityEquipmentSlot.CHEST;
    static final int renderIndex = 0; // 0 is cloth, 1 is chain, 2 is iron, 3 is diamond and 4 is gold
    public static final Item packItem = new ItemParachutePack(ItemArmor.ArmorMaterial.LEATHER, renderIndex, armorType, "pack");//.setUnlocalizedName("pack").setRegistryName(packResource);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(packItem);
        event.getRegistry().register(parachuteItem);
    }

    @SubscribeEvent
    public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(new SoundEvent(new ResourceLocation(Parachute.modid + ":chuteopen")).setRegistryName("chuteopen"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(Parachute.modid + ":burn")).setRegistryName("burn"));
        ParachuteCommonProxy.openChute = getRegisteredSoundEvent(Parachute.modid + ":chuteopen");
        ParachuteCommonProxy.burnChute = getRegisteredSoundEvent(Parachute.modid + ":burn");
    }

    private static SoundEvent getRegisteredSoundEvent(String id) {
        SoundEvent soundevent = SoundEvent.REGISTRY.getObject(new ResourceLocation(id));
        if (soundevent == null) {
            throw new IllegalStateException("Invalid Sound requested: " + id);
        } else {
            return soundevent;
        }
    }
}
