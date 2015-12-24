package com.parachute.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ParachuteFontRenderer extends FontRenderer {

    protected static ResourceLocation locationFontTexture;
    private final Minecraft mc;
    protected int[] charWidth = new int[256];
    protected float posX;
    protected float posY;

    public ParachuteFontRenderer(Minecraft mc, ResourceLocation fontTexture) {
        super(mc.gameSettings, fontTexture, mc.getTextureManager(), mc.getLanguageManager().isCurrentLocaleUnicode());
        locationFontTexture = fontTexture;
        mc.getTextureManager().bindTexture(locationFontTexture);
        readFontTexture();
        this.mc = mc;
    }

    public int drawStringWithShadow(String str, float strX, float strY, int color) {
        return this.drawString(str, strX, strY, color, true);
    }

    public int drawString(String str, float strX, float strY, int color, boolean shadows) {
        GlStateManager.enableAlpha();
        int j;

        if (shadows) {
            j = renderString(str, strX + 1.0F, strY + 1.0F, color, true);
            j = Math.max(j, renderString(str, strX, strY, color, false));
        } else {
            j = renderString(str, strX, strY, color, false);
        }

        return j;
    }

    private int renderString(String str, float strX, float strY, int color, boolean italic) {
        if (str == null) {
            return 0;
        } else {
            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            if (italic) {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            float red = (float) (color >> 16 & 255) / 255.0F;
            float blue = (float) (color >> 8 & 255) / 255.0F;
            float green = (float) (color & 255) / 255.0F;
            float alpha = (float) (color >> 24 & 255) / 255.0F;
            GlStateManager.color(red, blue, green, alpha);
            posX = strX;
            posY = strY;
            renderStringAtPos(str, italic);
            return (int) posX;
        }
    }

    private void renderStringAtPos(String str, boolean italic) {
        for (int i = 0; i < str.length(); ++i) {
            char c0 = str.charAt(i);

            int j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c0);

            float f1 = getCharWidth(c0) / 32f;
            boolean flag1 = (c0 == 0 || j == -1) && italic;

            if (flag1) {
                posX -= f1;
                posY -= f1;
            }

            renderCharAtPos(j, c0, italic);

            if (flag1) {
                this.posX += f1;
                this.posY += f1;
            }

            posX += (float)((int)renderCharAtPos(j, c0, false));

        }
    }

    private float renderCharAtPos(int charCode, char ch, boolean unused) {
        if (ch == 32) {
            return 4.0F;
        } else if ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch) != -1) {
            return renderDefaultChar(charCode, unused);
        }
        return 0;
    }

    protected float renderDefaultChar(int ch, boolean flag) {
        float x = (float) (ch % 16 * 8);
        float y = (float) (ch / 16 * 8);
        float f2 = flag ? 1.0F : 0.0F;

        mc.getTextureManager().bindTexture(locationFontTexture);

        float f3 = (float) charWidth[ch] - 0.01F;

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(x / 128.0F, y / 128.0F);
        GL11.glVertex3f(posX + f2, posY, 0.0F);
        GL11.glTexCoord2f(x / 128.0F, (y + 7.99F) / 128.0F);
        GL11.glVertex3f(posX - f2, posY + 7.99F, 0.0F);
        GL11.glTexCoord2f((x + f3 - 1.0F) / 128.0F, y / 128.0F);
        GL11.glVertex3f(posX + f3 - 1.0F + f2, posY, 0.0F);
        GL11.glTexCoord2f((x + f3 - 1.0F) / 128.0F, (y + 7.99F) / 128.0F);
        GL11.glVertex3f(posX + f3 - 1.0F - f2, posY + 7.99F, 0.0F);
        GL11.glEnd();

        return (float) charWidth[ch];
    }

    private void readFontTexture() {
        BufferedImage bufferedimage;

        try {
            bufferedimage = TextureUtil.readBufferedImage(getResourceInputStream(locationFontTexture));
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }

        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        int[] aint = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
        int k = j / 16;
        int l = i / 16;
        byte b0 = 1;
        float f = 8.0F / (float) l;
        int i1 = 0;

        while (i1 < 256) {
            int j1 = i1 % 16;
            int k1 = i1 / 16;

            if (i1 == 32) {
                charWidth[i1] = 3 + b0;
            }

            int l1 = l - 1;

            while (true) {
                if (l1 >= 0) {
                    int i2 = j1 * l + l1;
                    boolean flag = true;

                    for (int j2 = 0; j2 < k && flag; ++j2) {
                        int k2 = (k1 * l + j2) * i;

                        if ((aint[i2 + k2] >> 24 & 255) != 0) {
                            flag = false;
                        }
                    }

                    if (flag) {
                        --l1;
                        continue;
                    }
                }

                ++l1;
                charWidth[i1] = (int) (0.5D + (double) ((float) l1 * f)) + b0;
                ++i1;
                break;
            }
        }
    }

    protected InputStream getResourceInputStream(ResourceLocation location) throws IOException {
        return Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
    }

    public int getStringWidth(String str) {
        if (str == null) {
            return 0;
        } else {
            int i = 0;
            boolean flag = false;

            for (int j = 0; j < str.length(); ++j) {
                char c0 = str.charAt(j);
                int k = this.getCharWidth(c0);

                if (k < 0 && j < str.length() - 1) {
                    ++j;
                    c0 = str.charAt(j);

                    if (c0 != 108 && c0 != 76) {
                        if (c0 == 114 || c0 == 82) {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;

                if (flag && k > 0) {
                    ++i;
                }
            }

            return i;
        }
    }

    public int getCharWidth(char ch) {
        if (ch == 32) {
            return 4;
        } else {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch);

            if (ch > 0 && i != -1) {
                return charWidth[i];
            } else {
                return 0;
            }
        }
    }
}
