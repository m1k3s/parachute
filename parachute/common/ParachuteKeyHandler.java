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

package parachute.common;

import java.util.EnumSet;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;


public class ParachuteKeyHandler extends KeyHandler {
	static KeyBinding ascendBinding = new KeyBinding("Parachute Ascend", Keyboard.KEY_C);
	static KeyBinding descendBinding = new KeyBinding("Parachute Descend", Keyboard.KEY_X);
	
    public ParachuteKeyHandler() {
        super(new KeyBinding[] {ascendBinding, descendBinding}, new boolean[] {true, true});
    }

    @Override
    public String getLabel() {
    	return "chutekeybindings";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		ParachutePacketHandler.sendKeyPress(kb.keyCode, true);
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		ParachutePacketHandler.sendKeyPress(kb.keyCode, false);
    }

    @Override
    public EnumSet<TickType> ticks() {
	    return EnumSet.of(TickType.CLIENT);
    }
}
