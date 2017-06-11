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

    private final int nSections = 3;

    public ModelRenderer[] sections = new ModelRenderer[nSections];

    public ModelParachute() {
        sections[0] = new ModelRenderer(this,0, 0);
        int width = 16;
        int height = 1;//0.35f;
        int depth = 16;
        sections[0].addBox(-8F, 0F, -8, width, height, depth);

        sections[1] = new ModelRenderer(this, 0, 0);
        sections[1].addBox(-8F, 0F, -16F, width, height, depth);
        sections[1].setRotationPoint(0F, 0F, -8F);
        sections[1].rotateAngleX = 6.021385919380437F;

        sections[2] = new ModelRenderer(this, 0, 0);
        sections[2].addBox(-8F, 0F, 0F, width, height, depth);
        sections[2].setRotationPoint(0F, 0F, 8F);
        sections[2].rotateAngleX = 0.2617993877991494F;
    }

    public void renderCanopy(float scale) {
        for (ModelRenderer pmr : sections) {
            pmr.render(scale);
        }
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        renderCanopy(scale);
    }

}
