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

//import com.parachute.common.ConfigHandler;
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
    protected static final ResourceLocation RETICULE2_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-reticule2.png");
    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-background.png");
    protected static final ResourceLocation NIGHT_TEXTURE = new ResourceLocation(Parachute.MODID + ":" + "textures/gui/hud-night.png");

    private static final int MOON_RISE = 12600;
    private static final int SUN_RISE = 22900;
    private static final int MAX_TICKS = 24000;

    private static final int COLOR_RED = 0xffff0000;
    private static final int COLOR_GREEN = 0xff00ff00;
    private static final int COLOR_YELLOW = 0xffffff00;

    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private static FontRenderer fontRenderer = MINECRAFT.fontRenderer;

    public static double altitude;
    private static boolean isVisible = true;

    private static int count = 0;

    private static final int HUD_WIDTH = 256;
    private static final int HUD_HEIGHT = 256;
    // fixme: find a way to detect icons in upper right and adjust Y_PADDING
    private static final int Y_PADDING = 120;
    private static final int X_PADDING = 20;

    double compassHeading;

    private String alt, compass, dist;

    public HudCompassRenderer() {
        super();
    }

    @SubscribeEvent
    public void drawCompassHUD(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable() || MINECRAFT.gameSettings.showDebugInfo || MINECRAFT.player.onGround) {
            return;
        }
        if (!isVisible || !MINECRAFT.gameSettings.fullScreen) {
            return;
        }
        if (MINECRAFT.inGameHasFocus && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ScaledResolution sr = new ScaledResolution(MINECRAFT);
            int width = sr.getScaledWidth() * sr.getScaleFactor();

            String position = ClientConfiguration.getHudPosition();
            if (position == null) {
                return;
            }

            // initialize hudX based on user selected position, 'left', 'center', or 'right'
            int hudX = position.equals("left") ? X_PADDING : position.equals("center") ? (width - HUD_WIDTH) / 2 : (width - HUD_WIDTH) - X_PADDING;

            int textX = hudX + (HUD_WIDTH / 2);
            int textY = Y_PADDING + (HUD_HEIGHT / 2);

            if (MINECRAFT.player.getRidingEntity() instanceof EntityParachute) {
                EntityParachute chute = (EntityParachute) MINECRAFT.player.getRidingEntity();
                if (chute == null) {
                    return;
                }
                // attempt to use unicode for the HUD font
                boolean unicodeFlag = fontRenderer.getUnicodeFlag();
                fontRenderer.setUnicodeFlag(true);

                BlockPos entityPos = new BlockPos(MINECRAFT.player.posX, MINECRAFT.player.getEntityBoundingBox().minY, MINECRAFT.player.posZ);

                altitude = getCurrentAltitude(entityPos);
                double homeDir = getHomeDirection(chute.rotationYaw);
                double distance = getHomeDistance();
                compassHeading = calcCompassHeading(chute.rotationYaw);
                boolean aadActive = ClientConfiguration.getAADState();

                GlStateManager.pushMatrix();

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                GlStateManager.scale(0.25, 0.25, 0.25);

                // 1. draw the background
                if (isNightTime()) {
                    drawTextureFixed(NIGHT_TEXTURE, hudX);
                }
                drawTextureFixed(BACKGROUND_TEXTURE, hudX);

                // 2. draw the compass ring
                drawTextureWithRotation((float) -compassHeading, COMPASS_TEXTURE, hudX);

                // 3. draw the home direction ring
                drawTextureWithRotation((float) homeDir, HOME_TEXTURE, hudX);

                // 4. draw the "where the hell is the front of the parachute"  bubble
//                if (ConfigHandler.getFrontBubble()) {
//                    float playerLook = MathHelper.wrapDegrees(MINECRAFT.player.getRotationYawHead() - chute.rotationYaw);
//                    drawTextureWithRotation(playerLook, BUBBLE_TEXTURE, hudX);
//
//
//                    // 5. draw the reticule on top
//                    drawTextureFixed(RETICULE_TEXTURE, hudX);
//                } else {
                    drawTextureFixed(RETICULE2_TEXTURE, hudX);
//                }

                // damp the update (20 ticks/second modulo 10 is about 1/2 second updates)
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
                drawCenteredString(fontRenderer, compass, textX, textY - (hFont * 2) - 2, COLOR_GREEN);

                // 2. draw the altitude text
                drawCenteredString(fontRenderer, alt, textX, textY - hFont, colorAltitude());

                // 3. draw the distance to the home/spawn point text
                drawCenteredString(fontRenderer, dist, textX, textY + 2, COLOR_GREEN);

                // 4. AAD active indicator
                drawCenteredString(fontRenderer, "§lAUTO", textX, textY + hFont + 4, aadActive ? COLOR_GREEN : COLOR_RED);

                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();

                GlStateManager.popMatrix();
                // reset the unicode font flag
                fontRenderer.setUnicodeFlag(unicodeFlag);
            }
        }
    }

    private boolean isNightTime() {
        long ticks = MINECRAFT.world.getWorldTime() % MAX_TICKS;
        return ticks > MOON_RISE && ticks < SUN_RISE;
    }

    // draw a fixed texture
    private void drawTextureFixed(ResourceLocation texture, int screenX) {
        GlStateManager.pushMatrix();

        MINECRAFT.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(screenX, HudCompassRenderer.Y_PADDING, 0, 0, HudCompassRenderer.HUD_WIDTH, HudCompassRenderer.HUD_HEIGHT);

        GlStateManager.popMatrix();
    }

    // draw a rotating texture
    private void drawTextureWithRotation(float degrees, ResourceLocation texture, int screenX) {
        GlStateManager.pushMatrix();

        float tx = screenX + (HudCompassRenderer.HUD_WIDTH / 2.0f);
        float ty = HudCompassRenderer.Y_PADDING + (HudCompassRenderer.HUD_HEIGHT / 2.0f);
        // translate to center and rotate
        GlStateManager.translate(tx, ty, 0);
        GlStateManager.rotate(degrees, 0, 0, 1);
        GlStateManager.translate(-tx, -ty, 0);

        MINECRAFT.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(screenX, HudCompassRenderer.Y_PADDING, 0, 0, HudCompassRenderer.HUD_WIDTH, HudCompassRenderer.HUD_HEIGHT);

        GlStateManager.popMatrix();
    }

    // Minecraft font style codes
    // §k	Obfuscated
    // §l	Bold
    // §m	Strikethrough
    // §n	Underline
    // §o	Italic
    // §r	Reset
    private String formatBold(double d) {
        return String.format("§l%.1f", d);
    }

    private double calcCompassHeading(double yaw) {
        return (((yaw + 180.0) % 360) + 360) % 360;
    }

    // difference angle in degrees the chute is facing from the home point.
    // zero degrees means the chute is facing the home point.
    // the home point can be either the world spawn point or a waypoint
    // set by the player in the config.
    private double getHomeDirection(double yaw) {
        BlockPos blockpos = MINECRAFT.world.getSpawnPoint();
        double delta = Math.atan2(blockpos.getZ() - MINECRAFT.player.posZ, blockpos.getX() - MINECRAFT.player.posX);
        double relAngle = delta - Math.toRadians(yaw);
        return MathHelper.wrapDegrees(Math.toDegrees(relAngle) - 90.0); // degrees
    }

    // Thanks to Pythagoras we can calculate the distance to home/spawn
    private double getHomeDistance() {
        BlockPos blockpos = MINECRAFT.world.getSpawnPoint();
        double a = Math.pow(blockpos.getZ() - MINECRAFT.player.posZ, 2);
        double b = Math.pow(blockpos.getX() - MINECRAFT.player.posX, 2);
        return Math.sqrt(a + b);
    }

    private int colorAltitude() {
        return altitude <= 10.0 ? COLOR_RED : altitude <= 15.0 ? COLOR_YELLOW : COLOR_GREEN;
    }

    // calculate altitude in meters above ground. starting at the entity
    // count down until a non-air block is encountered.
    // only allow altitude calculations in the surface world
    // return a weirdly random number if in nether or end.
    private double getCurrentAltitude(BlockPos entityPos) {
        if (MINECRAFT.world.provider.isSurfaceWorld()) {
            BlockPos blockPos = new BlockPos(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            while (MINECRAFT.world.isAirBlock(blockPos.down())) {
                blockPos = blockPos.down();
            }
            // calculate the entity's current altitude above the ground
            return (entityPos.getY() - 2.5) - blockPos.getY();
        }
        return 1000.0 * MINECRAFT.world.rand.nextGaussian();
    }

   // toggles HUD visibilty using a user defined key, 'H' is default
    public static void toggleHUDVisibility() {
        isVisible = !isVisible;
    }
}
