package com.snoworange.mousse.ui;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.modules.render.Info32k;
import com.snoworange.mousse.module.modules.system.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

public class Hud extends Gui {

    private Minecraft mc = Minecraft.getMinecraft();

    public static class ModuleComparator implements Comparator<Module> {

        @Override
        public int compare(Module arg0, Module arg1) {
            if(Minecraft.getMinecraft().fontRenderer.getStringWidth(arg0.getName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(arg1.getName())) {
                return -1;
            }

            if(Minecraft.getMinecraft().fontRenderer.getStringWidth(arg0.getName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(arg1.getName())) {
                return 1;
            }
            return 0;
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        Collections.sort(Main.moduleManager.modules, new ModuleComparator());
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;

        //Watermark
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if (HUD.watermark.isEnable()) {
                fr.drawString(Main.NAME + " " + Main.VERSION, 2, 12, 0x838d3b, true);
            }
        }

        //Arraylist
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if (HUD.arraylist.isEnable()) {
                int y = 40;
                final int[] counter = {1};
                for (Module mod : Main.moduleManager.getModuleList()) {
                    if (!mod.getName().equalsIgnoreCase("") && mod.isToggled()) {
                        fr.drawString(mod.getName(), 2, y, rainbow(counter[0] * 100));
                        y += fr.FONT_HEIGHT;
                        counter[0]++;
                    }
                }
            }
        }

        //32k
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if (Main.moduleManager.getModule("32kInfo").isEnabled()) {

                if (Info32k.has32k()) {
                    fr.drawString("32k in hotbar!", 100, 12, new Color(107, 154, 69).getRGB(), false);
                } else {
                    fr.drawString("No 32k in hotbar!", 100, 12, new Color(222, 39, 39).getRGB(), false);
                }
            }
        }
    }

    private void getTrueDurability() {

        ItemStack itemStack = mc.player.inventory.getCurrentItem();

        this.renderDurability(1, itemStack);
    }

    private void renderDurability(int i, ItemStack is) {

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        ScaledResolution sr = new ScaledResolution(mc);

        fr.drawStringWithShadow(is.getDisplayName() + ": " + (is.getMaxDamage() - is.getItemDamage()) + " / " + is.getMaxDamage() + " (" + is.getItemDamage() + ")", 2, sr.getScaledHeight() - 10, -1);

    }


    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 10.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.7f, 0.7f).getRGB();
    }
}
