/*
 * HudCompassRenderer.java
 *
 *  Copyright (c) 2018 Michael Sheppard
 *
 * =====GPL=============================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 * =====================================================================
 */

package com.parachute.client;

import com.parachute.common.ConfigHandler;
import com.parachute.common.EntityParachute;
import com.parachute.common.Parachute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


////////////////////////////////////////////////
// to position HUD in upper right corner
//      int padding = 20;
//
//      hudX = ((width * scale) - hudWidth) - padding;
//      hudY = padding;
//      int textX = (hudWidth * scale) - (hudWidth / 2) - (padding / 2);
//      int textY = hudY + (hudHeight / scale) - (padding / 2);
//
// to position HUD in upper left corner
//      int padding = 20;
//
//      hudX = padding;
//      hudY = padding;
//      int textX = hudX + (hudWidth / 4);
//      int textY = hudY + (hudHeight / 4);

@SideOnly(Side.CLIENT)
public class HudCompassRenderer extends Gui {
    protected static final ResourceLocation compassTexture = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-compass.png");
    protected static final ResourceLocation homeTexture = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-home.png");
    protected static final ResourceLocation bubbleTexture = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-bubble.png");
    protected static final ResourceLocation reticuleTexture = new ResourceLocation((Parachute.MODID + ":" + "textures/gui/hud-reticule.png"));
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static FontRenderer fontRenderer = mc.fontRenderer;

    public static double altitude;

    private static int count = 0;

    private final int hudWidth = 256;
    private final int hudHeight = 256;

    private final int colorGreen = 0xff00ff00;
    private final int colorRed = 0xffff0000;

    double compassHeading;

    private int hudX;
    private int hudY;

    // display variables
    private boolean aadActive;
    private String alt, compass, dist;

    public HudCompassRenderer() {
        super();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void drawCompassHUD(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.player.onGround) {
            return;
        }
        if (ClientConfiguration.getNoHUD()) {
            return;
        }
        if (mc.inGameHasFocus && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ScaledResolution sr = event.getResolution();
            int scale = sr.getScaleFactor();
            int width = sr.getScaledWidth();
            int padding = 20;

            hudX = ((width * scale) - hudWidth) - padding; // position HUD on top right
            hudY = padding; // top of HUD 'padding' pixels from top

            // initial text coords
            int textX = (hudWidth * scale) - (hudWidth / 2) - (padding / 2);
            int textY = hudY + (hudHeight / scale) - (padding / 2);

            if (mc.player.getRidingEntity() instanceof EntityParachute) {
                EntityParachute chute = (EntityParachute) mc.player.getRidingEntity();
                if (chute == null) {
                    return;
                }

                BlockPos entityPos = new BlockPos(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ);
                altitude = getCurrentAltitude(entityPos);
                double homeDir = getHomeDirection(chute.rotationYaw);
                double distance = getHomeDistance();
                compassHeading = calcCompassHeading(chute.rotationYaw);

                // scale the HUD to 50%
                GlStateManager.pushMatrix();

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                GlStateManager.scale(0.5, 0.5, 0.5);

                // 1. draw the compass
                drawTextureWithRotation((float) -compassHeading, compassTexture);

                // 2. draw the home direction
                drawTextureWithRotation((float) homeDir, homeTexture);

                // 3. draw the parachute/player facing bubble
                double playerLook = MathHelper.wrapDegrees(mc.player.getRotationYawHead() - chute.rotationYaw);
//                drawBubble(calcPlayerChuteFacing(playerLook), bubbleTexture);
                drawTextureWithRotation((float)calcBubbleDegrees(playerLook), bubbleTexture);

                // 4, draw the reticule on top
                drawReticule(reticuleTexture);

                // damping the update
                if (count % 10 == 0) {
                    aadActive = ConfigHandler.getIsAADActive();
                    alt = format(altitude);
                    compass = format(compassHeading);
                    dist = format(distance);
                }
                count++;

                int hFont = fontRenderer.FONT_HEIGHT;
                // draw the altitude text
                drawCenteredString(fontRenderer, alt, textX, textY - hFont - 8, colorAltitude());

                // draw the compass heading text
                drawCenteredString(fontRenderer, compass, textX, textY - (hFont * 2) - 8, colorGreen);

                // draw the distance to the home point text
                drawCenteredString(fontRenderer, dist, textX, textY + hFont - 2, colorGreen);

                // AAD active
                drawCenteredString(fontRenderer, "* AAD *", textX, textY + (hFont * 2) - 2, aadActive ? colorGreen : colorRed);

                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();

                GlStateManager.popMatrix();
            }
        }
    }

