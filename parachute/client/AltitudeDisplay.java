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
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AltitudeDisplay extends Gui {

    protected static final ResourceLocation hudTexPath = new ResourceLocation(Parachute.modid + ":" + "textures/gui/parachute-hud.png");
    protected static final ResourceLocation locationFontTexture = new ResourceLocation((Parachute.modid + ":" + "textures/font/ascii.png"));
    private static FontRenderer fontRenderer;
    public static double altitude;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final int guiWidth;
    private final int guiHeight;
    private final int ledWidth;
    private final int ledHeight;
    private final int fieldWidth;
    private final int colorYellow;
    private final int colorRed;
    private final int colorGreen;
    private final int colorBlue;
    private final int colorDimBlue;
    // AAD icon
    private final int aadWidth;
    private final int aadHeight;
    private final int ledY;

    private boolean renderCustomFont = ConfigHandler.getUseCustomFont(); // config variable

    public AltitudeDisplay() {
        super();

        guiWidth = 182;
        guiHeight = 39;
        ledWidth = 11;
        ledHeight = 5;
        colorYellow = 0xffaaaa00;
        colorRed = 0xffaa0000;
        colorGreen = 0xff00aa00;
        colorBlue = 0xff0000aa;
        colorDimBlue = 0xcc000088;
        aadWidth = 16;
        aadHeight = 25;
        ledY = 39;

        if (renderCustomFont) {
            fontRenderer = new ParachuteFontRenderer(mc, locationFontTexture);
        } else {
            fontRenderer = mc.fontRendererObj;
        }
        fieldWidth = fontRenderer.getStringWidth("000.0") / 2;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.thePlayer.onGround) {
            return;
        }
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int guiX = sr.getScaledWidth() / 2 - (guiWidth / 2); // left edge of GUI
        int guiY = 2; // top edge of GUI
        int textX = guiX + 50; // xcoord for text
        int textY = guiY + 22; // ycoord for text
        int ledX = 1;

        if (mc.inGameHasFocus && event.type == RenderGameOverlayEvent.ElementType.ALL) {
            if (ParachuteCommonProxy.onParachute(mc.thePlayer)) {
                mc.getTextureManager().bindTexture(hudTexPath);

                BlockPos entityPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                altitude = getCurrentAltitude(entityPos);

                double spawnDir = getSpawnDirection();
                String altitudeStr = format(altitude);
                // int x, int y, int textureX, int textureY, int width, int height
                drawTexturedModalRect(guiX, guiY, 0, 0, guiWidth, guiHeight); // draw the main gui

                // determine which LED to light, spawnDir is in range -180 to 180
                // for any value under -maxAngle or over maxAngle the LED is fixed to the
                // left or right end of the slider respectively.
                final double maxAngle = 80.0;
                if (spawnDir < -maxAngle) {
                    ledX = 1;
                } else if ((spawnDir - maxAngle) * (spawnDir - -maxAngle) < 0) {
                    ledX = (int) Math.floor((spawnDir + maxAngle) + 4);
                } else if (spawnDir > maxAngle) {
                    ledX = 170;
                }
                drawTexturedModalRect(guiX + ledX, guiY, ledX, ledY, ledWidth, ledHeight); // draw the lit LED

                // AAD status
                int aadIconX = ConfigHandler.getIsAADActive() ? 199 : 182;
                int aadIconY = 8;
                drawTexturedModalRect(guiX + guiWidth, guiY + 8, aadIconX, aadIconY, aadWidth, aadHeight); // draw the AAD indicator

                // finally draw the altitude and compass heading text
                double heading = (((mc.thePlayer.rotationYaw + 180.0) % 360) + 360) % 360;

                fontRenderer.drawStringWithShadow("Altitude", guiX + 28, guiY + 12, colorDimBlue);
                fontRenderer.drawStringWithShadow(altitudeStr, textX - fieldWidth, textY, colorAltitude());
                fontRenderer.drawStringWithShadow("Compass", guiX + 113, guiY + 12, colorDimBlue);
                fontRenderer.drawStringWithShadow(format(heading), (textX + 88) - fieldWidth, textY, colorCompass(heading));
            }
        }
    }

    // difference angle in degrees the player is facing from the spawn point.
    // zero degrees means the player is facing the spawn point.
    public double getSpawnDirection() {
        BlockPos blockpos = mc.theWorld.getSpawnPoint();
        double delta = Math.atan2(blockpos.getZ() - mc.thePlayer.posZ, blockpos.getX() - mc.thePlayer.posX);
        double relAngle = delta - (mc.thePlayer.rotationYaw * 0.0174532925199433); // radians
        return MathHelper.wrapAngleTo180_double((relAngle * 57.2957795130823) - 90.0); // degrees
    }

    public String format(double d) {
        return String.format("%.1f", d);
    }

    public int colorAltitude() {
        return (altitude <= 8.0 && altitude >= 0.0) ? colorRed : altitude < 0.0 ? colorYellow : colorGreen;
    }

    // quadrant color code
    // 315 to 45 green, 45 to 135 yellow, 135 to 225 red, 335 to 315 blue
    public int colorCompass(double d) {
        return (d >= 0 && d < 45.0) ? colorGreen : (d >= 45.0 && d < 135.0) ? colorYellow :
                (d >= 135.0 && d < 225.0) ? colorRed : (d >= 225.0 && d < 315.0) ? colorBlue : colorGreen;
    }
    
    public double getCurrentAltitude(BlockPos entityPos)
	{
		if (mc.theWorld.provider.isSurfaceWorld()) {
            BlockPos blockPos = new BlockPos(entityPos.getX(), entityPos.getY(), entityPos.getZ());
		    while (mc.theWorld.isAirBlock(blockPos.down())) {
			    blockPos = blockPos.down();
		    }
		    // calculate the players current altitude above the ground
		    return entityPos.getY() - blockPos.getY();
		}
		return 1000.0 * mc.theWorld.rand.nextGaussian();
	}

}
