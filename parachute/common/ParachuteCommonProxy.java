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
package com.parachute.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParachuteCommonProxy {

	private static final Logger logger = LogManager.getLogger(Parachute.modid);
    private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = leggings, 3 = boots
	public static final int armorSlot = 2;  // armor slot: 3 = helmet, 2 = chestplate, 1 = leggings, 0 = boots    
	public static final String parachuteName = "parachute";
	public static final String packName = "pack";
	private static boolean deployed = false;
	private static final double offsetY = 2.5;

	public void preInit()
	{
        int entityID = 1;
        EntityRegistry.registerModEntity(EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 20, true);

		Parachute.parachuteItem = new ItemParachute(ToolMaterial.IRON);
		Parachute.parachuteItem.setUnlocalizedName(parachuteName);
		GameRegistry.registerItem(Parachute.parachuteItem, parachuteName);

		final int renderIndex = 0; // 0 is cloth, 1 is chain, 2 is iron, 3 is diamond and 4 is gold
		Parachute.packItem = new ItemParachutePack(ArmorMaterial.LEATHER, renderIndex, armorType);
		Parachute.packItem.setUnlocalizedName(packName);
		GameRegistry.registerItem(Parachute.packItem, packName);

		PacketHandler.init();
	}

	public void Init()
	{
        MinecraftForge.EVENT_BUS.register(Parachute.instance);
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerFallEvent());
        MinecraftForge.EVENT_BUS.register(new ParachuteItemCraftedEvent());

		// recipe to craft the parachute
		GameRegistry.addRecipe(new ItemStack(Parachute.parachuteItem, 1), "###", "X X", " L ", '#', Blocks.wool, 'X', Items.string, 'L', Items.leather);

		// add parachute crafting achievement
        Parachute.buildParachute = new Achievement("achievement.buildParachute", "buildParachute", 0, 0, Parachute.parachuteItem, AchievementList.buildWorkBench);
        Parachute.buildParachute.registerStat();
        AchievementPage.registerAchievementPage(new AchievementPage("Parachute", Parachute.buildParachute));

        // add the parachute statistics
        Parachute.parachuteDeployed.registerStat();
        StatList.allStats.add(Parachute.parachuteDeployed);
		Parachute.parachuteDistance.initIndependentStat().registerStat();
		StatList.allStats.add(Parachute.parachuteDistance);
	}

	public void postInit()
	{
		// move along, nothing to see here...
	}

	// logging convenience functions
	public void info(String s)
	{
		logger.info(s);
	}

//	public void warn(String s)
//	{
//		logger.warn(s);
//	}
//
//	public void error(String s)
//	{
//		logger.error(s);
//	}

	public static boolean getAutoActivateAltitude(EntityPlayer player)
	{
		boolean altitudeReached = false;
        double altitude = ConfigHandler.getAADAltitude();

		BlockPos blockPos = new BlockPos(player.posX, player.posY - altitude, player.posZ);

		if (!player.worldObj.isAirBlock(blockPos) && player.fallDistance > 5.0) {
			altitudeReached = true;
		}
		return altitudeReached;
	}

	public static boolean isFalling(EntityPlayer entity)
	{
		return (entity.fallDistance > 0.0F && !entity.onGround && !entity.isOnLadder());
	}

	public static boolean onParachute(EntityPlayer entity)
	{
		return entity.isRiding() && deployed;
	}

	public static void setDeployed(boolean isDeployed)
	{
		deployed = isDeployed;
	}
	
	public static double getOffsetY()
	{
		return offsetY;
	}
	
}
