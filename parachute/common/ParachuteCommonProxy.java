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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

public class ParachuteCommonProxy {

	private static final Logger logger = FMLLog.getLogger();
	//	private final int entityID = EntityRegistry.findGlobalUniqueEntityId();
	static ItemArmor.ArmorMaterial NYLON = EnumHelper.addArmorMaterial("nylon", "", 15, new int[] {2, 5, 4, 1}, 12); // same as CHAIN
	static Item.ToolMaterial RIPSTOP = EnumHelper.addToolMaterial("ripstop", 0, 59, 2.0F, 0, 15); // same as WOOD
	private static final int armorType = 1; // armor type: 0 = helmet, 1 = chestplate, 2 = leggings,   3 = boots
	public static final int armorSlot = 2;  // armor slot: 0 = boots,  1 = leggings,   2 = chestplate, 3 = helmet
	public static final String hopnpopName = "hop_and_pop";
	public static final String parachuteName = "parachute";
	public static final String ripcordName = "ripcord";
	public static final String aadName = "auto_activation_device";
	private static boolean deployed = false;
	private static final double offsetY = 2.5;
	private static int entityID = 1;

	public void preInit()
	{
		EntityRegistry.registerModEntity(EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 20, true);

		final int renderIndex = 0;
		Parachute.parachuteItem = (ItemParachute) (new ItemParachute(NYLON, renderIndex, armorType)).setUnlocalizedName(parachuteName);
		GameRegistry.registerItem(Parachute.parachuteItem, parachuteName);

		Parachute.ripcordItem = (ItemRipCord) (new ItemRipCord()).setUnlocalizedName(ripcordName);
		GameRegistry.registerItem(Parachute.ripcordItem, ripcordName);

		Parachute.aadItem = (ItemAutoActivateDevice) (new ItemAutoActivateDevice()).setUnlocalizedName(aadName);
		GameRegistry.registerItem(Parachute.aadItem, aadName);

		Parachute.hopnpopItem = (ItemHopAndPop) (new ItemHopAndPop(RIPSTOP)).setUnlocalizedName(hopnpopName);
		GameRegistry.registerItem(Parachute.hopnpopItem, hopnpopName);

		PacketHandler.init();
	}

	public void Init()
	{
		FMLCommonHandler.instance().bus().register(Parachute.instance);

		// recipes to craft the parachutes, ripcord and AAD
		GameRegistry.addRecipe(new ItemStack(Parachute.parachuteItem, 1), new Object[] {
			"###", "X X", " L ", '#', Blocks.wool, 'X', Items.string, 'L', Items.leather
		});

		GameRegistry.addRecipe(new ItemStack(Parachute.hopnpopItem, 1), new Object[] {
			"###", "X X", " X ", '#', Blocks.wool, 'X', Items.string
		});

		GameRegistry.addRecipe(new ItemStack(Parachute.ripcordItem, 1), new Object[] {
			"#  ", " # ", "  *", '#', Items.string, '*', Items.iron_ingot
		});

		GameRegistry.addRecipe(new ItemStack(Parachute.aadItem, 1), new Object[] {
			" * ", " % ", " # ", '*', Items.comparator, '%', Items.redstone, '#', Parachute.ripcordItem});

		FMLCommonHandler.instance().bus().register(new AADTick());
	}

	public void postInit()
	{

	}

	public void info(String s)
	{
		logger.info(s);
	}

	public void warn(String s)
	{
		logger.warn(s);
	}

	public static boolean playerIsWearingParachute(EntityPlayer player)
	{
		ItemStack stack = player == null ? null : player.getCurrentArmor(armorSlot);
		if (stack != null) {
			Item item = stack.getItem();
			if (item != null && item instanceof ItemParachute) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFalling(EntityPlayer entity)
	{
		return (entity.fallDistance > 0.0F && !entity.onGround && !entity.isOnLadder());
	}

	public static boolean onParachute(EntityPlayer entity)
	{
		return entity.isRiding() && deployed;
	}

	public static boolean getDeployed()
	{
		return deployed;
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
