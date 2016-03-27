package com.parachute.common;


import net.minecraft.client.resources.I18n;

@SuppressWarnings("unused")
public class ParachuteServerProxy extends ParachuteCommonProxy {

	@Override
	public void preInit()
	{
		super.preInit();
		info(Parachute.modid + I18n.format("info.server.preinit"));
	}

	@Override
	public void Init()
	{
		super.Init();
		info(Parachute.modid + I18n.format("info.server.init"));
	}

	@Override
	public void postInit()
	{
		info(Parachute.modid + I18n.format("info.server.postinit"));
	}
}
