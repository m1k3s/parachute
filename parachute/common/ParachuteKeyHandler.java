//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package parachute.common;

import java.util.EnumSet;

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
//    	if (ItemParachute.deployed) {
    		ParachutePacketHandler.sendKeyPress(kb.keyCode, true);
//    	}
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
//    	if (ItemParachute.deployed) {
    		ParachutePacketHandler.sendKeyPress(kb.keyCode, false);
//    	}
    }

    @Override
    public EnumSet<TickType> ticks() {
	    return EnumSet.of(TickType.CLIENT);
    }
}
