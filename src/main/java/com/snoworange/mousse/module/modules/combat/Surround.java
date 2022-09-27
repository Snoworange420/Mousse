package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
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

    public Surround() {
        super("Surround", "", Category.COMBAT, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
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
            if (jumpDisable && mc.gameSettings.keyBindJump.isPressed()) {

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                disable();
            }
        }
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
                mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos.south(), EnumFacing.DOWN, new Vec3d(blockPos.south().getX(), blockPos.south().getY(), blockPos.south().getZ()), EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                Main.sendMessage("Placing obsidian > south");
            }

            if (mc.world.getBlockState(blockPos.west()).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 0, 0))).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos.west(), EnumFacing.DOWN, new Vec3d(blockPos.west().getX(), blockPos.west().getY(), blockPos.west().getZ()), EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                Main.sendMessage("Placing obsidian > west");
            }

            if (mc.world.getBlockState(blockPos.north()).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 0, -1))).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos.north(), EnumFacing.DOWN, new Vec3d(blockPos.north().getX(), blockPos.north().getY(), blockPos.north().getZ()), EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                Main.sendMessage("Placing obsidian > north");
            }

            if (mc.world.getBlockState(blockPos.east()).getBlock() instanceof BlockAir && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 0, 0))).isEmpty()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos.east(), EnumFacing.DOWN, new Vec3d(blockPos.east().getX(), blockPos.east().getY(), blockPos.east().getZ()), EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                Main.sendMessage("Placing obsidian > east");
            }
        }
    }
}
