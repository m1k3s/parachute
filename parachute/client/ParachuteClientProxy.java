/*
 * ParachuteClientProxy.java
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
package com.parachute.client;

import com.parachute.common.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


@SuppressWarnings("unused")
public class ParachuteClientProxy implements IProxy {

    public void preInit() {
        ModelResourceLocation parachuteResource = new ModelResourceLocation(Parachute.MODID + ":" + Parachute.PARACHUTE_NAME);
        ModelResourceLocation packResource = new ModelResourceLocation(Parachute.MODID + ":" + Parachute.PACK_NAME);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
        ModelLoader.setCustomModelResourceLocation(Parachute.PARACHUTE_ITEM, 0, parachuteResource);
        ModelLoader.setCustomModelResourceLocation(Parachute.ITEM_PARACHUTE_PACK, 0, packResource);
        ModKeyBinding.registerKeyBinding();
    }

    public void Init() {
        MinecraftForge.EVENT_BUS.register(new ConfigHandler.ConfigEventHandler());
        MinecraftForge.EVENT_BUS.register(new ParachuteInputEvent());
        MinecraftForge.EVENT_BUS.register(new HudCompassRenderer());
    }

    public void postInit() {}
}