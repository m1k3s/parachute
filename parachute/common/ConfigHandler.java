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

import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

	public static Configuration config;

	private static boolean singleUse;
	private static int heightLimit;
	private static String chuteColor;
	private static boolean thermals;
	private static boolean autoDismount;
	private static boolean weatherAffectsDrift;
	private static boolean lavaThermals;
	private static double minLavaDistance;
	private static double maxLavaDistance;
	private static boolean constantTurbulence;
	private static boolean showContrails;
	private static boolean dismountInWater;
    private static boolean isAADActive;
	private static double aadAltitude;
	private static double minFallDistance;
	private static boolean aadImmediate;
	private static boolean useSpawnPoint;
	private static int[] waypoint;
	private static int[] homepoint;

	private static final String aboutComments = I18n.translateToLocalFormatted("config.about.desc", Parachute.name, Parachute.mcversion);
	private static final String usageComment = I18n.translateToLocal("config.usage.desc"); // false
	private static final String heightComment = I18n.translateToLocal("config.height.desc"); // 256
	private static final String thermalComment = I18n.translateToLocal("config.thermal.desc"); // true
	private static final String lavaThermalComment = I18n.translateToLocal("config.lavathermal.desc"); // false
	private static final String minLavaDistanceComment = I18n.translateToLocal("config.minlavadistance.desc"); // 3.0
	private static final String maxLavaDistanceComment = I18n.translateToLocal("config.maxlavadistance.desc"); // 48
	private static final String autoComment = I18n.translateToLocal("config.autodismount.desc"); // true
	private static final String weatherComment = I18n.translateToLocal("config.weather.desc"); // true
	private static final String turbulenceComment = I18n.translateToLocal("config.turbulence.desc"); // false
	private static final String trailsComment = I18n.translateToLocal("config.trails.desc"); // false
	private static final String dismountComment = I18n.translateToLocal("config.waterdismount.desc"); // false
	private static final String lavaDisablesComment = I18n.translateToLocal("config.lavadisables.desc"); // true
    private static final String isAADActiveComment = I18n.translateToLocal("config.aadactive.desc"); // false
    private static final String aadAltitudeComment = I18n.translateToLocal("config.aadaltitude.desc"); // 10 meters
	private static final String aadImmedComment = I18n.translateToLocal("config.aadimmediate.desc"); // > minFalldistance meters
    private static final String minFallDistanceComment = I18n.translateToLocal("config.minfalldistance.desc"); // 5 meters
	private static final String useSpawnPointComment = I18n.translateToLocal("config.usespawnpoint.desc");
	private static final String colorComment = I18n.translateToLocal("config.colors.desc");
	private static final String waypointComment = I18n.translateToLocal("config.waypoint.desc");
	private static final String homepointComment = I18n.translateToLocal("config.homepoint.desc");
	private static final String[] colorValues = {
			"random",
			"black",
			"blue",
			"brown",
			"cyan",
			"gray",
			"green",
			"light_blue",
			"lime",
			"magenta",
			"orange",
			"pink",
			"purple",
			"red",
			"silver",
			"white",
			"yellow",
			"custom0",
			"custom1",
			"custom2",
			"custom3",
			"custom4",
			"custom5",
			"custom6",
			"custom7",
			"custom8",
			"custom9",
	};

	public static void startConfig(FMLPreInitializationEvent event)
	{
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load(); // only need to load config once during preinit
		updateConfigInfo();
	}

	public static void updateConfigInfo()
	{
		try {
			config.setCategoryComment(Configuration.CATEGORY_GENERAL, aboutComments);

			singleUse = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment).getBoolean(false);
			heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment, 100, 256).getInt();
			thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
			lavaThermals = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", false, lavaThermalComment).getBoolean(false);
			minLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment, 2.0, 10.0).getDouble(3.0);
			maxLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment, 10.0, 100.0).getDouble(48.0);
			autoDismount = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", true, autoComment).getBoolean(true);
			chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "random", colorComment, colorValues).getString();
			weatherAffectsDrift = config.get(Configuration.CATEGORY_GENERAL, "weatherAffectsDrift", true, weatherComment).getBoolean(true);
			constantTurbulence = config.get(Configuration.CATEGORY_GENERAL, "constantTurbulence", false, turbulenceComment).getBoolean(false);
			showContrails = config.get(Configuration.CATEGORY_GENERAL, "showContrails", false, trailsComment).getBoolean(false);
			dismountInWater = config.get(Configuration.CATEGORY_GENERAL, "dismountInWater", false, dismountComment).getBoolean(false);
            boolean lavaDisablesThermals = config.get(Configuration.CATEGORY_GENERAL, "lavaDisablesThermals", true, lavaDisablesComment).getBoolean(true);
            isAADActive = config.get(Configuration.CATEGORY_GENERAL, "isAADActive", false, isAADActiveComment).getBoolean(false);
            aadAltitude = config.get(Configuration.CATEGORY_GENERAL, "aadAltitude", 10.0, aadAltitudeComment, 5.0, 100.0).getDouble(10.0);
            minFallDistance = config.get(Configuration.CATEGORY_GENERAL, "minFallDistance", 5.0, minFallDistanceComment, 3.0, 10.0).getDouble(5.0);
			aadImmediate = config.get(Configuration.CATEGORY_GENERAL, "aadImmediate", false, aadImmedComment).getBoolean(false);
			useSpawnPoint = config.get(Configuration.CATEGORY_GENERAL, "usespawnpoint", true, useSpawnPointComment).getBoolean(false);
			waypoint = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[] {0,0}, waypointComment).getIntList();
			homepoint = config.get(Configuration.CATEGORY_GENERAL, "homepoint", new int[] {0,0}, homepointComment).getIntList();

			// if lava thermals are allowed check allow/disallow space bar thermals
			thermals = !(lavaThermals && lavaDisablesThermals);

		} catch (Exception e) {
			Parachute.proxy.info(I18n.translateToLocal("info.message.configfail"));
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}

	public static boolean getDismountInWater()
	{
		return dismountInWater;
	}

	public static double getMaxAltitude()
	{
		return heightLimit;
	}

	public static boolean getAllowThermals()
	{
		return thermals;
	}

	public static String getChuteColor()
	{
		return chuteColor;
	}

	public static boolean getAllowLavaThermals()
	{
		return lavaThermals;
	}

	public static boolean getWeatherAffectsDrift()
	{
		return weatherAffectsDrift;
	}

	public static double getMinLavaDistance()
	{
		return minLavaDistance;
	}

	public static double getMaxLavaDistance()
	{
		return maxLavaDistance;
	}

	public static boolean getAllowturbulence()
	{
		return constantTurbulence;
	}

	public static boolean getShowContrails()
	{
		return showContrails;
	}

	public static boolean isAutoDismount()
	{
		return autoDismount;
	}

    public static boolean getIsAADActive()
    {
        return isAADActive;
    }

	public static void setAADState(boolean state)
	{
		Property prop = config.get(Configuration.CATEGORY_GENERAL, "isAADActive", false, isAADActiveComment);
		prop.set(state);
		config.save();
		isAADActive = state;
	}

    public static double getAADAltitude()
    {
        return aadAltitude;
    }

    public static double getMinFallDistance() {
        return minFallDistance;
    }

    public static int getParachuteDamageAmount()
	{
		if (singleUse) {
			return Parachute.parachuteItem.getMaxDamage() + 1;
		}
		return 1;
	}

	public static boolean getAADImmediate()
	{
		return aadImmediate;
	}

	public static boolean getUseSpawnPoint()
	{
		return useSpawnPoint;
	}

	public static BlockPos getWaypoint()
	{
		return new BlockPos(waypoint[0], 0, waypoint[1]);
	}
	
	public static void setWaypoint(int x, int z)
	{
		Property prop = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[] {0,0}, waypointComment);
		prop.set(new int[] {x, z});
		config.save();
		waypoint[0] = x;
		waypoint[1] = z;
	}
	
	public static BlockPos getHomepoint()
	{
		return new BlockPos(homepoint[0], 0, homepoint[1]);
	}
	
	public static void setHomepoint(int x, int z)
	{
		Property prop = config.get(Configuration.CATEGORY_GENERAL, "homepoint", new int[] {0,0}, homepointComment);
		prop.set(new int[] {x, z});
		config.save();
		homepoint[0] = x;
		homepoint[1] = z;
	}
	
	public static String getWaypointString() {
        return String.format("%d %d", waypoint[0], waypoint[1]);
    }
    
    public static String getHomepointString() {
		return String.format("%d %d", homepoint[0], homepoint[1]);
	}

}
