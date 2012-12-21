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
//	static KeyBinding up = FMLClientHandler.instance().getClient().gameSettings.keyBindJump;
//	static KeyBinding down = FMLClientHandler.instance().getClient().gameSettings.keyBindSneak;
	
    public ParachuteKeyHandler() {
        super(new KeyBinding[] {ascendBinding, descendBinding}, new boolean[] {true, true});
//    	super(new KeyBinding[] {up, down}, new boolean[] {true, true});
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
