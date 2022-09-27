package com.snoworange.mousse.util;

import com.snoworange.mousse.Main;
import net.minecraft.client.Minecraft;

public class FontUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float drawStringWithShadow(boolean customFont, String text, int x, int y, JColor color) {
        return mc.fontRenderer.drawStringWithShadow(text, x, y, color.getRGB());
    }

    public static int getStringWidth(boolean customFont, String string) {
        return mc.fontRenderer.getStringWidth(string);
    }

    public static int getFontHeight(boolean customFont) {
        return mc.fontRenderer.FONT_HEIGHT;
    }
}
