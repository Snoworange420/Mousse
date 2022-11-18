package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class SelfBow extends Module {

    public SelfBow() {
        super("Selfbow", "shoots yourself when you use bow", Category.COMBAT);
    }

    int tick = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        tick = 0;
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

                tick++;

                if (tick >= 10) {

                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, -90, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                    mc.player.resetActiveHand();
                    tick = 0;
                }
            } else {
                tick = 0;
            }
        }
    }
}