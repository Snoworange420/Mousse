package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoPortal extends Module {

    public NoPortal() {
        super("NoPortal", "Cancels the nether portal color", Category.RENDER, 0);
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
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.toggled) {
            if (mc.world == null || mc.player == null) return;

            GuiIngameForge.renderPortal = false;
        }
    }
}
