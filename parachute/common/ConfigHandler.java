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

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static Configuration config = null;

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
    private static boolean dismounting;

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

    public static void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        updateConfigFromFile();
    }

    public static void updateConfigFromFile() {
        updateConfigInfo(true, true);
    }

    public static void updateConfigFromGUI() {
        updateConfigInfo(false, true);
    }

    public static void updateConfigFromFields() {
        updateConfigInfo(false, false);
    }

    private static void updateConfigInfo(boolean fromFile, boolean fromFields) {
        if (fromFile) {
            config.load();
        }

        config.setCategoryComment(Configuration.CATEGORY_GENERAL, aboutComments);

        Property singleUseProp = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment);
        Property heightLimitProp = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment, 100, 256);
        Property thermalsProp = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment);
        Property autoDismountProp = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", true, autoComment);
        Property dismountInWaterProp = config.get(Configuration.CATEGORY_GENERAL, "dismountInWater", false, dismountComment);

        Property showContrailsProp = config.get(Configuration.CATEGORY_GENERAL, "showContrails", false, trailsComment);
        Property burnVolumeProp = config.get(Configuration.CATEGORY_GENERAL, "burnVolume", 1.0, burnVolumeComment, 0.0, 1.0);

        Property lavaThermalsProp = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", false, lavaThermalComment);
        Property minLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment, 2.0, 10.0);
        Property maxLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment, 10.0, 100.0);
        Property lavaDisablesThermalProp = config.get(Configuration.CATEGORY_GENERAL, "lavaDisablesThermals", true, lavaDisablesComment);

        Property weatherAffectsDriftProp = config.get(Configuration.CATEGORY_GENERAL, "weatherAffectsDrift", true, weatherComment);
        Property constantTurbulenceProp = config.get(Configuration.CATEGORY_GENERAL, "constantTurbulence", false, turbulenceComment);

        Property isAADActiveProp = config.get(Configuration.CATEGORY_GENERAL, "isAADActive", false, isAADActiveComment);
        Property aadAltitudeProp = config.get(Configuration.CATEGORY_GENERAL, "aadAltitude", 10.0, aadAltitudeComment, 5.0, 100.0);
        Property minFallDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "minFallDistance", 5.0, minFallDistanceComment, 3.0, 10.0);
        Property aadImmediateProp = config.get(Configuration.CATEGORY_GENERAL, "aadImmediate", false, aadImmedComment);

        Property useSpawnPointProp = config.get(Configuration.CATEGORY_GENERAL, "usespawnpoint", true, useSpawnPointComment);
        Property waypointProp = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[]{0, 0}, waypointComment);
        Property homepointProp = config.get(Configuration.CATEGORY_GENERAL, "homepoint", new int[]{0, 0}, homepointComment);

        Property chuteColorProp = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "white");
        chuteColorProp.setComment(colorComment);
        chuteColorProp.setValidValues(colorValues);

        List<String> propertyOrder = new ArrayList<>();
        propertyOrder.add(singleUseProp.getName());
        propertyOrder.add(heightLimitProp.getName());
        propertyOrder.add(thermalsProp.getName());
        propertyOrder.add(autoDismountProp.getName());
        propertyOrder.add(dismountInWaterProp.getName());
        propertyOrder.add(showContrailsProp.getName());
        propertyOrder.add(burnVolumeProp.getName());
        propertyOrder.add(lavaThermalsProp.getName());
        propertyOrder.add(minLavaDistanceProp.getName());
        propertyOrder.add(maxLavaDistanceProp.getName());
        propertyOrder.add(lavaDisablesThermalProp.getName());
        propertyOrder.add(isAADActiveProp.getName());
        propertyOrder.add(aadImmediateProp.getName());
        propertyOrder.add(minFallDistanceProp.getName());
        propertyOrder.add(aadAltitudeProp.getName());
        propertyOrder.add(weatherAffectsDriftProp.getName());
        propertyOrder.add(constantTurbulenceProp.getName());
        propertyOrder.add(useSpawnPointProp.getName());
        propertyOrder.add(waypointProp.getName());
        propertyOrder.add(homepointProp.getName());
        propertyOrder.add(chuteColorProp.getName());
        config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propertyOrder);

        if (fromFields) {
            singleUse = singleUseProp.getBoolean(false);
            heightLimit = heightLimitProp.getInt(256);
            thermals = thermalsProp.getBoolean(true);
            lavaThermals = lavaThermalsProp.getBoolean(false);
            minLavaDistance = minLavaDistanceProp.getDouble(3.0);
            maxLavaDistance = maxLavaDistanceProp.getDouble(48.0);
            autoDismount = autoDismountProp.getBoolean(true);
            chuteColor = chuteColorProp.getString();
            weatherAffectsDrift = weatherAffectsDriftProp.getBoolean(true);
            constantTurbulence = constantTurbulenceProp.getBoolean(false);
            showContrails = showContrailsProp.getBoolean(false);
            dismountInWater = dismountInWaterProp.getBoolean(false);
            isAADActive = isAADActiveProp.getBoolean(false);
            aadAltitude = aadAltitudeProp.getDouble(10.0);
            minFallDistance = minFallDistanceProp.getDouble(5.0);
            aadImmediate = aadImmediateProp.getBoolean(false);
            useSpawnPoint = useSpawnPointProp.getBoolean(true);
            burnVolume = burnVolumeProp.getDouble(1.0);
            waypoint = waypointProp.getIntList();
            homepoint = homepointProp.getIntList();
        }

        // if lava thermals are allowed check allow/disallow space bar thermals
        thermals = thermals ? !(lavaThermals && lavaDisablesThermalProp.getBoolean()) : thermals;
