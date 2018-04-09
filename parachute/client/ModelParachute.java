/*
 * ModelParachute.java
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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelParachute extends ModelBase {

    private final int nSections = 6;

    public ModelRenderer[] sections = new ModelRenderer[nSections];

    public ModelParachute() {
        sections[0] = new ModelRenderer(this,0, 0);
        int x = 16; // front/back
        int y = 1;  // up/down
        int z = 16; // left/right
//        sections[0].addBox(-8F, 0F, -8, width, height, depth);
//
//        sections[1] = new ModelRenderer(this, 0, 0);
//        sections[1].addBox(-8F, 0F, -16F, width, height, depth);
//        sections[1].setRotationPoint(0F, 0F, -8F);
//        sections[1].rotateAngleX = -0.2617993877991494F;
//
//        sections[2] = new ModelRenderer(this, 0, 0);
//        sections[2].addBox(-8F, 0F, 0F, width, height, depth);
//        sections[2].setRotationPoint(0F, 0F, 8F);
//        sections[2].rotateAngleX = 0.2617993877991494F;

        final float d2r = (float)Math.toRadians(1.0);

        sections[0] = new ModelRenderer(this);
        sections[0].addBox(-8f, -8.25f, -5f, x, y, 4);
        sections[0].setRotationPoint(0F, 0F, -36F);
        sections[0].rotateAngleX = -45.0f * d2r;

        sections[1] = new ModelRenderer(this);
        sections[1].addBox(-8F, 1.6F, -32F, x, y, z);
        sections[1].rotateAngleX = -15.0f * d2r;

        sections[2] = new ModelRenderer(this);
        sections[2].addBox(-8F, -0.5F, -16F, x, y, z);
        sections[2].rotateAngleX = -7.5f * d2r;

        sections[3] = new ModelRenderer(this);
        sections[3].addBox(-8F, -0.5F, 0F, x, y, z);
        sections[3].rotateAngleX = 7.5f * d2r;

        sections[4] = new ModelRenderer(this);
        sections[4].addBox(-8F, 1.6F, 16F, x, y, z);
        sections[4].rotateAngleX = 15.0f * d2r;

        sections[5] = new ModelRenderer(this);
        sections[5].addBox(-8f, -8.25f, 1f, x, y, 4);
        sections[5].setRotationPoint(0F, 0F, 36F);
        sections[5].rotateAngleX = 45.0f * d2r;
    }

    public void renderCanopy(float scale) {
        for (ModelRenderer pmr : sections) {
            pmr.render(scale);
        }
    }

    @Override
    public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float scale) {
        renderCanopy(scale);
    }

}
