/*
 * ConfigHandler.java
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

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

@Config(modid = Parachute.MODID)
@Config.LangKey("parachutemod.config.title")

public class ModConfigHandler {

    private static Configuration config = null;

    @Config.Name("Single Use")
    @Config.Comment("set to true for parachute single use")
    private static boolean singleUse;

    @Config.Name("Altitude Limit")
    @Config.Comment("0 (zero) disables altitude limiting")
    private static int heightLimit;

    @Config.Name("Parachute Color")
    @Config.Comment("Select a parachute color, random, or custom[0-9]")
    private static String chuteColor;

    @Config.Name("Allow Thermals")
    @Config.Comment("enable thermal rise by pressing the space bar")
    private static boolean thermals;

    @Config.Name("Auto-Dismount")
    @Config.Comment("If true the parachute will dismount the player automatically, if false the player has to use LSHIFT to dismount the parachute")
    private static boolean autoDismount;

    @Config.Name("Weather Affects Drift")
    @Config.Comment("set to false if you don't want the drift rate to be affected by bad weather")
    private static boolean weatherAffectsDrift;

    @Config.Name("Lava Thermals")
    @Config.Comment("use lava heat to get thermals to rise up, optionally disables space bar thermals")
    private static boolean lavaThermals;

    @Config.Name("Minimum Lava Distance")
    @Config.RangeDouble(min = 3.0, max = 10.0)
    @Config.Comment("minimum distance from lava to grab thermals, if you go less than 3.0 you will most likely dismount in the lava!")
    private static double minLavaDistance;

    @Config.Name("Max lava rise")
    @Config.RangeDouble(min = 10, max = 120)
    @Config.Comment("maximum distance to rise from lava thermals")
    private static double maxLavaDistance;

    @Config.Name("Constant Turbulence")
    @Config.Comment("set to true to always feel the turbulent world of Minecraft")
    private static boolean constantTurbulence;

    @Config.Name("Show Contrails")
    @Config.Comment("set to true to show contrails from parachute")
    private static boolean showContrails;

    @Config.Name("Dismount in Water")
    @Config.Comment("true to dismount in water")
    private static boolean dismountInWater;

    @Config.Name("AAD Active")
    @Config.Comment("whether or not the AAD starts active")
    private static boolean isAADActive;

    @Config.Name("AAD Altitude")
    @Config.RangeDouble(min = 10.0, max = 100.0)
    @Config.Comment("altitude (in meters) at which auto deploy occurs")
    private static double aadAltitude;

    @Config.Name("Minimum Fall Distance")
    @Config.RangeDouble(min = 5.0, max = 20.0)
    @Config.Comment("minimum distance to fall before the AAD deploys")
    private static double minFallDistance;

    @Config.Name("AAD Immediate")
    @Config.Comment("AAD deploys immediately after the player falls more than minFallDistance")
    private static boolean aadImmediate;

    @Config.Name("Burn sound volume")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.Comment("set the burn sound volume (0.0 to 1.0)")
    private static double burnVolume;

    @Config.Name("Wapoint Coords")
    @Config.Comment("waypoint coordinates [X, Z]")
    private static int[] waypoint;

    private static boolean dismounting;
    private static boolean lavaDisablesThermals;
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

    @SuppressWarnings("unused")
    public static void updateConfigFromFields() {
        updateConfigInfo(false, false);
    }

    private static void updateConfigInfo(boolean fromFile, boolean fromFields) {
        if (fromFile) {
            config.load();
        }

//        config.setCategoryComment(Configuration.CATEGORY_GENERAL, aboutComments);
//
//        Property singleUseProp = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment);
//        Property heightLimitProp = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment, 100, 256);
//        Property thermalsProp = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment);
//        Property autoDismountProp = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", true, autoComment);
//        Property dismountInWaterProp = config.get(Configuration.CATEGORY_GENERAL, "dismountInWater", false, dismountComment);
//
//        Property showContrailsProp = config.get(Configuration.CATEGORY_GENERAL, "showContrails", false, trailsComment);
//        Property burnVolumeProp = config.get(Configuration.CATEGORY_GENERAL, "burnVolume", 1.0, burnVolumeComment, 0.0, 1.0);
//
//        Property lavaThermalsProp = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", false, lavaThermalComment);
//        Property minLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment, 2.0, 10.0);
//        Property maxLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment, 10.0, 100.0);
//        Property lavaDisablesThermalProp = config.get(Configuration.CATEGORY_GENERAL, "lavaDisablesThermals", true, lavaDisablesComment);
//
//        Property weatherAffectsDriftProp = config.get(Configuration.CATEGORY_GENERAL, "weatherAffectsDrift", true, weatherComment);
//        Property constantTurbulenceProp = config.get(Configuration.CATEGORY_GENERAL, "constantTurbulence", false, turbulenceComment);
//
//        Property isAADActiveProp = config.get(Configuration.CATEGORY_GENERAL, "isAADActive", false, isAADActiveComment);
//        Property aadAltitudeProp = config.get(Configuration.CATEGORY_GENERAL, "aadAltitude", 10.0, aadAltitudeComment, 5.0, 100.0);
//        Property minFallDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "minFallDistance", 5.0, minFallDistanceComment, 3.0, 10.0);
//        Property aadImmediateProp = config.get(Configuration.CATEGORY_GENERAL, "aadImmediate", false, aadImmedComment);
//
//        Property waypointProp = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[]{0, 0}, waypointComment);
//
//        Property chuteColorProp = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "white");
//        chuteColorProp.setComment(colorComment);
//        chuteColorProp.setValidValues(colorValues);

//        List<String> propertyOrder = new ArrayList<>();
//        propertyOrder.add(singleUseProp.getName());
//        propertyOrder.add(heightLimitProp.getName());
//        propertyOrder.add(thermalsProp.getName());
//        propertyOrder.add(autoDismountProp.getName());
//        propertyOrder.add(dismountInWaterProp.getName());
//        propertyOrder.add(showContrailsProp.getName());
//        propertyOrder.add(burnVolumeProp.getName());
//        propertyOrder.add(lavaThermalsProp.getName());
//        propertyOrder.add(minLavaDistanceProp.getName());
//        propertyOrder.add(maxLavaDistanceProp.getName());
//        propertyOrder.add(lavaDisablesThermalProp.getName());
//        propertyOrder.add(isAADActiveProp.getName());
//        propertyOrder.add(aadImmediateProp.getName());
//        propertyOrder.add(minFallDistanceProp.getName());
//        propertyOrder.add(aadAltitudeProp.getName());
//        propertyOrder.add(weatherAffectsDriftProp.getName());
//        propertyOrder.add(constantTurbulenceProp.getName());
//        propertyOrder.add(waypointProp.getName());
//        propertyOrder.add(chuteColorProp.getName());
//        config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propertyOrder);
//
//        if (fromFields) {
//            singleUse = singleUseProp.getBoolean(false);
//            heightLimit = heightLimitProp.getInt(256);
//            thermals = thermalsProp.getBoolean(true);
//            lavaThermals = lavaThermalsProp.getBoolean(false);
//            minLavaDistance = minLavaDistanceProp.getDouble(3.0);
//            maxLavaDistance = maxLavaDistanceProp.getDouble(48.0);
//            autoDismount = autoDismountProp.getBoolean(true);
//            chuteColor = chuteColorProp.getString();
//            weatherAffectsDrift = weatherAffectsDriftProp.getBoolean(true);
//            constantTurbulence = constantTurbulenceProp.getBoolean(false);
//            showContrails = showContrailsProp.getBoolean(false);
//            dismountInWater = dismountInWaterProp.getBoolean(false);
//            isAADActive = isAADActiveProp.getBoolean(false);
//            aadAltitude = aadAltitudeProp.getDouble(10.0);
//            minFallDistance = minFallDistanceProp.getDouble(5.0);
//            aadImmediate = aadImmediateProp.getBoolean(false);
//            burnVolume = burnVolumeProp.getDouble(1.0);
//            waypoint = waypointProp.getIntList();
//        }

        // if lava thermals are allowed check allow/disallow space bar thermals
        thermals = thermals && !(lavaThermals && lavaDisablesThermals);
//        boolean thermalsDisabled = !(lavaThermals && lavaDisablesThermalProp.getBoolean());
//        thermals = thermals ? thermalsDisabled : thermals;
        // used to signal that a player has dismounted
        dismounting = false;

//        singleUseProp.set(singleUse);
//        heightLimitProp.set(heightLimit);
//        thermalsProp.set(thermals);
//        lavaThermalsProp.set(lavaThermals);
//        minLavaDistanceProp.set(minLavaDistance);
//        maxLavaDistanceProp.set(maxLavaDistance);
//        autoDismountProp.set(autoDismount);
//        chuteColorProp.set(chuteColor);
//        weatherAffectsDriftProp.set(weatherAffectsDrift);
//        constantTurbulenceProp.set(constantTurbulence);
//        showContrailsProp.set(showContrails);
//        dismountInWaterProp.set(dismountInWater);
//        isAADActiveProp.set(isAADActive);
//        aadAltitudeProp.set(aadAltitude);
//        minFallDistanceProp.set(minFallDistance);
//        aadImmediateProp.set(aadImmediate);
//        burnVolumeProp.set(burnVolume);
//        waypointProp.set(waypoint);

//        if (config.hasChanged() && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
//            config.save();
//        }
    }

    @Mod.EventBusSubscriber(modid = Parachute.MODID)
    @SuppressWarnings("unused")
    public static class ConfigEventHandler {

        @SubscribeEvent
        public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Parachute.MODID)) {
                Parachute.proxy.info(String.format("Configuration changes have been updated for the %s", Parachute.NAME));
                ConfigManager.sync(Parachute.MODID, Config.Type.INSTANCE);
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
        Property prop = config.get(Configuration.CATEGORY_CLIENT, "chuteColor", "white");
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
        Property prop = config.get(Configuration.CATEGORY_CLIENT, "isAADActive", false);
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
            return ParachuteCommonProxy.parachuteItem.getMaxDamage(itemStack) + 1; //.getMaxDamage() + 1;
        }
        return 1;
    }

    public static boolean getAADImmediate() {
        return aadImmediate;
    }

    @SuppressWarnings("unused")
    public static BlockPos getWaypoint() {
        return new BlockPos(waypoint[0], 0, waypoint[1]);
    }

    public static void setWaypoint(int x, int z) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "waypoint", new int[]{0, 0});
        prop.set(new int[]{x, z});
        config.save();
        waypoint[0] = x;
        waypoint[1] = z;
    }

    public static String getWaypointString() {
        return String.format("%d %d", waypoint[0], waypoint[1]);
    }

    public static boolean isDismounting() {
        return dismounting;
    }

    public static void setIsDismounting(boolean value) {
        dismounting = value;
    }

}
