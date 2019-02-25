/*
 * RenderParachute.java
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

import com.parachute.common.EntityParachute;
import com.parachute.common.Parachute;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class RenderParachute extends Render<EntityParachute> {

    private static String curColor;
    protected static ModelBase modelParachute = new ModelParachute();
    private static ResourceLocation parachuteTexture = null;
    private static final Random rand = new Random(System.currentTimeMillis());
    private final float SCALE = 1.0f / 16.0f;

    public RenderParachute(RenderManager rm) {
        super(rm);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(@Nonnull EntityParachute entityparachute, double x, double y, double z, float rotationYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translated(x, y, z);
        GlStateManager.rotatef(90.0f - rotationYaw, 0.0f, 1.0f, 0.0f);
        bindEntityTexture(entityparachute);

        modelParachute.render(entityparachute, partialTicks, 0.0F, 0.0F, 0.0F, 0.0F, SCALE);
        if (entityparachute.getControllingPassenger() != null && Minecraft.getInstance().gameSettings.thirdPersonView > 0) {
            EntityPlayer rider = (EntityPlayer) entityparachute.getControllingPassenger();
            renderParachuteCords(rider);
        }

        GlStateManager.popMatrix();
        super.doRender(entityparachute, x, y, z, rotationYaw, partialTicks);
    }

    public void renderParachuteCords(EntityPlayer rider) {
        final float b = rider.getBrightness();

        // six section parachute
        final float x[] = { // front/back
                -8f, 0f,  8f, 0f, -8f, 0f,
                 8f, 0f, -8f, 0f,  8f, 0f,
                -8f, 0f,  8f, 0f, -8f, 0f,
                 8f, 0f, -8f, 0f,  8f, 0f
        };
        final float y[] = { // up/down
                0.52f, 1.5f, 0.52f, 1.5f, 0.2f,  1.5f,
                0.2f,  1.5f, 0.52f, 1.5f, 0.52f, 1.5f,
                0.2f,  1.5f, 0.2f,  1.5f, 0.05f, 1.5f,
                0.05f, 1.5f, 0.05f, 1.5f, 0.05f, 1.5f
        };
        final float z[] = { // left/right
                -34f, -3f, -34f, -3f, -20f, -3f,
                -20f, -3f,  34f,  3f,  34f,  3f,
                 20f,  3f,  20f,  3f, -8f,  -3f,
                 -8f, -3f,   8f,  3f,  8f,   3f
        };

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        GlStateManager.scalef(0.0625F, -1.0F, SCALE);

//        GlStateManager.glBegin(GL11.GL_LINES);
        GL11.glBegin(GL11.GL_LINES);
        GlStateManager.color3f(b * 0.5F, b * 0.5F, b * 0.65F); // blue-ish
        for (int k = 0; k < 24; k++) {
//            GlStateManager.verertex3f(x[k], y[k], z[k]);
            GL11.glVertex3f(x[k], y[k], z[k]);
        }
        GL11.glEnd();
//        GlStateManager.glEnd();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();

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
    protected ResourceLocation getEntityTexture(@Nonnull EntityParachute entity) {
        parachuteTexture = getParachuteColor(curColor);
        return parachuteTexture;
    }

}
