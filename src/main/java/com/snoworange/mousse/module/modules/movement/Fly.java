package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.GravityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fly extends Module {

    public Fly() {
        super("Fly", "Allows you to fly like a bird", Category.MOVEMENT, 0);
    }

    float speed = 1f;
    public boolean creative;
    public boolean velocity = true;

    @Override
    public void onEnable() {
        super.onEnable();
        if (creative) {

            if (mc.world == null || mc.player == null) return;

            mc.player.capabilities.allowFlying = true;
            mc.player.capabilities.setFlySpeed(speed / 20.0f);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.world == null || mc.player == null) return;

        if (!mc.player.isCreative()) {
            mc.player.capabilities.allowFlying = false;
        }
        mc.player.capabilities.isFlying = false;
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.world == null || mc.player == null) return;

            if (velocity) {
                mc.player.setVelocity(0.0, 0.0, 0.0);
                GravityUtils.moveEntityWithSpeed(mc.player, speed, true);
            }
        }
    }
}
