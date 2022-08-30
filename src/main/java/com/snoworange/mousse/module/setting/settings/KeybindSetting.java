package com.snoworange.mousse.module.setting.settings;

import com.snoworange.mousse.module.setting.Setting;
import org.lwjgl.input.Keyboard;

public class KeybindSetting extends Setting implements com.lukflug.panelstudio.settings.KeybindSetting {
    public int code;

    public KeybindSetting(int code) {
        this.name = "KeyBind";
        this.code = code;
    }

    public int getKeyCode() {
        return this.code;
    }

    public void setKeyCode(int code) {
        this.code = code;
    }

    @Override
    public int getKey() {
        return code;
    }

    @Override
    public String getKeyName() {
        return Keyboard.getKeyName(code);
    }

    @Override
    public void setKey(int key) {
        code = key;
    }
}