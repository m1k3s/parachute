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
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = Parachute.MODID,
    name = Parachute.NAME,
    version = Parachute.MODVERSION,
    acceptedMinecraftVersions = Parachute.MCVERSION,
    guiFactory = Parachute.GUIFACTORY
)

public class Parachute {

    private static Logger logger;// = LogManager.getLogger(Parachute.MODID);
    public static final String PARACHUTE_NAME = "parachute";
    public static final String PACK_NAME = "pack";

    public static SoundEvent OPENCHUTE;
    public static SoundEvent LIFTCHUTE;

    public static final Item PARACHUTE_ITEM = new ItemParachute(Parachute.PARACHUTE_NAME);
    public static final EntityEquipmentSlot ARMOR_TYPE = EntityEquipmentSlot.CHEST;
    static final int RENDER_INDEX = 1; // 0 is cloth, 1 is chain, 2 is iron, 3 is diamond and 4 is gold
    public static final Item ITEM_PARACHUTE_PACK = new ItemParachutePack(ArmorMaterial.LEATHER, RENDER_INDEX, ARMOR_TYPE, Parachute.PACK_NAME);

    public static final String MODID = "parachutemod";
    public static final String MODVERSION = "1.7.4";
    public static final String MCVERSION = "1.12.2";
    public static final String NAME = "Parachute Mod NG";
    public static final String GUIFACTORY = "com.parachute.client.ParachuteConfigGUIFactory";
    public static StatBasic parachuteDeployed = new StatBasic("stat.parachuteDeployed", new TextComponentTranslation("stat.parachuteDeployed"));
    public static StatBasic parachuteDistance = new StatBasic("stat.parachuteDistance",
            new TextComponentTranslation("stat.parachuteDistance"), StatBase.distanceStatType);

    @SidedProxy(clientSide = "com.parachute.client.ParachuteClientProxy", serverSide = "com.parachute.common.ParachuteServerProxy")
    public static IProxy clientProxy;

    @Mod.Instance(MODID)
    public static Parachute instance;

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        ConfigHandler.preInit(event);
        int entityID = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(Parachute.MODID, PARACHUTE_NAME),
                EntityParachute.class, PARACHUTE_NAME, entityID, Parachute.instance, 80, 5, true);

        GameRegistry.findRegistry(Item.class).registerAll(ITEM_PARACHUTE_PACK, PARACHUTE_ITEM);
        PacketHandler.init();
        clientProxy.preInit();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());

        // add the parachute statistics
        Parachute.parachuteDeployed.registerStat();
        Parachute.parachuteDistance.initIndependentStat().registerStat();
        clientProxy.Init();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        clientProxy.postInit();
    }

    public String getVersion() {
        return Parachute.MODVERSION;
    }

    public void info(String s) {
        logger.info(s);
    }

    public static boolean isFalling(EntityPlayer player) {
        return (player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder());
    }

    @SuppressWarnings("unused")
    public static Logger getLogger() {
        return logger;
    }

}


