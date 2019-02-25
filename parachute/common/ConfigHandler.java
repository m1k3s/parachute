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


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;


public class ConfigHandler {

//    public static void loadConfig() {
//        CommentedFileConfig.builder(Paths.get("config", Parachute.PARACHUTE_NAME, Parachute.MODID + ".toml")).build();
//    }

    public static class Common {
        public static ForgeConfigSpec.BooleanValue singleUse;
        public static ForgeConfigSpec.IntValue heightLimit;
        public static ForgeConfigSpec.BooleanValue thermals;
        public static ForgeConfigSpec.BooleanValue weatherAffectsDrift;
        public static ForgeConfigSpec.BooleanValue lavaThermals;
        public static ForgeConfigSpec.DoubleValue minLavaDistance;
        public static ForgeConfigSpec.DoubleValue maxLavaDistance;
        public static ForgeConfigSpec.BooleanValue constantTurbulence;
        public static ForgeConfigSpec.BooleanValue showContrails;
        public static ForgeConfigSpec.BooleanValue dismountInWater;

        public Common(ForgeConfigSpec.Builder builder) {
            Parachute.getLogger().info("Loading ConfigHandler.Common");
            builder.comment("Common Config").push("Common");

            singleUse = builder
                    .comment("set to true for parachute single use")
                    .translation("parachutemod.config.singleUse")
                    .define("singleUse", false);

            heightLimit = builder
                    .comment("0 (zero) disables altitude limiting")
                    .translation("parachutemod.config.heightLimit")
                    .defineInRange("heightLimit", 255, 0, 255);

            thermals = builder
                    .comment("enable thermal rise by pressing the space bar")
                    .translation("parachutemod.config.thermals")
                    .define("thermals", true);

            weatherAffectsDrift = builder
                    .comment("set to false if you don't want the drift rate to be affected by bad weather")
                    .translation("parachutemod.config.weatherAffectsDrift")
                    .define("weatherAffectsDrift", true);

            lavaThermals = builder
                    .comment("use lava heat to get thermals to rise up, optionally disables space bar thermals")
                    .translation("parachutemod.config.thermals")
                    .define("thermals", false);

            minLavaDistance = builder
                    .comment("minimum distance from lava to grab thermals, if you go less than 3.0 you will most likely dismount in the lava!")
                    .translation("parachutemod.config.minLavaDistance")
                    .defineInRange("minLavaDistance", 3.0, 2.0, 10.0);

            maxLavaDistance = builder
                    .comment("maximum distance to rise from lava thermals")
                    .translation("parachutemod.config.maxLavaDistance")
                    .defineInRange("maxLavaDistance", 48.0, 10.0, 100.0);

            constantTurbulence = builder
                    .comment("set to true to always feel the turbulent world of Minecraft")
                    .translation("parachutemod.config.constantTurbulence")
                    .define("constantTurbulence", false);

            showContrails = builder
                    .comment("set to true to show contrails from parachute")
                    .translation("parachutemod.config.showContrails")
                    .define("showContrails", true);

            dismountInWater = builder
                    .comment("if true, dismount in water")
                    .translation("parachutemod.config.dismountInWater")
                    .define("dismountInWater", false);

            builder.pop();
        }

        public static boolean getShowContrails() { return showContrails.get(); }

        public static boolean getDismountInWater() { return dismountInWater.get(); }

        public static boolean getAllowThermals() { return thermals.get(); }
    }

    public static class Client {
        public static ForgeConfigSpec.IntValue WASDControl;
        public static ForgeConfigSpec.IntValue chuteColor;
        public static ForgeConfigSpec.IntValue hudPosition;
        public static ForgeConfigSpec.BooleanValue aadState;
        private static final String[] COLORVALUES = { "random", "black", "blue", "brown", "cyan", "gray", "green", "light_blue",
                "lime", "magenta", "orange", "pink", "purple", "red", "silver", "white", "yellow",
                "custom0", "custom1", "custom2", "custom3", "custom4", "custom5", "custom6", "custom7", "custom8", "custom9",
        };
        private static final String[] HUDPOSVALUES= { "left", "center", "right" };
        private static final String[] STEERING_CONTROL = { "WASD", "Sight" };


