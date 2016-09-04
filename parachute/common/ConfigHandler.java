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

import net.minecraft.util.math.BlockPos;
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
    private static double burnVolume;
    private static int[] waypoint;
    private static int[] homepoint;

    private static final String aboutComments = String.format("%s Config - Michael Sheppard (crackedEgg) [Minecraft Version %s]", Parachute.name, Parachute.mcversion);
    private static final String usageComment = "set to true for parachute single use"; // false
    private static final String heightComment = "0 (zero) disables altitude limiting"; // 256
    private static final String thermalComment = "enable thermal rise by pressing the space bar"; // true
    private static final String lavaThermalComment = "use lava heat to get thermals to rise up, optionally disables space bar thermals"; // false
    private static final String minLavaDistanceComment = "minimum distance from lava to grab thermals, if you go less than 3.0 you will most likely dismount in the lava!"; // 3.0
    private static final String maxLavaDistanceComment = "maximum distance to rise from lava thermals"; // 48
    private static final String autoComment = "If true the parachute will dismount the player automatically, if false the player has to use LSHIFT to dismount the parachute"; // true
    private static final String weatherComment = "set to false if you don't want the drift rate to be affected by bad weather"; // true
    private static final String turbulenceComment = "set to true to always feel the turbulent world of Minecraft"; // false
    private static final String trailsComment = "set to true to show contrails from parachute"; // false
    private static final String dismountComment = "true to dismount in water"; // false
    private static final String lavaDisablesComment = "normal thermals are disabled by lava thermals"; // true
    private static final String isAADActiveComment = "whether or not the AAD starts active"; // false
    private static final String aadAltitudeComment = "altitude (in meters) at which auto deploy occurs"; // 10 meters
    private static final String aadImmedComment = "AAD deploys immediately after the player falls more than minFallDistance"; // > minFalldistance meters
    private static final String minFallDistanceComment = "minimum distance to fall before the AAD deploys"; // 5 meters
    private static final String useSpawnPointComment = "use spawn point for home direction if true or input your own coords if false";
    private static final String burnVolumeComment = "set the burn sound volume (0.0 to 1.0)";
    private static final String colorComment = "Select a parachute color, random, or custom[0-9]";
    private static final String waypointComment = "waypoint coordinates [X, Z]";
    private static final String homepointComment = "homepoint coordinates [X, Z]";
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

    public static void startConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load(); // only need to load config once during preinit
        updateConfigInfo();
    }

    public static void updateConfigInfo() {
        try {
            config.setCategoryComment(Configuration.CATEGORY_GENERAL, aboutComments);

            singleUse = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment).getBoolean(false);
            heightLimit = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment, 100, 256).getInt();
            thermals = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment).getBoolean(true);
            lavaThermals = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", false, lavaThermalComment).getBoolean(false);
            minLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment, 2.0, 10.0).getDouble(3.0);
            maxLavaDistance = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment, 10.0, 100.0).getDouble(48.0);
            autoDismount = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", true, autoComment).getBoolean(true);
            chuteColor = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "white", colorComment, colorValues).getString();
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
            burnVolume = config.get(Configuration.CATEGORY_GENERAL, "burnVolume", 1.0, burnVolumeComment, 0.0, 1.0).getDouble(1.0);
            waypoint = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[] {0,0}, waypointComment).getIntList();
            homepoint = config.get(Configuration.CATEGORY_GENERAL, "homepoint", new int[] {0,0}, homepointComment).getIntList();

            // if lava thermals are allowed check allow/disallow space bar thermals
            thermals = !(lavaThermals && lavaDisablesThermals);

        } catch (Exception e) {
            Parachute.proxy.info("failed to load or read the config file");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static boolean getDismountInWater() {
        return dismountInWater;
    }

    public static double getMaxAltitude() {
        return heightLimit;
    }

    public static boolean getAllowThermals() {
        return thermals;
    }

    public static String getChuteColor() {
        return chuteColor;
    }

    public static void setChuteColor(String color) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "white", colorComment);
        prop.set(color);
        config.save();
        chuteColor = color;
    }

    public static boolean getAllowLavaThermals() {
        return lavaThermals;
    }

    public static boolean getWeatherAffectsDrift() {
        return weatherAffectsDrift;
    }

    public static double getMinLavaDistance() {
        return minLavaDistance;
    }

    public static double getMaxLavaDistance() {
        return maxLavaDistance;
    }

    public static boolean getAllowturbulence() {
        return constantTurbulence;
    }

    public static boolean getShowContrails() {
        return showContrails;
    }

    public static boolean isAutoDismount() {
        return autoDismount;
    }

    public static boolean getIsAADActive() {
        return isAADActive;
    }

    public static void setAADState(boolean state) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "isAADActive", false, isAADActiveComment);
        prop.set(state);
        config.save();
        isAADActive = state;
    }

    public static double getAADAltitude() {
        return aadAltitude;
    }

    public static double getMinFallDistance() {
        return minFallDistance;
    }
    
    public static float getBurnVolume() {
		return (float)burnVolume;
	}

    public static int getParachuteDamageAmount() {
        if (singleUse) {
            return Parachute.parachuteItem.getMaxDamage() + 1;
        }
        return 1;
    }

    public static boolean getAADImmediate() {
        return aadImmediate;
    }

    public static boolean getUseSpawnPoint() {
        return useSpawnPoint;
    }

    public static BlockPos getWaypoint() {
        return new BlockPos(waypoint[0], 0, waypoint[1]);
    }

    public static void setWaypoint(int x, int z) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[] {0,0}, waypointComment);
        prop.set(new int[] {x, z});
        config.save();
        waypoint[0] = x;
        waypoint[1] = z;
    }

    public static BlockPos getHomepoint() {
        return new BlockPos(homepoint[0], 0, homepoint[1]);
    }

    public static void setHomepoint(int x, int z) {
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
