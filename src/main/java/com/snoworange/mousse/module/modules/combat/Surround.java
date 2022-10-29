package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.block.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Surround extends Module {

    public boolean jumpDisable = true;
    public BlockPos blockPos = null;
    public boolean rotate = true;

    public Surround() {
        super("Surround", "", Category.COMBAT, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.world == null || mc.player == null) return;

        blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            if (jumpDisable && mc.gameSettings.keyBindJump.isPressed()) {

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                disable();
            }
        }
    }

    private boolean placeBlock(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        EnumFacing side = BlockUtils.getPlaceableSide(pos);

        if (side == null){
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!BlockUtils.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add((new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5)));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();


        if (BlockUtils.blackList.contains(neighbourBlock) || BlockUtils.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        if (rotate) {
            BlockUtils.faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);

        return true;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            int obsidianIndex = -1;

            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                    obsidianIndex = i;
                }
            }

            if (obsidianIndex == -1) {
                Main.sendMessage("Obsidian not found in your hotbar!");
                return;
            }

            //Place obby
            if (mc.world.getBlockState(blockPos.south()).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 0, 1))).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                //mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                placeBlock(blockPos.south());
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                Main.sendMessage("Placing obsidian > south");
            }

            if (mc.world.getBlockState(blockPos.west()).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 0, 0))).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                //mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                placeBlock(blockPos.west());
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                Main.sendMessage("Placing obsidian > west");
            }

            if (mc.world.getBlockState(blockPos.north()).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 0, -1))).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                //mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                placeBlock(blockPos.north());
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                Main.sendMessage("Placing obsidian > north");
            }

            if (mc.world.getBlockState(blockPos.east()).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 0, 0))).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                //mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                placeBlock(blockPos.east());
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                Main.sendMessage("Placing obsidian > east");
            }

            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }
}
