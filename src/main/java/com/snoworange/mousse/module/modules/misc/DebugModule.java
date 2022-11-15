package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;

public class DebugModule extends Module {

    public DebugModule() {
        super("DebugModule", "idk", Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onTick() {
        Main.sendMessage("hello from debug module!");
        Main.sendMessage("1");

        disable();
    }
}
