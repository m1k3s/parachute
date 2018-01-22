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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = Parachute.MODID,
    name = Parachute.NAME,
    version = Parachute.MODVERSION,
    acceptedMinecraftVersions = Parachute.MCVERSION,
    guiFactory = Parachute.GUIFACTORY
)

public class Parachute {

    private static final Logger logger = LogManager.getLogger(Parachute.MODID);
    public static final String parachuteName = "parachute";
    public static final String packName = "pack";
    private static boolean deployed = false;

    public static SoundEvent OPENCHUTE;
    public static SoundEvent LIFTCHUTE;

    public static final Item parachuteItem = new ItemParachute(Parachute.parachuteName);
    public static final EntityEquipmentSlot armorType = EntityEquipmentSlot.CHEST;
    static final int renderIndex = 1; // 0 is cloth, 1 is chain, 2 is iron, 3 is diamond and 4 is gold
    public static final Item packItem = new ItemParachutePack(ItemArmor.ArmorMaterial.LEATHER, renderIndex, armorType, Parachute.packName);

    public static final String MODID = "parachutemod";
    public static final String MODVERSION = "1.7.4";
    public static final String MCVERSION = "1.12.2";
    public static final String NAME = "Parachute Mod NG";
    public static final String GUIFACTORY = "com.parachute.client.ParachuteConfigGUIFactory";
    public static StatBasic parachuteDeployed = new StatBasic("stat.parachuteDeployed", new TextComponentTranslation("stat.parachuteDeployed"));
    public static StatBasic parachuteDistance = new StatBasic("stat.parachuteDistance", new TextComponentTranslation("stat.parachuteDistance"), StatBase.distanceStatType);

    @SidedProxy(clientSide = "com.parachute.client.ParachuteClientProxy", serverSide = "com.parachute.common.ParachuteServerProxy")
    public static IProxy proxy;

    @Mod.Instance(MODID)
    public static Parachute instance;

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Construct(FMLConstructionEvent event) {
        int buildVersion = ForgeVersion.getBuildVersion();
        int minimumForgeBuildVersion = 2555;
        if (buildVersion < minimumForgeBuildVersion) {
            error(String.format("This mod requires Forge Mod Loader build version of %d or higher", minimumForgeBuildVersion));
            error(String.format("You are running Forge Mod Loader build version %d", buildVersion));
        }
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.preInit(event);
        int entityID = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(Parachute.MODID, parachuteName), EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 3, true);
        GameRegistry.findRegistry(Item.class).registerAll(packItem, parachuteItem);
        PacketHandler.init();
        proxy.preInit();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ConfigHandler.ConfigEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerFallEvent());
        MinecraftForge.EVENT_BUS.register(new ParachuteItemCraftedEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerMountEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());

        // add parachute crafting advancement
//        Parachute.buildParachute = new Advancement(new ResourceLocation(Parachute.MODID, parachuteName), "buildParachute", 0, 0, ParachuteCommonProxy.parachuteItem, "");
//        Parachute.buildParachute.registerStat();
//        AdvancementManager(new AchievementPage("Parachute", Parachute.buildParachute));

        // add the parachute statistics
        Parachute.parachuteDeployed.registerStat();
        Parachute.parachuteDistance.initIndependentStat().registerStat();
        proxy.Init();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
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
        return Parachute.MODVERSION;
    }

    public void info(String s) {
        logger.info(s);
    }

    public void error(String s) {
        logger.error(s);
    }

    public static boolean getAutoActivateAltitude(EntityPlayer player) {
        boolean altitudeReached = false;
        double altitude = ConfigHandler.getAADAltitude();
        double minFallDistance = ConfigHandler.getMinFallDistance();

        BlockPos blockPos = new BlockPos(player.posX, player.posY - altitude, player.posZ);

        if (!player.world.isAirBlock(blockPos) && player.fallDistance > minFallDistance) {
            altitudeReached = true;
        }
        return altitudeReached;
    }

    public static boolean canActivateAADImmediate(EntityPlayer player) {
        double minFallDistance = ConfigHandler.getMinFallDistance();
        return player.fallDistance > minFallDistance;
    }

    public static boolean isFalling(EntityPlayer player) {
        return (player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder());
    }

    public static boolean onParachute(EntityPlayer entity) {
        return entity.isRiding() && isDeployed();
    }

    public static void setDeployed(boolean isDeployed) {
        deployed = isDeployed;
    }

    public static boolean isDeployed() {
        return deployed;
    }

}


