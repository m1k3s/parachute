package parachute.common;

import net.minecraft.src.*;


public class ParachuteTexturedQuad
{
  public ParachuteTexturedQuad(PositionTextureVertex texCoords[]) {
    nVertices = 0;
    invertNormal = false;
    vertexPositions = texCoords;
    nVertices = texCoords.length;
  }

  public ParachuteTexturedQuad(PositionTextureVertex texCoords[], int i, int j, int k, int l) {
    this(texCoords);
//    float f = 0.0F;//0.0015625F;
//    float f1 = 0.0F;//0.003125F;
    
    // The "terrain.png" texture is used, the default size is 256x256. Appears to work with larger
    // HD textures using MCPatcher. Cool.
    texCoords[0] = texCoords[0].setTexturePosition((float)k / 256F/*- f*/, (float)j / 256F/*+ f1*/);
    texCoords[1] = texCoords[1].setTexturePosition((float)i / 256F/*+ f*/, (float)j / 256F/*+ f1*/);
    texCoords[2] = texCoords[2].setTexturePosition((float)i / 256F/*+ f*/, (float)l / 256F/*- f1*/);
    texCoords[3] = texCoords[3].setTexturePosition((float)k / 256F/*- f*/, (float)l / 256F/*- f1*/);
  }
  
  public void updateTextureCoords(int i, int j, int k, int l) {
//  	float f = 0.0F;//0.0015625F;
//    float f1 = 0.0F;//0.003125F;
  	vertexPositions[0] = vertexPositions[0].setTexturePosition((float)k / 256F/*- f*/, (float)j / 256F/*+ f1*/);
    vertexPositions[1] = vertexPositions[1].setTexturePosition((float)i / 256F/*+ f*/, (float)j / 256F/*+ f1*/);
    vertexPositions[2] = vertexPositions[2].setTexturePosition((float)i / 256F/*+ f*/, (float)l / 256F/*- f1*/);
    vertexPositions[3] = vertexPositions[3].setTexturePosition((float)k / 256F/*- f*/, (float)l / 256F/*- f1*/);
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
      tessellator.setNormal(-(float)vec3d2.xCoord, -(float)vec3d2.yCoord, -(float)vec3d2.zCoord);
    } else {
      tessellator.setNormal((float)vec3d2.xCoord, (float)vec3d2.yCoord, (float)vec3d2.zCoord);
    }
    
    for (int i = 0; i < 4; i++) {
      PositionTextureVertex positiontexturevertex = vertexPositions[i];
      tessellator.addVertexWithUV((float)positiontexturevertex.vector3D.xCoord * f, (float)positiontexturevertex.vector3D.yCoord * f, (float)positiontexturevertex.vector3D.zCoord * f, positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY);
    }

    tessellator.draw();
  }

  public PositionTextureVertex vertexPositions[];
  public int nVertices;
  private boolean invertNormal;
}
