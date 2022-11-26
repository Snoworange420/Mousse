package com.snoworange.mousse.util.render;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import java.awt.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;

import static com.snoworange.mousse.Main.mc;

public class RenderUtils2
{
    public static BufferBuilder bufferbuilder;
    public static Tessellator tessellator;

    public static void glSetup2d() {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
    }

    public static void glShutdown2d() {
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    public static void glSetup3d() {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(Float.intBitsToFloat(Float.floatToIntBits(0.699028f) ^ 0x7F32F380));
    }

    public static void glShutdown3d() {
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawCircle(final BlockPos blockPos, final double n, final double n2, final Color color, final Color color2) {
        glSetup3d();
        final AxisAlignedBB box = new AxisAlignedBB(blockPos.getX() - mc.getRenderManager().viewerPosX, blockPos.getY() - mc.getRenderManager().viewerPosY, blockPos.getZ() - mc.getRenderManager().viewerPosZ, blockPos.getX() + 1 - mc.getRenderManager().viewerPosX, blockPos.getY() + 1 - mc.getRenderManager().viewerPosY, blockPos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
        renderCircle(bufferbuilder, box, n, n2, color, color2);
        glShutdown3d();
    }

    public static void renderCircle(final BufferBuilder bufferBuilder, final AxisAlignedBB axisAlignedBB, final double n, final double n2, final Color color, final Color color2) {
        final float red = color.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.09405973f) ^ 0x7EBFA263);
        final float green = color.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.00881383f) ^ 0x7F6F67E2);
        final float blue = color.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.008068395f) ^ 0x7F7B314D);
        final float red2 = color2.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.0103932405f) ^ 0x7F554869);
        final float green2 = color2.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.0101895705f) ^ 0x7F59F228);
        final float blue2 = color2.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.04023848f) ^ 0x7E5BD11B);
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(axisAlignedBB.maxX - Double.longBitsToDouble(Double.doubleToLongBits(3.3258652357410368) ^ 0x7FEA9B5F3B9349E6L), axisAlignedBB.minY, axisAlignedBB.maxZ - Double.longBitsToDouble(Double.doubleToLongBits(3.0713387425351564) ^ 0x7FE8921A0BF10295L) + n).color(red, green, blue, Float.intBitsToFloat(Float.floatToIntBits(12.126821f) ^ 0x7EC20775)).endVertex();
        for (int i = 0; i < 361; ++i) {
            //final Color c = new Color(ColorUtils.BESTCOLOR(i / 6, 255));
            final Color c = new Color(255, 255, 255);
            bufferBuilder.pos(axisAlignedBB.maxX - Double.longBitsToDouble(Double.doubleToLongBits(3.4943924475908426) ^ 0x7FEBF484070E5625L) + Math.sin(Math.toRadians(i)) * n, axisAlignedBB.minY, axisAlignedBB.maxZ - Double.longBitsToDouble(Double.doubleToLongBits(2.180035505873708) ^ 0x7FE170B6748EC569L) + Math.cos(Math.toRadians(i)) * n).color(c.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.09911574f) ^ 0x7EB5FD31), c.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.011103338f) ^ 0x7F4AEAC6), c.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.011012881f) ^ 0x7F4B6F5F), Float.intBitsToFloat(Float.floatToIntBits(13.719199f) ^ 0x7EDB81D7)).endVertex();
        }
        tessellator.draw();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 360; ++i) {
            //final Color c = new Color(ColorUtils.BESTCOLOR(i / 6, 255));
            final Color c = new Color(255, 255, 255);
            bufferBuilder.pos(axisAlignedBB.maxX - Double.longBitsToDouble(Double.doubleToLongBits(21.168408199457257) ^ 0x7FD52B1CCCBD0C13L) + Math.sin(Math.toRadians(i)) * n, axisAlignedBB.minY, axisAlignedBB.maxZ - Double.longBitsToDouble(Double.doubleToLongBits(17.860148308328778) ^ 0x7FD1DC32ADF5FB59L) + Math.cos(Math.toRadians(i)) * n).color(c.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.009253376f) ^ 0x7F689B79), c.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.015045835f) ^ 0x7F0982CE), c.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.10712188f) ^ 0x7EA462B7), Float.intBitsToFloat(Float.floatToIntBits(8.495716f) ^ 0x7E34DD47)).endVertex();
            bufferBuilder.pos(axisAlignedBB.maxX - Double.longBitsToDouble(Double.doubleToLongBits(12.511052483536039) ^ 0x7FC905A8ABCEA75BL) + Math.sin(Math.toRadians(i)) * n, axisAlignedBB.minY + n2, axisAlignedBB.maxZ - Double.longBitsToDouble(Double.doubleToLongBits(22.499547648576335) ^ 0x7FD67FE25ACD6DD7L) + Math.cos(Math.toRadians(i)) * n).color(c.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.11554704f) ^ 0x7E93A3ED), c.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.4653487f) ^ 0x7D91422F), c.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.011744561f) ^ 0x7F3F6C42), Float.intBitsToFloat(Float.floatToIntBits(3.2699992E38f) ^ 0x7F7601E5)).endVertex();
            bufferBuilder.pos(axisAlignedBB.maxX - Double.longBitsToDouble(Double.doubleToLongBits(43.823459124280355) ^ 0x7FA5E9671BCC303FL) + Math.sin(Math.toRadians(i + 1)) * n, axisAlignedBB.minY, axisAlignedBB.maxZ - Double.longBitsToDouble(Double.doubleToLongBits(3.8602556055677395) ^ 0x7FEEE1CDB0E0E9B6L) + Math.cos(Math.toRadians(i + 1)) * n).color(c.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.010982755f) ^ 0x7F4CF103), c.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.11211056f) ^ 0x7E9A9A39), c.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.010084451f) ^ 0x7F5A3941), Float.intBitsToFloat(Float.floatToIntBits(357.41248f) ^ 0x7C8187FF)).endVertex();
            bufferBuilder.pos(axisAlignedBB.maxX - Double.longBitsToDouble(Double.doubleToLongBits(18.12502371312355) ^ 0x7FD220018DD71713L) + Math.sin(Math.toRadians(i + 1)) * n, axisAlignedBB.minY + n2, axisAlignedBB.maxZ - Double.longBitsToDouble(Double.doubleToLongBits(31.04659279765212) ^ 0x7FDF0BED816E251FL) + Math.cos(Math.toRadians(i + 1)) * n).color(c.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.1610062f) ^ 0x7D5BDECF), c.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.012648355f) ^ 0x7F303B0C), c.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.010003909f) ^ 0x7F5CE76F), Float.intBitsToFloat(Float.floatToIntBits(2.5075545E38f) ^ 0x7F3CA5BE)).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
    }

    static {
        RenderUtils2.tessellator = Tessellator.getInstance();
        RenderUtils2.bufferbuilder = RenderUtils2.tessellator.getBuffer();
    }
}

