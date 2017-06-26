
/*
 * ParachuteCommonProxy.java
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

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.common.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParachuteCommonProxy {

    private static final Logger logger = LogManager.getLogger(Parachute.MODID);
    private static final String parachuteName = "parachute";
    private static final String packName = "pack";
    private static boolean deployed = false;
    private static final double offsetY = 2.5;

    public static SoundEvent openChute;
    public static SoundEvent burnChute;

    public static ModelResourceLocation parachuteResource = new ModelResourceLocation(Parachute.MODID + ":" + parachuteName);
    public static ModelResourceLocation packResource = new ModelResourceLocation(Parachute.MODID + ":" + packName);

    public void preInit(FMLPreInitializationEvent event) {
        int entityID = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(Parachute.MODID, parachuteName), EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 3, true);
        PacketHandler.init();
    }

    @SuppressWarnings("unchecked") // no type specifiers in minecraft StatList
    public void Init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ConfigHandler.ConfigEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerFallEvent());
        MinecraftForge.EVENT_BUS.register(new ParachuteItemCraftedEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerMountEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHurtEvent());

        // add parachute crafting achievement
//        Parachute.buildParachute = new Advancement("achievement.buildParachute", "buildParachute", 0, 0, ParachuteModRegistration.parachuteItem, AdvancementList.Listener);
//        Parachute.buildParachute.registerStat();
//        AdvancementManager(new AchievementPage("Parachute", Parachute.buildParachute));

        // add the parachute statistics
        Parachute.parachuteDeployed.registerStat();
        Parachute.parachuteDistance.initIndependentStat().registerStat();
        int fv = ForgeVersion.getBuildVersion();
        if (fv < 1928) {
            StatList.ALL_STATS.add(Parachute.parachuteDeployed); // not needed in forge 1928 and higher
            StatList.ALL_STATS.add(Parachute.parachuteDistance);
            info("Forge Version is " + fv + ", manually registered parachute stats.");
        } else {
            info("Forge Version is " + fv + ", Forge auto registered parachute stats.");
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
        // move along, nothing to see here...
    }

    // logging convenience functions
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

    public static double getOffsetY() {
        return offsetY;
    }

}
