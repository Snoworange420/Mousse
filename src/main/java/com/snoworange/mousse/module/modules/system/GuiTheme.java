package com.snoworange.mousse.module.modules.system;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GuiTheme extends Module {

    public GuiTheme() {
        super("GuiTheme", "Theme of a gui", Category.SYSTEM, 2);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Main.themeManager.cycle();
        Main.sendMessage("Current GUI Theme: " + Main.themeManager.getCurrentTheme().getName());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.toggled) {
            disable();
        }
    }
}
