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
import net.minecraftforge.fml.common.event.*;


@SuppressWarnings("unused")
public class ParachuteClientProxy extends ParachuteCommonProxy {

    // grab the 'jump' key from the game settings. defaults to the space bar. This allows the
    // player to change the jump key and the parachute will use the new jump key
    private static final int ascendKey = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();

    @SuppressWarnings("unchecked")
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelResourceLocation parachuteResource = new ModelResourceLocation(Parachute.MODID + ":" + ParachuteCommonProxy.parachuteName);
        ModelResourceLocation packResource = new ModelResourceLocation(Parachute.MODID + ":" + ParachuteCommonProxy.packName);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new); // java 8
        ModelLoader.setCustomModelResourceLocation(ParachuteCommonProxy.parachuteItem, 0, parachuteResource);
        ModelLoader.setCustomModelResourceLocation(ParachuteCommonProxy.packItem, 0, packResource);
        ModKeyBindings.registerKeyBinding();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void Init(FMLInitializationEvent event) {
        super.Init(event);

        MinecraftForge.EVENT_BUS.register(new KeyPressTick(ascendKey));
        if (!ConfigHandler.getNoHUD() && ConfigHandler.getUseCompassHUD()) {
            MinecraftForge.EVENT_BUS.register(new HudCompassRenderer());
        } else {
            MinecraftForge.EVENT_BUS.register(new HudGuiRenderer());
        }
        MinecraftForge.EVENT_BUS.register(new ParachuteInputEvent());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

}
