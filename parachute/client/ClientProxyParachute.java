//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package parachute.client;

import parachute.common.CommonProxyParachute;
import parachute.common.EntityParachute;
import parachute.common.ParachuteKeyHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxyParachute extends CommonProxyParachute
{
    @Override
    public void registerRenderTextures() {
    	MinecraftForgeClient.preloadTexture("/textures/parachuteItem.png");
    }
    
    @Override
    public void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute());   
	}
    
    @Override
    public void registerKeyHandler() {
    	KeyBindingRegistry.registerKeyBinding(new ParachuteKeyHandler());
    }
    
}

