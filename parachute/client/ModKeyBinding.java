package com.parachute.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class ModKeyBinding {

    private static final String CATEGORY = "key.category.parachutemod:general";

    public static final KeyBinding POWEREDFLIGHT = new KeyBinding("key.parachutemod:poweredflight", KeyConflictContext.IN_GAME, Keyboard.KEY_P, CATEGORY);

    public static void registerKeyBinding() {
        ClientRegistry.registerKeyBinding(POWEREDFLIGHT);
    }
}
