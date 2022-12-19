package com.snoworange.mousse.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.snoworange.mousse.Main;
import com.snoworange.mousse.setting.Setting;
import com.snoworange.mousse.util.chat.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
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
    public List<Setting<?>> settings = new ArrayList<Setting<?>>();

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

    public Module(String name, String description, Category category, int type, boolean toggled) {
        super();
        this.name = name;
        this.description = description;
        this.key = 0;
        this.category = category;
        this.toggled = toggled;
        init();
    }

    public int getType() {
        return this.type;
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

    public boolean isEnabled() {
        return toggled;
    }

    public boolean isDisabled() {
        return !toggled;
    }

    public boolean hasSettings() {
        return this.settings.size() > 0;
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

        if (Main.moduleManager.getModule("Announcer").isToggled() && !Objects.equals(this.name, "ShulkerPeek") && !Objects.equals(this.name, "GuiTheme")) {

            if (mc.world != null) {
                ChatUtils.sendMessage("{}" + TextFormatting.GREEN + " enabled.", this.name);
            }
        }
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.register(this);

        if (Main.moduleManager.getModule("Announcer").isToggled() && !Objects.equals(this.name, "ShulkerPeek") && !Objects.equals(this.name, "GuiTheme")) {

            if (mc.world != null) {
                ChatUtils.sendMessage("{}" + TextFormatting.RED + " disabled.", this.name);
            }
        }
    }

    public void onTick() {

    }

    public void onPlayerTick() {

    }

    public void onFastTick() {

    }

    public void onUpdate() {

    }

    public void onRender2d() {

    }

    public void onRender3d() {

    }

    public void enable() {
        this.onEnable();
        this.toggled = true;
    }

    public void disable() {
        this.onDisable();
        this.toggled = false;
    }

    public void init() {

    }

    public Setting getSettingByName(final String s) {
        return this.settings.stream().findFirst().orElse(null);
    }

    public void addSetting(Setting... settings) {
        this.settings.addAll(Arrays.<Setting<?>>asList(settings));
    }
}