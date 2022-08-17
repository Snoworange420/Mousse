package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.EntityUtils;
import com.snoworange.mousse.util.GravityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fly extends Module {

    public Fly() {
        super("Fly", "Prevents getting fall damage by sending packets", Category.MOVEMENT, 0);
    }

    float speed = 1f;
    public boolean creative;
    public boolean velocity;

    @Override
    public void onEnable() {
        super.onEnable();
        if (creative) {
            mc.player.capabilities.allowFlying = true;
            mc.player.capabilities.setFlySpeed(speed / 20.0f);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.capabilities.allowFlying = false;
        mc.player.capabilities.isFlying = false;
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (velocity) {
                mc.player.setVelocity(0.0, 0.0, 0.0);
                GravityUtils.moveEntityWithSpeed(mc.player, speed, true);
            }
        }
    }
}
