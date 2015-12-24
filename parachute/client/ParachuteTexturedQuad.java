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

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Vec3;

public class ParachuteTexturedQuad {

	final private float texSize = 16F;
	public PositionTextureVertex vertexPositions[];
	public int nVertices;
	private boolean invertNormal;

	public ParachuteTexturedQuad(PositionTextureVertex texCoords[])
	{
		nVertices = 0;
		invertNormal = false;
		vertexPositions = texCoords;
		nVertices = texCoords.length;
	}

	public ParachuteTexturedQuad(PositionTextureVertex texCoords[], int texU1, int texV1, int texU2, int texV2)
	{
		this(texCoords);
		texCoords[0] = texCoords[0].setTexturePosition((float) texU2 / texSize, (float) texV1 / texSize);
		texCoords[1] = texCoords[1].setTexturePosition((float) texU1 / texSize, (float) texV1 / texSize);
		texCoords[2] = texCoords[2].setTexturePosition((float) texU1 / texSize, (float) texV2 / texSize);
		texCoords[3] = texCoords[3].setTexturePosition((float) texU2 / texSize, (float) texV2 / texSize);
	}

	public void flipFace()
	{
		PositionTextureVertex texCoords[] = new PositionTextureVertex[vertexPositions.length];

		for (int i = 0; i < vertexPositions.length; i++) {
			texCoords[i] = vertexPositions[vertexPositions.length - i - 1];
		}

		vertexPositions = texCoords;
	}

	public void draw(WorldRenderer worldrenderer, float face)
	{
		Vec3 vec3 = vertexPositions[1].vector3D.subtractReverse(vertexPositions[0].vector3D);
		Vec3 vec31 = vertexPositions[1].vector3D.subtractReverse(vertexPositions[2].vector3D);
		Vec3 vec32 = vec31.crossProduct(vec3).normalize();
		worldrenderer.startDrawingQuads();

		if (invertNormal) {
			worldrenderer.setNormal(-((float) vec32.xCoord), -((float) vec32.yCoord), -((float) vec32.zCoord));
		} else {
			worldrenderer.setNormal((float) vec32.xCoord, (float) vec32.yCoord, (float) vec32.zCoord);
		}

		for (int i = 0; i < 4; ++i) {
			PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
			worldrenderer.addVertexWithUV(positiontexturevertex.vector3D.xCoord * (double) face, 
					positiontexturevertex.vector3D.yCoord * (double) face,
					positiontexturevertex.vector3D.zCoord * (double) face,
					(double) positiontexturevertex.texturePositionX,
					(double) positiontexturevertex.texturePositionY);
		}

		Tessellator.getInstance().draw();
	}

}