//        boolean thermalsDisabled = !(lavaThermals && lavaDisablesThermalProp.getBoolean());
//        thermals = thermals ? thermalsDisabled : thermals;
        // used to signal that a player has dismounted
        dismounting = false;

        singleUseProp.set(singleUse);
        heightLimitProp.set(heightLimit);
        thermalsProp.set(thermals);
        lavaThermalsProp.set(lavaThermals);
        minLavaDistanceProp.set(minLavaDistance);
        maxLavaDistanceProp.set(maxLavaDistance);
        autoDismountProp.set(autoDismount);
        chuteColorProp.set(chuteColor);
        weatherAffectsDriftProp.set(weatherAffectsDrift);
        constantTurbulenceProp.set(constantTurbulence);
        showContrailsProp.set(showContrails);
        dismountInWaterProp.set(dismountInWater);
        isAADActiveProp.set(isAADActive);
        aadAltitudeProp.set(aadAltitude);
        minFallDistanceProp.set(minFallDistance);
        aadImmediateProp.set(aadImmediate);
        useSpawnPointProp.set(useSpawnPoint);
        burnVolumeProp.set(burnVolume);
        waypointProp.set(waypoint);
        homepointProp.set(homepoint);

        if (config.hasChanged()) {
            config.save();
        }
    }

    @SuppressWarnings("unused")
    public static class ConfigEventHandler {
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Parachute.modid)) {
                Parachute.proxy.info(String.format("Configuration changes have been updated for the %s", Parachute.name));
                ConfigHandler.updateConfigFromGUI();
            }
        }
    }

    public static Configuration getConfig() {
        return config;
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
        Property prop = config.get(Configuration.CATEGORY_CLIENT, "chuteColor", "white", colorComment);
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
        Property prop = config.get(Configuration.CATEGORY_CLIENT, "isAADActive", false, isAADActiveComment);
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
        return (float) burnVolume;
    }

    public static int getParachuteDamageAmount(ItemStack itemStack) {
        if (singleUse) {
            return Parachute.parachuteItem.getMaxDamage(itemStack) + 1; //.getMaxDamage() + 1;
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
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[]{0, 0}, waypointComment);
        prop.set(new int[]{x, z});
        config.save();
        waypoint[0] = x;
        waypoint[1] = z;
    }

    public static BlockPos getHomepoint() {
        return new BlockPos(homepoint[0], 0, homepoint[1]);
    }

    public static void setHomepoint(int x, int z) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "homepoint", new int[]{0, 0}, homepointComment);
        prop.set(new int[]{x, z});
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

    public static boolean isDismounting() {
        return dismounting;
    }

    public static void setIsDismounting(boolean value) {
        dismounting = value;
    }

}
