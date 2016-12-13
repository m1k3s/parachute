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
// Copyright © 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.client;

import com.parachute.common.ParachuteCommonProxy;
import com.parachute.common.EntityParachute;
import com.parachute.common.Parachute;
// import com.parachute.common.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.*;


@SuppressWarnings("unused")
public class ParachuteClientProxy extends ParachuteCommonProxy {

    // grab the 'jump' key from the game settings. defaults to the space bar. This allows the
    // player to change the jump key and the parachute will use the new jump key
    private static final int ascendKey = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
    private static final int leftKey = Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode();
    private static final int rightKey = Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode();
    private static final int forwardKey = Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode();
    private static final int backKey = Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode();

    @SuppressWarnings("unchecked")
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // ConfigHandler.startConfig(event);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new); // java 8
        ModelLoader.setCustomModelResourceLocation(Parachute.parachuteItem, 0, ParachuteCommonProxy.parachuteResource);
        ModelLoader.setCustomModelResourceLocation(Parachute.packItem, 0, ParachuteCommonProxy.packResource);

        // info(Parachute.modid + I18n.format("info.client.preinit"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void Init(FMLInitializationEvent event) {
        super.Init(event);

        MinecraftForge.EVENT_BUS.register(new KeyPressTick(ascendKey));
//        MinecraftForge.EVENT_BUS.register(new ControlKeyPressTick(leftKey, rightKey, forwardKey, backKey));
        MinecraftForge.EVENT_BUS.register(new HudGuiRenderer());

        // info(Parachute.modid + I18n.format("info.client.init"));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        // info(Parachute.modid + I18n.format("info.client.postinit"));
    }

    // user has changed entries in the GUI config. save the results.
    // @SuppressWarnings("unused")
    // @SubscribeEvent
    // public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    // {
    // if (event.getModID().equals(Parachute.modid)) {
    // info(I18n.format("info.message.changes", Parachute.name));
    // ConfigHandler.updateConfigInfo();
    // }
    // }

}
