package com.snoworange.mousse.event;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Module;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgeEventHandler extends Event {

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.toggled) {
                m.onUpdate();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.toggled) {
                m.onTick();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        for (Module m : Main.moduleManager.getModuleList()) {
            if (m.toggled) {
                m.onPlayerTick();
            }
        }
    }
}