package com.snoworange.mousse.module.setting;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Module;

import java.util.ArrayList;

/**
 * @author SrgantMooMoo
 * @since 4/2/2022
 */

public class SettingManager {
    private final ArrayList<Setting> settings = new ArrayList<>();

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public ArrayList<Setting> getSettingsByMod(Module mod) {
        ArrayList<Setting> out = new ArrayList<Setting>();
        for(Setting s : getSettings()) {
            if(s.parent.equals(mod)) {
                out.add(s);
            }
        }
        return out;
    }

    public Setting getSettingByName(Module mod, String name) {
        for (Module m : Main.moduleManager.modules) {
            for (Setting set : m.settings) {
                if (set.name.equalsIgnoreCase(name) && set.parent == mod) {
                    return set;
                }
            }
        }

        System.err.println("[Mousse] Error setting not found: '" + name +"'!");
        return null;
    }
}
