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

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class ParachuteTexturedQuad {

	final private float texSize = 16F;
	public PositionTextureVertex vertexPositions[];
	public int nVertices;
	private boolean invertNormal;

	public ParachuteTexturedQuad(PositionTextureVertex texCoords[]) {
		nVertices = 0;
		invertNormal = false;
		vertexPositions = texCoords;
		nVertices = texCoords.length;
	}

	public ParachuteTexturedQuad(PositionTextureVertex texCoords[], int i, int j, int k, int l) {
		this(texCoords);
		texCoords[0] = texCoords[0].setTexturePosition((float) k / texSize, (float) j / texSize);
		texCoords[1] = texCoords[1].setTexturePosition((float) i / texSize, (float) j / texSize);
		texCoords[2] = texCoords[2].setTexturePosition((float) i / texSize, (float) l / texSize);
		texCoords[3] = texCoords[3].setTexturePosition((float) k / texSize, (float) l / texSize);
	}

	public void flipFace() {
		PositionTextureVertex texCoords[] = new PositionTextureVertex[vertexPositions.length];

		for (int i = 0; i < vertexPositions.length; i++) {
			texCoords[i] = vertexPositions[vertexPositions.length - i - 1];
		}

		vertexPositions = texCoords;
	}

	public void draw(Tessellator tessellator, float f) {
		Vec3 vec3d = vertexPositions[1].vector3D.subtract(vertexPositions[0].vector3D);
		Vec3 vec3d1 = vertexPositions[1].vector3D.subtract(vertexPositions[2].vector3D);
		Vec3 vec3d2 = vec3d1.crossProduct(vec3d).normalize();
		tessellator.startDrawingQuads();

		if (invertNormal) {
			tessellator.setNormal(-(float) vec3d2.xCoord, -(float) vec3d2.yCoord, -(float) vec3d2.zCoord);
		} else {
			tessellator.setNormal((float) vec3d2.xCoord, (float) vec3d2.yCoord, (float) vec3d2.zCoord);
		}

		for (int i = 0; i < 4; i++) {
			PositionTextureVertex positiontexturevertex = vertexPositions[i];
			tessellator.addVertexWithUV((float) positiontexturevertex.vector3D.xCoord * f,
					(float) positiontexturevertex.vector3D.yCoord * f, (float) positiontexturevertex.vector3D.zCoord * f,
					positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY);
		}

		tessellator.draw();
	}

}