        public Client(ForgeConfigSpec.Builder builder) {
            Parachute.getLogger().info("Loading ConfigHandler.Client");
            builder.comment("Client Config").push("Client");

            WASDControl = builder
                    .comment("if true steering is 'WASD', otherwise steering is by sight  [false/true|default:true]")
                    .translation("parachutemod.config.steeringControl")
                    .defineInRange("WASDControl", 0, 0, 1);

            aadState = builder
                    .comment("if true autoactivate is on [false/true|default:true]")
                    .translation("parachutemod.config.aadState")
                    .define("aadState", true);

            hudPosition = builder
                    .comment("HUD position is one of left|center|right [left/center/right|default:right]")
                    .translation("parachutemod.config.hudPosition")
                    .defineInRange("hudPosition", 2, 0, 2);

            chuteColor = builder
                    .comment("Parachute color, can be a minecraft color, random, or custom")
                    .translation("parachutemod.config.chuteColor")
                    .defineInRange("chuteColor", 0, 0, 26);

            builder.pop();
        }

        public static String getSteeringControl() { return STEERING_CONTROL[WASDControl.get()]; }

        public static String getChuteColor() { return COLORVALUES[chuteColor.get()]; }

        public static boolean getAADState() { return aadState.get(); }

        public static String getHUDPosition() { return HUDPOSVALUES[hudPosition.get()]; }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<ConfigHandler.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }


    static final ForgeConfigSpec commonSpec;
    public static final Common SERVER;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        LogManager.getLogger().info("Loaded Parachute config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        LogManager.getLogger().info("Parachute config: {} just got changed on the file system!", configEvent.getConfig().getFileName());
    }
}

