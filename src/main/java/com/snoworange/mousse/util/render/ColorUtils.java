package com.snoworange.mousse.util.render;

import java.awt.*;

public class ColorUtils {

    public static boolean RAINBOWONLY = true;
    public static boolean RAINBOW = true;
    public static boolean GRADIENT = true;

    public static int HSBtoRGB(float h, float s, float b) {
        return Color.HSBtoRGB(h, s, b);
    }

    public static Color alpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static int rainbow(final long n, final int n2) {
        final float hue = (float)((System.currentTimeMillis() * ((double) Double.longBitsToDouble(Double.doubleToLongBits(0.32656142855281983) ^ 0x7FEDE661E7F13DE1L) / Double.longBitsToDouble(Double.doubleToLongBits(0.1945486318633371) ^ 0x7FECE6F835AAD366L)) + n * ((long)(-1942993408) ^ 0xFFFFFFFF8C3047F4L)) % (Double.longBitsToDouble(Double.doubleToLongBits(3.110081473398666E-4) ^ 0x7FE92DD9D255502FL) / ((double) Double.longBitsToDouble(Double.doubleToLongBits(0.46281910238594925) ^ 0x7FE49ED4032D8CF2L) / Double.longBitsToDouble(Double.doubleToLongBits(0.009576666126914051) ^ 0x7FDA9CEE5E8EF6E3L))) / (Double.longBitsToDouble(Double.doubleToLongBits(4.4598247589669275E-4) ^ 0x7FE07658260861DEL) / ((double) Double.longBitsToDouble(Double.doubleToLongBits(0.46281910238594925) ^ 0x7FE49ED4032D8CF2L) / Double.longBitsToDouble(Double.doubleToLongBits(0.36770462035546114) ^ 0x7FE38878F5C0F282L))));
        final int rgb = Color.HSBtoRGB(hue, ((Double) Double.longBitsToDouble(Double.doubleToLongBits(2.142034642084924) ^ 0x7FE8BB7A976CA299L)).floatValue(), ((Double) Double.longBitsToDouble(Double.doubleToLongBits(2.671600220784651) ^ 0x7FECC6F6765B94A3L)).floatValue());
        final int red = rgb >> 16 & 0xFF;
        final int green = rgb >> 8 & 0xFF;
        final int blue = rgb & 0xFF;
        final int color = toRGBA(red, green, blue, n2);
        return color;
    }

    public static int toRGBA(final int n, final int n2, final int n3, final int n4) {
        return (n << 16) + (n2 << 8) + (n3 << 0) + (n4 << 24);
    }

    public static int BESTCOLOR(final int n, final int n2) {
        return ColorUtils.RAINBOWONLY ? (ColorUtils.GRADIENT ? rainbow(n, n2) : rainbow((long)742087092 ^ 0x2C3B59B5L, n2)) : toRGBA(((Double) Double.longBitsToDouble(Double.doubleToLongBits(0.06430018703629382) ^ 0x7FEE75FA207293A1L)).intValue(), ((Double) Double.longBitsToDouble(Double.doubleToLongBits(5.414493988340931E307) ^ 0x7FD346B7901F8193L)).intValue(), ((Double) Double.longBitsToDouble(Double.doubleToLongBits(0.09573721170691775) ^ 0x7FD7623BE14A9BD1L)).intValue(), n2);
    }
}