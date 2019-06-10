/*
 * ParachuteModel.java
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

import com.parachute.common.ParachuteEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParachuteModel extends EntityModel<ParachuteEntity> {
    private RendererModel[] sections = new RendererModel[6];

    public ParachuteModel() {
        sections[0] = new RendererModel(this, 0, 0).setTextureSize(16, 16);
        sections[1] = new RendererModel(this, 0, 0).setTextureSize(16, 16);
        sections[2] = new RendererModel(this, 0, 0).setTextureSize(16, 16);
        sections[3] = new RendererModel(this, 0, 0).setTextureSize(16, 16);
        sections[4] = new RendererModel(this, 0, 0).setTextureSize(16, 16);
        sections[5] = new RendererModel(this, 0, 0).setTextureSize(16, 16);

        int x = 16; // front/back
        int y = 2;  // up/down
        int z = 16; // left/right
        final float d2r = (float) Math.toRadians(1.0);

        sections[0].addBox(-8f, -8.25f, -5f, x, y, 4);
        sections[0].setRotationPoint(0F, 0F, -36F);
        sections[0].rotateAngleX = -45.0f * d2r;

        sections[1].addBox(-8F, 1.6F, -32F, x, y, z);
        sections[1].rotateAngleX = -15.0f * d2r;

        sections[2].addBox(-8F, -0.5F, -16F, x, y, z);
        sections[2].rotateAngleX = -7.5f * d2r;

        sections[3].addBox(-8F, -0.5F, 0F, x, y, z);
        sections[3].rotateAngleX = 7.5f * d2r;

        sections[4].addBox(-8F, 1.6F, 16F, x, y, z);
        sections[4].rotateAngleX = 15.0f * d2r;

        sections[5].addBox(-8f, -8.25f, 1f, x, y, 4);
        sections[5].setRotationPoint(0F, 0F, 36F);
        sections[5].rotateAngleX = 45.0f * d2r;
    }

    public void renderCanopy(float scale) {
        for (RendererModel pmr : sections) {
            pmr.render(scale);
        }
    }

    public void render(ParachuteEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        func_212844_a_(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        renderCanopy(scale);
    }

}
