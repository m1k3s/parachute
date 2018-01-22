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

import com.parachute.client.ClientConfiguration;
import net.minecraft.item.ItemStack;
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
    private static double burnVolume;
    private static boolean useCompassHUD;
    private static boolean noHUD;

    private static double forwardMomentum;
    private static double backMomentum;
    private static double rotationMomentum;
    private static double slideMomentum;

    private static final String aboutComments = String.format("%s Config - Michael Sheppard (crackedEgg) [Minecraft Version %s]", Parachute.NAME, Parachute.MCVERSION);
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
    private static final String burnVolumeComment = "set the burn sound volume (0.0 to 1.0)";
    private static final String colorComment = "Select a parachute color, random, or custom[0-9]";
    private static final String forwardMotionComment = "delta forward momentum value";
    private static final String backMotionComment = "delta back momentum value";
    private static final String rotationMomentumComment = "delta rotation momentum value";
    private static final String slideMotionComment = "delta slide momentum value";
    private static final String useCompassHUDComment = "use the new compass HUD";
    private static final String noHUDComment = "Disable the HUD";
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

        config.setCategoryComment(Configuration.CATEGORY_GENERAL, aboutComments);

        Property singleUseProp = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, usageComment);
        Property heightLimitProp = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, heightComment, 100, 256);
        Property thermalsProp = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, thermalComment);
        Property autoDismountProp = config.get(Configuration.CATEGORY_GENERAL, "autoDismount", false, autoComment);
        Property dismountInWaterProp = config.get(Configuration.CATEGORY_GENERAL, "dismountInWater", false, dismountComment);

        Property forwardMotionProp = config.get(Configuration.CATEGORY_GENERAL, "forwardMomentum", 0.015, forwardMotionComment);
        Property backMotionProp = config.get(Configuration.CATEGORY_GENERAL, "backMomentum", 0.008, backMotionComment);
        Property leftMotionProp = config.get(Configuration.CATEGORY_GENERAL, "rotationMomentum", 0.2, rotationMomentumComment);
        Property slideMotionProp = config.get(Configuration.CATEGORY_GENERAL, "slideMomentum", 0.005, slideMotionComment);

        Property showContrailsProp = config.get(Configuration.CATEGORY_GENERAL, "showContrails", true, trailsComment);
        Property burnVolumeProp = config.get(Configuration.CATEGORY_GENERAL, "burnVolume", 1.0, burnVolumeComment, 0.0, 1.0);
        Property useCompassHUDProp = config.get(Configuration.CATEGORY_GENERAL, "useCompassHUD", true, useCompassHUDComment);
        Property noHUDProp = config.get(Configuration.CATEGORY_GENERAL, "noHUD", false, noHUDComment);

        Property lavaThermalsProp = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", true, lavaThermalComment);
        Property minLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, minLavaDistanceComment, 2.0, 10.0);
        Property maxLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, maxLavaDistanceComment, 10.0, 100.0);
        Property lavaDisablesThermalProp = config.get(Configuration.CATEGORY_GENERAL, "lavaDisablesThermals", false, lavaDisablesComment);

        Property weatherAffectsDriftProp = config.get(Configuration.CATEGORY_GENERAL, "weatherAffectsDrift", true, weatherComment);
        Property constantTurbulenceProp = config.get(Configuration.CATEGORY_GENERAL, "constantTurbulence", false, turbulenceComment);

        Property isAADActiveProp = config.get(Configuration.CATEGORY_GENERAL, "isAADActive", false, isAADActiveComment);
        Property aadAltitudeProp = config.get(Configuration.CATEGORY_GENERAL, "aadAltitude", 10.0, aadAltitudeComment, 5.0, 100.0);
        Property minFallDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "minFallDistance", 5.0, minFallDistanceComment, 3.0, 10.0);
        Property aadImmediateProp = config.get(Configuration.CATEGORY_GENERAL, "aadImmediate", false, aadImmedComment);

        Property chuteColorProp = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "black");
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
        propertyOrder.add(useCompassHUDProp.getName());
        propertyOrder.add(noHUDProp.getName());
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
        propertyOrder.add(chuteColorProp.getName());
        propertyOrder.add(forwardMotionProp.getName());
        propertyOrder.add(backMotionProp.getName());
        propertyOrder.add(leftMotionProp.getName());
        propertyOrder.add(slideMotionProp.getName());
        config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propertyOrder);

        if (fromFields) {
            singleUse = singleUseProp.getBoolean(false);
            heightLimit = heightLimitProp.getInt(256);
            thermals = thermalsProp.getBoolean(true);
            lavaThermals = lavaThermalsProp.getBoolean(true);
            minLavaDistance = minLavaDistanceProp.getDouble(3.0);
            maxLavaDistance = maxLavaDistanceProp.getDouble(48.0);
            autoDismount = autoDismountProp.getBoolean(false);
            chuteColor = chuteColorProp.getString();
            weatherAffectsDrift = weatherAffectsDriftProp.getBoolean(true);
            constantTurbulence = constantTurbulenceProp.getBoolean(false);
            showContrails = showContrailsProp.getBoolean(true);
            dismountInWater = dismountInWaterProp.getBoolean(false);
            isAADActive = isAADActiveProp.getBoolean(false);
            aadAltitude = aadAltitudeProp.getDouble(10.0);
            minFallDistance = minFallDistanceProp.getDouble(5.0);
            aadImmediate = aadImmediateProp.getBoolean(false);
            burnVolume = burnVolumeProp.getDouble(1.0);
            forwardMomentum = forwardMotionProp.getDouble(0.015);
            backMomentum = backMotionProp.getDouble(0.008);
            rotationMomentum = leftMotionProp.getDouble(0.2);
            slideMomentum = slideMotionProp.getDouble(0.005);
            useCompassHUD = useCompassHUDProp.getBoolean(true);
            noHUD = noHUDProp.getBoolean(false);
        }

        // if lava thermals are allowed check allow/disallow space bar thermals
        thermals = thermals && !(lavaThermals && lavaDisablesThermalProp.getBoolean());

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
        burnVolumeProp.set(burnVolume);
        forwardMotionProp.set(forwardMomentum);
        backMotionProp.set(backMomentum);
        leftMotionProp.set(rotationMomentum);
        slideMotionProp.set(slideMomentum);
        useCompassHUDProp.set(useCompassHUD);
        noHUDProp.set(noHUD);

        if (config.hasChanged()) {
            config.save();
        }
    }

    // only used on the client
    public static class ConfigEventHandler {
        @SubscribeEvent
        public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Parachute.MODID)) {
                ConfigHandler.updateConfigFromGUI();
                // update the client side options
                ClientConfiguration.setChuteColor(chuteColor);
                ClientConfiguration.setNoHUD(noHUD);
                ClientConfiguration.setUseCompassHUD(useCompassHUD);
                ClientConfiguration.setBurnVolume(burnVolume);
                Parachute.instance.info(String.format("Configuration changes have been updated for the %s client", Parachute.NAME));
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
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "black", colorComment);
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

    public static double getForwardMomentum() {
        return forwardMomentum;
    }

    public static double getBackMomentum() {
        return backMomentum;
    }

    public static double getRotationMomentum() {
        return rotationMomentum;
    }

    public static double getSlideMomentum() {
        return slideMomentum;
    }

    public static boolean getUseCompassHUD() {
        return  useCompassHUD;
    }

    public static boolean getNoHUD() {
        return noHUD;
    }

}
