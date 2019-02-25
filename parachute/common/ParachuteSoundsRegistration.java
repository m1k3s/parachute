/*
 * ParachuteSoundsRegistration.java
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

//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.SoundEvent;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import javax.annotation.Nonnull;
//
//@SuppressWarnings("unused")
//@Mod.EventBusSubscriber
//public class ParachuteSoundsRegistration {
//
//    @SubscribeEvent
//    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
//        Parachute.OPENCHUTE = new SoundEvent(new ResourceLocation(Parachute.MODID + ":chuteopen")).setRegistryName("chuteopen");
//        Parachute.LIFTCHUTE = new SoundEvent(new ResourceLocation(Parachute.MODID + ":lift")).setRegistryName("lift");
//
//        event.getRegistry().registerAll(Parachute.OPENCHUTE, Parachute.LIFTCHUTE);

//        Parachute.OPENCHUTE = getRegisteredSoundEvent(Parachute.MODID + ":chuteopen");
//        Parachute.LIFTCHUTE = getRegisteredSoundEvent(Parachute.MODID + ":lift");
//    }

//    private static SoundEvent getRegisteredSoundEvent(String id) {
//        SoundEvent soundevent = SoundEvent.registerSounds().REGISTRY.getObject(new ResourceLocation(id));
//        if (soundevent == null) {
//            throw new IllegalStateException("Invalid Sound requested: " + id);
//        } else {
//            return soundevent;
//        }
//    }

//}
