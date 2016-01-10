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

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import parachute.common.EntityParachute;
import parachute.common.ItemParachute;
import parachute.common.Parachute;

import cpw.mods.fml.common.FMLCommonHandler;

import java.nio.IntBuffer;
import java.util.Random;

public class RenderParachute extends Render {

	protected static int colorIndex;
	protected static Random rand;
	protected static ModelBase modelParachute;
	protected static boolean randomColor;
	protected static String clothColor;
	
	public RenderParachute() {
		shadowSize = 0.0F;
		colorIndex = Parachute.instance.getChuteColor();
		randomColor = (colorIndex == -1);
		boolean useTexturePack = Parachute.instance.getTextureRule();

		rand = new Random(System.currentTimeMillis());
		if (randomColor) {
			if (useTexturePack) {
				clothColor = "/textures/blocks/cloth_" + rand.nextInt(16) + ".png";
			} else {
				clothColor = "/mods/ParachuteMod/textures/blocks/cloth_" + rand.nextInt(16) + ".png";
			}
		} else {
			if (useTexturePack) {
				clothColor = "/textures/blocks/cloth_" + colorIndex + ".png";
			} else {
				clothColor = "/mods/ParachuteMod/textures/blocks/cloth_" + colorIndex + ".png";
			}
		}

		modelParachute = new ModelParachute();
	}

	public void renderParachute(EntityParachute entityparachute, double x, double y, double z, float rotation, float center) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(180.0F - rotation, 0.0F, 1.0F, 0.0F);

		// rock the parachute when hit
		float time = (float) entityparachute.getTimeSinceHit() - center;
		float damage = (float) entityparachute.getDamageTaken() - center;

		if (damage < 0.0F) {
			damage = 0.0F;
		}

		if (time > 0.0F) {
			GL11.glRotatef(MathHelper.sin(time) * time * damage / 20.0F	* (float) entityparachute.getForwardDirection(), 0.0F,	0.0F, 1.0F);
		}

		loadTexture(clothColor);

		modelParachute.render(entityparachute, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

		if (entityparachute.riddenByEntity != null) {
			EntityPlayer rider = (EntityPlayer) entityparachute.riddenByEntity;
			renderCords(rider, center);
		}

		GL11.glPopMatrix();
	}

	public void doRender(Entity entity, double x, double y, double z, float rotation, float center) {
		renderParachute((EntityParachute) entity, x, y, z, rotation, center);
	}

	public void renderCords(EntityPlayer rider, float center) {
		float x = -8.0F;
		float y = 3.0F;
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			y = 2.25F;
		}
		float z = 0.0F;

		float b = rider.getBrightness(center);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glScalef(0.0625F, -1.0F, 0.0625F);

		GL11.glBegin(GL11.GL_LINES);
		// left end
		GL11.glColor3f(b * 0.5F, b * 0.5F, b * 0.65F); // slightly blue

		GL11.glVertex3f(-8F, 0.37F, -31.5F); 	// top - front
		GL11.glVertex3f(x, y, z); 				// bottom

		GL11.glVertex3f(8F, 0.37F, -31.5F);     // ...back
		GL11.glVertex3f(x, y, z);
		
		// left middle
		GL11.glVertex3f(-8F, 0.12F, -16F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0.12F, -16F);
		GL11.glVertex3f(x, y, z);

		// right end
		GL11.glColor3f(b * 0.65F, b * 0.5F, b * 0.5F); // slightly red

		GL11.glVertex3f(-8F, 0.37F, 31.5F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0.37F, 31.5F);
		GL11.glVertex3f(x, y, z);

		// right middle
		GL11.glVertex3f(-8F, 0.12F, 16F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0.12F, 16F);
		GL11.glVertex3f(x, y, z);
		
		// center
		GL11.glColor3f(b * 0.5F, b * 0.65F, b * 0.5F); // slightly green
		
		GL11.glVertex3f(-8F, 0F, 0F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0F, 0F);
		GL11.glVertex3f(x, y, z);

		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public static void setParachuteColor(int index) {
		colorIndex = index;
		boolean useTexturePack = Parachute.instance.getTextureRule();

		if (index == -1) { // get a random color
			if (useTexturePack) {
				clothColor = "/textures/blocks/cloth_" + rand.nextInt(16) + ".png";
			} else {
				clothColor = "/mods/ParachuteMod/textures/blocks/cloth_" + rand.nextInt(16) + ".png";
			}
			randomColor = true;
		} else {
			if (useTexturePack) {
				clothColor = "/textures/blocks/cloth_" + colorIndex + ".png";
			} else {
				clothColor = "/mods/ParachuteMod/textures/blocks/cloth_" + colorIndex + ".png";
			}
			randomColor = false;
		}
	}

	public static void randomParachuteColor() {
		setParachuteColor(-1);
	}

	public static boolean isColorRandom() {
		return randomColor;
	}
}