    // drawReticule
    private void drawReticule(ResourceLocation texture) {
        GlStateManager.pushMatrix();

        // scale again, final scale is 25% of original size
        GlStateManager.scale(0.5, 0.5, 0.5);
        mc.getTextureManager().bindTexture(texture);
        // draw the bubble
        drawTexturedModalRect(hudX, hudY, 0, 0, hudWidth, hudHeight);

        GlStateManager.popMatrix();
    }

    // drawTexturedModalRect
    // Params: int screenX, int screenY, int textureX, int textureY, int width, int height
//    private void drawBubble(float bubble, ResourceLocation texture) {
//        GlStateManager.pushMatrix();
//
//        // scale again, final scale is 25% of original size
//        GlStateManager.scale(0.5, 0.5, 0.5);
//        mc.getTextureManager().bindTexture(texture);
//        // draw the bubble
//        drawTexturedModalRect(hudX + bubble, hudY + (hudHeight / 2) - 9, 0, 0, 16, 16);
//        // draw the line
//        drawTexturedModalRect(hudX, hudY + 20, 0, 20, hudWidth, hudHeight - 20);
//
//        GlStateManager.popMatrix();
//    }

    // draw the compass/home textures
    private void drawTextureWithRotation(float degrees, ResourceLocation texture) {
        GlStateManager.pushMatrix();

        float tx = hudX + (hudWidth / 2);
        float ty = hudY + (hudHeight / 2);
        // scale again, final scale is 25% of original size
        GlStateManager.scale(0.5, 0.5, 0.5);
        // translate to center and rotate
        GlStateManager.translate(tx, ty, 0);
        GlStateManager.rotate(degrees, 0, 0, 1);
        GlStateManager.translate(-tx, -ty, 0);

        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(hudX, hudY, 0, 0, hudWidth, hudHeight);

        GlStateManager.popMatrix();
    }

    public String format(double d) {
        return String.format("%.1f", d);
    }

    private double calcCompassHeading(double yaw) {
        return (((yaw + 180.0) % 360) + 360) % 360;
    }

//    private int calcPlayerChuteFacing(double playerLook) {
//        int bubble = (int) Math.floor(playerLook + 120.0);
//        bubble = bubble < 58 ? 58 : bubble > 184 ? 184 : bubble;
//        return bubble;
//    }

    private double calcBubbleDegrees(double playerlook) {
        return MathHelper.wrapDegrees(playerlook);
    }

    // difference angle in degrees the chute is facing from the home point.
    // zero degrees means the chute is facing the home point.
    // the home point can be either the world spawn point or a waypoint
    // set by the player in the config.
    public double getHomeDirection(double yaw) {
        BlockPos blockpos = mc.world.getSpawnPoint();
        double delta = Math.atan2(blockpos.getZ() - mc.player.posZ, blockpos.getX() - mc.player.posX);
        double relAngle = delta - Math.toRadians(yaw);
        return MathHelper.wrapDegrees(Math.toDegrees(relAngle) - 90.0); // degrees
    }

    public double getHomeDistance() {
        BlockPos blockpos = mc.world.getSpawnPoint();
        double a = Math.pow(blockpos.getZ() - mc.player.posZ, 2);
        double b = Math.pow(blockpos.getX() - mc.player.posX, 2);
        return Math.sqrt(a + b);
    }

    public int colorAltitude() {
        int colorYellow = 0xffffff00;
        return altitude <= 10.0 ? colorRed : altitude <= 15.0 && altitude > 10.0 ? colorYellow : colorGreen;
    }

    // calculate altitude in meters above ground. starting at the entity
    // count down until a non-air block is encountered.
    // only allow altitude calculations in the surface world
    // return a weirdly random number if in nether or end.
    public double getCurrentAltitude(BlockPos entityPos) {
        if (mc.world.provider.isSurfaceWorld()) {
            BlockPos blockPos = new BlockPos(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            while (mc.world.isAirBlock(blockPos.down())) {
                blockPos = blockPos.down();
            }
            // calculate the entity's current altitude above the ground
            return entityPos.getY() - blockPos.getY();
        }
        return 1000.0 * mc.world.rand.nextGaussian();
    }
}
