/*
 * ModKeyBinding.java
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

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    private static final String CATEGORY = "key.category.parachutemod.general";
    public static final KeyBinding TOGGLE_HUD_VISIBILITY = new KeyBinding("key.parachutemod.toggle_hud", GLFW.GLFW_KEY_H, CATEGORY);

    public static void registerKeyBinding() {
        ClientRegistry.registerKeyBinding(TOGGLE_HUD_VISIBILITY);
    }
}
