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
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.client;

import com.parachute.common.ParachuteCommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AltitudeDisplay {

	public static double altitude = 0.0;
	private double spawnDir;
//	private String indicator;
//	private int indicatorColor;
	private final Minecraft mc = Minecraft.getMinecraft();
	private int screenX;
	private int screenY;

    private final String altitudeLabel = "Altitude: ";
	private final int titleWidth = mc.fontRendererObj.getStringWidth(altitudeLabel);
	private final int fieldWidth = mc.fontRendererObj.getStringWidth("000.0");
	private final int totalWidth = titleWidth + fieldWidth;
	private final int colorYellow = 0xffffff00;
	private final int colorRed = 0xffaa0000;
	private final int colorGreen = 0xff00aa00;
	private final int colorWhite = 0xffffffff;

	public AltitudeDisplay()
	{
		super();
		ScaledResolution sr = new ScaledResolution(mc);
		screenX = sr.getScaledWidth();
		screenY = sr.getScaledHeight();
	}

	// the altitudeStr display is placed in the food bar space because
	// the food bar is removed when riding boats, parachutes, etc.
	// when in creativemode we lower the display a bit
	public void updateWindowScale()
	{
		ScaledResolution sr = new ScaledResolution(mc);
		screenX = (sr.getScaledWidth() / 2) + 10;
		if (mc.thePlayer.capabilities.isCreativeMode) {
			screenY = sr.getScaledHeight() - 30;
		} else {
			screenY = sr.getScaledHeight() - 38;
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event)
	{
		if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.thePlayer.onGround) {
			return;
		}

		if (mc.inGameHasFocus && event.type == RenderGameOverlayEvent.ElementType.ALL) {
			if (ParachuteCommonProxy.onParachute(mc.thePlayer)) {
				updateWindowScale();
				String altitudeStr = format(altitude);
				spawnDir = getSpawnDirection();
				String heading = format(spawnDir);
//				calcSpawnDirectionIndicator();
				int stringWidth = mc.fontRendererObj.getStringWidth(altitudeStr);
				int nextX = totalWidth - stringWidth;
				mc.fontRendererObj.drawStringWithShadow(altitudeLabel, screenX, screenY, colorWhite);
				mc.fontRendererObj.drawStringWithShadow(altitudeStr, screenX + nextX, screenY, colorAltitude());
				nextX = totalWidth + 5;
				mc.fontRendererObj.drawStringWithShadow("(" + heading + ")", screenX + nextX, screenY, colorSpawnDir());
//				mc.fontRendererObj.drawStringWithShadow(indicator, screenX + nextX, screenY, indicatorColor);
			}
		}
	}
	
	public String format(double d)
	{
		return String.format("%.1f", d);
	}
	
	public static void setAltitudeDouble(double alt)
	{
		altitude = alt;
	}

	// differnece angle in degrees the player is facing from the spawn point.
	// zero degrees means the player is facing the spawn point.
	public double getSpawnDirection()
	{
		BlockPos blockpos = mc.theWorld.getSpawnPoint();
		double d0 = Math.atan2(blockpos.getZ()- mc.thePlayer.posZ, blockpos.getX() - mc.thePlayer.posX);
		double relAngle = d0 - (mc.thePlayer.rotationYaw * 0.0174532925199433); // radians
		return MathHelper.wrapAngleTo180_double((relAngle * 57.2957795130823) - 90.0); // degrees
	}

	private int colorAltitude()
	{
		return (altitude <= 8.0 && altitude >= 0.0) ? colorRed : altitude < 0.0 ? colorYellow : colorGreen;
	}

	private int colorSpawnDir()
	{
		return (spawnDir >= -5.0 && spawnDir <= 5.0) ? colorWhite : colorYellow;
	}

//	private void calcSpawnDirectionIndicator()
//	{
//		if (spawnDir >= -5.0 && spawnDir <= 5.0) {
//			indicator = "<*>";
//			indicatorColor = colorGreen;
//		} else if (spawnDir >= -90.0 && spawnDir <= 90.0) {
//			indicator = "< >";
//			indicatorColor = colorYellow;
//		} else {
//			indicator = "<< >>";
//			indicatorColor = colorRed;
//		}
//	}

}
