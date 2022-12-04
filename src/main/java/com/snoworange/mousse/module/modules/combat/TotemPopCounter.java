package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.event.listeners.TotemPopEvent;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TotemPopCounter extends Module {

    public TotemPopCounter() {
        super("TotemPopNotify", "btich", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        if (this.toggled) {
            Main.sendMessage(event.getPlayer().getName() + " popped a totem!");
        }
    }
}
