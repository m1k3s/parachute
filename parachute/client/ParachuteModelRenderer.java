/*
 * ParachuteModelRenderer.java
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

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

import org.lwjgl.opengl.GL11;

public class ParachuteModelRenderer {

    private ParachuteTexturedQuad faces[];
    private final int left;
    private final int top;
    private float rotationPointX;
    private float rotationPointY;
    private float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    private boolean compiled;
    private int displayList;
    private boolean mirror;
    private boolean showModel;
    private float textureWidth;
    private float textureHeight;

    public ParachuteModelRenderer(int x, int y) {
        textureWidth = 64.0F;
        textureHeight = 32.0F;
        compiled = false;
        displayList = 0;
        mirror = false;
        showModel = true;
        left = x;
        top = y;
        setTextureSize(textureWidth, textureHeight);
    }

    public void addBox(float x, float y, float z, float w, float h, float d) {
        PositionTextureVertex[] corners = new PositionTextureVertex[8];
        faces = new ParachuteTexturedQuad[6];

        float width = x + w;
        float height = y + h;
        float depth = z + d;

        if (mirror) {
            float tmp = width;
            width = x;
            x = tmp;
        }

        corners[0] = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        corners[1] = new PositionTextureVertex(width, y, z, 0.0F, 8F);
        corners[2] = new PositionTextureVertex(width, height, z, 8F, 8F);
        corners[3] = new PositionTextureVertex(x, height, z, 8F, 0.0F);
        corners[4] = new PositionTextureVertex(x, y, depth, 0.0F, 0.0F);
        corners[5] = new PositionTextureVertex(width, y, depth, 0.0F, 8F);
        corners[6] = new PositionTextureVertex(width, height, depth, 8F, 8F);
        corners[7] = new PositionTextureVertex(x, height, depth, 8F, 0.0F);

        // sides may be smaller than 16, need to account for that.
        int r1 = (int)((w > 16) ? 16 : w);
        int r2 = (int)((d > 16) ? 16 : d);
        int bottom = (int)((h > 16) ? 16 : h);

        faces[0] = new ParachuteTexturedQuad(
            new PositionTextureVertex[] { // right face
                corners[5], corners[1], corners[2], corners[6]
            }, left, top, left + r1, top + bottom);

        faces[1] = new ParachuteTexturedQuad(
            new PositionTextureVertex[] { // left face
                corners[0], corners[4], corners[7], corners[3]
            }, left, top, left + r1, top + bottom);

        faces[2] = new ParachuteTexturedQuad(
            new PositionTextureVertex[] { // top face
                corners[5], corners[4], corners[0], corners[1]
            }, left, top, left + r1, top + r2);

        faces[3] = new ParachuteTexturedQuad(
            new PositionTextureVertex[] { // bottom face
                corners[2], corners[3], corners[7], corners[6]
            }, left, top, left + r1, top + r2);

        faces[4] = new ParachuteTexturedQuad(
            new PositionTextureVertex[] { // back face
                corners[1], corners[0], corners[3], corners[2]
            }, left, top, left + r1, top + bottom);

        faces[5] = new ParachuteTexturedQuad(
            new PositionTextureVertex[] { // front face
                corners[4], corners[5], corners[6], corners[7]
            }, left, top, left + r1, top + bottom);

        if (mirror) {
            for (ParachuteTexturedQuad face : faces) {
                face.flipFace();
            }
        }
    }

    public void setRotationPoint(float x, float y, float z) {
        rotationPointX = x;
        rotationPointY = y;
        rotationPointZ = z;
    }

    public void render(float scale) {
        if (!showModel) {
            return;
        }
        if (!compiled) {
            compileDisplayList(scale);
        }
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
            if (rotateAngleZ != 0.0F) {
                GlStateManager.rotate(rotateAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
            }
            if (rotateAngleY != 0.0F) {
                GlStateManager.rotate(rotateAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
            }
            if (rotateAngleX != 0.0F) {
                GlStateManager.rotate(rotateAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
            }
            GlStateManager.callList(displayList);
            GlStateManager.popMatrix();
        } else if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
            GlStateManager.callList(displayList);
            GlStateManager.translate(-rotationPointX * scale, -rotationPointY * scale, -rotationPointZ * scale);
            GlStateManager.popMatrix();
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.callList(displayList);
            GlStateManager.popMatrix();
        }
    }

    @SuppressWarnings("unused")
    public void renderWithRotation(float f) {
        if (!showModel) {
            return;
        }
        if (!compiled) {
            compileDisplayList(f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(rotationPointX * f, rotationPointY * f, rotationPointZ * f);
        if (rotateAngleY != 0.0F) {
            GlStateManager.rotate(rotateAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
        }
        if (rotateAngleX != 0.0F) {
            GlStateManager.rotate(rotateAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
        }
        if (rotateAngleZ != 0.0F) {
            GlStateManager.rotate(rotateAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.callList(displayList);
        GlStateManager.popMatrix();
    }

    @SuppressWarnings("unused")
    public void postRender(float f) {
        if (!showModel) {
            return;
        }
        if (!compiled) {
            compileDisplayList(f);
        }
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(rotationPointX * f, rotationPointY * f, rotationPointZ * f);
            if (rotateAngleZ != 0.0F) {
                GlStateManager.rotate(rotateAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
            }
            if (rotateAngleY != 0.0F) {
                GlStateManager.rotate(rotateAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
            }
            if (rotateAngleX != 0.0F) {
                GlStateManager.rotate(rotateAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
            }
            GlStateManager.popMatrix();
        } else if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(rotationPointX * f, rotationPointY * f, rotationPointZ * f);
            GlStateManager.popMatrix();
        }
    }

    private void compileDisplayList(float scale) {
        displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        VertexBuffer vertexBuffer = Tessellator.getInstance().getBuffer();

        for (ParachuteTexturedQuad face : faces) {
            face.draw(vertexBuffer, scale);
        }

        GL11.glEndList();
        compiled = true;
    }

    public final void setTextureSize(float width, float height) {
        textureWidth = width;
        textureHeight = height;
    }

}
