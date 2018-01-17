/*
 * HudGuiRenderer.java
 *
 * Copyright (c) 2017 Michael Sheppard
 *
 *  =====GPL=============================================================
 * $program is free software: you can redistribute it and/or modify
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
 *
 */
package com.parachute.client;

import com.parachute.common.ConfigHandler;
import com.parachute.common.EntityParachute;
import com.parachute.common.Parachute;
import com.parachute.common.ParachuteCommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
//import net.minecraft.client.renderer.RenderHelper;
//import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class HudGuiRenderer extends Gui {

    // the parachute-hud.png image uses some ELD Unofficial Continuation Project textures, modified to suit the parachute HUD.
    protected static final ResourceLocation hudTexture = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/parachute-hud.png");
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
//    private final int colorDimBlue;
    private final int colorDimGreen;
    private final int colorDimRed;
    private final int colorDimYellow;
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
    private final int bigLedXY;
    // waypoint
    public static int wayPointX;
    public static int wayPointZ;
    private static boolean wayPointEnabled;

    double compassHeading;

    public HudGuiRenderer() {
        super();
        hudWidth = 182;
        hudHeight = 39;
        ledWidth = 11;
        ledHeight = 5;
        colorYellow = 0xffaaaa00;
        colorDimYellow = 0xcc888800;
        colorRed = 0xffaa0000;
        colorDimRed = 0xcc880000;
        colorGreen = 0xff00aa00;
        colorBlue = 0xff0000aa;
        colorDimGreen = 0xcc008800;
        aadWidth = 16;
        aadHeight = 25;
        bigLedXY = 16;
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

        fontRenderer = mc.fontRenderer;
        fieldWidth = fontRenderer.getStringWidth("000.0") / 2;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (ConfigHandler.getNoHUD() || ConfigHandler.getUseCompassHUD()) {
            return;
        }
        if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.player.onGround) {
            return;
        }

        if (mc.inGameHasFocus && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ScaledResolution sr = new ScaledResolution(mc);
            int hudX = (sr.getScaledWidth() / 2) - (hudWidth / 2); // left edge of GUI
            int hudY = 2; // top edge of GUI
            int textX = hudX + 30; // xcoord for text
            int textY = hudY + 22; // ycoord for text

            if (ParachuteCommonProxy.onParachute(mc.player)) {
                mc.getTextureManager().bindTexture(hudTexture);
                EntityParachute chute = (EntityParachute) mc.player.getRidingEntity();
                if (chute == null) {
                    return;
                }

//                GlStateManager.enableRescaleNormal();
//                GlStateManager.enableBlend();
//                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
//                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//                RenderHelper.enableGUIStandardItemLighting();

                BlockPos entityPos = new BlockPos(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ);
                altitude = getCurrentAltitude(entityPos);
                double homeDir = getHomeDirection(chute.rotationYaw);
                double distance = getHomeDistance();
                compassHeading = calcCompassHeading(chute.rotationYaw);

                // Params: int screenX, int screenY, int textureX, int textureY, int width, int height
                drawTexturedModalRect(hudX, hudY, 0, 0, hudWidth, hudHeight); // draw the main hud
                drawTexturedModalRect(hudX, hudY + hudHeight, 0, 60, hudWidth, 9); // draw the chute bubble frame

                // determine which LED to light, homeDir is in range -180 to 180
                // for any value under -80 or over 80 the LED is fixed to the
                // left or right end of the slider respectively.
                int ledX = calcHomeDirection(homeDir);
                drawTexturedModalRect(hudX + ledX, hudY, ledX, ledY, ledWidth, ledHeight); // draw the lit LED

                // this indicator points to the parachute facing direction. once the
                // red indicator is centered the player is facing in the same
                // direction as the chute.
                double playerLook = MathHelper.wrapDegrees(mc.player.getRotationYawHead() - chute.rotationYaw);
                int chuteBubble = calcPlayerChuteFacing(playerLook);
                drawTexturedModalRect(hudX + chuteBubble, hudY + hudHeight, 67, 46, 3, 7); // draw the chute bubble

                // AAD status
                int aadIconX;
                int aadIconY = 8;
                aadIconX = ConfigHandler.getIsAADActive() ? 199 : 182;
                drawTexturedModalRect(hudX + hudWidth + 2, hudY + 8, aadIconX, aadIconY, aadWidth, aadHeight); // draw the AAD indicator

                // manual dismount indicator
                if (ConfigHandler.isAutoDismount()) { // auto dismount is engaged
                    drawTexturedModalRect(hudX - 18, hudY + yOffset, dark, lightY, bigLedXY, bigLedXY);
                } else { // auto dismount is disabled
                    if (altitude > 10) {
                        drawTexturedModalRect(hudX - 18, hudY + yOffset, green, lightY, bigLedXY, bigLedXY);
                    } else if (altitude <= 10 && altitude > 3) {
                        drawTexturedModalRect(hudX - 18, hudY + yOffset, red, lightY, bigLedXY, bigLedXY);
                    } else if (altitude <= 3) { // make this blink
                        if ((blink % blinkTime) == 0) {
                            blinkX = blinkX == red ? darkRed : red;
                        }
                        drawTexturedModalRect(hudX - 18, hudY + yOffset, blinkX, lightY, bigLedXY, bigLedXY);
                        blink++;
                    }
                }

                if (wayPointEnabled) {
                    double waypointDirection = getWaypointDirection(wayPointX, wayPointZ);
                    // draw the waypoint heading
                    if (waypointDirection < -80) {
                        ledX = 1;
                    } else if ((waypointDirection - 80) * (waypointDirection - -80) < 0) {
                        ledX = (int) Math.floor((waypointDirection + 80.0) + 4);
                    } else if (waypointDirection > 80) {
                        ledX = 170;
                    }
                    // draw the waypoint bar background
                    drawTexturedModalRect(hudX, hudY + hudHeight + 9, 0, 0, hudWidth, ledHeight);
                    // draw the lit LED
                    drawTexturedModalRect(hudX + ledX, hudY + hudHeight + 9, ledX, ledY, ledWidth, ledHeight);
                }

                // draw the altitude text
                fontRenderer.drawStringWithShadow(format(altitude), (textX + 5) - fieldWidth, textY, colorAltitude());
                // draw the compass heading text
                fontRenderer.drawStringWithShadow(format(compassHeading), (textX + 118) - fieldWidth, textY, colorDimGreen);//colorCompass(compassHeading));
                // draw the distance to the home point text
                fontRenderer.drawStringWithShadow(format(distance), (textX + 65) - fieldWidth, textY, colorDimGreen);
            }
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.disableRescaleNormal();
//            GlStateManager.disableBlend();
        }
    }

    public String format(double d) {
        return String.format("%.1f", d);
    }

    private double calcCompassHeading(double yaw) {
        return (((yaw + 180.0) % 360) + 360) % 360;
    }

    private int calcPlayerChuteFacing(double playerLook) {
        int bubble = (int) Math.floor(playerLook + 91.0);
        bubble = bubble < 0 ? 0 : bubble > 179 ? 179 : bubble;
        return bubble;
    }

    private int calcHomeDirection(double dir) {
        int ledX = (int) Math.floor(dir + 84.0);
        ledX = ledX < 0 ? 0 : ledX > 170 ? 170 : ledX;
        return ledX;
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
        return (altitude <= 10.0 && altitude >= 0.0) ? colorDimRed : altitude <= 15.0 && altitude > 10.0 ? colorDimYellow : colorDimGreen;
    }

    // quadrant color code
    // 315 to 44 green, mostly north
    // 45 to 134 yellow, mostly east
    // 135 to 224 red, mostly south
    // 225 to 314 blue, mostly west
    @SuppressWarnings("unused")
    public int colorCompass(double d) {
        return (d >= 0 && d < 45.0) ? colorGreen : (d >= 45.0 && d < 135.0) ? colorYellow :
                (d >= 135.0 && d < 225.0) ? colorRed : (d >= 225.0 && d < 315.0) ? colorBlue : colorGreen;
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

    // difference angle in degrees the player is facing from the waypoint.
    // zero degrees means the player is facing the waypoint.
    public double getWaypointDirection(int waypointX, int waypointZ) {
        BlockPos blockpos = new BlockPos(waypointX, 0, waypointZ);
        double delta = Math.atan2(blockpos.getZ() - mc.player.posZ, blockpos.getX() - mc.player.posX);
        double relAngle = delta - Math.toRadians(mc.player.rotationYaw);
        return MathHelper.wrapDegrees(Math.toDegrees(relAngle) - 90.0); // degrees
    }

    @SuppressWarnings("unused")
    public static int[] getWaypoint() {
        return new int[]{wayPointX, wayPointZ};
    }

    public static void setWaypoint(int[] waypoint) {
        wayPointX = waypoint[0];
        wayPointZ = waypoint[1];
    }

    public static void enableWaypoint(boolean enabled) {
        wayPointEnabled = enabled;
    }

    public static boolean getEnableWaypoint() {
        return wayPointEnabled;
    }

}
