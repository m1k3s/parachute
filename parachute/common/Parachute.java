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

import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
		modid = Parachute.modid,
		name = Parachute.name,
		version = Parachute.modversion,
		acceptedMinecraftVersions = Parachute.mcversion,
		guiFactory = Parachute.guifactory
)

public class Parachute {

	public static final String modid = "parachutemod";
	public static final String modversion = "1.0.0";
	public static final String mcversion = "1.8.8";
	public static final String name = "Parachute Mod NG";
	public static final String guifactory = "com.parachute.client.ParachuteConfigGUIFactory";
	public static StatBasic parachuteDeployed = new StatBasic("stat.parachuteDeployed", new ChatComponentTranslation("stat.parachuteDeployed"));
	public static StatBasic parachuteDistance = new StatBasic("stat.parachuteDistance", new ChatComponentTranslation("stat.parachuteDistance", new Object[0]), StatBase.distanceStatType);
    public static Achievement buildParachute;

	@SidedProxy(clientSide = "com.parachute.client.ParachuteClientProxy", serverSide = "com.parachute.common.ParachuteServerProxy")
	public static ParachuteCommonProxy proxy;

	public static ItemParachute parachuteItem;
	public static ItemParachutePack packItem;

	@Mod.Instance(modid)
	public static Parachute instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigHandler.startConfig(event);
		proxy.preInit();
	}

	@Mod.EventHandler
	public void Init(FMLInitializationEvent event)
	{
		proxy.Init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}

	public String getVersion()
	{
		return Parachute.modversion;
	}

	// user has changed entries in the GUI config. save the results.
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equals(Parachute.modid)) {
			Parachute.proxy.info("Configuration changes have been updated for the " + Parachute.name);
			ConfigHandler.updateConfigInfo();
		}
	}

}
