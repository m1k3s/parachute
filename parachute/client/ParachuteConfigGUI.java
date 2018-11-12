/*
 * ParachuteConfigGUI.java
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
package com.parachute.client;

import com.parachute.common.ConfigHandler;
import com.parachute.common.Parachute;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ParachuteConfigGUI extends GuiConfig {

    public ParachuteConfigGUI(GuiScreen parentScreen) {
        super(parentScreen,
              new ConfigElement(ConfigHandler.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
              Parachute.MODID, false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.getConfig().toString()));

    }

}
