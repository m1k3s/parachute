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

package parachute.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;


public class ModelParachute extends ModelBase {
	
	static final int w = 16;
	static final int d = 16;
	static final int h = 1;

	public ParachuteModelRenderer[] sections = new ParachuteModelRenderer[4];
	
	public ModelParachute() {
		sections[0] = new ParachuteModelRenderer(0, 0);
		sections[0].addBox(-8F, -1F, -16F, w, h, d);
		sections[0].rotateAngleX = 3.27249234748937F;
		
		sections[1] = new ParachuteModelRenderer(0, 0);
		sections[1].addBox(-8F, -1F, 0F, w, h, d);
		sections[1].rotateAngleX = 3.01069295969022F;

		sections[2] = new ParachuteModelRenderer(0, 0);
		sections[2].addBox(-8F, 0F, -24F, w, h, d);
		sections[2].setRotationPoint(0F, 0F, -8F);
		sections[2].rotateAngleX = 6.021385919380437F;

		sections[3] = new ParachuteModelRenderer(0, 0);
		sections[3].addBox(-8F, 0F, 8F, w, h, d);
		sections[3].setRotationPoint(0F, 0F, 8F);
		sections[3].rotateAngleX = 0.2617993877991494F;
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		for (int k = 0; k < 4; k++) {
			sections[k].render(f5);
		}
	}

}
