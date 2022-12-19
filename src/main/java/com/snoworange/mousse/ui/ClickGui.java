package com.snoworange.mousse.ui;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.Setting;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;
import com.snoworange.mousse.ui.theme.ThemeManager;
import com.snoworange.mousse.util.misc.FileUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class ClickGui extends GuiScreen {

    private double posX, posY, dragX, dragY;
    private double posX2, posY2, dragX2, dragY2;
    public boolean dragging, dragging2;
    private Module selectedModule;
    public boolean listeningForKey;

    //TODO: Clickgui rewrite

    public ClickGui() {
        dragging = false;
        posX = 100;
        posY = 50;
        posX2 = 700;
        posY2 = 50;
        height = height + 200;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (dragging) {
            posX = mouseX - dragX;
            posY = mouseY - dragY;
        }

        if (dragging2) {
            posX2 = mouseX - dragX2;
            posY2 = mouseY - dragY2;
        }

        //Draw category
        int offset = 0;
        for (Category category : Category.values()) {
            Gui.drawRect((int) posX + 1 + offset, (int) posY, (int) (posX + 80 + offset), (int) posY + 15, ThemeManager.getTheme().c0.getRGB());
            mc.fontRenderer.drawString(category.name, (int) (posX + 4.5) + offset, (int) ((float) posY + 4.5), -1);
            offset += 80;
        }

        //Draw modules
        int yoffsetModules = 15;
        int xoffsetModules = 0;
        for (Category c : Category.values()) {
            for (Module m : Main.moduleManager.getModuleList()) {
                if (c.equals(m.getCategory()) && m.getType() < 1) {
                    if (c.opened) {
                        Gui.drawRect((int) posX + xoffsetModules, (int) (posY + 1 + yoffsetModules), (int) (posX + 80 + xoffsetModules), (int) ((float) posY + 17.5 + yoffsetModules), m.isToggled() ? ThemeManager.getTheme().getC1().getRGB() : ThemeManager.getTheme().getC5().getRGB());
                        mc.fontRenderer.drawString(m.getName(), (int) ((float) posX + 2.5 + xoffsetModules), (int) ((float) posY + 6.5) + yoffsetModules, new Color(170, 170, 170).getRGB());
                        mc.fontRenderer.drawString(m.hasSettings() ? "+" : "", (int) ((float) posX + 72.5 + xoffsetModules), (int) ((float) posY + 6.5) + yoffsetModules, new Color(170, 170, 170).getRGB());
                        yoffsetModules += 17.5;
                    }
                }
            }

            xoffsetModules += 80;
            yoffsetModules = 15;
        }

        //SettingsManager
        Gui.drawRect((int) posX2, (int) posY2, (int) (posX2 + 150), (int) (posY2 + 250), ThemeManager.getTheme().getC5().getRGB());
        Gui.drawRect((int) posX2, (int) posY2, (int) (posX2 + 150), (int) (posY2 + 17.5), ThemeManager.getTheme().getC1().getRGB());

        if (selectedModule == null) {
            return;
        }

        mc.fontRenderer.drawString(selectedModule.getName(), (int) (posX2 + 5), (int) (posY2 + 5), -1);

        if (!listeningForKey) {
            mc.fontRenderer.drawString("Bind: " + getKeyDisplayString(selectedModule.getKey()), (int) (posX2 + 5), (int) (posY2 + 5 + 17.5), -1);
        } else {
            mc.fontRenderer.drawString("Listening... ", (int) (posX2 + 5), (int) (posY2 + 5 + 17.5), -1);
        }

        //Setting (fr)
        int offY = 0;

        for (Setting setting : selectedModule.settings) {
            Gui.drawRect((int) (posX2), (int) (posY2 + 35 + offY), (int) (posX2 + 150), (int) (posY2 + 35 + 17.5 + offY), setting instanceof BooleanSetting ? ((BooleanSetting) setting).isEnable() ? ThemeManager.getTheme().getC2().getRGB() : ThemeManager.getTheme().getC5().getRGB() : ThemeManager.getTheme().getC5().getRGB());
            fontRenderer.drawString(setting instanceof ModeSetting ? setting.name + " > " + ((ModeSetting) setting).getMode() : setting instanceof NumberSetting ? setting.name + " > " + setting.value : setting.name, (int) posX2 + 5, (int) (posY2 + 40 + offY), -1);
            offY += 17.5;
        }
    }

    //Thank you Minecraft
    public static String getKeyDisplayString(int key)
    {
        if (key < 0)
        {
            switch (key)
            {
                case -100:
                    return I18n.format("key.mouse.left");
                case -99:
                    return I18n.format("key.mouse.right");
                case -98:
                    return I18n.format("key.mouse.middle");
                default:
                    return I18n.format("key.mouseButton", key + 101);
            }
        }
        else
        {
            return key < 256 ? Keyboard.getKeyName(key) : String.format(String.valueOf((char)(key - 256)).toUpperCase(Locale.ROOT));
        }
    }



    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        //Drag module shit
        if (isInside(mouseX, mouseY, posX, posY - 12.5, posX + 480, posY + 12.5) && mouseButton == 0) {
            dragging = true;
            dragX = mouseX - posX;
            dragY = mouseY - posY;
        }

        //Drag setting panel
        if (isInside(mouseX, mouseY, posX2, posY2, posX2 + 250, posY2 + 15) && mouseButton == 0) {
            dragging2 = true;
            dragX2 = mouseX - posX2;
            dragY2 = mouseY - posY2;
        }

        //Settingsmanager
        if (isInside(mouseX, mouseY, posX2, posY2 + 17.5, posX2 + 150, posY2 + 30)) {
            if (!listeningForKey) {
                listeningForKey = true;
            } else if (listeningForKey) {
                listeningForKey = false;
            }
        }

        int yoffsetModules = 15;
        int xoffsetModules = 0;

        //Click stuff
        for (Category c : Category.values()) {
            for (Module m : Main.moduleManager.getModuleList()) {
                if (c.equals(m.getCategory()) && m.getType() < 1) {
                    if (c.opened) {
                        if (isInside(mouseX, mouseY, posX + xoffsetModules, posY + 1 + yoffsetModules, posX + 80 + xoffsetModules, posY + 15 + yoffsetModules)) {
                            if (mouseButton == 0) {
                                m.toggle();
                            }

                            if (mouseButton == 1) {
                                selectedModule = m;
                            }
                        }

                        yoffsetModules += 17.5;
                    }
                }
            }

            xoffsetModules += 80;
            yoffsetModules = 15;
        }

        //Implementing opening stuff soon...

        /*
        int cpoffset = 0;
        for (Category c : Category.values()) {
            if (isInside(mouseX, mouseY, posX, posY + 1, posX + cpoffset, posY + 12.5)) {
                if (mouseButton == 1) {

                    c.toggle();

                    Main.sendMessage(c.name);
                }
            }

            cpoffset += 80;
        }

         */

        //Settings stuff
        if (selectedModule == null) return;

        int offY = 0;

        for (Setting setting : selectedModule.settings) {
            if (isInside(mouseX, mouseY, (int) (posX2), (int) (posY2 + 35 + offY), (int) (posX2 + 150), (int) (posY2 + 35 + 17.5 + offY))) {
                if (mouseButton == 0) {
                    if (setting instanceof BooleanSetting) {
                        ((BooleanSetting) setting).toggle();
                    } else if (setting instanceof ModeSetting) {
                        ((ModeSetting) setting).cycle();
                    } else if (setting instanceof NumberSetting) {

                        double settingValue = ((NumberSetting) setting).getValue();
                        float minx = (float) posX2;
                        float maxx = (float) posX2 + 150;

                        if (mouseX <= minx) {
                            ((NumberSetting) setting).setValue(((NumberSetting) setting).getMinimum());
                        }

                        if (mouseX >= maxx) {
                            ((NumberSetting) setting).setValue(((NumberSetting) setting).getMaximum());
                        }
                    } else {
                        Main.sendMessage("?!");
                    }
                }

            }
            offY += 17.5;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
        dragging2 = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (selectedModule == null || keyCode <= 0) {
            return;
        }

        if (listeningForKey) {
            if (keyCode != 1) {
                selectedModule.setKey(keyCode);
                listeningForKey = false;
            } else if (keyCode == 1) {
                keyCode = 0;
                selectedModule.setKey(keyCode);
                listeningForKey = false;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();

        dragging = false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        FileUtils.saveAll();
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }
}