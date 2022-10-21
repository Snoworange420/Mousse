package com.snoworange.mousse.module.modules.player;

import com.snoworange.mousse.event.listeners.AddCollisionBoxToListEvent;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.EntityUtils;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Jesus extends Module {

    public Jesus() {
        super("Jesus", "allows you to walk on water, Jesus used this hack ~2000 years ago.", Category.PLAYER, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private static boolean isAboveBlock(Entity entity, BlockPos pos) {
        return entity.posY >= pos.getY();
    }

    @SubscribeEvent
    public void onCollision(AddCollisionBoxToListEvent event) {
        if (mc.player != null && (event.getBlock() instanceof BlockLiquid) && (EntityUtils.isDrivenByPlayer(event.getEntity()) || event.getEntity() == mc.player) && !(event.getEntity() instanceof EntityBoat) && !mc.player.isSneaking() && mc.player.fallDistance < 3 && !EntityUtils.isInLiquid() && (EntityUtils.isAboveWater(mc.player, false) || EntityUtils.isAboveWater(mc.player.getRidingEntity(), false)) && isAboveBlock(mc.player, event.getPos())) {
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D).offset(event.getPos());
            if (event.getEntityBox().intersects(axisalignedbb)) event.getCollidingBoxes().add(axisalignedbb);
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            if (EntityUtils.isInLiquid() && !mc.player.isSneaking()) {
                mc.player.motionY = 0.1;
                if (mc.player.getRidingEntity() != null && !(mc.player.getRidingEntity() instanceof EntityBoat)) {
                    mc.player.getRidingEntity().motionY = 0.3;
                }
            }
        }
    }
}
