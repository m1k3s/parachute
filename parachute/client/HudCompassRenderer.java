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
    protected static final ResourceLocation COMPASS_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-compass.png");
    protected static final ResourceLocation HOME_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-home.png");
    protected static final ResourceLocation BUBBLE_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-bubble.png");
    protected static final ResourceLocation RETICULE_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-reticule.png");
    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-background.png");
    protected static final ResourceLocation NIGHT_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-night.png");
    protected static final ResourceLocation CLOCK_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-clock.png");
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

    private String alt, compass, dist;

    public HudCompassRenderer() {
        super();
    }

    @SuppressWarnings({"unused", "IfCanBeSwitch"})
    @SubscribeEvent
    public void drawCompassHUD(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.player.onGround) {
            return;
        }
        if (ClientConfiguration.getNoHUD() || !mc.gameSettings.fullScreen) {
            return;
        }
        if (mc.inGameHasFocus && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ScaledResolution sr = new ScaledResolution(mc);
            int scale = sr.getScaleFactor();
            int width = sr.getScaledWidth() * scale;
            int height = sr.getScaledHeight() * scale;
            int padding = 20;

            String position = ClientConfiguration.getHudPosition();
            if (position == null) {
                position = "right";
            }

            if (position.equals("left")) {
                hudX = padding;
            } else if (position.equals("center")) {
                hudX = (width - hudWidth) / 2;
            } else {
                hudX = (width - hudWidth) - padding;
            }
            hudY = padding;

            int textX = hudX + (hudWidth / 2);
            int textY = hudY + (hudHeight / 2);

            if (mc.player.getRidingEntity() instanceof EntityParachute) {
                EntityParachute chute = (EntityParachute) mc.player.getRidingEntity();
                if (chute == null) {
                    return;
                }
                fontRenderer.setUnicodeFlag(true);

                BlockPos entityPos = new BlockPos(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ);
                altitude = getCurrentAltitude(entityPos);
                double homeDir = getHomeDirection(chute.rotationYaw);
                double distance = getHomeDistance();
                compassHeading = calcCompassHeading(chute.rotationYaw);
                boolean aadActive = ConfigHandler.getIsAADActive();

                GlStateManager.pushMatrix();

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                GlStateManager.scale(0.25, 0.25, 0.25);

                // 1. draw the background
                if (isNightTime()) {
                    drawTextureFixed(NIGHT_TEXTURE);
                }
                drawTextureFixed(BACKGROUND_TEXTURE);

                // 2. draw the compass ring
                drawTextureWithRotation((float) -compassHeading, COMPASS_TEXTURE);

                // 3. draw the home direction ring
                drawTextureWithRotation((float) homeDir, HOME_TEXTURE);

                // 4. draw the parachute/player facing bubble
                float playerLook = MathHelper.wrapDegrees(mc.player.getRotationYawHead() - chute.rotationYaw);
                drawTextureWithRotation(playerLook, BUBBLE_TEXTURE);

                // 5. draw the reticule on top
                drawTextureFixed(RETICULE_TEXTURE);

                // 6. draw the digital clock
                drawClock(CLOCK_TEXTURE);

                // damping the update (20 ticks/second modulo 10 is about 1/2 second updates)
                if (count % 10 == 0) {
                    alt = formatBold(altitude);
                    compass = formatBold(compassHeading);
                    dist = formatBold(distance);
                }
                count++;

                // scale text up to 50%
                GlStateManager.scale(2.0, 2.0, 2.0);
                // scale the text coords as well
                textX /= 2;
                textY /= 2;

                int hFont = fontRenderer.FONT_HEIGHT;
                // 1. draw the compass heading text
                drawCenteredString(fontRenderer, compass, textX, textY - (hFont * 2) - 2, colorGreen);

                // 2. draw the altitude text
                drawCenteredString(fontRenderer, alt, textX, textY - hFont, colorAltitude());

                // 3. draw the distance to the home/spawn point text
                drawCenteredString(fontRenderer, dist, textX, textY + 2, colorGreen);

                // 4. AAD active indicator
                drawCenteredString(fontRenderer, "§lAUTO", textX, textY + hFont + 4, aadActive ? colorGreen : colorRed);

                // 5. Minecraft time in HH:MM:SS
                drawCenteredString(fontRenderer, formatMinecraftTime(mc.world.getWorldTime()), textX, textY + (hudHeight / 4) + 3, colorRed);

                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();

                GlStateManager.popMatrix();
                fontRenderer.setUnicodeFlag(false);
            }
        }
    }

    private boolean isNightTime() {
        long ticks = mc.world.getWorldTime() % 24000;
        return ticks > 12550 && ticks < 23000;
    }

    private void drawClock(ResourceLocation texture) {
        GlStateManager.pushMatrix();

        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect((hudX + (hudWidth / 2)) - 51, hudY + hudHeight + 2, 0, 0, 102, 25);

        GlStateManager.popMatrix();
    }

    // draw a texture
    private void drawTextureFixed(ResourceLocation texture) {
        GlStateManager.pushMatrix();

        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(hudX, hudY, 0, 0, hudWidth, hudHeight);

        GlStateManager.popMatrix();
    }

    // draw the compass/home textures
    private void drawTextureWithRotation(float degrees, ResourceLocation texture) {
        GlStateManager.pushMatrix();

        float tx = hudX + (hudWidth / 2);
        float ty = hudY + (hudHeight / 2);
        // translate to center and rotate
        GlStateManager.translate(tx, ty, 0);
        GlStateManager.rotate(degrees, 0, 0, 1);
        GlStateManager.translate(-tx, -ty, 0);

        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(hudX, hudY, 0, 0, hudWidth, hudHeight);

        GlStateManager.popMatrix();
    }

    // §k	Obfuscated
    // §l	Bold
    // §m	Strikethrough
    // §n	Underline
    // §o	Italic
    // §r	Reset
    public String formatBold(double d) {
        return String.format("§l%.1f", d);
    }

    public String formatMinecraftTime(double ticks) {
        double real = (ticks % 24000) * 3.6; // ticks to seconds wrapping ticks if necessary
        int hours = MathHelper.floor(((real / 3600.0) + 6) % 24); // Minecraft ticks are offset from calendar clock by 6 hours
        int minutes = MathHelper.floor((real / 60.0) % 60);
        int seconds = MathHelper.floor(real % 60);
        return String.format("§l%02d:%02d:%02d", hours, minutes, seconds);
    }

    private double calcCompassHeading(double yaw) {
        return (((yaw + 180.0) % 360) + 360) % 360;
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
