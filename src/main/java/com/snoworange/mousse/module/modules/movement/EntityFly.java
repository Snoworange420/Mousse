package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.GravityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityFly extends Module {
    public EntityFly() {
        super("EntityFly", "", Category.MOVEMENT, 0);
    }

    int speed = 1;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (!mc.player.isRiding() || mc.player.getRidingEntity() == null) {
                return;
            }

            mc.player.getRidingEntity().setVelocity(0, 0, 0);
            mc.player.getRidingEntity().rotationYaw = mc.player.rotationYaw;

            GravityUtils.moveEntityWithSpeed(mc.player.getRidingEntity(), speed, true);
        }
    }
}
