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

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParachuteCommonProxy {

    private static final Logger logger = LogManager.getLogger(Parachute.modid);
    public static final EntityEquipmentSlot armorType = EntityEquipmentSlot.CHEST; // type: ARMOR, index: 0 = helmet, 1 = chestplate, 2 = leggings, 3 = boots
    private static final String parachuteName = "parachute";
    private static final String packName = "pack";
    private static boolean deployed = false;
    private static final double offsetY = 2.5;

    public static SoundEvent openChute;
    public static SoundEvent burnChute;

    protected static ModelResourceLocation parachuteResource = new ModelResourceLocation(Parachute.modid + ":" + parachuteName);
    protected static ModelResourceLocation packResource = new ModelResourceLocation(Parachute.modid + ":" + packName);

    public void preInit(FMLPreInitializationEvent event) {
        int entityID = 1;
        //ResourceLocation registryName, Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates
        EntityRegistry.registerModEntity(parachuteResource, EntityParachute.class, parachuteName, entityID, Parachute.instance, 80, 3, true);

        Parachute.parachuteItem = new ItemParachute().setUnlocalizedName(parachuteName).setRegistryName(parachuteResource);
        GameRegistry.register(Parachute.parachuteItem);

        final int renderIndex = 0; // 0 is cloth, 1 is chain, 2 is iron, 3 is diamond and 4 is gold
        Parachute.packItem = new ItemParachutePack(ArmorMaterial.LEATHER, renderIndex, armorType).setUnlocalizedName(packName).setRegistryName(packResource);
        GameRegistry.register(Parachute.packItem);

        GameRegistry.register(new SoundEvent(new ResourceLocation(Parachute.modid + ":chuteopen")).setRegistryName("chuteopen"));
        openChute = getRegisteredSoundEvent(Parachute.modid + ":chuteopen");
        
        GameRegistry.register(new SoundEvent(new ResourceLocation(Parachute.modid + ":burn")).setRegistryName("burn"));
        burnChute = getRegisteredSoundEvent(Parachute.modid + ":burn");
        
        PacketHandler.init();
    }

    @SuppressWarnings("unchecked") // no type specifiers in minecraft StatList
    public void Init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(Parachute.instance);
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerFallEvent());
        MinecraftForge.EVENT_BUS.register(new ParachuteItemCraftedEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerMountEvent());

        // recipe to craft the parachute
        GameRegistry.addRecipe(new ItemStack(Parachute.parachuteItem, 1), "###", "X X", " L ", '#', Blocks.WOOL, 'X', Items.STRING, 'L', Items.LEATHER);

        // add parachute crafting achievement
        Parachute.buildParachute = new Achievement("achievement.buildParachute", "buildParachute", 0, 0, Parachute.parachuteItem, AchievementList.BUILD_WORK_BENCH);
        Parachute.buildParachute.registerStat();
        AchievementPage.registerAchievementPage(new AchievementPage("Parachute", Parachute.buildParachute));

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

        if (!player.worldObj.isAirBlock(blockPos) && player.fallDistance > minFallDistance) {
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
        return entity.isRiding() && deployed;
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

    private static SoundEvent getRegisteredSoundEvent(String id) {
        SoundEvent soundevent = SoundEvent.REGISTRY.getObject(new ResourceLocation(id));
        if (soundevent == null) {
            throw new IllegalStateException("Invalid Sound requested: " + id);
        } else {
            return soundevent;
        }
    }

}
