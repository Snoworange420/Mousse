package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import org.lwjgl.input.Keyboard;

public class Dispenser32k extends Module {
    public boolean useKillaura = true;

    public int cps = 12;

    public BlockPos placedPos;
    //public BlockPos hopperPos;
    public boolean shouldKillaura;
    public boolean hasPlacedStuff;
    public EntityPlayer entityPlayer;
    public int cpsTick;//i guess ill do it this way, not with system time
    public boolean swappedShulker;
    public boolean placedRedstone;
    public boolean placedHopper;
    public boolean preparedToPlaceHopper = false;

    public Dispenser32k() {
        super("Dispenser32k", "automatically sets up the 32k bypass for you", Category.COMBAT, 0);
        this.setKey(Keyboard.KEY_G);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.placedPos = null;
        this.shouldKillaura = false;
        this.hasPlacedStuff = false;
        this.swappedShulker = false;
        this.placedRedstone = false;
        this.placedHopper = false;
        preparedToPlaceHopper = false;
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

            if (!this.hasPlacedStuff && (hopperIndex == -1 || shulkerIndex == -1 || dispenserIndex == -1 || obsidianIndex == -1 || redstoneIndex == -1)) {
                disable();
                Main.sendMessage("Blocks not found in your hotbar!");
                return;
            }

            if (enchantedSwordIndex == -1 && !hasPlacedStuff) {

                double closestBlockPosDistance = 4;//maybe?
                BlockPos closestBlockPos = null;
                BlockPos emptyDirectionBlock = null; //using to check if block is air in 2x3 range (not working lol)

                float yaw = 0.0f;
                yaw = MathHelper.wrapDegrees(mc.player.rotationYaw);

                for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY + 2, mc.player.posZ + 3))) {

                    //Check for player's opposide direction
                    if (yaw >= -45.0f && yaw < 45.0f) {
                        emptyDirectionBlock = blockPos.north();
                    } else if ((yaw >= 45.0f && yaw < 135.0f)) {
                        emptyDirectionBlock = blockPos.east();
                    } else if ((yaw >= 135.0f && yaw <= 180.0) || (yaw >= -180.0f && yaw < -135.0f)) {
                        emptyDirectionBlock = blockPos.south();
                    } else if (yaw >= -135.0f && yaw < -45.0) {
                        emptyDirectionBlock = blockPos.west();
                    } else return;

                    if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(-1, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 1))).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, -1))).isEmpty() && !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up().up()).getBlock() instanceof BlockAir && mc.world.getBlockState(emptyDirectionBlock).getBlock() instanceof BlockAir && mc.world.getBlockState(emptyDirectionBlock.up()).getBlock() instanceof BlockAir) {
                        closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        closestBlockPos = blockPos;
                    }
                }

                if (closestBlockPos != null) {
                    this.placeStuff(hopperIndex, shulkerIndex, redstoneIndex, dispenserIndex, obsidianIndex, closestBlockPos.down(), EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
                } else {
                    Main.sendMessage("Cannot find a empty block!");
                }

            }

            if (enchantedSwordIndex != -1) {
                //we have a 32k
                this.shouldKillaura = true;

                //wow this was a fricking pain to figure out
                if (mc.player.inventory.currentItem != enchantedSwordIndex) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(enchantedSwordIndex));
                    mc.player.inventory.currentItem = enchantedSwordIndex;
                    mc.playerController.updateController();
                }

            } else {
                this.shouldKillaura = false;
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

                if (mc.player.inventory.mainInventory.get(mc.player.inventory.currentItem).getItem() instanceof ItemAir) {
                    for (int i = 0; i < 9; i++) {
                        ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                        if (itemStack.getItem() instanceof ItemAir) {
                            if (mc.player.inventory.currentItem != i) {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                                mc.player.inventory.currentItem = i;
                                mc.playerController.updateController();
                            }
                            break;
                        }
                    }
                }

                mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                Main.sendMessage("32k found in slot " + enchantedSwordIndex);
            }

            if (this.shouldKillaura && useKillaura) {

                double closestEntityDistance = 8;//range

                for (Entity entity : mc.world.loadedEntityList) {
                    if (!(entity instanceof EntityPlayer) || entity instanceof EntityPlayerSP || entity.isDead) {
                        continue;
                    }

                    if (mc.player.getDistance(entity) < closestEntityDistance && ((EntityPlayer) entity).getHealth() > 0/*this doesnt seem to do anything */) {
                        this.entityPlayer = (EntityPlayer) entity;
                        closestEntityDistance = mc.player.getDistance(entity);
                    }

                }

                if (this.entityPlayer != null) {

                    this.cpsTick++;

                    if (this.cpsTick >= (20 / (cps))) {

                        mc.playerController.attackEntity(mc.player, this.entityPlayer);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        this.cpsTick = 0;
                    }
                }
            }

            //Swap shulker box
            if (!swappedShulker && !placedHopper && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser && mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(shulkerIndex));
                mc.player.inventory.currentItem = shulkerIndex;
                mc.playerController.updateController();
                mc.playerController.windowClick(mc.player.openContainer.windowId, mc.player.openContainer.inventorySlots.get(0).slotNumber, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                mc.playerController.updateController();
                swappedShulker = true;
                //Main.sendMessage("Swapping shulker box!");
            }

            //place hopper if shulker is swapped and dispenser is empty
            if (swappedShulker && placedRedstone && !placedHopper && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerDispenser && mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty()) {
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
                    mc.player.closeScreen();
                    //this.placeHopper(hopperIndex, closestHopperPos, EnumFacing.UP, new Vec3d(closestHopperPos.getX(), closestHopperPos.getY(), closestHopperPos.getZ()));
                    Main.sendMessage("Placing hopper...");

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                    //Place hopper
                    if (mc.world.getBlockState(closestHopperPos).getBlock() instanceof BlockAir && mc.world.getBlockState(closestHopperPos.up()).getBlock() instanceof BlockShulkerBox) {
                        Main.sendMessage("Shulker box detected!");
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
                        mc.player.inventory.currentItem = hopperIndex;
                        mc.playerController.updateController();
                        mc.playerController.processRightClickBlock(mc.player, mc.world, closestHopperPos, EnumFacing.UP/* we are placing on the top? */, new Vec3d(closestHopperPos.getX(), closestHopperPos.getY(), closestHopperPos.getZ()), EnumHand.MAIN_HAND);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        placedHopper = true;
                    }

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                    if (placedHopper) {
                        //open hopper
                        mc.playerController.processRightClickBlock(mc.player, mc.world, closestHopperPos, EnumFacing.UP, new Vec3d(closestHopperPos.getX(), closestHopperPos.getY(), closestHopperPos.getZ()), EnumHand.MAIN_HAND);
                        Main.sendMessage("Opening hopper!");
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
        if (mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) {//shouldent happen
            mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianIndex));
            mc.player.inventory.currentItem = obsidianIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            //Main.sendMessage("Placing obby!");
        }
        this.placedPos = blockPos.up();
        //this.hopperPos = blockPos;

        boolean placedDispenser = false;
        float yaw = 0;
        //Place dispenser
        if (mc.world.getBlockState(this.placedPos).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(this.placedPos.up()).getBlock() instanceof BlockAir) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(dispenserIndex));
            mc.player.inventory.currentItem = dispenserIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedPos, EnumFacing.UP/* we are placing on the top? */, new Vec3d(this.placedPos.getX(), this.placedPos.getY(), this.placedPos.getZ()), EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            placedDispenser = true;
            yaw = MathHelper.wrapDegrees(mc.player.rotationYaw);
            //Main.sendMessage("Placing dispenser!");
        }
        this.placedPos = blockPos.up(2);

        if (placedDispenser) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            //Main.sendMessage("Opening dispenser!");
            mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedPos, enumFacing, new Vec3d(this.placedPos.getX(), this.placedPos.getY(), this.placedPos.getZ()), EnumHand.MAIN_HAND);
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        placedRedstone = false;

        //Place redstone block
        if (mc.world.getBlockState(this.placedPos).getBlock().equals(Blocks.DISPENSER) && mc.world.getBlockState(this.placedPos.up()).getBlock() instanceof BlockAir) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(redstoneIndex));
            mc.player.inventory.currentItem = redstoneIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedPos, EnumFacing.UP/* we are placing on the top? */, new Vec3d(this.placedPos.getX(), this.placedPos.getY(), this.placedPos.getZ()), EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            //Main.sendMessage("Placing redstone block!");
            placedRedstone = true;
        }

        //mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        this.hasPlacedStuff = true;
    }

    
    public void placeHopper(int hopperIndex, BlockPos hopperPos, EnumFacing enumFacing, Vec3d vec3d) {

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        Block[] SHULKERS;

        SHULKERS = new Block[]{Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX};

        //Place hopper
        if (mc.world.getBlockState(hopperPos).getBlock() instanceof BlockAir && mc.world.getBlockState(hopperPos.up()).getBlock() instanceof BlockShulkerBox) {
            Main.sendMessage("Shulker box detected!");
            mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
            mc.player.inventory.currentItem = hopperIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, hopperPos, EnumFacing.UP/* we are placing on the top? */, new Vec3d(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ()), EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            placedHopper = true;
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

       if (placedHopper) {
           //open hopper
           mc.playerController.processRightClickBlock(mc.player, mc.world, hopperPos, enumFacing, new Vec3d(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ()), EnumHand.MAIN_HAND);
           Main.sendMessage("Opening hopper!");

           /*
           if (!(mc.player.inventory.mainInventory.get(mc.player.inventory.currentItem).getItem() instanceof ItemAir)) {
               for (int i = 0; i < 9; i++) {
                   ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                   if (itemStack.getItem() instanceof ItemAir) {
                       if (mc.player.inventory.currentItem != i) {
                           mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                           mc.player.inventory.currentItem = i;
                           mc.playerController.updateController();
                       }
                   }
               }
           }

            */
       }

       this.hasPlacedStuff = true;
    }
}
