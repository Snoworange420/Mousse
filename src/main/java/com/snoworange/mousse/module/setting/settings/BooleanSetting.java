package com.snoworange.mousse.module.setting.settings;

import com.lukflug.panelstudio.settings.Toggleable;
import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.setting.Setting;

public class BooleanSetting extends Setting implements Toggleable {
    public boolean enabled;

    public BooleanSetting(String name, Module parent, boolean enabled) {
        this.name = name;
        this.parent = parent;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        //if (Main.INSTANCE.saveLoad != null) {
        //    Main.INSTANCE.saveLoad.save();
        //}
    }

    public void toggle() {
        this.enabled = !this.enabled;

        //if (Main.INSTANCE.saveLoad != null) {
        //    Main.INSTANCE.saveLoad.save();
        //}
    }

    public boolean isOn() {
        return this.isEnabled();
    }
}