package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;

public class Announcer extends Module {

    public Announcer() {
        super("Announcer", "announces things", Category.MISC, 0);
    }

    private static Announcer instance;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Announcer getInstance() {
        if (instance == null) {
            instance = new Announcer();
        }
        return instance;
    }
}
