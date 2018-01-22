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


@SideOnly(Side.CLIENT)
public class HudCompassRenderer extends Gui {
    protected static final ResourceLocation compassTexture = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-compass.png");
    protected static final ResourceLocation homeTexture = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-home.png");
    protected static final ResourceLocation bubbleTexture = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-bubble.png");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static FontRenderer fontRenderer = mc.fontRenderer;

    public static double altitude;

    private final int hudWidth = 256;
    private final int hudHeight = 256;

//    private final int colorYellow = 0xffaaaa00;
//    private final int colorRed = 0xffaa0000;
//    private final int colorGreen = 0xff00aa00;
//    private final int colorBlue = 0xff0000aa;
    private final int colorDimGreen = 0xcc008800;
    private final int colorDimRed = 0xcc880000;

    double compassHeading;

    private int hudX;
    private int hudY;

    public HudCompassRenderer() {
        super();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void drawCompassHUD(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.player.onGround) {
            return;
        }
        if (ClientConfiguration.getNoHUD() || !ClientConfiguration.getUseCompassHUD()) {
            return;
        }
        if (mc.inGameHasFocus && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ScaledResolution sr = event.getResolution();

            hudX = 5; // left edge of GUI screen
            hudY = 5; // top edge of GUI screen
            int textX = hudX + (hudWidth / 4); // xcoord for text
            int textY = hudY + (hudHeight / 4); // ycoord for text

            if (mc.player.getRidingEntity() instanceof EntityParachute) {
                EntityParachute chute = (EntityParachute) mc.player.getRidingEntity();
                if (chute == null) {
                    return;
                }

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                BlockPos entityPos = new BlockPos(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ);
                altitude = getCurrentAltitude(entityPos);
                double homeDir = getHomeDirection(chute.rotationYaw);
                double distance = getHomeDistance();
                compassHeading = calcCompassHeading(chute.rotationYaw);

                // scale the HUD to 50%
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5, 0.5, 0.5);

                // draw the parachute/player facing bubble underneath
                double playerLook = MathHelper.wrapDegrees(mc.player.getRotationYawHead() - chute.rotationYaw);
                drawBubble(calcPlayerChuteFacing(playerLook), bubbleTexture);

                // draw the compass
                drawTextureWithRotation((float)compassHeading, compassTexture);

                // draw the home direction
                drawTextureWithRotation((float)homeDir, homeTexture);

                // draw the altitude text
                String text;
                int height = fontRenderer.FONT_HEIGHT;
                drawCenteredString(fontRenderer, format(altitude), textX, textY - height - 8, colorAltitude());

                // draw the compass heading text
                drawCenteredString(fontRenderer, format(compassHeading), textX, textY - (height * 2) - 8, colorDimGreen);

                // draw the distance to the home point text
                drawCenteredString(fontRenderer, format(distance), textX, textY + height - 2, colorDimGreen);

                boolean aadActive = ConfigHandler.getIsAADActive();
                drawCenteredString(fontRenderer, "* AAD *", textX, textY + (height * 2) - 2, aadActive ? colorDimGreen : colorDimRed);

                GlStateManager.popMatrix();

                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }
        }
    }

    // drawTexturedModalRect
    // Params: int screenX, int screenY, int textureX, int textureY, int width, int height
    private void drawBubble(float bubble, ResourceLocation texture) {
        GlStateManager.pushMatrix();

        // scale again, final scale is 25% of original size
        GlStateManager.scale(0.5, 0.5, 0.5);
        mc.getTextureManager().bindTexture(texture);
        // draw the bubble
        drawTexturedModalRect(hudX + bubble, hudY + (hudHeight / 2) - 9, 0, 0, 16, 16);
        // draw the line
        drawTexturedModalRect(hudX, hudY + 20, 0, 20, hudWidth, hudHeight - 20);

        GlStateManager.popMatrix();
    }

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

    private int calcPlayerChuteFacing(double playerLook) {
        int bubble = (int) Math.floor(playerLook + 120.0);
        bubble = bubble < 8 ? 8 : bubble > 248 ? 248 : bubble;
        return bubble;
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
        int colorDimYellow = 0xcc888800;
        return altitude <= 10.0 ? colorDimRed : altitude <= 15.0 && altitude > 10.0 ? colorDimYellow : colorDimGreen;
    }

    // quadrant color code
    // 315 to 44 green, mostly north
    // 45 to 134 yellow, mostly east
    // 135 to 224 red, mostly south
    // 225 to 314 blue, mostly west
    @SuppressWarnings("unused")
//    public int colorCompass(double d) {
//        return (d >= 0 && d < 45.0) ? colorGreen : (d >= 45.0 && d < 135.0) ? colorYellow :
//                (d >= 135.0 && d < 225.0) ? colorRed : (d >= 225.0 && d < 315.0) ? colorBlue : colorGreen;
//    }

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
