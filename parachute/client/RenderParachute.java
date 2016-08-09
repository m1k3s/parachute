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
import com.parachute.common.EntityParachute;
import com.parachute.common.Parachute;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderParachute extends Render<EntityParachute> {

    private static String curColor = ConfigHandler.getChuteColor();
    protected static ModelBase modelParachute = new ModelParachute();
    private static ResourceLocation parachuteTexture = null;
    private static final Random rand = new Random(System.currentTimeMillis());

    public RenderParachute(RenderManager rm) {
        super(rm);
        shadowSize = 0.0F;
    }

    public void doRender(EntityParachute entityparachute, double x, double y, double z, float rotationYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);

        if (!bindEntityTexture(entityparachute)) {
            return;
        }
        modelParachute.render(entityparachute, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        if (entityparachute.getControllingPassenger() != null && Minecraft.getMinecraft().gameSettings.thirdPersonView > 0) {
            EntityPlayer rider = (EntityPlayer) entityparachute.getControllingPassenger();
            renderParachuteCords(rider, partialTicks);
        }

        GlStateManager.popMatrix();
        super.doRender(entityparachute, x, y, z, rotationYaw, partialTicks);
    }

    public void renderParachuteCords(EntityPlayer rider, float partialTicks) {
        final float b = rider.getBrightness(partialTicks);

        final float lx[] = {-8f, 0f, -8f, 0f, -8f, 0f, 8f, 0f, -8f, 0f, 8f, 0f, -8f, 0f, 8f, 0f};
        final float ly[] = {0.25f, 1.5f, 0.25f, 1.5f, 0f, 1.5f, 0f, 1.5f, 0.25f, 1.5f, 0.25f, 1.5f, 0f, 1.5f, 0f, 1.5f};
        final float lz[] = {-23.5f, -3f, -23.5f, -3f, -8f, -3f, -8f, -3f, 23.5f, 3f, 23.5f, 3f, 8f, 3f, 8f, 3f};

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        GlStateManager.scale(0.0625F, -1.0F, 0.0625F);

        GlStateManager.glBegin(GL11.GL_LINES);
        GlStateManager.color(b * 0.5F, b * 0.5F, b * 0.65F); // slightly blue
        for (int k = 0; k < 16; k++) {
            if (k > 7) {
                GlStateManager.color(b * 0.65F, b * 0.5F, b * 0.5F); // slightly red
            }
            GlStateManager.glVertex3f(lx[k], ly[k], lz[k]);
        }
        GlStateManager.glEnd();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }

    public static void setParachuteColor(String color) {
        if (color.equalsIgnoreCase("random")) {
            if (rand.nextBoolean()) {
                parachuteTexture = new ResourceLocation("textures/blocks/wool_colored_" + getRandomColor() + ".png");
            } else {
                parachuteTexture = new ResourceLocation(Parachute.modid + ":textures/blocks/" + getRandomCustomColor() + ".png");
            }
        } else if (color.toLowerCase().startsWith("custom")) {
            parachuteTexture = new ResourceLocation(Parachute.modid + ":textures/blocks/" + color + ".png");
        } else {
            parachuteTexture = new ResourceLocation("textures/blocks/wool_colored_" + color + ".png");
        }
        curColor = color;
    }

    protected static ResourceLocation getParachuteColor(String color) {
        if (parachuteTexture == null) {
            if (color.equalsIgnoreCase("random")) {
                if (rand.nextBoolean()) {
                    parachuteTexture = new ResourceLocation("textures/blocks/wool_colored_" + getRandomColor() + ".png");
                } else {
                    parachuteTexture = new ResourceLocation(Parachute.modid + ":textures/blocks/" + getRandomCustomColor() + ".png");
                }
            } else if (color.toLowerCase().startsWith("custom")) {
                parachuteTexture = new ResourceLocation(Parachute.modid + ":textures/blocks/" + color + ".png");
            } else {
                parachuteTexture = new ResourceLocation("textures/blocks/wool_colored_" + color + ".png");
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

    // return the string 'custom' and append a random digit
    // between zero and nine
    protected static String getRandomCustomColor() {
        return "custom" + rand.nextInt(10);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityParachute entity) {
        parachuteTexture = getParachuteColor(curColor);
        return parachuteTexture;
    }

}
