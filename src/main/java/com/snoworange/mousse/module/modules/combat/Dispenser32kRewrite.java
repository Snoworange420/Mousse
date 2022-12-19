package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.util.block.BlockUtils;
import com.snoworange.mousse.util.entity.InventoryUtils;
import com.snoworange.mousse.util.math.Timer;
import net.minecraft.block.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.*;
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

    public int stage = 0;
    public BlockPos basePos;
    public BlockPos tempBasePos;
    public BlockPos redstonePos;
    public EnumFacing dispenserDirection;
    public boolean placeVertically;
    Timer timeoutTimer = new Timer();

    BooleanSetting silent;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting smartRedstone;
    BooleanSetting autoClose;
    BooleanSetting autoDisable;
    BooleanSetting select32kSlot;
    BooleanSetting debugMessages;
    BooleanSetting timeout;

    public Dispenser32kRewrite() {
        super("Dispenser32kNew", "rewrite", Category.COMBAT);
    }

    @Override
    public void init() {
        super.init();

        silent = new BooleanSetting("Silent Swap", true);
        swing = new BooleanSetting("Swing", true);
        rotate = new BooleanSetting("Rotate", true);
        smartRedstone = new BooleanSetting("Smart Redstone", true);
        autoClose = new BooleanSetting("Auto Close", true);
        autoDisable = new BooleanSetting("Auto Disable", true);
        select32kSlot = new BooleanSetting("Select 32k Slot", false);
        debugMessages = new BooleanSetting("Debug Messages", false);
        timeout = new BooleanSetting("Timeout", true);

        addSetting(silent, swing, rotate, smartRedstone, autoClose, autoDisable, select32kSlot, debugMessages, timeout);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        timeoutTimer.reset();

        stage = 0;
        basePos = null;
        redstonePos = null;
        tempBasePos = null;
        dispenserDirection = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (stage != 8 && mc.currentScreen instanceof GuiDispenser) {
            mc.player.closeScreen();
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.isEnabled()) {

            if (mc.player == null || mc.world == null) return;

            if (timeoutTimer.passedMs(3200L) && timeout.isEnable()) disable();

            //item checks
            int hopperIndex = -1;
            int redstoneIndex = -1;
            int dispenserIndex = -1;
            int shulkerIndex = InventoryUtils.findShulker();
            int enchantedSwordIndex = -1;
            int revertedSwordIndex = -1;

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

                if (itemStack.getItem() instanceof ItemSword && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) > Enchantments.SHARPNESS.getMaxLevel()) {
                    enchantedSwordIndex = i;
                }

                if (itemStack.getItem() instanceof ItemSword && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) <= Enchantments.SHARPNESS.getMaxLevel()) {
                    revertedSwordIndex = i;
                }
            }

            if (stage == 0 && (hopperIndex == -1 || dispenserIndex == -1 ||  redstoneIndex == -1 || shulkerIndex == -1)) {

                if (hopperIndex == -1) {
                    Main.sendMessage("Missing hopper in your hotbar!");
                }

                if (dispenserIndex == -1) {
                    Main.sendMessage("Missing dispenser in your hotbar!");
                }

                if (redstoneIndex == -1) {
                    Main.sendMessage("Missing redstone block in your hotbar!");
                }

                if (shulkerIndex == -1) {
                    Main.sendMessage("Cannot find shulker box in your inventory!");
                }

                disable();
            }

            if (stage == 0) {

                //Normal placement
                if (basePos == null) {
                    searchBestPlacement();

                    //search if we can place vertically when after searching for invalid placement
                    if (basePos == null) {
                        searchBestPlacementVertically();

                        //if even thats not possible place block and continue
                        if (basePos == null) {

                            //place block so we can place (?)
                            update(dispenserIndex);
                            placeBlock(tempBasePos);

                            searchBestPlacement();
                        }
                    }
                }

                if (basePos != null) stage = 1;
            }

            if (stage == 1) {
                stage = 2; //here should come something but i removed idk
            }

            if (stage == 2) {

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                float yaw = 0.0f;

                if (dispenserDirection == EnumFacing.NORTH) {
                    yaw = -179.0f;
                } else if (dispenserDirection == EnumFacing.EAST) {
                    yaw = -89.0f;
                } else if (dispenserDirection == EnumFacing.SOUTH) {
                    yaw = 1.0f;
                } else if (dispenserDirection == EnumFacing.WEST) {
                    yaw = 91.0f;
                }

                if (debugMessages.isEnable()) Main.sendMessage("Dispenser direction: " + dispenserDirection.getOpposite().getName() + ", Place Vertically: " + placeVertically);

                //Sends rotation packet to rotate dispenser, if needed
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(!placeVertically ? yaw : 0, !placeVertically ? mc.player.rotationPitch : -90, mc.player.onGround));

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                update(dispenserIndex);
                mc.playerController.updateController();

                //Place dispenser

                if (!placeVertically) {
                    placeBlock(basePos.up());
                } else {
                    placeBlock(basePos);
                }

                if (swing.isEnable()) mc.player.swingArm(EnumHand.MAIN_HAND);

                //Open dispenser
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                if (!placeVertically) {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, basePos.up(), EnumFacing.UP, new Vec3d(basePos.up().getX(), basePos.up().getY(), basePos.up().getZ()), EnumHand.MAIN_HAND);
                } else {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, basePos, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);
                }

                if (swing.isEnable()) mc.player.swingArm(EnumHand.MAIN_HAND);

                stage = 3;
            }

            if (stage == 3) {

                if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser && mc.player.openContainer.inventorySlots != null) {

                    //swap shulker box
                    if (mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty()) {
                        //mausbutton 0
                        mc.playerController.windowClick(mc.player.openContainer.windowId, shulkerIndex, 0, ClickType.QUICK_MOVE, mc.player);
                    }

                    //check if shulker is "actually" is swapped
                    if (mc.player.openContainer.inventorySlots.get(0).getStack().getItem() instanceof ItemShulkerBox) {

                        if (mc.currentScreen instanceof GuiDispenser) mc.player.closeScreen();

                        stage = 4;
                    }
                }
            }

            if (stage == 4) {

                //place redstone
                if (redstonePos != null) {

                    update(redstoneIndex);
                    placeBlock(redstonePos);

                    stage = 5;

                } else {

                    Main.sendMessage("Coudn't find any valid redstone placement! disabling...");
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    disable();
                }
            }

            if (stage == 5) {

                //place hopper
                if (!placeVertically) {
                    update(hopperIndex);
                    placeBlock(getBlockPosFromDirection(basePos, dispenserDirection.getOpposite()));
                } else {
                    update(hopperIndex);
                    placeBlock(basePos.down(2));
                }

                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

                stage = 6;
            }

            if (stage == 6) {

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                if (!placeVertically) {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, getBlockPosFromDirection(basePos, dispenserDirection.getOpposite()), EnumFacing.UP, new Vec3d(getBlockPosFromDirection(basePos, dispenserDirection.getOpposite()).getX(), getBlockPosFromDirection(basePos, dispenserDirection.getOpposite()).getY(), getBlockPosFromDirection(basePos, dispenserDirection.getOpposite()).getZ()), EnumHand.MAIN_HAND);
                } else {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, basePos.down(2), EnumFacing.UP, new Vec3d(basePos.down(2).getX(), basePos.down(2).getY(), basePos.down(2).getZ()), EnumHand.MAIN_HAND);
                }

                stage = 7;
            }

            if (stage == 7) {

                //swap 32k
                if (enchantedSwordIndex == -1 && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerHopper && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {
                    for (int i = 0; i < 5; i++) {
                        if (mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= Short.MAX_VALUE) {
                            enchantedSwordIndex = i;
                            break;
                        }
                    }

                    if (enchantedSwordIndex == -1) {
                        return;
                    }

                    int airIndex = -1;

                    for (int i = 0; i < 9; i++) {
                        ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                        if (itemStack.getItem() instanceof ItemAir) {
                            airIndex = i;
                        }
                    }

                    if (select32kSlot.isEnable()) {

                        if (revertedSwordIndex != -1) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(revertedSwordIndex));
                            mc.player.inventory.currentItem = revertedSwordIndex;
                            mc.playerController.updateController();
                        }

                        if (airIndex != -1) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(airIndex));
                            mc.player.inventory.currentItem = airIndex;
                            mc.playerController.updateController();
                        }
                    }

                    mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, airIndex != -1 ? airIndex : (revertedSwordIndex != -1 ? revertedSwordIndex : mc.player.inventory.currentItem), ClickType.SWAP, mc.player);

                    if (debugMessages.isEnable()) Main.sendMessage("32k found in slot " + enchantedSwordIndex);

                    stage = 8;
                }
            }

            if (stage == 8) {

                if (mc.currentScreen instanceof GuiHopper && autoClose.isEnable() && enchantedSwordIndex != -1) mc.player.closeScreen();

                if (autoDisable.isEnable()) disable();

                stage = 9;
            }

            if (stage == 9) {

            }
        }
    }

    public void searchBestPlacement() {

        BlockPos closestBlockPos = null;
        EnumFacing direction = null;

        placeVertically = false;

        if (basePos == null) {
            for (int i = 0; i < 4; i++) {
                for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY, mc.player.posZ + 3))) {
                    if (basePos == null) {

                        if (i == 0) {
                            direction = EnumFacing.NORTH;
                        } else if (i == 1) {
                            direction = EnumFacing.EAST;
                        } else if (i == 2) {
                            direction = EnumFacing.SOUTH;
                        } else if (i == 3) {
                            direction = EnumFacing.WEST;
                        }

                        tempBasePos = blockPos;

                        if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 4
                                && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, 0, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(getBlockPosFromDirection(blockPos, direction).add(0, 0, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(getBlockPosFromDirection(blockPos, direction).add(0, 1, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos.add(0, 0, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(getBlockPosFromDirection(blockPos, direction).add(0, 0, 0))).isEmpty()
                                && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(getBlockPosFromDirection(blockPos, direction).add(0, 1, 0))).isEmpty()
                                && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir
                                && (mc.world.getBlockState(blockPos.up().north()).isFullBlock()
                                || mc.world.getBlockState(blockPos.up().east()).isFullBlock()
                                || mc.world.getBlockState(blockPos.up().south()).isFullBlock()
                                || mc.world.getBlockState(blockPos.up().west()).isFullBlock()
                                || mc.world.getBlockState(blockPos.up().down()).isFullBlock()
                        )
                                && (mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).north()).isFullBlock()
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).east()).isFullBlock()
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).south()).isFullBlock()
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).west()).isFullBlock()
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).down()).isFullBlock()
                        )
                                && (smartRedstone.isEnable() ? (
                                mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir
                                        || mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir
                                        || mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir
                                        || mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir
                        ) : mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir
                        )
                                && mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction)).getBlock() instanceof BlockAir
                                && mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).up()).getBlock() instanceof BlockAir
                                && !(mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).north()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).east()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).south()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).west()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                                || mc.world.getBlockState(getBlockPosFromDirection(blockPos, direction).down()).getBlock().equals(Blocks.REDSTONE_BLOCK))
                        ) {

                            closestBlockPos = blockPos;
                        }

                        //continue
                        if (closestBlockPos != null) {

                            basePos = closestBlockPos;
                            dispenserDirection = direction.getOpposite();

                            //Redstone position defining, needs rewrite
                            if (smartRedstone.isEnable()) {
                                if (i == 0) {
                                    //if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().north();
                                    if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().east();
                                    if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().south();
                                    if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().west();
                                } else if (i == 1) {
                                    if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().north();
                                    //if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().east();
                                    if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().south();
                                    if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().west();
                                } else if (i == 2) {
                                    if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().north();
                                    if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().east();
                                    //if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().south();
                                    if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().west();
                                } else if (i == 3) {
                                    if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().north();
                                    if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().east();
                                    if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().south();
                                    //if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir) redstonePos = blockPos.up().west();
                                } else {
                                    if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) redstonePos = blockPos.up(2);
                                }
                            } else {
                                if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) redstonePos = blockPos.up(2);
                            }

                            stage = 1;

                        }
                    }
                }
            }
        }
    }

    public void searchBestPlacementVertically() {

        BlockPos closestBlockPos = null;

        placeVertically = true;

        if (basePos == null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 1.3, mc.player.posY + 2, mc.player.posZ - 1.3), new BlockPos(mc.player.posX + 1.3, mc.player.posY + 3, mc.player.posZ + 1.3))) {
                if (basePos == null) {

                    if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 4
                            && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, 0, 0))).isEmpty()
                            && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, -1, 0))).isEmpty()
                            && mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB(blockPos.add(0, -2, 0))).isEmpty()
                            && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos.add(0, 0, 0))).isEmpty()
                            && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos.add(0, -1, 0))).isEmpty()
                            && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos.add(0, -2, 0))).isEmpty()
                            && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir
                            && (mc.world.getBlockState(blockPos.north()).isFullBlock()
                            || mc.world.getBlockState(blockPos.east()).isFullBlock()
                            || mc.world.getBlockState(blockPos.south()).isFullBlock()
                            || mc.world.getBlockState(blockPos.west()).isFullBlock()
                            || mc.world.getBlockState(blockPos.up()).isFullBlock()
                    )
                            && mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir
                            && mc.world.getBlockState(blockPos.down(2)).getBlock() instanceof BlockAir
                            && (mc.world.getBlockState(blockPos.down(2).north()).isFullBlock()
                            || mc.world.getBlockState(blockPos.down(2).east()).isFullBlock()
                            || mc.world.getBlockState(blockPos.down(2).south()).isFullBlock()
                            || mc.world.getBlockState(blockPos.down(2).west()).isFullBlock()
                            || mc.world.getBlockState(blockPos.down(3)).isFullBlock()
                    )
                            && !(mc.world.getBlockState(blockPos.down(2).north()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                            || mc.world.getBlockState(blockPos.down(2).east()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                            || mc.world.getBlockState(blockPos.down(2).south()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                            || mc.world.getBlockState(blockPos.down(2).west()).getBlock().equals(Blocks.REDSTONE_BLOCK)
                            || mc.world.getBlockState(blockPos.down(3)).getBlock().equals(Blocks.REDSTONE_BLOCK))
                    ) {

                        closestBlockPos = blockPos;
                    }

                    //continue
                    if (closestBlockPos != null) {

                        basePos = closestBlockPos;
                        dispenserDirection = EnumFacing.DOWN; //does nothing

                        //Redstone position defining, needs rewrite
                        if (mc.world.getBlockState(blockPos.north()).getBlock() instanceof BlockAir) redstonePos = blockPos.north();
                        if (mc.world.getBlockState(blockPos.east()).getBlock() instanceof BlockAir) redstonePos = blockPos.east();
                        if (mc.world.getBlockState(blockPos.south()).getBlock() instanceof BlockAir) redstonePos = blockPos.south();
                        if (mc.world.getBlockState(blockPos.west()).getBlock() instanceof BlockAir) redstonePos = blockPos.west();
                        if (mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) redstonePos = blockPos.up();

                        stage = 1;
                    }
                }
            }
        }
    }

    public BlockPos getBlockPosFromDirection(BlockPos blockPos, EnumFacing direction) {

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
            mc.player.connection.sendPacket (new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        if (rotate.isEnable() && stage != 2) BlockUtils.faceVectorPacketInstant(hitVec);

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);

        if (swing.isEnable()) mc.player.swingArm(EnumHand.MAIN_HAND);

        return true;
    }
}

//end of the code (bruh)