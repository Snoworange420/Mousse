package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.util.render.ParticleUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Particles extends Module {

    public Particles() {
        super("Particles", "draws particles on Gui screens", Category.RENDER);
    }

    BooleanSetting clearOnGuiSwitch;

    @Override
    public void init() {
        super.init();

        clearOnGuiSwitch = new BooleanSetting("Clear Particles on GUI Switch", true);
        addSetting(clearOnGuiSwitch);
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
    public void onGui(GuiOpenEvent event) {
        if (this.toggled && mc.world != null) {
            if (clearOnGuiSwitch.enable) {
                ParticleUtils.reset();
            }
        }
    }
}
