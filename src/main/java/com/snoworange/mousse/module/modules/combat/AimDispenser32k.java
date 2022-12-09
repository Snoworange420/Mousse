package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.block.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AimDispenser32k extends Module {

    public AimDispenser32k() {
        super("AimDispenser32k", "old auto32k lol", Category.COMBAT);
    }

    BlockPos obsidianPos;
    public int stage = 0;

    @Override
    public void onEnable() {
        super.onEnable();

        obsidianPos = null;
        stage = 0;

        if (mc.objectMouseOver == null || mc.objectMouseOver.sideHit == null) {
            Main.sendMessage("Unable to enable Auto32k! Please aim at a block to enable.");
            disable();
        }

        obsidianPos = mc.objectMouseOver.getBlockPos().offset(mc.objectMouseOver.sideHit);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.toggled) {

            int hopperIndex = -1;
            int redstoneIndex = -1;
            int dispenserIndex = -1;
            int obsidianIndex = -1;
            int shulkerIndex = -1;
            int enchantedSwordIndex = -1;

            for (int i = 0; i < 9; i++) {

                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.HOPPER))) {
                    hopperIndex = i;
                }

                if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))) {
                    redstoneIndex = i;
                }

                if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.DISPENSER))) {
                    dispenserIndex = i;
                }

                if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                    obsidianIndex = i;
                }

                if (itemStack.getItem() instanceof ItemShulkerBox) {
                    shulkerIndex = i;
                }

                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    enchantedSwordIndex = i;
                }
            }

            if (stage == 0 && (hopperIndex == -1 || shulkerIndex == -1 || dispenserIndex == -1 ||  redstoneIndex == -1 || obsidianIndex == -1)) {

                if (hopperIndex == -1) {
                    Main.sendMessage("Missing hopper in your hotbar!");
                }

                if (shulkerIndex == -1) {
                    Main.sendMessage("Missing shulker box in your hotbar!");
                }

                if (obsidianIndex == -1) {
                    Main.sendMessage("Missing obsidian in your hotbar!");
                }

                if (dispenserIndex == -1) {
                    Main.sendMessage("Missing dispenser in your hotbar!");
                }

                if (redstoneIndex == -1) {
                    Main.sendMessage("Missing redstone block in your hotbar!");
                }

                disable();
                return;
            }

            if (stage == 0) {
                update(obsidianIndex);
                placeBlock(obsidianPos);

                update(dispenserIndex);
                placeBlock(obsidianPos.up());

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.playerController.processRightClickBlock(mc.player, mc.world, obsidianPos.up(), EnumFacing.UP, new Vec3d(obsidianPos.up().getX(), obsidianPos.up().getY(), obsidianPos.up().getZ()), EnumHand.MAIN_HAND);

                stage = 1;
            }

            //Swap shulker box
            if (stage == 1 && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser) {

                if (mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty()) mc.playerController.windowClick(mc.player.openContainer.windowId, mc.player.openContainer.inventorySlots.get(0).slotNumber, shulkerIndex, ClickType.SWAP, mc.player);

                if (mc.player.openContainer.inventorySlots.get(0).getStack().getItem() instanceof ItemShulkerBox) {
                    mc.player.closeScreen();
                    stage = 2;
                }
            }

            if (stage == 2) {

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                update(redstoneIndex);
                placeBlock(obsidianPos.up(2));

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                stage = 3;
            }

            if (stage == 3) {
                if (mc.world.getBlockState(obsidianPos.up().north()).getBlock() instanceof BlockShulkerBox) {
                    update(hopperIndex);
                    placeBlock(obsidianPos.north());

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                    mc.playerController.processRightClickBlock(mc.player, mc.world, obsidianPos.north(), EnumFacing.UP, new Vec3d(obsidianPos.north().getX(), obsidianPos.north().getY(), obsidianPos.north().getZ()), EnumHand.MAIN_HAND);

                    stage = 4;
                }

                if (mc.world.getBlockState(obsidianPos.up().east()).getBlock() instanceof BlockShulkerBox) {
                    update(hopperIndex);
                    placeBlock(obsidianPos.east());

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                    mc.playerController.processRightClickBlock(mc.player, mc.world, obsidianPos.east(), EnumFacing.UP, new Vec3d(obsidianPos.east().getX(), obsidianPos.east().getY(), obsidianPos.east().getZ()), EnumHand.MAIN_HAND);

                    stage = 4;
                }

                if (mc.world.getBlockState(obsidianPos.up().south()).getBlock() instanceof BlockShulkerBox) {
                    update(hopperIndex);
                    placeBlock(obsidianPos.south());

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                    mc.playerController.processRightClickBlock(mc.player, mc.world, obsidianPos.south(), EnumFacing.UP, new Vec3d(obsidianPos.south().getX(), obsidianPos.south().getY(), obsidianPos.south().getZ()), EnumHand.MAIN_HAND);

                    stage = 4;
                }

                if (mc.world.getBlockState(obsidianPos.up().west()).getBlock() instanceof BlockShulkerBox) {
                    update(hopperIndex);
                    placeBlock(obsidianPos.west());

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                    mc.playerController.processRightClickBlock(mc.player, mc.world, obsidianPos.west(), EnumFacing.UP, new Vec3d(obsidianPos.west().getX(), obsidianPos.west().getY(), obsidianPos.west().getZ()), EnumHand.MAIN_HAND);

                    stage = 4;
                }
            }

            if (stage == 4) {

                if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerHopper) {
                    if (enchantedSwordIndex == -1 && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {
                        for (int i = 0; i < 5; i++) {
                            if (mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= Short.MAX_VALUE) {
                                enchantedSwordIndex = i;
                                break;
                            }
                        }
                    }

                    int airindex = -1;

                    for (int i = 0; i < 9; i++) {

                        ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                        if (itemStack.getItem() instanceof ItemAir) {
                            airindex = i;
                        }
                    }

                    if (airindex != -1) {
                        update(airindex);
                    }

                    if (enchantedSwordIndex != -1) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                        Main.sendMessage("32k found in slot " + enchantedSwordIndex);

                        stage = 5;
                    }
                }
            }

            if (stage == 5) {
                disable();
            }
        }
    }

    public void update(int index) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(index));
        mc.player.inventory.currentItem = index;
        mc.playerController.updateController();
    }

    private boolean placeBlock(BlockPos pos) {

        if (mc.world == null || mc.player == null) disable();

        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        EnumFacing side = BlockUtils.getPlaceableSide(pos);

        if (side == null) {
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

        BlockUtils.faceVectorPacketInstant(hitVec);

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);

        return true;
    }
}
