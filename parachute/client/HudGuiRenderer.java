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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HudGuiRenderer extends Gui {

	// the parachute-hud.png image uses some ELD Unofficial Continuation Project textures, modified to suit the parachute HUD.
	protected static final ResourceLocation hudTexture = new ResourceLocation(Parachute.modid + ":" + "textures/gui/parachute-hud.png");
	private static FontRenderer fontRenderer;
	public static double altitude;
	private final Minecraft mc = Minecraft.getMinecraft();

	private int blink = 0;
	private final int hudWidth;
	private final int hudHeight;
	private final int ledWidth;
	private final int ledHeight;
	private final int fieldWidth;
	private final int colorYellow;
	private final int colorRed;
	private final int colorGreen;
	private final int colorBlue;
	private final int colorDimBlue;
	private final int colorDimGreen;
	// AAD icon
	private final int aadWidth;
	private final int aadHeight;
	private final int ledY;
	// manual dismount indicators
	private final int lightY;
	private final int red;
	private final int darkRed;
	private final int green;
	private final int dark;
	private int blinkX;
	private final int blinkTime;
	private final int yOffset;
	// waypoint
	public static int wayPointX;
	public static int wayPointZ;
	private static boolean wayPointEnabled;

	public HudGuiRenderer()
	{
		super();
		hudWidth = 182;
		hudHeight = 39;
		ledWidth = 11;
		ledHeight = 5;
		colorYellow = 0xffaaaa00;
		colorRed = 0xffaa0000;
		colorGreen = 0xff00aa00;
		colorBlue = 0xff0000aa;
		colorDimBlue = 0xcc000088;
		colorDimGreen = 0xcc008800;
		aadWidth = 16;
		aadHeight = 25;
		ledY = 39;
		lightY = 44;
		red = 0;
		darkRed = 48;
		green = 16;
		dark = 32;
		blinkX = red;
		blinkTime = 5;
		yOffset = 14;
		wayPointX = 0;
		wayPointZ = 0;
		// disable the waypoint display
		wayPointEnabled = false;

		fontRenderer = mc.fontRendererObj;
		fieldWidth = fontRenderer.getStringWidth("000.0") / 2;
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event)
	{
		if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.thePlayer.onGround) {
			return;
		}
		ScaledResolution sr = new ScaledResolution(mc);
		int hudX = sr.getScaledWidth() / 2 - (hudWidth / 2); // left edge of GUI
		int hudY = 2; // top edge of GUI
		int textX = hudX + 30; // xcoord for text
		int textY = hudY + 22; // ycoord for text
		int ledX = 1;

		if (mc.inGameHasFocus && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			if (ParachuteCommonProxy.onParachute(mc.thePlayer)) {
				mc.getTextureManager().bindTexture(hudTexture);

				BlockPos entityPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);
				altitude = getCurrentAltitude(entityPos);
				double homeDir = getHomeDirection();
				double distance = getHomeDistance();
				double heading = (((mc.thePlayer.rotationYaw + 180.0) % 360) + 360) % 360;

				// Params: int screenX, int screenY, int textureX, int textureY, int width, int height
				drawTexturedModalRect(hudX, hudY, 0, 0, hudWidth, hudHeight); // draw the main hud

				// determine which LED to light, spawnDir is in range -180 to 180
				// for any value under -80 or over 80 the LED is fixed to the
				// left or right end of the slider respectively.
				if (homeDir < -80) {
					ledX = 1;
				} else if ((homeDir - 80) * (homeDir - -80) < 0) {
					ledX = (int)Math.floor((homeDir + 80.0) + 4);
				} else if (homeDir > 80) {
					ledX = 170;
				}
				drawTexturedModalRect(hudX + ledX, hudY, ledX, ledY, ledWidth, ledHeight); // draw the lit LED

				// AAD status
				int aadIconX;
				int aadIconY = 8;
				if (ConfigHandler.getIsAADActive()) {
					aadIconX = 199;
				} else {
					aadIconX = 182;
				}
				drawTexturedModalRect(hudX + hudWidth + 2, hudY + 8, aadIconX, aadIconY, aadWidth, aadHeight); // draw the AAD indicator

				// manual dismount indicator
				if (ConfigHandler.isAutoDismount()) { // auto dismount is engaged
					drawTexturedModalRect(hudX - 18, hudY + yOffset, dark, lightY, 16, 16);
				} else { // auto dismount is disabled
					if (altitude > 10) {
						drawTexturedModalRect(hudX - 18, hudY + yOffset, green, lightY, 16, 16);
					} else if (altitude <= 10 && altitude > 3) {
						drawTexturedModalRect(hudX - 18, hudY + yOffset, red, lightY, 16, 16);
					} else if (altitude <= 3) { // make this blink
						if ((blink % blinkTime) == 0) {
							blinkX = blinkX == red ? darkRed : red;
						}
						drawTexturedModalRect(hudX - 18, hudY + yOffset, blinkX, lightY, 16, 16);
						blink++;
					}
				}

				if (wayPointEnabled) {
					double waypointDirection = getWaypointDirection(wayPointX, wayPointZ);
					// draw the waypoint heading
					if (waypointDirection < -80) {
						ledX = 1;
					} else if ((waypointDirection - 80) * (waypointDirection - -80) < 0) {
						ledX = (int)Math.floor((waypointDirection + 80.0) + 4);
					} else if (waypointDirection > 80) {
						ledX = 170;
					}
					// draw the waypoint bar background
					drawTexturedModalRect(hudX, hudY + hudHeight, 0, 0, hudWidth, ledHeight);
					// draw the lit LED
					drawTexturedModalRect(hudX + ledX, hudY + hudHeight, ledX, ledY, ledWidth, ledHeight);
				}

				// draw the altitude text
				fontRenderer.drawStringWithShadow(I18n.format("gui.hud.altitude"), hudX + 18, hudY + 12, colorDimBlue);
				fontRenderer.drawStringWithShadow(format(altitude), (textX + 5) - fieldWidth, textY, colorAltitude());
				// draw the compass heading text
				fontRenderer.drawStringWithShadow(I18n.format("gui.hud.compass"), hudX + 123, hudY + 12, colorDimBlue);
				fontRenderer.drawStringWithShadow(format(heading), (textX + 118) - fieldWidth, textY, colorCompass(heading));
				// draw the distance to the home point text
				fontRenderer.drawStringWithShadow(format(distance), (textX + 65) - fieldWidth, textY, colorDimGreen);
			}
		}
	}

	public String format(double d)
	{
		return String.format("%.1f", d);
	}
	
	// difference angle in degrees the player is facing from the home point.
	// zero degrees means the player is facing the home point.
	// the home point can be either the world spawn point or a waypoint
	// set by the player in the config.
	public double getHomeDirection()
	{
		BlockPos blockpos;
		if (ConfigHandler.getUseSpawnPoint()) {
			blockpos = mc.theWorld.getSpawnPoint();
		} else {
			blockpos = ConfigHandler.getWaypoint();
		}
		double delta = Math.atan2(blockpos.getZ()- mc.thePlayer.posZ, blockpos.getX() - mc.thePlayer.posX);
		double relAngle = delta - Math.toRadians(mc.thePlayer.rotationYaw);
		return MathHelper.wrapAngleTo180_double(Math.toDegrees(relAngle) - 90.0); // degrees
	}

	public double getHomeDistance()
	{
		BlockPos blockpos;
		if (ConfigHandler.getUseSpawnPoint()) {
			blockpos = mc.theWorld.getSpawnPoint();
		} else {
			blockpos = ConfigHandler.getWaypoint();
		}
		double a = Math.pow(blockpos.getZ()- mc.thePlayer.posZ, 2);
		double b = Math.pow(blockpos.getX() - mc.thePlayer.posX, 2);
		return Math.sqrt(a + b);
	}

	public int colorAltitude()
	{
		return (altitude <= 10.0 && altitude >= 0.0) ? colorRed : altitude < 0.0 ? colorYellow : colorGreen;
	}

	// quadrant color code
	// 315 to 45 green, 45 to 135 yellow, 135 to 225 red, 335 to 315 blue
	public int colorCompass(double d)
	{
		return (d >= 0 && d < 45.0) ? colorGreen : (d >= 45.0 && d < 135.0) ? colorYellow :
				(d >= 135.0 && d < 225.0) ? colorRed : (d >= 225.0 && d < 315.0) ? colorBlue : colorGreen;
	}

	// calculate altitude in meters above ground. starting at the entity
	// count down until a non-air block is encountered.
	// only allow altitude calculations in the surface world
	// return a weirdly random number if in nether or end.
	public double getCurrentAltitude(BlockPos entityPos)
	{
		if (mc.theWorld.provider.isSurfaceWorld()) {
			BlockPos blockPos = new BlockPos(entityPos.getX(), entityPos.getY(), entityPos.getZ());
			while (mc.theWorld.isAirBlock(blockPos.down())) {
				blockPos = blockPos.down();
			}
			// calculate the entity's current altitude above the ground
			return entityPos.getY() - blockPos.getY();
		}
		return 1000.0 * mc.theWorld.rand.nextGaussian();
	}

	// difference angle in degrees the player is facing from the waypoint.
	// zero degrees means the player is facing the waypoint.
	public double getWaypointDirection(int waypointX, int waypointZ)
	{
		BlockPos blockpos = new BlockPos(waypointX, 0, waypointZ);
		double delta = Math.atan2(blockpos.getZ()- mc.thePlayer.posZ, blockpos.getX() - mc.thePlayer.posX);
		double relAngle = delta - Math.toRadians(mc.thePlayer.rotationYaw);
		return MathHelper.wrapAngleTo180_double(Math.toDegrees(relAngle) - 90.0); // degrees
	}

	public static void setWaypoint(int waypointXIn, int waypointZIn)
	{
		wayPointX = waypointXIn;
		wayPointZ = waypointZIn;
	}

	public static void enableWaypoint(boolean enabled)
	{
		wayPointEnabled = enabled;
	}

	public static boolean getEnableWaypoint()
	{
		return wayPointEnabled;
	}

	public static String getWaypointString()
	{
		return String.format("%d %d", wayPointX, wayPointZ);
	}

}