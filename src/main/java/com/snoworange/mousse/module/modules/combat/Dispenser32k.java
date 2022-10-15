package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Dispenser32k extends Module {

    public BlockPos placedPos;
    public BlockPos redstonePos;
    public boolean shouldKillaura;
    public boolean hasPlacedStuff;
    public boolean swappedShulker;
    public boolean placedRedstone;
    public boolean placedHopper;
    public boolean prepareToPlaceRedstone;
    public boolean preparedToPlaceHopper = false;
    public static int redstoneDelay = 1;
    public int rstick = 0;

    public Dispenser32k() {
        super("Dispenser32k", "automatically sets up the 32k bypass for you", Category.COMBAT, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.placedPos = null;
        redstonePos = null;
        this.hasPlacedStuff = false;
        this.swappedShulker = false;
        this.placedRedstone = false;
        this.placedHopper = false;
        prepareToPlaceRedstone = false;
        preparedToPlaceHopper = false;
        rstick = 0;
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

            int hopperIndex = -1;
            int redstoneIndex = -1;
            int dispenserIndex = -1;
            int obsidianIndex = -1;
            int shulkerIndex = -1;
            int airIndex = -1;
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

                if (itemStack.getItem() instanceof ItemAir) {
                    airIndex = i;
                }

                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    enchantedSwordIndex = i;
                }
            }

            if (!this.hasPlacedStuff && (hopperIndex == -1 || shulkerIndex == -1 || dispenserIndex == -1 || obsidianIndex == -1 || redstoneIndex == -1)) {

                if (hopperIndex == -1) {
                    Main.sendMessage("Missing hopper in your hotbar!");
                }

                if (shulkerIndex == -1) {
                    Main.sendMessage("Missing shulker box in your hotbar!");
                }

                if (dispenserIndex == -1) {
                    Main.sendMessage("Missing dispenser in your hotbar!");
                }

                if (obsidianIndex == -1) {
                    Main.sendMessage("Missing obsidian in your hotbar!");
                }

                if (redstoneIndex == -1) {
                    Main.sendMessage("Missing redstone block in your hotbar!");
                }

                disable();
                return;
            }

            if (enchantedSwordIndex == -1 && !hasPlacedStuff) {

                double closestBlockPosDistance = 4;//maybe?
                BlockPos closestBlockPos = null;
                BlockPos emptyDirectionBlock = null; //using to check if block is air in 2x3 range

                float yaw;
                yaw = MathHelper.wrapDegrees(mc.player.rotationYaw);

                for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY + 1, mc.player.posZ + 3))) {

                    //Check for player's opposide direction, there should be a better way to do this
                    if (yaw >= -45.0f && yaw < 45.0f) {
                        emptyDirectionBlock = blockPos.north();
                    } else if ((yaw >= 45.0f && yaw < 135.0f)) {
                        emptyDirectionBlock = blockPos.east();
                    } else if ((yaw >= 135.0f && yaw <= 180.0) || (yaw >= -180.0f && yaw < -135.0f)) {
                        emptyDirectionBlock = blockPos.south();
                    } else if (yaw >= -135.0f && yaw < -45.0) {
                        emptyDirectionBlock = blockPos.west();
                    } else return;

                    if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && /* !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && */ mc.world.getBlockState(blockPos).isFullBlock() && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && mc.world.getBlockState(emptyDirectionBlock).getBlock() instanceof BlockAir && mc.world.getBlockState(emptyDirectionBlock.up()).getBlock() instanceof BlockAir) {
                        closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        closestBlockPos = blockPos;

                        //redstone stuff
                        if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDirectionBlock.up())) {
                            redstonePos = blockPos.up().north();
                        } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDirectionBlock.up())) {
                            redstonePos = blockPos.up().east();
                        } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDirectionBlock.up())) {
                            redstonePos = blockPos.up().south();
                        } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDirectionBlock.up())) {
                            redstonePos = blockPos.up().west();
                        } else {
                            redstonePos = blockPos.up(2);
                        }
                    }
                }

                if (closestBlockPos != null) {
                    this.placeStuff(hopperIndex, shulkerIndex, redstoneIndex, dispenserIndex, obsidianIndex, closestBlockPos.down(), EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
                } else {

                    //Alternate placing method

                    for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY + 1, mc.player.posZ + 3))) {

                        if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && mc.world.getBlockState(emptyDirectionBlock).getBlock() instanceof BlockAir && mc.world.getBlockState(emptyDirectionBlock.up()).getBlock() instanceof BlockAir) {
                            closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            closestBlockPos = blockPos;

                            //redstone stuff
                            if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDirectionBlock.up())) {
                                redstonePos = blockPos.up().north();
                            } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDirectionBlock.up())) {
                                redstonePos = blockPos.up().east();
                            } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDirectionBlock.up())) {
                                redstonePos = blockPos.up().south();
                            } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDirectionBlock.up())) {
                                redstonePos = blockPos.up().west();
                            } else {
                                redstonePos = blockPos.up(2);
                            }
                        }
                    }

                    if (closestBlockPos != null) {
                        this.placeStuff(hopperIndex, shulkerIndex, redstoneIndex, dispenserIndex, obsidianIndex, closestBlockPos.down(), EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
                    } else {
                        Main.sendMessage("Cannot find a empty block!");
                        disable();
                    }
                }
            }

            if (enchantedSwordIndex == -1 && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerHopper && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {
                //this is very weird.. but i dont have to get the hopperInventory from GuiHopper
                for (int i = 0; i < 5; i++) {
                    if (mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= Short.MAX_VALUE) {
                        enchantedSwordIndex = i;
                        break;
                    }
                }

                if (enchantedSwordIndex == -1) {
                    return;
                }

                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                    if (itemStack.getItem() instanceof ItemAir) {
                        if (i != -1) {
                            if (mc.player.inventory.currentItem != i) {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                                mc.player.inventory.currentItem = i;
                                mc.playerController.updateController();
                            }
                        }
                        break;
                    }
                }

                mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                Main.sendMessage("32k found in slot " + enchantedSwordIndex);
            }

            if (!swappedShulker && !placedHopper) {
                if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser && mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty()) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(shulkerIndex));
                    mc.player.inventory.currentItem = shulkerIndex;
                    mc.playerController.updateController();
                    mc.playerController.windowClick(mc.player.openContainer.windowId, mc.player.openContainer.inventorySlots.get(0).slotNumber, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                }

                if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser && mc.player.openContainer.inventorySlots.get(0).getStack().getItem() instanceof ItemShulkerBox) {
                    mc.player.closeScreen();
                    swappedShulker = true;
                    prepareToPlaceRedstone = true;
                }
            }

            if (prepareToPlaceRedstone) {
                rstick++;

                if (rstick > redstoneDelay && redstonePos != null) {

                    if (mc.world.getBlockState(redstonePos).getBlock() instanceof BlockAir && (mc.world.getBlockState(redstonePos.down()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.north()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.east()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.south()).getBlock() instanceof BlockDispenser) || mc.world.getBlockState(redstonePos.west()).getBlock() instanceof BlockDispenser) {

                        //Place redstone block
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(redstoneIndex));
                        mc.player.inventory.currentItem = redstoneIndex;
                        mc.playerController.updateController();
                        placeBlock(redstonePos);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                        placedRedstone = true;
                    }
                }
            }

            //place hopper if shulker is swapped and redstone block is placed
            if (swappedShulker && placedRedstone && !placedHopper) {
                prepareToPlaceRedstone = false;
                preparedToPlaceHopper = true;
            }

            if (preparedToPlaceHopper) {
                double hopperBlockDistance = 4;
                BlockPos closestHopperPos = null;

                for (BlockPos hopperPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY + 2, mc.player.posZ + 3))) {

                    if (mc.player.getDistance(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ()) < hopperBlockDistance && mc.world.getBlockState(hopperPos.up()).getBlock() instanceof BlockShulkerBox && mc.world.getBlockState(hopperPos).getBlock() instanceof BlockAir) {
                        hopperBlockDistance = mc.player.getDistance(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ());
                        closestHopperPos = hopperPos;
                    }
                }

                if (closestHopperPos != null) {

                    if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser)  mc.player.closeScreen();

                    //Place hopper
                    if (mc.world.getBlockState(closestHopperPos).getBlock() instanceof BlockAir && mc.world.getBlockState(closestHopperPos.up()).getBlock() instanceof BlockShulkerBox) {
                        Main.sendMessage("Shulker box detected!");
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
                        mc.player.inventory.currentItem = hopperIndex;
                        mc.playerController.updateController();
                        placeBlock(closestHopperPos);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                        placedHopper = true;
                    }

                    if (placedHopper) {
                        //open hopper
                        mc.playerController.processRightClickBlock(mc.player, mc.world, closestHopperPos, EnumFacing.UP, new Vec3d(closestHopperPos.getX(), closestHopperPos.getY(), closestHopperPos.getZ()), EnumHand.MAIN_HAND);

                        if (airIndex != -1) {
                            if (mc.player.inventory.currentItem != airIndex) {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(airIndex));
                                mc.player.inventory.currentItem = airIndex;
                                mc.playerController.updateController();
                            }
                        }
                    } else {
                        Main.sendMessage("Cannot find shulker box!");
                    }
                }
            }

            if (placedHopper) {
                preparedToPlaceHopper = false;
            }
        }
    }

    public void placeStuff(int hopperIndex, int shulkerIndex, int redstoneIndex, int dispenserIndex, int obsidianIndex, BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        //Place obby
        if (mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
            mc.player.inventory.currentItem = obsidianIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        this.placedPos = blockPos.up();

        boolean placedDispenser = false;

        //Place dispenser
        if ((mc.world.getBlockState(this.placedPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(this.placedPos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(this.placedPos).isFullBlock()) && mc.world.getBlockState(this.placedPos.up()).getBlock() instanceof BlockAir) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(dispenserIndex));
            mc.player.inventory.currentItem = dispenserIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedPos, EnumFacing.UP, new Vec3d(this.placedPos.getX(), this.placedPos.getY(), this.placedPos.getZ()), EnumHand.MAIN_HAND);
            placeBlock(this.placedPos);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            placedDispenser = true;
        }

        this.placedPos = blockPos.up(2);

        if (placedDispenser) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedPos, enumFacing, new Vec3d(this.placedPos.getX(), this.placedPos.getY(), this.placedPos.getZ()), EnumHand.MAIN_HAND);
        }

        this.hasPlacedStuff = true;
    }

    private boolean placeBlock(BlockPos pos) {
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