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


public class ClientConfiguration {
    private static String chuteColor;
    private static boolean noHUD;
    private static boolean useCompassHUD;

    public ClientConfiguration() {}

    public static void setChuteColor(String color) {
        chuteColor = color;
    }

    public static void setNoHUD(boolean value) {
        noHUD = value;
    }

    public static void setUseCompassHUD(boolean value) {
        useCompassHUD = value;
    }

    public static String getChuteColor() {
        return chuteColor;
    }

    public static boolean getNoHUD() {
        return noHUD;
    }

    public static boolean getUseCompassHUD() {
        return useCompassHUD;
    }
}
