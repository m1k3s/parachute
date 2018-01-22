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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


@SuppressWarnings("unused")
public class ParachuteClientProxy implements IProxy {

    // grab the 'jump' key from the game settings. defaults to the space bar. This allows the
    // player to change the jump key and the parachute will use the new jump key
    private static final int ascendKey = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();

    public void preInit() {
        ModelResourceLocation parachuteResource = new ModelResourceLocation(Parachute.MODID + ":" + Parachute.parachuteName);
        ModelResourceLocation packResource = new ModelResourceLocation(Parachute.MODID + ":" + Parachute.packName);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
        ModelLoader.setCustomModelResourceLocation(Parachute.parachuteItem, 0, parachuteResource);
        ModelLoader.setCustomModelResourceLocation(Parachute.packItem, 0, packResource);
        RenderParachute.setParachuteColor(ConfigHandler.getChuteColor());
    }

    public void Init() {
        MinecraftForge.EVENT_BUS.register(new ConfigHandler.ConfigEventHandler());
        MinecraftForge.EVENT_BUS.register(new KeyPressTick(ascendKey));
        MinecraftForge.EVENT_BUS.register(new ParachuteInputEvent());
        MinecraftForge.EVENT_BUS.register(new HudCompassRenderer());
        MinecraftForge.EVENT_BUS.register(new HudGuiRenderer());
    }

    public void postInit() {}
}
