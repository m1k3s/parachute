package parachute.client;

import parachute.common.CommonProxyParachute;
import parachute.common.EntityParachute;
import parachute.common.ParachuteKeyHandler;
import parachute.common.RenderParachute;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.Packet;
import net.minecraftforge.client.MinecraftForgeClient;
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

