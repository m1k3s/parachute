/*
 * ConfigHandler.java
 *
 *  Copyright (c) 2019 Michael Sheppard
 *
 * =====GPL=============================================================
 * This program is free software: you can redistribute it and/or modify
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
 */
package com.parachute.common;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Paths;


public class ConfigHandler {

    public static void loadConfig() {
        CommentedFileConfig.builder(Paths.get("config", Parachute.PARACHUTE_NAME, Parachute.MODID + ".toml")).build();
    }

    public static class CommonConfig {
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
        public static ForgeConfigSpec.BooleanValue lavaDisablesThermals;
        public static ForgeConfigSpec.DoubleValue forwardMomentum;
        public static ForgeConfigSpec.DoubleValue backMomentum;
        public static ForgeConfigSpec.DoubleValue rotationMomentum;
        public static ForgeConfigSpec.DoubleValue slideMomentum;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            Parachute.getLogger().info("Loading ConfigHandler.CommonConfig");
            builder.comment("CommonConfig Config").push("CommonConfig");

            singleUse = builder
                    .comment("set to true for parachute single use")
                    .translation("config.parachutemod.singleUse")
                    .define("singleUse", false);

            heightLimit = builder
                    .comment("0 (zero) disables altitude limiting")
                    .translation("config.parachutemod.heightLimit")
                    .defineInRange("heightLimit", 256, 0, 256);

            thermals = builder
                    .comment("enable thermal rise by pressing the space bar")
                    .translation("config.parachutemod.thermals")
                    .define("thermals", true);

            weatherAffectsDrift = builder
                    .comment("set to false if you don't want the drift rate to be affected by bad weather")
                    .translation("config.parachutemod.weatherAffectsDrift")
                    .define("weatherAffectsDrift", true);

            lavaThermals = builder
                    .comment("use lava heat to get thermals to rise up, optionally disables space bar thermals")
                    .translation("config.parachutemod.thermals")
                    .define("thermals", true);

            minLavaDistance = builder
                    .comment("minimum distance from lava to grab thermals, if you go less than 3.0 you will most likely dismount in the lava!")
                    .translation("config.parachutemod.minLavaDistance")
                    .defineInRange("minLavaDistance", 5.0, 2.0, 10.0);

            maxLavaDistance = builder
                    .comment("maximum distance to rise from lava thermals")
                    .translation("config.parachutemod.maxLavaDistance")
                    .defineInRange("maxLavaDistance", 48.0, 10.0, 100.0);

            constantTurbulence = builder
                    .comment("set to true to always feel the turbulent world of Minecraft")
                    .translation("config.parachutemod.constantTurbulence")
                    .define("constantTurbulence", false);

            showContrails = builder
                    .comment("set to true to show contrails from parachute")
                    .translation("config.parachutemod.showContrails")
                    .define("showContrails", true);

            dismountInWater = builder
                    .comment("if true, dismount in water")
                    .translation("config.parachutemod.dismountInWater")
                    .define("dismountInWater", false);

            lavaDisablesThermals = builder
                    .comment("if true normal thermals are disabled by lava thermals")
                    .translation("config.parachutemod.lavaDisablesThermals")
                    .define("lavaDisablesThermals", false);

            forwardMomentum = builder
                    .comment("delta forward momentum value")
                    .translation("config.parachutemod.forwardMomentum")
                    .defineInRange("forwardMomentum", 0.015, 0.01, 0.02);

            backMomentum = builder
                    .comment("delta back momentum value")
                    .translation("config.parachutemod.backMomentum")
                    .defineInRange("backMomentum", 0.008, 0.005, 0.01);

            rotationMomentum = builder
                    .comment("delta rotation momentum value")
                    .translation("config.parachutemod.rotationMomentum")
                    .defineInRange("rotationMomentum", 0.2, 0.1, 0.3);

            slideMomentum = builder
                    .comment("delta slide momentum value")
                    .translation("config.parachutemod.slideMomentum")
                    .defineInRange("slideMomentum", 0.005, 0.004, 0.008);

            builder.pop();
        }

        public static boolean getShowContrails() { return showContrails.get(); }

        public static boolean getDismountInWater() { return dismountInWater.get(); }

