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

import com.parachute.common.ConfigHandler;
import com.parachute.common.Parachute;
import com.parachute.common.ParachuteCommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AltitudeDisplay  extends Gui {

	protected static final ResourceLocation hudTexPath = new ResourceLocation(Parachute.modid + ":" + "textures/gui/hud.png");
	public static double altitude = 0.0;
	private double spawnDir;
	private final Minecraft mc = Minecraft.getMinecraft();

	private final int guiWidth = 182;
	private final int guiHeight = 32;
	private final int ledWidth = 10;
	private final int ledHeight = 10;
	private final int fieldWidth = mc.fontRendererObj.getStringWidth("000.0") / 2;
	private final int colorYellow = 0xffffff00;
	private final int colorRed = 0xffaa0000;
	private final int colorGreen = 0xff00aa00;
	// LED dimensions are 10x10
	private final int ledY = 32;
	private final int redLEDX = 0;
	private final int greenLEDX = 10;
	private final int yellowLEDX = 20;
	// AAD icon
	private final int aadWidth = 16;
	private final int aadHeight = 25;

	public AltitudeDisplay()
	{
		super();
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event)
	{
		if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.thePlayer.onGround) {
			return;
		}
		ScaledResolution sr = new ScaledResolution(mc);
		int guiX = sr.getScaledWidth() / 2 - (guiWidth / 2); // left edge of GUI
		int guiY = 2; // top edge of GUI
		int textX = guiX + 45;
		int textY = guiY + 22; // y coord for text
		int ledX = 6;
		int ledType = redLEDX;

		if (mc.inGameHasFocus && event.type == RenderGameOverlayEvent.ElementType.ALL) {
			if (ParachuteCommonProxy.onParachute(mc.thePlayer)) {
				// render the hud gui
				mc.getTextureManager().bindTexture(hudTexPath);

				spawnDir = getSpawnDirection();
				String altitudeStr = format(altitude);

				// int x, int y, int textureX, int textureY, int width, int height
				drawTexturedModalRect(guiX, guiY, 0, 0, guiWidth, guiHeight); // draw the gui outline

				if (spawnDir > 80) {
					ledX = 6;
					ledType = redLEDX;
				} else if (spawnDir <= 80 && spawnDir > 70) {
					ledX = 16;
					ledType = redLEDX;
				} else if (spawnDir <= 70 && spawnDir > 60) {
					ledX = 26;
					ledType = redLEDX;
				} else if (spawnDir <= 60 && spawnDir > 50) {
					ledX = 36;
					ledType = redLEDX;
				} else if (spawnDir <= 50 && spawnDir > 40) {
					ledX = 46;
					ledType = yellowLEDX;
				} else if (spawnDir <= 40 && spawnDir > 30) {
					ledX = 56;
					ledType = yellowLEDX;
				} else if (spawnDir <= 30 && spawnDir > 20) {
					ledX = 66;
					ledType = yellowLEDX;
				} else if (spawnDir <= 20 && spawnDir > 5) {
					ledX = 76;
					ledType = yellowLEDX;
				} else if (spawnDir <= 5 && spawnDir >= -5) {
					ledX = 86;
					ledType = greenLEDX;
				} else if (spawnDir >= -20 && spawnDir < -5) {
					ledX = 96;
					ledType = yellowLEDX;
				} else if (spawnDir >= -30 && spawnDir < -20) {
					ledX = 106;
					ledType = yellowLEDX;
				} else if (spawnDir >= -40 && spawnDir < -30) {
					ledX = 116;
					ledType = yellowLEDX;
				} else if (spawnDir >= -50 && spawnDir < -40) {
					ledX = 126;
					ledType = yellowLEDX;
				} else if (spawnDir >= -60 && spawnDir < -50) {
					ledX = 136;
					ledType = redLEDX;
				} else if (spawnDir >= -70 && spawnDir < -60) {
					ledX = 146;
					ledType = redLEDX;
				}  else if (spawnDir >= -80 && spawnDir < -70) {
					ledX = 156;
					ledType = redLEDX;
				} else if (spawnDir <= -80) {
					ledX = 166;
					ledType = redLEDX;
				}
				drawTexturedModalRect(guiX + ledX, guiY + 1, ledType, ledY, ledWidth, ledHeight); // draw the LEDs
				// AAD status
				int aadIconX;
				int aadIconY = 53;
				if (ConfigHandler.getIsAADActive()) {
					aadIconX = 53;
				} else {
					aadIconX = 36;
				}
				drawTexturedModalRect(guiX + guiWidth, guiY + 3, aadIconX, aadIconY, aadWidth, aadHeight);

				// finally draw the altitude and compass heading text
				double heading = ((mc.thePlayer.rotationYaw % 360) + 360) % 360;
				mc.fontRendererObj.drawStringWithShadow(altitudeStr, textX - fieldWidth, textY, colorAltitude());
				mc.fontRendererObj.drawStringWithShadow(format(heading), (textX + 91) - fieldWidth, textY, colorRed);
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

}