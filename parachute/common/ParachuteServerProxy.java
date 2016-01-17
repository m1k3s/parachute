package com.parachute.common;


@SuppressWarnings("unused")
public class ParachuteServerProxy extends ParachuteCommonProxy {

	@Override
	public void preInit()
	{
		super.preInit();
		info(Parachute.modid + " DedicatedServer preInit is complete");
	}

	@Override
	public void Init()
	{
		super.Init();
		info(Parachute.modid + " DedicatedServer Init is complete");
	}

	@Override
	public void postInit()
	{
		info(Parachute.modid + " DedicatedServer postInit is complete.");
	}
}
