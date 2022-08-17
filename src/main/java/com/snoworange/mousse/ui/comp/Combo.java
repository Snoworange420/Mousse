package com.snoworange.mousse.ui.comp;

import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.ui.ClickGui;
import com.snoworange.mousse.ui.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Combo extends Comp {

    public Combo(double x, double y, ClickGui parent, Module module, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, parent.posX + x - 70, parent.posY + y, parent.posX + x, parent.posY + y + 10) && mouseButton == 0) {
            int max = setting.getOptions().size();
            if (parent.modeIndex + 1 >= max) {
                parent.modeIndex = 0;
            } else {
                parent.modeIndex++;
            }
            setting.setValString(setting.getOptions().get(parent.modeIndex));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        Gui.drawRect((int) (parent.posX + x - 70), (int) (parent.posY + y), (int) (parent.posX + x), (int) (parent.posY + y + 10),setting.getValBoolean() ? new Color(131, 141, 59).getRGB() : new Color(40, 40, 40).getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString(setting.getName() + ": " + setting.getValString(), (int)(parent.posX + x - 69), (int)(parent.posY + y + 1), new Color(200,200,200).getRGB());
    }
}
