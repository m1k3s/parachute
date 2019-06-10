/*
 * ParachuteRenderer.java
 *
 *  Copyright (c) 2019 Michael Sheppard
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

import com.mojang.blaze3d.platform.GlStateManager;
import com.parachute.common.Parachute;

import java.util.Random;

import com.parachute.common.ParachuteEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ParachuteRenderer extends EntityRenderer<ParachuteEntity> {

    private static String curColor;
    protected final ParachuteModel parachuteModel = new ParachuteModel();
    private static ResourceLocation parachuteTexture = null;
    private static final Random rand = new Random(System.currentTimeMillis());
    private final float SCALE = 1.0f / 16.0f;

    public ParachuteRenderer(EntityRendererManager rm) {
        super(rm);
        shadowSize = 0.0F;
        curColor = "random";
    }

    @Override
    public void doRender(@Nonnull ParachuteEntity parachuteEntity, double x, double y, double z, float rotationYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translated(x, y, z);
        GlStateManager.rotatef(90.0f - rotationYaw, 0.0f, 1.0f, 0.0f);
        bindEntityTexture(parachuteEntity);

        parachuteModel.render(parachuteEntity, partialTicks, 0.0F, 0.0F, 0.0F, 0.0F, SCALE);
        if (parachuteEntity.getControllingPassenger() != null && Minecraft.getInstance().gameSettings.thirdPersonView > 0) {
            PlayerEntity rider = (PlayerEntity) parachuteEntity.getControllingPassenger();
            renderParachuteCords(rider);
        }

        GlStateManager.popMatrix();
        super.doRender(parachuteEntity, x, y, z, rotationYaw, partialTicks);
    }

    public boolean isMultiPass() { return false; }

    public void renderParachuteCords(PlayerEntity rider) {
        final float b = rider.getBrightness();

        // six section parachute
        final float[] x = { // front/back
                -8f, 0f, 8f, 0f, -8f, 0f,
                8f, 0f, -8f, 0f, 8f, 0f,
                -8f, 0f, 8f, 0f, -8f, 0f,
                8f, 0f, -8f, 0f, 8f, 0f
        };
        final float[] y = { // up/down
                0.52f, 1.5f, 0.52f, 1.5f, 0.2f, 1.5f,
                0.2f, 1.5f, 0.52f, 1.5f, 0.52f, 1.5f,
                0.2f, 1.5f, 0.2f, 1.5f, 0.05f, 1.5f,
                0.05f, 1.5f, 0.05f, 1.5f, 0.05f, 1.5f
        };
        final float[] z = { // left/right
                -34f, -3f, -34f, -3f, -20f, -3f,
                -20f, -3f, 34f, 3f, 34f, 3f,
                20f, 3f, 20f, 3f, -8f, -3f,
                -8f, -3f, 8f, 3f, 8f, 3f
        };

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture();
        GlStateManager.disableLighting();

        GlStateManager.scalef(0.0625F, -1.0F, SCALE);

        GL11.glBegin(GL11.GL_LINES);
        GlStateManager.color3f(b * 0.5F, b * 0.5F, b * 0.65F); // blue-ish
        for (int k = 0; k < 24; k++) {
            GL11.glVertex3f(x[k], y[k], z[k]);
        }
        GL11.glEnd();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture();

        GlStateManager.popMatrix();
    }

    public static void setParachuteColor(String color) {
        if (color.equalsIgnoreCase("random")) {
            if (rand.nextBoolean()) {
                parachuteTexture = new ResourceLocation("textures/block/" + getRandomColor() + "_wool" + ".png");
            } else {
                parachuteTexture = new ResourceLocation(Parachute.MODID + ":textures/block/" + getRandomCustomColor() + ".png");
            }
        } else if (color.toLowerCase().startsWith("custom")) {
            parachuteTexture = new ResourceLocation(Parachute.MODID + ":textures/block/" + color + ".png");
        } else {
            parachuteTexture = new ResourceLocation("textures/block/" + color + "_wool" + ".png");
        }
        curColor = color;
    }

    protected static ResourceLocation getParachuteColor(String color) {
        if (parachuteTexture == null) {
            if (color.equalsIgnoreCase("random")) {
                if (rand.nextBoolean()) {
                    parachuteTexture = new ResourceLocation("textures/block/" + getRandomColor() + "_wool" + ".png");
                } else {
                    parachuteTexture = new ResourceLocation(Parachute.MODID + ":textures/block/" + getRandomCustomColor() + ".png");
                }
            } else if (color.toLowerCase().startsWith("custom")) {
                parachuteTexture = new ResourceLocation(Parachute.MODID + ":textures/block/" + color + ".png");
            } else {
                parachuteTexture = new ResourceLocation("textures/block/" + color + "_wool" + ".png");
            }
            curColor = color;
        }
        return parachuteTexture;
    }

    protected static String getRandomColor() {
        String[] colors = {
                "black",
                "blue",
                "brown",
                "cyan",
                "gray",
                "green",
                "light_blue",
                "lime",
                "magenta",
                "orange",
                "pink",
                "purple",
                "red",
                "silver",
                "white",
                "yellow"
        };

        return colors[rand.nextInt(16)];
    }

    @Override
    public boolean isMultipass() {
        return false;
    }

    // return the string 'custom' and append a random digit
    // between zero and nine
    protected static String getRandomCustomColor() {
        return "custom" + rand.nextInt(10);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull ParachuteEntity entity) {
        parachuteTexture = getParachuteColor(curColor);
        return parachuteTexture;
    }

}
