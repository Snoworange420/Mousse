package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.*;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SelfBow extends Module {

    public SelfBow() {
        super("Selfbow", "shoots yourself when you use bow", Category.COMBAT);
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
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.player == null || mc.world == null) return;

            if (mc.player.inventory.getCurrentItem().getItem().equals(Items.BOW)) {
                if (mc.player.inventory.getCurrentItem().getItemUseAction() == EnumAction.BOW) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, -90, mc.player.onGround));
                }
            }
        }
    }
}
