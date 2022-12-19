package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.event.listeners.RenderPortalOverlayEvent;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.EntityUtils;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoPortal extends Module {

    public NoPortal() {
        super("NoPortal", "Cancels the nether portal color and stuff", Category.RENDER, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        GuiIngameForge.renderPortal = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        GuiIngameForge.renderPortal = true;
    }


    @SubscribeEvent
    public void onRenderPortalOverlay(RenderPortalOverlayEvent event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            event.setCanceled(true);
        }
    }
}
