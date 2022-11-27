package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.util.block.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Dispenser32kRewrite extends Module {

    //TODO: this is broken why tf


    public int stage = 0;
    public BlockPos basePos;
    public EnumFacing dispenserDirection;

    BooleanSetting silent;

    public Dispenser32kRewrite() {
        super("Dispenser32kNew", "rewrite", Category.COMBAT);
    }

    @Override
    public void init() {
        super.init();

        silent = new BooleanSetting("SilentSwap", true);
        addSetting(silent);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        stage = 0;
        basePos = null;
        dispenserDirection = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.isEnabled()) {

            if (mc.player == null || mc.world == null) return;

            int hopperIndex = -1;
            int redstoneIndex = -1;
            int dispenserIndex = -1;
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

                if (itemStack.getItem() instanceof ItemShulkerBox) {
                    shulkerIndex = i;
                }

                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    enchantedSwordIndex = i;
                }
            }

            if (stage == 0 && (hopperIndex == -1 || shulkerIndex == -1 || dispenserIndex == -1 ||  redstoneIndex == -1)) {

                if (hopperIndex == -1) {
                    Main.sendMessage("Missing hopper in your hotbar!");
                }

                if (shulkerIndex == -1) {
                    Main.sendMessage("Missing shulker box in your hotbar!");
                }

                if (dispenserIndex == -1) {
                    Main.sendMessage("Missing dispenser in your hotbar!");
                }

                if (redstoneIndex == -1) {
                    Main.sendMessage("Missing redstone block in your hotbar!");
                }

                disable();
            }

            if (stage == 0) {

            }
            
            if (stage == 1) {

                update(dispenserIndex);
                placeBlock(basePos);

                float yaw = 0.0f;

                if (dispenserDirection == EnumFacing.NORTH) {
                    yaw = -179.0f;
                } else if (dispenserDirection == EnumFacing.EAST) {
                    yaw = -89.0f;
                } else if (dispenserDirection == EnumFacing.SOUTH) {
                    yaw = 1.0f;
                } else if (dispenserDirection == EnumFacing.WEST) {
                    yaw = 91.0f;
                } else {
                    Main.sendMessage("??????");
                }

                placeBlock(basePos.up());

                Main.sendMessage("Dispenser Direction: " + dispenserDirection.getName());
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, mc.player.rotationPitch, mc.player.onGround));

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.playerController.processRightClickBlock(mc.player, mc.world, basePos.up(), EnumFacing.UP, new Vec3d(basePos.up().getX(), basePos.up().getY(), basePos.up().getZ()), EnumHand.MAIN_HAND);

                stage = 2;
            }

            if (stage == 2) {
                if (mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty()) mc.playerController.windowClick(mc.player.openContainer.windowId, mc.player.openContainer.inventorySlots.get(0).slotNumber, shulkerIndex, ClickType.SWAP, mc.player);

                if (mc.player.openContainer.inventorySlots.get(0).getStack().getItem() instanceof ItemShulkerBox) {
                    mc.player.closeScreen();
                    stage = 3;
                }
            }

            if (stage == 3) {
                update(redstoneIndex);
                placeBlock(basePos.up(2));

                stage = 4;
            }

            if (stage == 4) {
                disable();
            }
        }
    }

    public boolean searchBestPlacement(EnumFacing direction) {
        
        BlockPos closestBlockPos = null;

        for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY, mc.player.posZ + 3))) {

            if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 4
                    && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, 0, 0))).isEmpty()
                    && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty()
                    && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty()
                    && mc.world.getBlockState(blockPos).isFullBlock()
                    && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir
                    && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir
                    && /* --> */ mc.world.getBlockState(doStuffWithBlockPosDirection(blockPos, direction)).getBlock() instanceof BlockAir
                    && mc.world.getBlockState(doStuffWithBlockPosDirection(blockPos, direction).up()).getBlock() instanceof BlockAir
                    && !(mc.world.getBlockState(doStuffWithBlockPosDirection(blockPos, direction).north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(doStuffWithBlockPosDirection(blockPos, direction).west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(doStuffWithBlockPosDirection(blockPos, direction).south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(doStuffWithBlockPosDirection(blockPos, direction).east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(doStuffWithBlockPosDirection(blockPos, direction).down()).getBlock().equals(Blocks.REDSTONE_BLOCK)))
            {
                
                closestBlockPos = blockPos;
            }

            if (closestBlockPos != null) {
                basePos = closestBlockPos;
                dispenserDirection = direction.getOpposite();

                return true;
            }
        }

        return false;
    }

    public BlockPos doStuffWithBlockPosDirection(BlockPos blockPos, EnumFacing direction) {

        if (direction.equals(EnumFacing.NORTH)) {
            return blockPos.north();
        }

        if (direction.equals(EnumFacing.EAST)) {
            return blockPos.east();
        }

        if (direction.equals(EnumFacing.SOUTH)) {
            return blockPos.south();
        }

        if (direction.equals(EnumFacing.WEST)) {
            return blockPos.west();
        }

        if (direction.equals(EnumFacing.DOWN)) {
            return blockPos.down();
        }

        if (direction.equals(EnumFacing.UP)) {
            return blockPos.up();
        }

        return null;
    }

    public void update(int index) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(index));

        if (!silent.isEnable()) {
            mc.player.inventory.currentItem = index;
        }

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
