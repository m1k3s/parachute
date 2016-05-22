package com.parachute.common;


// import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.event.*;

@SuppressWarnings("unused")
public class ParachuteServerProxy extends ParachuteCommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		// info(Parachute.modid + I18n.translateToLocal("info.server.preinit"));
	}

	@Override
	public void Init(FMLInitializationEvent event)
	{
		super.Init(event);
		// info(Parachute.modid + I18n.translateToLocal("info.server.init"));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		// info(Parachute.modid + I18n.translateToLocal("info.server.postinit"));
	}
}
