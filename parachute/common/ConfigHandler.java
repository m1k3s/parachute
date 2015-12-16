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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

	public static Configuration config;
	public static final String aboutCategory = "About";

	private static boolean singleUse = false;
	private static int heightLimit = 256;
	private static String chuteColor = "random";
	private static boolean thermals = true;
	private static boolean autoDismount = true;
	private static boolean weatherAffectsDrift;
	private static boolean lavaThermals;
	private static double minLavaDistance;
	private static double maxLavaDistance;
	private static boolean allowTurbulence;
	private static boolean showContrails;
	private static boolean altitudeMSL;
	private static boolean fixedGlideRate;
	private static boolean dismountInWater;
	private static boolean lavaDisablesThermals;
	private static boolean isAADActive;
	private static double aadAltitude;

	private static final String aboutComments = Parachute.name + " Config\nMichael Sheppard (crackedEgg)"
			+ " For Minecraft Version " + Parachute.mcversion + "\n";
	private static final String usageComment = "set to true for parachute single use"; // false
	private static final String heightComment = "0 (zero) disables altitude limiting"; // 256
	private static final String thermalComment = "enable thermal rise by pressing the space bar"; // true
	private static final String lavaThermalComment = "use lava heat to get thermals to rise up, disables space bar thermals"; // false
	private static final String minLavaDistanceComment = "minimum distance from lava to grab thermals, if you\n"
			+ "go less than 3.0 you will most likely dismount in the lava!"; // 3.0
	private static final String maxLavaDistanceComment = "maximum distance to rise from lava thermals"; // 48
	private static final String autoComment = "If true the parachute will dismount the player automatically,\n"
			+ "if false the player has to use LSHIFT to dismount the parachute"; // true
	private static final String weatherComment = "set to false if you don't want the drift rate to be affected by bad weather"; // true
	private static final String turbulenceComment = "set to true to feel the turbulent world of Minecraft"; // false
	private static final String trailsComment = "set to true to show contrails from parachute"; // false
	private static final String altitudeMSLComment = "false to show altitude above ground, true shows altitude above ground (MSL)"; // false
	private static final String glideRateComment = "true to use a constant glide rate, forward speed"; // false
    private static final String dismountComment = "true to dismount in water"; // false
    private static final String lavaDisablesComment = "normal thermals are disabled by lava thermals"; // true
    private static final String isAADActiveComment = "whether or not the AAD is active"; // false
    private static final String aadAltitudeComment = "altitude (in meters) at which auto deploy occurs"; // 10 meters
	private static final String colorComment = "Parachute Colors Allowed:\n"
			+ "black, blue, brown, cyan, gray, green, light_blue, lime,\n"
			+ "magenta, orange, pink, purple, red, silver, white, yellow,\n"
			+ "random - randomly chosen color each time chute is opened\n" // random is default
			+ "custom[0-9] - allows use of a custom texture called 'custom' with a single number appended";

	public static void startConfig(FMLPreInitializationEvent event)
	{
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load(); // only need to load config once during pre init
		updateConfigInfo();
	}

	public static void updateConfigInfo()
	{
		try {
			config.addCustomCategoryComment(aboutCategory, aboutComments);

			singleUse = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment).getBoolean(false);
			heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment).getInt();
			thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
			lavaThermals = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", false, lavaThermalComment).getBoolean(false);
			minLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment).getDouble(3.0);
			maxLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment).getDouble(48.0);
			autoDismount = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", true, autoComment).getBoolean(true);
			chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "random", colorComment).getString();
			weatherAffectsDrift = config.get(Configuration.CATEGORY_GENERAL, "weatherAffectsDrift", true, weatherComment).getBoolean(true);
			allowTurbulence = config.get(Configuration.CATEGORY_GENERAL, "allowTurbulence", false, turbulenceComment).getBoolean(false);
			showContrails = config.get(Configuration.CATEGORY_GENERAL, "showContrails", false, trailsComment).getBoolean(false);
			altitudeMSL = config.get(Configuration.CATEGORY_GENERAL, "altitudeMSL", false, altitudeMSLComment).getBoolean(false);
			fixedGlideRate = config.get(Configuration.CATEGORY_GENERAL, "fixedGlideRate", false, glideRateComment).getBoolean(false);
            dismountInWater = config.get(Configuration.CATEGORY_GENERAL, "dismountInWater", false, dismountComment).getBoolean(false);
            lavaDisablesThermals = config.get(Configuration.CATEGORY_GENERAL, "lavaDisablesThermals", true, lavaDisablesComment).getBoolean(true);
            isAADActive = config.get(Configuration.CATEGORY_GENERAL, "isAADActive", false, isAADActiveComment).getBoolean(false);
            aadAltitude = config.get(Configuration.CATEGORY_GENERAL, "aadAltitude", 10.0, aadAltitudeComment).getDouble(10.0);

			// if using lava thermals check allow/disallow space bar thermals, clamp the minimum lava distance.
			if (lavaThermals) {
                thermals = lavaDisablesThermals ? false : true;
				minLavaDistance = minLavaDistance < 2.0 ? 2.0 : minLavaDistance;
			}
		} catch (Exception e) {
			Parachute.proxy.info("failed to load or read the config file");
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

    public static boolean getIsAADActive()
    {
        return isAADActive;
    }

    public static double getAADAltitude()
    {
        return aadAltitude;
    }
	
	public static boolean getFixedGlideRate()
	{
		return fixedGlideRate;
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
		return allowTurbulence;
	}

	public static boolean getShowContrails()
	{
		return showContrails;
	}

	public static boolean isAutoDismount()
	{
		return autoDismount;
	}

	public static boolean getAltitudeMSL()
	{
		return altitudeMSL;
	}

	public static int getParachuteDamageAmount()
	{
		if (singleUse) {
			return Parachute.parachuteItem.getMaxDamage() + 1;
		}
		return 1;
	}
}
