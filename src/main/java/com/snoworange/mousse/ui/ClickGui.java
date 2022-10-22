package com.snoworange.mousse.ui;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.misc.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class ClickGui extends GuiScreen {

    public double posX, posY, width, height, dragX, dragY;
    private double posX2, posY2, dragX2, dragY2;
    public boolean dragging;
    public boolean dragging2;
    public Category selectedCategory;
    private Module selectedModule;
    public int modeIndex;
    public boolean openedCombat = true;
    public boolean openedExploit = true;
    public boolean openedRender = true;
    public boolean openedMovement = true;
    public boolean openedPlayer = true;
    public boolean openedMisc = true;
    public boolean listeningForKey;

    //TODO: Clickgui rewrite

    public ClickGui() {
        dragging = false;
        dragging2 = false;
        posX = 100;
        posY = 50;
        posX2 = 650;
        posY2 = 50;
        width = posX + 150 * 2;
        height = height + 200;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        mc.fontRenderer.drawString("Mousse Client by Jonakip, Snoworange & Huub" ,0, 5, -1);

        /*
        mc.fontRenderer.drawString("PosX, PosY: " + posX + " " + posY, 100, 100, -1);
        mc.fontRenderer.drawString("MouseX, MouseY: " + mouseX + " " + mouseY, 100, 115, -1);
        mc.fontRenderer.drawString("PosX2, PosY2: " + posX2 + " " + posY2, 100, 135, -1);
        */

        if (dragging) {
            posX = mouseX - dragX;
            posY = mouseY - dragY;
        }

        if (dragging2) {
            posX2 = mouseX - dragX2;
            posY2 = mouseY - dragY2;
        }

        width = posX + 285;
        height = posY + 150;

        int offset = 0;

        offset = 0;
        for (Category category : Category.values()) {
            Gui.drawRect((int) posX + 1 + offset, (int) posY, (int) (posX + 80 + offset), (int) posY + 15, new Color(131, 141, 59).getRGB());
            mc.fontRenderer.drawString(category.name, (int) (posX + 4.5) + offset, (int) ((float) posY + 4.5), -1);
            offset += 80;
        }

        //Combat

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (openedCombat && m.getCategory() == Category.COMBAT) {
                Gui.drawRect((int) posX, (int) (posY + 1 + offset), (int) (posX + 80), (int) ((float) posY + 17.5 + offset), m.isToggled() ? new Color(131, 141, 59).getRGB() : new Color(40, 40, 40).getRGB());
                mc.fontRenderer.drawString(m.getName(), (int) ((float) posX + 2.5), (int) ((float) posY + 6.5) + offset, new Color(170, 170, 170).getRGB());
                offset += 17.5;
            }
        }

        //Exploit

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (openedExploit && m.getCategory() == Category.EXPLOIT) {
                Gui.drawRect((int) posX + 80, (int) (posY + 1 + offset), (int) (posX + 160), (int) ((float) posY + 17.5 + offset), m.isToggled() ? new Color(131, 141, 59).getRGB() : new Color(40, 40, 40).getRGB());
                mc.fontRenderer.drawString(m.getName(), (int) ((float) posX + 82.5), (int) ((float) posY + 6.5) + offset, new Color(170, 170, 170).getRGB());
                offset += 17.5;
            }
        }

        //Render

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (openedRender && m.getCategory() == Category.RENDER) {
                Gui.drawRect((int) posX + 160, (int) (posY + 1 + offset), (int) (posX + 240), (int) ((float) posY + 17.5 + offset), m.isToggled() ? new Color(131, 141, 59).getRGB() : new Color(40, 40, 40).getRGB());
                mc.fontRenderer.drawString(m.getName(), (int) ((float) posX + 162.5), (int) ((float) posY + 6.5) + offset, new Color(170, 170, 170).getRGB());
                offset += 17.5;
            }
        }

        //Movement

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (openedMovement && m.getCategory() == Category.MOVEMENT) {
                Gui.drawRect((int) posX + 240, (int) (posY + 1 + offset), (int) (posX + 320), (int) ((float) posY + 17.5 + offset), m.isToggled() ? new Color(131, 141, 59).getRGB() : new Color(40, 40, 40).getRGB());
                mc.fontRenderer.drawString(m.getName(), (int) ((float) posX + 242.5), (int) ((float) posY + 6.5) + offset, new Color(170, 170, 170).getRGB());
                offset += 17.5;
            }
        }

        //Player

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (openedPlayer && m.getCategory() == Category.PLAYER) {
                Gui.drawRect((int) posX + 320, (int) (posY + 1 + offset), (int) (posX + 400), (int) ((float) posY + 17.5 + offset), m.isToggled() ? new Color(131, 141, 59).getRGB() : new Color(40, 40, 40).getRGB());
                mc.fontRenderer.drawString(m.getName(), (int) ((float) posX + 322.5), (int) ((float) posY + 6.5) + offset, new Color(170, 170, 170).getRGB());
                offset += 17.5;
            }
        }

        //Misc

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (openedMisc && m.getCategory() == Category.MISC) {
                Gui.drawRect((int) posX + 400, (int) (posY + 1 + offset), (int) (posX + 480), (int) ((float) posY + 17.5 + offset), m.isToggled() ? new Color(131, 141, 59).getRGB() : new Color(40, 40, 40).getRGB());
                mc.fontRenderer.drawString(m.getName(), (int) ((float) posX + 402.5), (int) ((float) posY + 6.5) + offset, new Color(170, 170, 170).getRGB());
                offset += 17.5;
            }
        }

        //SettingsManager
        Gui.drawRect((int) posX2, (int) posY2, (int) (posX2 + 150), (int) (posY2 + 250), new Color(40, 40, 40).getRGB());
        Gui.drawRect((int) posX2, (int) posY2, (int) (posX2 + 150), (int) (posY2 + 17.5), new Color(131, 141, 59).getRGB());

        if (selectedModule == null) {
            return;
        }

        mc.fontRenderer.drawString(selectedModule.getName(), (int) (posX2 + 5), (int) (posY2 + 5), -1);

        if (!listeningForKey) {
            mc.fontRenderer.drawString("Bind: " + getKeyDisplayString(selectedModule.getKey()), (int) (posX2 + 5), (int) (posY2 + 5 + 17.5), -1);
        } else {
            mc.fontRenderer.drawString("Listening... ", (int) (posX2 + 5), (int) (posY2 + 5 + 17.5), -1);
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
        if (isInside(mouseX, mouseY, posX, posY - 12.5, width + 160, posY + 12.5) && mouseButton == 0) {
            dragging = true;
            dragX = mouseX - posX;
            dragY = mouseY - posY;
        }

        if (isInside2(mouseX, mouseY, posX2, posY2, posX2 + 250, posY2 + 250) && mouseButton == 0) {
            dragging2 = true;
            dragX2 = mouseX - posX2;
            dragY2 = mouseY - posY2;
        }

        //Settingsmanager

        if (isInside2(mouseX, mouseY, posX2, posY2 + 17.5, posX2 + 150, posY2 + 30)) {
            if (!listeningForKey) {
                listeningForKey = true;
            } else if (listeningForKey) {
                listeningForKey = false;
            }
        }

        int offset = 0;

        //Combat

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.getCategory() == Category.COMBAT) {
                if (isInside(mouseX, mouseY, posX, posY + 1 + offset, posX + 80, posY + 15 + offset)) {
                    if (mouseButton == 0) {
                        m.toggle();
                    }

                    if (mouseButton == 1) {
                        selectedModule = m;
                    }
                }
                offset += 17.5;
            }
        }

        //Exploit

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.getCategory() == Category.EXPLOIT) {
                if (isInside(mouseX, mouseY, posX + 80, posY + 1 + offset, posX + 160, posY + 15 + offset)) {
                    if (mouseButton == 0) {
                        m.toggle();
                    }

                    if (mouseButton == 1) {
                        selectedModule = m;
                    }
                }
                offset += 17.5;
            }
        }

        //Render

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.getCategory() == Category.RENDER) {
                if (isInside(mouseX, mouseY, posX + 160, posY + 1 + offset, posX + 240, posY + 15 + offset)) {
                    if (mouseButton == 0) {
                        m.toggle();
                    }

                    if (mouseButton == 1) {
                        selectedModule = m;
                    }
                }
                offset += 17.5;
            }
        }

        //Movement

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.getCategory() == Category.MOVEMENT) {
                if (isInside(mouseX, mouseY, posX + 240, posY + 1 + offset, posX + 320, posY + 15 + offset)) {
                    if (mouseButton == 0) {
                        m.toggle();
                    }

                    if (mouseButton == 1) {
                        selectedModule = m;
                    }
                }
                offset += 17.5;
            }
        }

        //Player

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.getCategory() == Category.PLAYER) {
                if (isInside(mouseX, mouseY, posX + 320, posY + 1 + offset, posX + 400, posY + 15 + offset)) {
                    if (mouseButton == 0) {
                        m.toggle();
                    }

                    if (mouseButton == 1) {
                        selectedModule = m;
                    }
                }
                offset += 17.5;
            }
        }

        //Misc

        offset = 15;
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.getCategory() == Category.MISC) {
                if (isInside(mouseX, mouseY, posX + 400, posY + 1 + offset, posX + 480, posY + 15 + offset)) {
                    if (mouseButton == 0) {
                        m.toggle();
                    }

                    if (mouseButton == 1) {
                        selectedModule = m;
                    }
                }
                offset += 17.5;
            }
        }

        offset = 0;

        //Combat
        if (isInside(mouseX, mouseY, posX, posY + 1 + offset, posX + 75, posY + 12.5 + offset)) {
            if (mouseButton == 1 || mouseButton == 0) {

                selectedCategory = Category.COMBAT;

                if (mouseButton == 1) {
                    if (!openedCombat) {
                        openedCombat = true;
                    } else if (openedCombat) {
                        openedCombat = false;
                    }
                }
            }
        }

        //Exploit
        if (isInside(mouseX, mouseY, posX + 80, posY + 1 + offset, posX + 155, posY + 12.5 + offset)) {
            if (mouseButton == 1 || mouseButton == 0) {

                selectedCategory = Category.EXPLOIT;

                if (mouseButton == 1) {
                    if (!openedExploit) {
                        openedExploit = true;
                    } else if (openedExploit) {
                        openedExploit = false;
                    }
                }
            }
        }

        //Render
        if (isInside(mouseX, mouseY, posX + 155, posY + 1 + offset, posX + 235, posY + 12.5 + offset)) {
            if (mouseButton == 1 || mouseButton == 0) {

                selectedCategory = Category.RENDER;


                if (mouseButton == 1) {
                    if (!openedRender) {
                        openedRender = true;
                    } else if (openedRender) {
                        openedRender = false;
                    }
                }
            }
        }

        //Movement
        if (isInside(mouseX, mouseY, posX + 235, posY + 1 + offset, posX + 315, posY + 12.5 + offset)) {
            if (mouseButton == 1 || mouseButton == 0) {

                selectedCategory = Category.MOVEMENT;

                if (mouseButton == 1) {
                    if (!openedMovement) {
                        openedMovement = true;
                    } else if (openedMovement) {
                        openedMovement = false;
                    }
                }
            }
        }

        //Player
        if (isInside(mouseX, mouseY, posX + 315, posY + 1 + offset, posX + 395, posY + 12.5 + offset)) {
            if (mouseButton == 1 || mouseButton == 0) {

                selectedCategory = Category.PLAYER;

                if (mouseButton == 1) {
                    if (!openedPlayer) {
                        openedPlayer = true;
                    } else if (openedPlayer) {
                        openedPlayer = false;
                    }
                }
            }
        }

        //Misc
        if (isInside(mouseX, mouseY, posX + 395, posY + 1 + offset, posX + 475, posY + 12.5 + offset)) {
            if (mouseButton == 1 || mouseButton == 0) {

                selectedCategory = Category.MISC;

                if (mouseButton == 1) {
                    if (!openedMisc) {
                        openedMisc = true;
                    } else if (openedMisc) {
                        openedMisc = false;
                    }
                }
            }
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

        if (selectedModule == null) {
            return;
        }

        if (keyCode <= 0) {
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
        dragging2 = false;
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    public boolean isInside2(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    public ScaledResolution getScaledRes() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

}
