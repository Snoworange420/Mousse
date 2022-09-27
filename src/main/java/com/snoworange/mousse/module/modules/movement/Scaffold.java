package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Scaffold extends Module {

    public Scaffold() {
        super("Scaffold", "", Category.MOVEMENT, 0);
    }
    public BlockPos blockPos = null;

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

            blockPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);

            int blockIndex = -1;

            for (int i = 8; i > -1; i--) {
                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                if (itemStack.getItem() instanceof ItemBlock) {
                    blockIndex = i;
                }
            }

            if (blockIndex == -1) return;

            if (mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir) {
                placeBlock(blockIndex, blockPos, EnumFacing.UP, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            }
        }
    }

    public void placeBlock(int blockIndex, BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        if (mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 0, 0))).isEmpty()) {

            if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemBlock && !(mc.player.inventory.currentItem == blockIndex)) {
                blockIndex = mc.player.inventory.currentItem;
            }

            mc.player.connection.sendPacket(new CPacketHeldItemChange(blockIndex));
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

    }
}
