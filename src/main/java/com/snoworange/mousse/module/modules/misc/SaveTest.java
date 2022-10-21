package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.misc.FileUtils;

public class SaveTest extends Module {

    public SaveTest() {
        super("SaveTest", "", Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        FileUtils.saveBinds(FileUtils.mousse);
        Main.sendMessage("Saved keybinds.");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        FileUtils.loadBinds(FileUtils.mousse);
        Main.sendMessage("Loaded keybinds.");
    }
}
