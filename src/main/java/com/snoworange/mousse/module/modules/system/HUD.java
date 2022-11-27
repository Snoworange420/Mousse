package com.snoworange.mousse.module.modules.system;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HUD extends Module {

    public HUD() {
        super("HUD", "aww", Category.SYSTEM, 2, true);
    }

    public static BooleanSetting watermark;
    public static BooleanSetting arraylist;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void init() {
        super.init();

        watermark = new BooleanSetting("Watermark", true);
        arraylist = new BooleanSetting("ArrayList", true);

        addSetting(watermark, arraylist);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

    }
}
