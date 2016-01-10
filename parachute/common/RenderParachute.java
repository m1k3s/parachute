package parachute.common;

import net.minecraft.src.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;

import java.nio.IntBuffer;
import java.util.Random;
//
// Copyright 2011 Michael Sheppard (crackedEgg)
//

public class RenderParachute extends Render {

	protected static int colorIndex;
	protected static Random rand;
	protected static ModelBase modelParachute;
	protected static int coords[] = new int[2];
	protected static boolean randomColor;
	
	public RenderParachute() {
		shadowSize = 0.0F;
		colorIndex = Parachute.instance.getChuteColor();
		randomColor = (colorIndex == -1);

		rand = new Random(System.currentTimeMillis());
		if (randomColor) {
			coords = genRandomColor();
		} 

		modelParachute = new ModelParachute(coords[0], coords[1]);
	}

	// this doesn't account for the white wool color, you can still choose
	// white wool in the config file though.
	protected int[] genRandomColor() {
		int uv[] = new int[2];
		uv[0] = (genRandomColorColumn() - 1) * 16;
		uv[1] = (genRandomColorRow() - 1) * 16;

		// if using the light grey wool texture set 'u' to 16
		if (uv[0] > 16 && uv[1] > 208)
			uv[0] = 16;

		return uv;
	}

	// return a 'column' number of 2 or 3
	protected int genRandomColorColumn() {
		int x = 0;
		while ((x = rand.nextInt(4)) < 2)
			; // we want 2 or 3 only

		return x;
	}

	// return a 'row' number between 8 and 15 inclusive,
	protected int genRandomColorRow() {
		int y = 0;
		while ((y = rand.nextInt(16)) < 8)
			; // we want 8 to 15

		return y;
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

		loadTexture("/terrain.png");

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
		float x = -5.0F;
		float y = (float) (rider.prevPosY + (rider.posY - rider.prevPosY) * (double) center) * 0.0125F + 0.75F;
		float z = 0.0F;

		float b = rider.getBrightness(center);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glScalef(0.0625F, -1.0F, 0.0625F);

		GL11.glBegin(GL11.GL_LINES);
		// left side
		GL11.glColor3f(b * 0.5F, b * 0.5F, b * 0.65F); // slightly blue

		GL11.glVertex3f(-8F, 0.25F, -23.5F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0.25F, -23.5F);
		GL11.glVertex3f(x, y, z);

		// front
		GL11.glVertex3f(-8F, 0F, -8F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0F, -8F);
		GL11.glVertex3f(x, y, z);

		// right side
		GL11.glColor3f(b * 0.65F, b * 0.5F, b * 0.5F); // slightly red

		GL11.glVertex3f(-8F, 0.25F, 23.5F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0.25F, 23.5F);
		GL11.glVertex3f(x, y, z);

		// back
		GL11.glVertex3f(-8F, 0F, 8F);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(8F, 0F, 8F);
		GL11.glVertex3f(x, y, z);
		GL11.glEnd();

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	// generates a set of UVs for the wool textures in 'terrain.png'
	// updates the tex coords
	public static void setParachuteColor(int index) {
		final int uv[][] = new int[][] {
				{ 16, 112 }, // black - 0
				{ 16, 128 }, // red - 1
				{ 16, 144 }, // green - 2
				{ 16, 160 }, // brown - 3
				{ 16, 176 }, // blue - 4
				{ 16, 192 }, // purple - 5
				{ 16, 208 }, // cyan - 6
				{ 16, 224 }, // light grey - 7
				{ 32, 112 }, // dark grey - 8
				{ 32, 128 }, // magenta - 9
				{ 32, 144 }, // lime - 10
				{ 32, 160 }, // yellow - 11
				{ 32, 176 }, // light blue - 12
				{ 32, 192 }, // pink - 13
				{ 32, 208 }, // orange - 14
				{ 0, 16 }    // white - 15
		};

		colorIndex = index;

		if (index == -1) { // get a random color
			rand = new Random(System.currentTimeMillis());
			int x = 0;
			while ((x = rand.nextInt(4)) < 2)
				; // we want 2 or 3 only

			int y = 0;
			while ((y = rand.nextInt(16)) < 8)
				; // we want 8 to 15 inclusive

			coords[0] = (x - 1) * 16;
			coords[1] = (y - 1) * 16;

			// if using the light grey wool texture set 'u' to 16
			if (coords[0] > 16 && coords[1] > 208)
				coords[0] = 16;

			randomColor = true;
		} else {
			if (index > 15)
				index = 0;
			if (index < 0) // shouldn't happen
				index = 0;

			coords[0] = uv[index][0];
			coords[1] = uv[index][1];
			
			randomColor = false;
		}
	}

	public static void randomParachuteColor() {
		setParachuteColor(-1);
		((ModelParachute) modelParachute).updateTextureCoords(coords[0], coords[1]);
	}

	public static boolean isColorRandom() {
		return randomColor;
	}
}
