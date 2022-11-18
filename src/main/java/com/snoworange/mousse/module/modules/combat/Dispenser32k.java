package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;
import com.snoworange.mousse.util.block.BlockUtils;
import com.snoworange.mousse.util.render.ColorUtils;
import com.snoworange.mousse.util.render.RenderUtils2;
import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
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
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class Dispenser32k extends Module {

    public BlockPos placedPos;
    public BlockPos redstonePos;
    public BlockPos renderHopperPos;
    public BlockPos oldHopperPos;
    public BlockPos closestHopperPos = null;
    public EnumFacing dispenserDirection;
    public boolean prepareFastHopper;
    public double wallHeight;
    public double radius;
    public double heightValue = 1.0;
    public boolean hasPlacedStuff, swappedShulker, placedRedstone, placedHopper, disableRadius, clickedHopper = false;
    public int rstick = 0;

    public static Dispenser32k instance;

    public Dispenser32k() {
        super("Dispenser32k", "automatically dispenses 32k shulker and swap it to your main hand", Category.COMBAT, 0);
        instance = this;
    }

    BooleanSetting autoClose;
    BooleanSetting fastHopper;
    NumberSetting redstoneDelay;
    BooleanSetting allowVertical;
    BooleanSetting renderCircle;
    BooleanSetting silentSwap;
    BooleanSetting swapToSuperweaponIndex;
    ModeSetting speed;
    BooleanSetting autoDisable;
    BooleanSetting blockShulker;
    BooleanSetting openHopperWithPacket;
    BooleanSetting disableOnDeath;


    @Override
    public void init() {
        super.init();

        autoClose = new BooleanSetting("Auto Close", true);
        redstoneDelay = new NumberSetting("Redstone Place Delay", null, 0, 0, 20, 1);
        fastHopper = new BooleanSetting("Fast Hopper Place", true);
        allowVertical = new BooleanSetting("Allow Vertical Place", true);
        renderCircle = new BooleanSetting("Render Hopper Radius", true);
        silentSwap = new BooleanSetting("Silent Swap", true);
        swapToSuperweaponIndex = new BooleanSetting("Select 32k Slot", true);
        speed = new ModeSetting("Speed", "Safe", "Safe", "2-Tick");
        autoDisable = new BooleanSetting("Auto Disable", true);
        openHopperWithPacket = new BooleanSetting("Open Hopper with Packet", true);
        blockShulker = new BooleanSetting("Block Shulker", false);
        disableOnDeath = new BooleanSetting("Disable on Death", true);

        addSetting(autoClose, /* redstoneDelay, */ fastHopper, allowVertical, renderCircle, silentSwap, swapToSuperweaponIndex, speed, autoDisable, openHopperWithPacket, disableOnDeath);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        redstonePos = null;

        this.placedPos = null;
        this.hasPlacedStuff = false;

        swappedShulker = false;
        placedRedstone = false;
        placedHopper = false;
        disableRadius = false;
        clickedHopper = false;
        prepareFastHopper = false;

        rstick = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        disableRadius = true;
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            // Item checks

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

                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    enchantedSwordIndex = i;
                }
            }

            if (!this.hasPlacedStuff && (hopperIndex == -1 || shulkerIndex == -1 || dispenserIndex == -1 ||  redstoneIndex == -1)) {

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
                return;
            }

            //Dispenser placement and redstone blockpos defining

            //THERE SHOULD BE A BETTER WAY TO DO THIS

            if (enchantedSwordIndex == -1 && !hasPlacedStuff) {

                double closestBlockPosDistance = 4;//maybe?
                BlockPos closestBlockPos = null;
                BlockPos emptyDispenserDirectionBlock = null; //using to check if block is air in 2x3 range

                for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY, mc.player.posZ + 3))) {

                    if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && /* !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && */ mc.world.getBlockState(blockPos).isFullBlock() && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.north()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.north().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.north().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                        //north

                        closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        closestBlockPos = blockPos;

                        emptyDispenserDirectionBlock = blockPos.north();
                        dispenserDirection = EnumFacing.SOUTH;

                        closestHopperPos = emptyDispenserDirectionBlock;

                        //redstone stuff
                        if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().north();
                        } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().east();
                        } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().south();
                        } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().west();
                        } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                            redstonePos = blockPos.up(2);
                        } else {
                            Main.sendMessage("Cannot place redstone block!");
                        }
                    } else if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && /* !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && */ mc.world.getBlockState(blockPos).isFullBlock() && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.east()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.east().up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.north().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.east().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                        //east

                        closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        closestBlockPos = blockPos;

                        emptyDispenserDirectionBlock = blockPos.east();
                        dispenserDirection = EnumFacing.WEST;

                        closestHopperPos = emptyDispenserDirectionBlock;

                        //redstone stuff
                        if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().north();
                        } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().east();
                        } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().south();
                        } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().west();
                        } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                            redstonePos = blockPos.up(2);
                        } else {
                            Main.sendMessage("Cannot place redstone block!");
                        }
                    } else if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && /* !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && */ mc.world.getBlockState(blockPos).isFullBlock() && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.south()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.south().up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.north().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.south().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                        //south

                        closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        closestBlockPos = blockPos;

                        emptyDispenserDirectionBlock = blockPos.south();
                        dispenserDirection = EnumFacing.NORTH;

                        closestHopperPos = emptyDispenserDirectionBlock;

                        //redstone stuff
                        if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().north();
                        } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().east();
                        } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().south();
                        } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().west();
                        } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                            redstonePos = blockPos.up(2);
                        } else {
                            Main.sendMessage("Cannot place redstone block!");
                        }
                    } else if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && /* !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && */ mc.world.getBlockState(blockPos).isFullBlock() && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.west()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.west().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.west().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                        //west

                        closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        closestBlockPos = blockPos;

                        emptyDispenserDirectionBlock = blockPos.west();
                        dispenserDirection = EnumFacing.EAST;

                        closestHopperPos = emptyDispenserDirectionBlock;

                        //redstone stuff
                        if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().north();
                        } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().east();
                        } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().south();
                        } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                            redstonePos = blockPos.up().west();
                        } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                            redstonePos = blockPos.up(2);
                        } else {
                            Main.sendMessage("Cannot place redstone block!");
                        }
                    }
                }

                if (closestBlockPos != null) {
                    this.placeStuff(hopperIndex, shulkerIndex, redstoneIndex, dispenserIndex, obsidianIndex, closestBlockPos.down(), EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
                } else {

                    if (allowVertical.enable) {

                        for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 2, mc.player.posY + 2, mc.player.posZ - 2), new BlockPos(mc.player.posX + 2, mc.player.posY + 4, mc.player.posZ + 2))) {

                            if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 0, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, -1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, -2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 0, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 0, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 0, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 0, -1))).isEmpty() && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.down(2)).getBlock() instanceof BlockAir && (mc.world.getBlockState(blockPos.north()).getBlock() instanceof BlockAir || mc.world.getBlockState(blockPos.east()).getBlock() instanceof BlockAir || mc.world.getBlockState(blockPos.south()).getBlock() instanceof BlockAir || mc.world.getBlockState(blockPos.west()).getBlock() instanceof BlockAir) && (mc.world.getBlockState(blockPos.north()).isFullBlock() || mc.world.getBlockState(blockPos.east()).isFullBlock() || mc.world.getBlockState(blockPos.south()).isFullBlock() || mc.world.getBlockState(blockPos.west()).isFullBlock())) {
                                closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                closestBlockPos = blockPos;

                                dispenserDirection = EnumFacing.DOWN;

                                closestHopperPos = blockPos.down(2);

                                //redstone stuff
                                if (mc.world.getBlockState(blockPos.north()).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.north();
                                } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.east();
                                } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.south();
                                } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.west();
                                } else {
                                    Main.sendMessage("Cannot place redstone block!");
                                }
                            }
                        }
                    }

                    if (closestBlockPos != null) {
                        this.placeStuffVertical(dispenserIndex, shulkerIndex, closestBlockPos, EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
                    } else {
                        for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY, mc.player.posZ + 3))) {

                            if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.north()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.north().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.north().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.north().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                                //north

                                closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                closestBlockPos = blockPos;

                                emptyDispenserDirectionBlock = blockPos.north();
                                dispenserDirection = EnumFacing.SOUTH;

                                closestHopperPos = emptyDispenserDirectionBlock;

                                //redstone stuff
                                if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().north();
                                } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().east();
                                } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().south();
                                } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().west();
                                } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.up(2);
                                } else {
                                    Main.sendMessage("Cannot place redstone block!");
                                }
                            } else if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.east()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.east().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.east().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.east().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                                //east

                                closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                closestBlockPos = blockPos;

                                emptyDispenserDirectionBlock = blockPos.east();
                                dispenserDirection = EnumFacing.WEST;

                                closestHopperPos = emptyDispenserDirectionBlock;

                                //redstone stuff
                                if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().north();
                                } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().east();
                                } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().south();
                                } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().west();
                                } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.up(2);
                                } else {
                                    Main.sendMessage("Cannot place redstone block!");
                                }
                            } else if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.south()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.south().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.south().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.south().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                                //south

                                closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                closestBlockPos = blockPos;

                                emptyDispenserDirectionBlock = blockPos.south();
                                dispenserDirection = EnumFacing.NORTH;

                                closestHopperPos = emptyDispenserDirectionBlock;

                                //redstone stuff
                                if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().north();
                                } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().east();
                                } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().south();
                                } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().west();
                                } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.up(2);
                                } else {
                                    Main.sendMessage("Cannot place redstone block!");
                                }
                            } else if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir && /* --> */ mc.world.getBlockState(blockPos.west()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.west().up()).getBlock() instanceof BlockAir && !(mc.world.getBlockState(blockPos.west().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().west()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().south()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().east()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(blockPos.west().down()).getBlock().equals(Blocks.REDSTONE_BLOCK))) {

                                //west

                                closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                closestBlockPos = blockPos;

                                emptyDispenserDirectionBlock = blockPos.west();
                                dispenserDirection = EnumFacing.EAST;

                                closestHopperPos = emptyDispenserDirectionBlock;

                                //redstone stuff
                                if (mc.world.getBlockState(blockPos.up().north()).getBlock() instanceof BlockAir && !blockPos.up().north().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().north();
                                } else if (mc.world.getBlockState(blockPos.up().east()).getBlock() instanceof BlockAir && !blockPos.up().east().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().east();
                                } else if (mc.world.getBlockState(blockPos.up().south()).getBlock() instanceof BlockAir && !blockPos.up().south().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().south();
                                } else if (mc.world.getBlockState(blockPos.up().west()).getBlock() instanceof BlockAir && !blockPos.up().west().equals(emptyDispenserDirectionBlock.up())) {
                                    redstonePos = blockPos.up().west();
                                } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() instanceof BlockAir) {
                                    redstonePos = blockPos.up(2);
                                } else {
                                    Main.sendMessage("Cannot place redstone block!");
                                }
                            }
                        }

                        if (closestBlockPos != null) {
                            this.placeStuff(hopperIndex, shulkerIndex, redstoneIndex, dispenserIndex, obsidianIndex, closestBlockPos.down(), EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
                        } else {
                            Main.sendMessage("Cannot find any valid placements... disabeling!");
                            disable();
                        }
                    }
                }
            }

            // Shulker box swapping
            if (!swappedShulker && !placedHopper) {

                if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser && mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty()) {

                    //Prepare for shulker swapping
                    if (!silentSwap.enable) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(shulkerIndex));
                        mc.player.inventory.currentItem = shulkerIndex;
                        mc.playerController.updateController();
                    }

                    //swap shulker
                    mc.playerController.windowClick(mc.player.openContainer.windowId, mc.player.openContainer.inventorySlots.get(0).slotNumber, silentSwap.enable ? shulkerIndex : !silentSwap.enable ? mc.player.inventory.currentItem : mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

                    //final shulker box check
                    if (mc.player.openContainer.inventorySlots.get(0).getStack().getItem() instanceof ItemShulkerBox) {
                        swappedShulker = true;
                        mc.player.closeScreen();
                    }
                }
            }

            //if mode is 2-Tick places instantly redstone block quickly after the shulker check
            if (!speed.is("2-Tick") && redstonePos != null && !placedRedstone) {

                //Valid placement check
                if (mc.world.getBlockState(redstonePos).getBlock() instanceof BlockAir && (mc.world.getBlockState(redstonePos.down()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.north()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.east()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.south()).getBlock() instanceof BlockDispenser) || mc.world.getBlockState(redstonePos.west()).getBlock() instanceof BlockDispenser) {

                    //Place redstone block
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround)); //Sends rotation packet to reset the modified rotation yaw in dispenser placing phase
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                    if (silentSwap.enable) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(redstoneIndex));
                        mc.playerController.updateController();
                    } else {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(redstoneIndex));
                        mc.player.inventory.currentItem = redstoneIndex;
                        mc.playerController.updateController();
                    }

                    placeBlock(redstonePos);

                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                    placedRedstone = true;
                }
            }

            //normal
            if (speed.is("Safe") && redstonePos != null && swappedShulker && !placedRedstone) {

                if (mc.world.getBlockState(redstonePos).getBlock() instanceof BlockAir && (mc.world.getBlockState(redstonePos.down()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.north()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.east()).getBlock() instanceof BlockDispenser || mc.world.getBlockState(redstonePos.south()).getBlock() instanceof BlockDispenser) || mc.world.getBlockState(redstonePos.west()).getBlock() instanceof BlockDispenser) {

                    //Place redstone block
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround)); //Sends rotation packet to reset the modified rotation yaw in dispenser placing phase

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                    if (silentSwap.enable) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(redstoneIndex));
                        mc.playerController.updateController();
                    } else {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(redstoneIndex));
                        mc.player.inventory.currentItem = redstoneIndex;
                        mc.playerController.updateController();
                    }

                    placeBlock(redstonePos);

                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

                    //mc.playerController.processRightClickBlock(mc.player, mc.world, redstonePos, EnumFacing.UP, new Vec3d(redstonePos.getX(), redstonePos.getY(), redstonePos.getZ()), EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                    placedRedstone = true;
                }
            }

            //Delay
            //rstick++;

            //fast hopper stuff
            if (fastHopper.enable && prepareFastHopper) {
                if (closestHopperPos != null) {
                    //Place hopper
                    if (mc.world.getBlockState(closestHopperPos).getBlock() instanceof BlockAir) {
                        ;
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                        if (silentSwap.enable) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
                            mc.playerController.updateController();
                        } else {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
                            mc.player.inventory.currentItem = hopperIndex;
                            mc.playerController.updateController();
                        }

                        placeBlock(closestHopperPos);

                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                        prepareFastHopper = false;

                        if (mc.world.getBlockState(closestHopperPos).getBlock() instanceof BlockHopper) {
                            this.wallHeight = Double.longBitsToDouble(Double.doubleToLongBits(2.226615095116189E307) ^ 0x7FBFB542DC55A837L);
                            renderHopperPos = closestHopperPos;
                        }
                    }
                }
            }

            //place hopper if shulker is swapped and redstone block is placed
            if (placedRedstone && !placedHopper) {

                if (!fastHopper.enable) {

                    double hopperBlockDistance = 4;

                    //Search for closest shulker box
                    for (BlockPos hopperPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 2, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY + 2, mc.player.posZ + 3))) {

                        if (mc.player.getDistance(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ()) < hopperBlockDistance && mc.world.getBlockState(hopperPos.up()).getBlock() instanceof BlockShulkerBox && mc.world.getBlockState(hopperPos).getBlock() instanceof BlockAir) {
                            hopperBlockDistance = mc.player.getDistance(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ());
                            closestHopperPos = hopperPos;
                        }
                    }

                    if (closestHopperPos != null) {

                        //Place hopper
                        if (mc.world.getBlockState(closestHopperPos).getBlock() instanceof BlockAir && mc.world.getBlockState(closestHopperPos.up()).getBlock() instanceof BlockShulkerBox) {
                            Main.sendMessage("Shulker box detected!");
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                            if (silentSwap.enable) {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
                                mc.playerController.updateController();
                            } else {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
                                mc.player.inventory.currentItem = hopperIndex;
                                mc.playerController.updateController();
                            }

                            placeBlock(closestHopperPos);

                            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                            placedHopper = true;

                            //open hopper

                            if (openHopperWithPacket.enable) {
                                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(closestHopperPos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                            } else {
                                mc.playerController.processRightClickBlock(mc.player, mc.world, closestHopperPos, EnumFacing.UP, new Vec3d(closestHopperPos.getX(), closestHopperPos.getY(), closestHopperPos.getZ()), EnumHand.MAIN_HAND);
                            }

                            clickedHopper = true;
                        }
                    }
                }

                if (fastHopper.enable && !clickedHopper && closestHopperPos != null) {

                    if (openHopperWithPacket.enable) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(closestHopperPos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    } else {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, closestHopperPos, EnumFacing.UP, new Vec3d(closestHopperPos.getX(), closestHopperPos.getY(), closestHopperPos.getZ()), EnumHand.MAIN_HAND);
                    }

                    placedHopper = true;
                    clickedHopper = true;
                }

                if (mc.world.getBlockState(closestHopperPos).getBlock() instanceof BlockHopper) {
                    this.wallHeight = Double.longBitsToDouble(Double.doubleToLongBits(2.226615095116189E307) ^ 0x7FBFB542DC55A837L);
                    renderHopperPos = closestHopperPos;
                }
            }

            //Swap 32k
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

                if (swapToSuperweaponIndex.enable && mc.player.inventory.currentItem != airIndex) {
                    for (int i = 0; i < 9; i++) {
                        ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                        if (itemStack.getItem() instanceof ItemAir) {
                            airIndex = i;
                        }
                    }

                    if (airIndex != -1) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(airIndex));
                        mc.player.inventory.currentItem = airIndex;
                        mc.playerController.updateController();
                    }
                } else if (!swapToSuperweaponIndex.enable) {
                    for (int i = 0; i < 9; i++) {
                        ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                        if (itemStack.getItem() instanceof ItemAir) {
                            airIndex = i;
                        }
                    }
                }

                if (airIndex != -1) {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, airIndex, ClickType.SWAP, mc.player);
                    Main.sendMessage("32k found in slot " + enchantedSwordIndex);
                }

                if (autoClose.enable) {
                    mc.player.closeScreen();
                }

                if (autoDisable.enable) {
                    disable();
                }
            }

            //render shit
            if (renderHopperPos != null) {
                if (!(mc.world.getBlockState(this.renderHopperPos).getBlock() instanceof BlockHopper) || mc.player.getDistanceSqToCenter(this.renderHopperPos) > Double.longBitsToDouble(Double.doubleToLongBits(0.925710585628769) ^ 0x7FBDDF6BCE5AC52FL)) {
                    this.renderHopperPos = null;
                }
            }
        }

        //disable >>
        if (renderHopperPos != null) {
            if (disableRadius) {
                this.renderHopperPos = null;
            }
        }
    }

    //pls dont mind this stuff below
    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        if (renderCircle.enable) {

            //draw stuff
            if (this.wallHeight < heightValue) {
                this.wallHeight += Double.longBitsToDouble(Double.doubleToLongBits(3751.2945874163925) ^ 0x7FD9347793877A0BL) * (double) heightValue;
            } else if (this.wallHeight > heightValue) {
                this.wallHeight -= Double.longBitsToDouble(Double.doubleToLongBits(114.68668265750983) ^ 0x7FD8D113DC7F3B77L);
            }

            if (renderHopperPos != null) {
                RenderUtils2.drawCircle(renderHopperPos, Double.longBitsToDouble(Double.doubleToLongBits(0.14553988619673233) ^ 0x7FE2A10D0DBD4061L), this.wallHeight, new Color(ColorUtils.BESTCOLOR(0, 255)), new Color(ColorUtils.BESTCOLOR(0, 255)));
                oldHopperPos = renderHopperPos;
                this.radius = Double.longBitsToDouble(Double.doubleToLongBits(0.14070361133713452) ^ 0x7FE20293708FA091L);
            }

            if (renderHopperPos == null && oldHopperPos != null) {
                RenderUtils2.drawCircle(oldHopperPos, this.radius, this.wallHeight, new Color(ColorUtils.BESTCOLOR(0, 255)), new Color(ColorUtils.BESTCOLOR(0, 255)));
                if (this.wallHeight > Double.longBitsToDouble(Double.doubleToLongBits(1.1989844897406259E308) ^ 0x7FE557B6C1188A7BL)) {
                    this.wallHeight -= Double.longBitsToDouble(Double.doubleToLongBits(219.7551656050837) ^ 0x7FD2E1B3C896855BL);
                    return;
                }
                if (this.radius > Double.longBitsToDouble(Double.doubleToLongBits(6.522680943073321E306) ^ 0x7FA293C429F2655FL)) {
                    this.radius -= Double.longBitsToDouble(Double.doubleToLongBits(90.1592080629349) ^ 0x7FEF13A9EE9A7DBDL);
                }
                else {
                    this.radius = Double.longBitsToDouble(Double.doubleToLongBits(7.96568863695466E307) ^ 0x7FDC5BD9D9AD2AC5L);
                }
            }
        }
    }

    public void placeStuff(int hopperIndex, int shulkerIndex, int redstoneIndex, int dispenserIndex, int obsidianIndex, BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        //Place obby (forced)
        if (mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) {
            if (silentSwap.enable) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                mc.playerController.updateController();
            } else {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
                mc.player.inventory.currentItem = obsidianIndex;
                mc.playerController.updateController();
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, EnumFacing.UP, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }

        this.placedPos = blockPos.up();

        //Dispenser placing phase
        //if (mc.world.getBlockState(this.placedPos).isFullBlock() && mc.world.getBlockState(this.placedPos.up()).getBlock() instanceof BlockAir) {

        //Check witch direction the player needs to be rotate to rotate dispenser
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

        if (silentSwap.enable) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(dispenserIndex));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(dispenserIndex));
            mc.player.inventory.currentItem = dispenserIndex;
            mc.playerController.updateController();
        }

        //Sends rotation packet to rotate dispenser
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, 0, mc.player.onGround));
        mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedPos, EnumFacing.UP, new Vec3d(this.placedPos.getX(), this.placedPos.getY(), this.placedPos.getZ()), EnumHand.MAIN_HAND);
        //placeBlock(this.placedPos);

        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

        prepareFastHopper = true;

        mc.player.swingArm(EnumHand.MAIN_HAND);
        //}

        this.placedPos = blockPos.up(2);

        //Open dispenser
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedPos, enumFacing, new Vec3d(this.placedPos.getX(), this.placedPos.getY(), this.placedPos.getZ()), EnumHand.MAIN_HAND);

        this.hasPlacedStuff = true;
    }

    public void placeStuffVertical(int dispenserIndex, int shulkerIndex, BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        //Update inventory slots to dispenser

        if (silentSwap.enable) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(dispenserIndex));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(dispenserIndex));
            mc.player.inventory.currentItem = dispenserIndex;
            mc.playerController.updateController();
        }

        //Place dispenser
        placeBlock(blockPos);
        mc.player.swingArm(EnumHand.MAIN_HAND);

        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));

        //Open dispenser
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), EnumHand.MAIN_HAND);

        prepareFastHopper = true;

        this.hasPlacedStuff = true;
    }

    //TODO: rewrite hopper placement
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

    @SubscribeEvent
    public void onDeath(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {
            if (mc.player.isDead && disableOnDeath.enable) {
                disable();
            }
        }
    }
}