        public static boolean getAllowThermals() { return thermals.get(); }

        public static boolean getLavaThermals() { return lavaThermals.get(); }

        public static boolean getLavaDisablesThermals() { return lavaDisablesThermals.get(); }

        public static boolean getSingleUse() { return singleUse.get(); }

        public static double getMinLavaDistance() {return minLavaDistance.get(); }

        public static double getMaxLavaDistance() { return maxLavaDistance.get(); }

        public static boolean getConstantTurbulence() { return constantTurbulence.get(); }

        public static int getHeightLimit() { return heightLimit.get(); }

        public static double getForwardMomentum() { return forwardMomentum.get(); }

        public static double getBackMomentum() { return backMomentum.get(); }

        public static double getRotationMomentum() { return rotationMomentum.get(); }

        public static double getSlideMomentum() { return slideMomentum.get(); }

        public static boolean getWeatherAffectsDrift() { return weatherAffectsDrift.get(); }

    }

    public static class ClientConfig {
        public static ForgeConfigSpec.IntValue WASDControl;
        public static ForgeConfigSpec.IntValue chuteColor;
        public static ForgeConfigSpec.IntValue hudPosition;
        public static ForgeConfigSpec.BooleanValue useFlyingSound;
        public static ForgeConfigSpec.DoubleValue burnVolume;
        private static final String[] COLORVALUES = { "random", "black", "blue", "brown", "cyan", "gray", "green", "light_blue",
                "lime", "magenta", "orange", "pink", "purple", "red", "silver", "white", "yellow",
                "custom0", "custom1", "custom2", "custom3", "custom4", "custom5", "custom6", "custom7", "custom8", "custom9",
        };
        private static final String[] HUDPOSVALUES= { "left", "center", "right" };
        private static final String[] STEERING_CONTROL = { "WASD", "Sight" };

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            Parachute.getLogger().info("Loading ConfigHandler.ClientConfig");

            builder.comment("ClientConfig Config").push("ClientConfig");

            WASDControl = builder
                    .comment("if index is 0 (zero) steering is 'WASD', otherwise steering is by sight  [index 0-1]")
                    .translation("config.parachutemod.steeringControl")
                    .defineInRange("WASDControl", 0, 0, 1);

            hudPosition = builder
                    .comment("HUD position is one of left|center|right [index 0|1|2]")
                    .translation("config.parachutemod.hudPosition")
                    .defineInRange("hudPosition", 2, 0, 2);

            chuteColor = builder
                    .comment("Parachute color, can be a minecraft color, random, or custom [index 0-26]",
                            "Color indexes correspond to random, black, blue, brown, cyan, gray, green, light_blue",
                            "lime, magenta, orange, pink, purple, red, silver, white, yellow",
                            "custom0 through custom9"
                    )
                    .translation("config.parachutemod.chuteColor")
                    .defineInRange("chuteColor", 0, 0, 26);

            burnVolume = builder
                    .comment("set the burn sound volume (0.0 to 1.0)")
                    .translation("config.parachutemod.burnVolume")
                    .defineInRange("burnVolme", 0.5, 0.0, 1.0);

            useFlyingSound = builder
                    .comment("set to true to hear the wind while flying")
                    .translation("config.parachutemod.useFlyingSound")
                    .define("useFlyingSound", true);

            builder.pop();
        }

        public static String getSteeringControl() { return STEERING_CONTROL[WASDControl.get()]; }

        public static String getChuteColor() { return COLORVALUES[chuteColor.get()]; }

        public static String getHUDPosition() { return HUDPOSVALUES[hudPosition.get()]; }

        public static boolean getUseFlyingSound() { return useFlyingSound.get(); }

        public static double getBurnVolume() { return burnVolume.get(); }
    }

    static final ForgeConfigSpec clientSpec;
    public static final ClientConfig CLIENT_CONFIG;
    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        clientSpec = specPair.getRight();
        CLIENT_CONFIG = specPair.getLeft();
    }


    static final ForgeConfigSpec commonSpec;
    public static final CommonConfig COMMON_CONFIG;
    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        commonSpec = specPair.getRight();
        COMMON_CONFIG = specPair.getLeft();
    }
}
