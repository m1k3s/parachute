/*
 * ClientConfiguration.java
 *
 *  Copyright (c) 2018 Michael Sheppard
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

package com.parachute.client;


// The ClientConfiguration class contains all the client
// controlled options. The other config options are
// server side options.
@SuppressWarnings("unused")
public class ClientConfiguration {
    private static String chuteColor;
    private static boolean noHUD;
    private static double burnVolume;
    private static String hudPosition;
    private static boolean altitudeMSL;
    private static String steeringControl;
    private static boolean frontBubble;

    public ClientConfiguration() {}

    public static void setChuteColor(String color) {
        chuteColor = color;
    }

    public static void setNoHUD(boolean value) {
        noHUD = value;
    }

    public static void setBurnVolume(double value) {
        burnVolume = value;
    }

    public static void setHudPosition(String value) {
        hudPosition = value;
    }

    public static void setAltitudeMSL(boolean value) {
        altitudeMSL = value;
    }

    public static void setSteeringControl(String value) {
        steeringControl = value;
    }

    public static void setFrontBubble(boolean value) { frontBubble = value; }

    public static String getChuteColor() {
        return chuteColor;
    }

    public static boolean getNoHUD() {
        return noHUD;
    }

    public static float getBurnVolume() {
        return (float)burnVolume;
    }

    public static String getHudPosition() {
        return hudPosition;
    }

    public static boolean getAltitdeMSL() {
        return altitudeMSL;
    }

    public static String getSteeringControl() {
        return steeringControl;
    }

    public static boolean getFrontBubble() { return frontBubble; }

}
