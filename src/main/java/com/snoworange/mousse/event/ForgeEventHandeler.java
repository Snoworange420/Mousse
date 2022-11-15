package com.snoworange.mousse.event;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgeEventHandeler {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        for (Module m : Main.moduleManager.modules) {
            if (m.isToggled() || m.isEnabled()) {
                m.onTick();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        for (Module m : Main.moduleManager.modules) {

            if (m.isToggled() || m.isEnabled()) {
                m.onPlayerTick();
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        for (Module m : Main.moduleManager.modules) {
            if ((m.isToggled() || m.isEnabled()) && event.getEntityLiving() instanceof EntityPlayer) {
                m.onUpdate();
            }
        }
    }
}
