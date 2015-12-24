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

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class ModelParachute extends ModelBase {

	final float width = 16.0f;
	final float height = 0.35f;
	final float depth = 16.0f;
	final int nSections = 3;
	
	public ParachuteModelRenderer[] sections = new ParachuteModelRenderer[nSections];

	public ModelParachute()
	{
		sections[0] = new ParachuteModelRenderer(0, 0);
		sections[0].addBox(-8F, 0F, -8F, width, height, depth);

		sections[1] = new ParachuteModelRenderer(0, 0);
		sections[1].addBox(-8F, 0F, -16F, width, height, depth);
		sections[1].setRotationPoint(0F, 0F, -8F);
		sections[1].rotateAngleX = 6.021385919380437F;

		sections[2] = new ParachuteModelRenderer(0, 0);
		sections[2].addBox(-8F, 0F, 0F, width, height, depth);
		sections[2].setRotationPoint(0F, 0F, 8F);
		sections[2].rotateAngleX = 0.2617993877991494F;
	}

	public void renderCanopy(float center)
	{
		for (ParachuteModelRenderer pmr : sections) {
			pmr.render(center);
		}
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		renderCanopy(f5);
	}
	
}
