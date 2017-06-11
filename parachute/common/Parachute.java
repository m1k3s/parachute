/*
 * Parachute.java
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

import net.minecraft.item.Item;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(
    modid = Parachute.modid,
    name = Parachute.name,
    version = Parachute.modversion,
    acceptedMinecraftVersions = Parachute.mcversion,
    guiFactory = Parachute.guifactory,
    dependencies = "required-after:FML"
)

public class Parachute {

    public static final String modid = "parachutemod";
    public static final String modversion = "1.7.0";
    public static final String mcversion = "1.12";
    public static final String name = "Parachute Mod NG";
    public static final String guifactory = "com.parachute.client.ParachuteConfigGUIFactory";
    public static StatBasic parachuteDeployed = new StatBasic("stat.parachuteDeployed", new TextComponentTranslation("stat.parachuteDeployed"));
    public static StatBasic parachuteDistance = new StatBasic("stat.parachuteDistance", new TextComponentTranslation("stat.parachuteDistance"), StatBase.distanceStatType);
//    public static Achievement buildParachute;

    @SidedProxy(clientSide = "com.parachute.client.ParachuteClientProxy", serverSide = "com.parachute.common.ParachuteServerProxy")
    public static ParachuteCommonProxy proxy;

    public static Item parachuteItem;
    public static Item packItem;

    @Mod.Instance(modid)
    public static Parachute instance;

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Construct(FMLConstructionEvent event) {
        int buildVersion = ForgeVersion.getBuildVersion();
        int minimumForgeBuildVersion = 2201;
        if (buildVersion < minimumForgeBuildVersion) {
            proxy.error(String.format("This mod requires Forge Mod Loader build version of %d or higher", minimumForgeBuildVersion));
            proxy.error(String.format("You are running Forge Mod Loader build version %d", buildVersion));
        }
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.preInit(event);
        proxy.preInit(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        proxy.Init(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void ServerLoad(FMLServerStartingEvent event) {
        // register parachute commands
        event.registerServerCommand(new SetWaypointCommand());
        event.registerServerCommand(new EnableWaypointCommand());
        event.registerServerCommand(new ShowWaypointCommand());
    }

    public String getVersion() {
        return Parachute.modversion;
    }

}


