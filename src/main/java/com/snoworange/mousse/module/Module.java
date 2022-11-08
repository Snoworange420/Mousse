package com.snoworange.mousse.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.snoworange.mousse.Main;
import com.snoworange.mousse.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Module {

    public String name, description;
    public int key;
    private Category category;
    public boolean toggled;

    public int type;
    public List<Setting> settings = new ArrayList<Setting>();

    public Minecraft mc = Minecraft.getMinecraft();

    public Module(String name, String description, Category category) {
        super();
        this.name = name;
        this.description = description;
        this.key = 0;
        this.category = category;
        this.toggled = false;
        init();
    }

    public Module(String name, String description, Category category, int type) {
        super();
        this.name = name;
        this.description = description;
        this.key = 0;
        this.category = category;
        this.toggled = false;
        init();
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        if (toggled) {
            if (!this.toggled) {
                toggle();
            } else {
                if (this.toggled) {
                    toggle();
                }
            }
        }
    }

    public final void toggle() {
        if (toggled) {
            onDisable();
            toggled = false;
        } else {
            onEnable();
            toggled = true;
        }
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        if (Main.moduleManager.getModule("Announcer").isToggled() && !Objects.equals(this.name, "ShulkerPeek")) {
            Main.sendMessage(this.name + ChatFormatting.GREEN + " enabled." + ChatFormatting.RESET);
        }
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.register(this);
        if (Main.moduleManager.getModule("Announcer").isToggled() && !Objects.equals(this.name, "ShulkerPeek")) {
            Main.sendMessage(this.name + ChatFormatting.RED + " disabled." + ChatFormatting.RESET);
        }
    }

    public void enable() {
        this.onEnable();
        this.toggled = true;
    }

    public void disable() {
        this.onDisable();
        this.toggled = false;
    }

    public void onUpdate() {

    }

    public void onTick() {

    }

    public void onPlayerTick() {

    }

    public void init() {

    }

    public boolean isNull() {
        if (mc.player == null || mc.world == null) {
            return true;
        }

        return false;
    }

    public void addSetting(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }
}
