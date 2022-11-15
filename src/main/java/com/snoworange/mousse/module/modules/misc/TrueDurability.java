package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;

public class TrueDurability extends Module {

    public static TrueDurability instance;

    public TrueDurability() {
        super("TrueDurability", "displays the real durability on glitched items with negative damage value", Category.MISC);
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}