//public class ConfigHandler {

   /* private static Configuration config = null;

    private static boolean singleUse;
    private static int heightLimit;
    private static String chuteColor;
    private static boolean thermals;
    private static boolean weatherAffectsDrift;
    private static boolean lavaThermals;
    private static double minLavaDistance;
    private static double maxLavaDistance;
    private static boolean constantTurbulence;
    private static boolean showContrails;
    private static boolean dismountInWater;
    private static boolean aadActive;
    private static double burnVolume;
    private static String hudPosition;
    private static String steeringControl;
    private static boolean useFlyingSound;

    private static double forwardMomentum;
    private static double backMomentum;
    private static double rotationMomentum;
    private static double slideMomentum;

    private static final String GERNERAL_COMMENTS = String.format("%s Config - Michael Sheppard (crackedEgg) [Minecraft Version %s]", Parachute.NAME, Parachute.MCVERSION);
    private static final String USAGE_COMMENT = "set to true for parachute single use"; // false
    private static final String HEIGHT_COMMENT = "0 (zero) disables altitude limiting"; // 256
    private static final String THERMAL_COMMENT = "enable thermal rise by pressing the space bar"; // true
    private static final String LAVA_THERMAL_COMMENT = "use lava heat to get thermals to rise up, optionally disables space bar thermals"; // false
    private static final String MIN_LAVA_DISTANCE_COMMENT = "minimum distance from lava to grab thermals, if you go less than 3.0 you will most likely dismount in the lava!"; // 3.0
    private static final String MAX_LAVA_DISTANCE_COMMENT = "maximum distance to rise from lava thermals"; // 48
    private static final String WEATHER_COMMENT = "set to false if you don't want the drift rate to be affected by bad weather"; // true
    private static final String TURBULENCE_COMMENT = "set to true to always feel the turbulent world of Minecraft"; // false
    private static final String TRAILS_COMMENT = "set to true to show contrails from parachute"; // false
    private static final String DISMOUNT_COMMENT = "true to dismount in water"; // false
    private static final String LAVA_DISABLES_COMMENT = "normal thermals are disabled by lava thermals"; // true
    private static final String IS_AAD_ACTIVE_COMMENT = "whether or not the AAD starts active"; // false
    private static final String BURN_VOLUME_COMMENT = "set the burn sound volume (0.0 to 1.0)";
    private static final String COLOR_COMMENT = "Select a parachute color, random, or custom[0-9]";
    private static final String FORWARD_MOTION_COMMENT = "delta forward momentum value";
    private static final String BACK_MOTION_COMMENT = "delta back momentum value";
    private static final String ROTATION_MOMENTUM_COMMENT = "delta rotation momentum value";
    private static final String SLIDE_MOTION_COMMENT = "delta slide momentum value";
    private static final String HUD_POSITION_COMMENT = "The HUD can be positioned in the upper left, upper center, or upper right";
    private static final String STEERING_CONTROL_COMMENT = "set to true to steer by player look direction, else WASD steering";
    private static final String USE_FLYING_SOUND = "set to true to hear the wind while flying";
    private static final String[] COLOR_VALUES = {
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

    private static final String[] HUD_POSITIONS = {
            "left",
            "center",
            "right"
    };

    private static final String[] STEERING_CONTROL = {
            "WASD",
            "Sight"
    };

    public static void preInit(FMLCommonSetupEvent event) {
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

        config.setCategoryComment(Configuration.CATEGORY_GENERAL, GERNERAL_COMMENTS);

        Property singleUseProp = config.get(Configuration.CATEGORY_GENERAL, "singleUse", false, USAGE_COMMENT);
        Property heightLimitProp = config.get(Configuration.CATEGORY_GENERAL, "heightLimit", 256, HEIGHT_COMMENT, 100, 256);
        Property thermalsProp = config.get(Configuration.CATEGORY_GENERAL, "allowThermals", true, THERMAL_COMMENT);
        Property dismountInWaterProp = config.get(Configuration.CATEGORY_GENERAL, "dismountInWater", false, DISMOUNT_COMMENT);

        Property steeringControlProp = config.get(Configuration.CATEGORY_GENERAL, "steeringControl", "WASD");
        steeringControlProp.setComment(STEERING_CONTROL_COMMENT);
        steeringControlProp.setValidValues(STEERING_CONTROL);

        Property forwardMotionProp = config.get(Configuration.CATEGORY_GENERAL, "forwardMomentum", 0.015, FORWARD_MOTION_COMMENT);
        Property backMotionProp = config.get(Configuration.CATEGORY_GENERAL, "backMomentum", 0.008, BACK_MOTION_COMMENT);
        Property leftMotionProp = config.get(Configuration.CATEGORY_GENERAL, "rotationMomentum", 0.2, ROTATION_MOMENTUM_COMMENT);
        Property slideMotionProp = config.get(Configuration.CATEGORY_GENERAL, "slideMomentum", 0.005, SLIDE_MOTION_COMMENT);

        Property showContrailsProp = config.get(Configuration.CATEGORY_GENERAL, "showContrails", true, TRAILS_COMMENT);
        Property burnVolumeProp = config.get(Configuration.CATEGORY_GENERAL, "burnVolume", 1.0, BURN_VOLUME_COMMENT, 0.0, 1.0);
        Property useFlyingSoundProp = config.get(Configuration.CATEGORY_GENERAL, "useFlyingSound", true, USE_FLYING_SOUND);

        Property lavaThermalsProp = config.get(Configuration.CATEGORY_GENERAL, "lavaThermals", true, LAVA_THERMAL_COMMENT);
        Property minLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "minLavaDistance", 3.0, MIN_LAVA_DISTANCE_COMMENT, 2.0, 10.0);
        Property maxLavaDistanceProp = config.get(Configuration.CATEGORY_GENERAL, "maxLavaDistance", 48.0, MAX_LAVA_DISTANCE_COMMENT, 10.0, 100.0);
        Property lavaDisablesThermalProp = config.get(Configuration.CATEGORY_GENERAL, "lavaDisablesThermals", false, LAVA_DISABLES_COMMENT);

        Property weatherAffectsDriftProp = config.get(Configuration.CATEGORY_GENERAL, "weatherAffectsDrift", true, WEATHER_COMMENT);
        Property constantTurbulenceProp = config.get(Configuration.CATEGORY_GENERAL, "constantTurbulence", false, TURBULENCE_COMMENT);

        Property isAADActiveProp = config.get(Configuration.CATEGORY_GENERAL, "aadActive", true, IS_AAD_ACTIVE_COMMENT);

        Property chuteColorProp = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", "black");
        chuteColorProp.setComment(COLOR_COMMENT);
        chuteColorProp.setValidValues(COLOR_VALUES);

        Property hudPositionProp = config.get(Configuration.CATEGORY_GENERAL, "hudPosition", "right");
        hudPositionProp.setComment(HUD_POSITION_COMMENT);
        hudPositionProp.setValidValues(HUD_POSITIONS);

        List<String> generalPropOrder = new ArrayList<>();
        generalPropOrder.add(singleUseProp.getName());
        generalPropOrder.add(heightLimitProp.getName());
        generalPropOrder.add(thermalsProp.getName());
        generalPropOrder.add(dismountInWaterProp.getName());
        generalPropOrder.add(showContrailsProp.getName());
        generalPropOrder.add(burnVolumeProp.getName());
        generalPropOrder.add(useFlyingSoundProp.getName());
        generalPropOrder.add(hudPositionProp.getName());
        generalPropOrder.add(lavaThermalsProp.getName());
        generalPropOrder.add(minLavaDistanceProp.getName());
        generalPropOrder.add(maxLavaDistanceProp.getName());
        generalPropOrder.add(lavaDisablesThermalProp.getName());
        generalPropOrder.add(isAADActiveProp.getName());
        generalPropOrder.add(weatherAffectsDriftProp.getName());
        generalPropOrder.add(constantTurbulenceProp.getName());
        generalPropOrder.add(chuteColorProp.getName());
        generalPropOrder.add(steeringControlProp.getName());
        generalPropOrder.add(forwardMotionProp.getName());
        generalPropOrder.add(backMotionProp.getName());
        generalPropOrder.add(leftMotionProp.getName());
        generalPropOrder.add(slideMotionProp.getName());
        config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, generalPropOrder);

        if (fromFields) {
            singleUse = singleUseProp.getBoolean(false);
            heightLimit = heightLimitProp.getInt(256);
            thermals = thermalsProp.getBoolean(true);
            lavaThermals = lavaThermalsProp.getBoolean(true);
            minLavaDistance = minLavaDistanceProp.getDouble(3.0);
            maxLavaDistance = maxLavaDistanceProp.getDouble(48.0);
            chuteColor = chuteColorProp.getString();
            weatherAffectsDrift = weatherAffectsDriftProp.getBoolean(true);
            constantTurbulence = constantTurbulenceProp.getBoolean(false);
            showContrails = showContrailsProp.getBoolean(true);
            dismountInWater = dismountInWaterProp.getBoolean(false);
            aadActive = isAADActiveProp.getBoolean(true);
            burnVolume = burnVolumeProp.getDouble(1.0);
            useFlyingSound = useFlyingSoundProp.getBoolean(true);
            forwardMomentum = forwardMotionProp.getDouble(0.015);
            backMomentum = backMotionProp.getDouble(0.008);
            rotationMomentum = leftMotionProp.getDouble(0.2);
            slideMomentum = slideMotionProp.getDouble(0.005);
            hudPosition = hudPositionProp.getString();
            ConfigHandler.steeringControl = steeringControlProp.getString();
        }

        // if lava thermals are allowed check allow/disallow space bar thermals
        thermals = thermals && !(lavaThermals && lavaDisablesThermalProp.getBoolean());

        singleUseProp.set(singleUse);
        heightLimitProp.set(heightLimit);
        thermalsProp.set(thermals);
        lavaThermalsProp.set(lavaThermals);
        minLavaDistanceProp.set(minLavaDistance);
        maxLavaDistanceProp.set(maxLavaDistance);
        chuteColorProp.set(chuteColor);
        weatherAffectsDriftProp.set(weatherAffectsDrift);
        constantTurbulenceProp.set(constantTurbulence);
        showContrailsProp.set(showContrails);
        dismountInWaterProp.set(dismountInWater);
        isAADActiveProp.set(aadActive);
        burnVolumeProp.set(burnVolume);
        useFlyingSoundProp.set(useFlyingSound);
        forwardMotionProp.set(forwardMomentum);
        backMotionProp.set(backMomentum);
        leftMotionProp.set(rotationMomentum);
        slideMotionProp.set(slideMomentum);
        hudPositionProp.set(hudPosition);
        steeringControlProp.set(steeringControl);

        if (config.hasChanged()) {
            config.save();
        }
    }

    // only used on the client
    public static class ConfigEventHandler {
        @SubscribeEvent
        public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Parachute.MODID)) {
//                ConfigHandler.updateConfigFromGUI();
                // update the client side options
//                ClientConfiguration.setChuteColor(chuteColor);
//                ClientConfiguration.setBurnVolume(burnVolume);
//                ClientConfiguration.setHudPosition(hudPosition);
//                ClientConfiguration.setSteeringControl(steeringControl);
//                ClientConfiguration.setAADState(aadActive);
//                ClientConfiguration.setUseFlyingSound(useFlyingSound);
                Parachute.getLogger().info(String.format("Configuration changes have been updated for the %s client", Parachute.MODID));
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

    @SuppressWarnings("unused")
    public static void setChuteColor(String color) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "chuteColor", chuteColor, COLOR_COMMENT);
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

    public boolean getShowContrails() {ss
        return showContrails;
    }

    public static boolean getAADState() {
        return aadActive;
    }

    public static void setAADState(boolean state) {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "aadActive", aadActive, IS_AAD_ACTIVE_COMMENT);
        prop.set(state);
        config.save();
        aadActive = state;
    }

    public static float getBurnVolume() {
        return (float) burnVolume;
    }

    public static int getParachuteDamageAmount(ItemStack itemStack) {
        if (singleUse) {
            return Parachute.PARACHUTE_ITEM.getMaxDamage(itemStack) + 1;
        }
        return 1;
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

    public static String getHudPosition() {
        return hudPosition;
    }

    public static String getSteeringControl() {
        return steeringControl;
    }

    public static boolean getUseFlyingSound() { return useFlyingSound; }

//    public static void setUseFlyingSound(boolean value) { useFlyingSound = value; }*/
//}
