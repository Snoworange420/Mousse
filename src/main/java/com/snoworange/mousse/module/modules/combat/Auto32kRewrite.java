package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Auto32kRewrite extends Module {

    public Auto32kRewrite() {
        super("Auto32k+", "", Category.COMBAT, 0);
        this.setKey(Keyboard.KEY_G);
    }

    private BlockPos basePos;
    private int direction;
    private int tickCount = 0;
    private int hopper;
    private int shulker;
    private int solidBlock;
    private int dispenser;
    private int redstone;
    private int air;
    private int mode = 1;
    private boolean opentoggle;
    private int tickAmount = 0;
    private boolean hasRequiredItems;

    static boolean isSuperWeapon(ItemStack item) {
        if (item == null) {
            return false;
        }

        if (item.getTagCompound() == null) {
            return false;
        }

        if (item.getEnchantmentTagList().getTagType() == 0) {
            return false;
        }

        NBTTagList enchants = (NBTTagList) item.getTagCompound().getTag("ench");

        for (int i = 0; i < enchants.tagCount(); i++) {
            NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                int lvl = enchant.getInteger("lvl");
                if (lvl >= 16) {
                    return true;
                }
                break;
            }
        }

        return false;

    }

    static int getBlockNotRedstone() {
        for (int i = 0; i < 9; i++) {
            if (Minecraft.getMinecraft().player.inventory.getStackInSlot(i) == ItemStack.EMPTY || !(Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) || !Block.getBlockFromItem(Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem()).getDefaultState().isFullBlock() || Block.getBlockFromItem(Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem()).equals(Blocks.REDSTONE_BLOCK) || Block.getBlockFromItem(Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem()).equals(Blocks.DISPENSER)) {
                continue;
            }

            return i;
        }

        return -1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        //ROTATIONS.setCompletedAction(key, true);
    }

    @Override
    public void onEnable() {

        super.onEnable();

        tickAmount = 0;
        opentoggle = true;
        hasRequiredItems = true;

        if (mc.objectMouseOver == null || mc.objectMouseOver.sideHit == null) return;

        if (!run()) {
            //this.setEnabled(false);
            onDisable();
        }
    }

    private boolean run() {
        basePos = null;
        tickCount = 0;

        //java.util.function.Predicate<Entity> predicate = (java.util.function.Predicate<Entity>) entity -> !(entity instanceof EntityItem);

        if (mc.objectMouseOver == null || mc.objectMouseOver.sideHit == null) {
            return false;
        } else {
            basePos = mc.objectMouseOver.getBlockPos().offset(mc.objectMouseOver.sideHit);
        }

        //Check if place is in range
        Vec3d eyesPos = new Vec3d(mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ);

        if (eyesPos.squareDistanceTo(new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ())) > 16) {
            mc.player.sendMessage(new TextComponentString("Location is too far away!"));
            hasRequiredItems = false;
            return false;
        }

        hopper = InventoryUtils.findItemInHotbar(Item.getItemById(154));
        if (hopper == -1) {
            mc.player.sendMessage(new TextComponentString("Hopper not found in your hotbar!"));
            hasRequiredItems = false;
            return false;
        }

        for (int i = 219; i <= 234; i++) {
            shulker = InventoryUtils.findItemInHotbar(Item.getItemById(i));
            if (shulker != -1) {
                break;
            }

            if (i == 234) {
                mc.player.sendMessage(new TextComponentString("Shulker box not found in your hotbar!"));
                hasRequiredItems = false;
                return false;
            }
        }

        if (mode == 1) {
            //Check for block in hotbar
            solidBlock = getBlockNotRedstone();
            if (solidBlock == -1) {
                mc.player.sendMessage(new TextComponentString("Blocks not found in your hotbar!"));
                hasRequiredItems = false;
                return false;
            }

            //Check for dispenser in hotbar
            dispenser = InventoryUtils.findItemInHotbar(Item.getItemById(23));
            if (dispenser == -1) {
                mc.player.sendMessage(new TextComponentString("Dispenser not found in your hotbar!"));
                hasRequiredItems = false;
                return false;
            }

            //Check for redstone block in hopper
            redstone = InventoryUtils.findItemInHotbar(Item.getItemById(152));
            if (redstone == -1) {
                mc.player.sendMessage(new TextComponentString("Redstone block not found in your hotbar!"));
                hasRequiredItems = false;
                return false;
            }

            air = InventoryUtils.findItemInHotbar(Item.getItemById(0));

        }

        //Direction
        direction = MathHelper.floor((double) (mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        return true;
    }

    private void onTickCloseScreen() {
        if (tickAmount == 8 && this.mode == 1) {
            mc.player.closeScreen();
        }
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && this.toggled && hasRequiredItems == true) {

            //Close Dispenser GUI when 8 ticks is passed from time it's enabled
            onTickCloseScreen();
            tickAmount++;
            //mc.player.sendMessage(new TextComponentString("Tick: " + tickAmount));

            //Equip 32k
            if ((mc.player.openContainer instanceof ContainerPlayer) || (mc.player.openContainer instanceof ContainerHopper)) {
                for (int x = 0; x < mc.player.openContainer.inventorySlots.size(); x++) {
                    if (isSuperWeapon(mc.player.openContainer.inventorySlots.get(x).getStack()) && !isSuperWeapon(mc.player.inventoryContainer.inventorySlots.get(mc.player.inventory.currentItem).getStack())) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, x, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

                        return;
                    }
                }
            }

            if (!isToggled() || tickCount++ == 0) return;

            if (mode == 0) {
                //Place hopper
                mc.player.inventory.currentItem = hopper;
                //SelfUtils.placeBlockMainHand(false, -1, rotate.getValue(), key, key, true, true, basePos);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.playerController.processRightClickBlock(mc.player, mc.world, basePos, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                //Place shulker
                mc.player.inventory.currentItem = shulker;
                //SelfUtils.placeBlockMainHand(false, -1, rotate.getValue(), key, key, true, true, new BlockPos(basePos.getX(), basePos.getY() + 1, basePos.getZ()));
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.playerController.processRightClickBlock(mc.player, mc.world, basePos, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY() + 1, basePos.getZ()), EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                if (opentoggle == true) {
                    endSequence();
                    opentoggle = false;
                }

            } else if (mode == 1) {
                //if(tickCount % placeDelay.getValue() != 0) {
                //    tickCount++;
                //    return;
                //}

                //Place block
                BlockPos block;
                switch (direction) {
                    //South +Z
                    case 0:
                        block = new BlockPos(basePos.add(0, 0, 1));
                        break;
                    //West -X
                    case 1:
                        block = new BlockPos(basePos.add(-1, 0, 0));
                        break;
                    //North -Z
                    case 2:
                        block = new BlockPos(basePos.add(0, 0, -1));
                        break;
                    //East +X
                    default:
                        block = new BlockPos(basePos.add(1, 0, 0));
                }
                if (mc.world.getBlockState(block).getMaterial().isReplaceable()) {
                    mc.player.inventory.currentItem = solidBlock;
                    //SelfUtils.placeBlockMainHand(false, -1, rotate.getValue(), key, key, true, true, block);
                    mc.playerController.processRightClickBlock(mc.player, mc.world, basePos, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);
                }
                //End Place block

                //Place dispenser
                switch (direction) {
                    //South +Z
                    case 0:
                        block = new BlockPos(basePos.add(0, 1, 0));
                        break;
                    //West -X
                    case 1:
                        block = new BlockPos(basePos.add(0, 1, 0));
                        break;
                    //North -Z
                    case 2:
                        block = new BlockPos(basePos.add(0, 1, 0));
                        break;
                    //East +X
                    default:
                        block = new BlockPos(basePos.add(0, 1, 0));
                }

                if (mc.world.getBlockState(block).getMaterial().isReplaceable()) {
                    mc.player.inventory.currentItem = dispenser;
                    //SelfUtils.placeBlockMainHand(false, -1, rotate.getValue(), key, key, true, true, block);

                    //mc.playerController.processRightClickBlock(mc.player, mc.world, basePos, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);
                    //mc.player.inventory.currentItem = shulker;
                    //mc.playerController.processRightClickBlock(mc.player, mc.world, block, EnumFacing.UP, new Vec3d(block.getX(), block.getY(), block.getZ()), EnumHand.MAIN_HAND);

                    mc.playerController.processRightClickBlock(mc.player, mc.world, basePos, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);

                    //mc.player.inventory.currentItem = shulker;
                    mc.playerController.processRightClickBlock(mc.player, mc.world, block, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);

                    tickCount++;
                    return;
                }

                if ((mc.player.openContainer.inventorySlots.get(0).getStack().isEmpty() && mc.player.openContainer instanceof ContainerDispenser)) {
                    mc.player.inventory.currentItem = shulker;
                    mc.playerController.windowClick(mc.player.openContainer.windowId, mc.player.openContainer.inventorySlots.get(0).slotNumber, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                    //mc.player.closeScreen();
                }

                //Place redstone block
                switch (direction) {
                    //South +Z
                    case 0:
                        block = new BlockPos(basePos.add(0, 2, 0));
                        break;
                    //West -X
                    case 1:
                        block = new BlockPos(basePos.add(0, 2, 0));
                        break;
                    //North -Z
                    case 2:
                        block = new BlockPos(basePos.add(0, 2, 0));
                        break;
                    //East +X
                    default:
                        block = new BlockPos(basePos.add(0, 2, 0));
                }
                if (mc.world.getBlockState(block).getMaterial().isReplaceable()) {
                    mc.player.inventory.currentItem = InventoryUtils.findItemInHotbar(Item.getItemById(152));
                    //SelfUtils.placeBlockMainHand(false, -1, rotate.getValue(), key, key, true, true, block);
                    mc.playerController.processRightClickBlock(mc.player, mc.world, block, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);
                    tickCount++;
                }
                //End Place redstone block

                //Place hopper
                switch (direction) {
                    //South +Z
                    case 0:
                        block = new BlockPos(basePos.add(0, 0, -1));
                        break;
                    //West -X
                    case 1:
                        block = new BlockPos(basePos.add(1, 0, 0));
                        break;
                    //North -Z
                    case 2:
                        block = new BlockPos(basePos.add(0, 0, 1));
                        break;
                    //East +X
                    default:
                        block = new BlockPos(basePos.add(-1, 0, 0));
                }

                mc.player.inventory.currentItem = hopper;
                //SelfUtils.placeBlockMainHand(false, -1, rotate.getValue(), key, key, true, true, basePos);

                /*
                if (tickAmount >= 20 && tickAmount <= 22 && this.mode == 1) {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, block, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY() - 1, basePos.getZ()), EnumHand.MAIN_HAND);
                }

                //mc.player.inventory.currentItem = shulker;

                if (tickAmount >= 25 && tickAmount <= 28 && opentoggle == true) {
                    endSequence();
                    opentoggle = false;
                }
                */

                if (tickAmount >= 15 && tickAmount <= 17 && this.mode == 1) {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, block, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY() - 1, basePos.getZ()), EnumHand.MAIN_HAND);
                }

                //mc.player.inventory.currentItem = shulker;

                if (tickAmount >= 20 && tickAmount <= 23 && opentoggle == true) {
                    endSequence();
                    opentoggle = false;
                }


                //setEnabled(false);
            }
        }
    }

        private void endSequence() {

            //mc.client.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(basePos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            mc.playerController.processRightClickBlock(mc.player, mc.world, basePos, EnumFacing.UP, new Vec3d(basePos.getX(), basePos.getY(), basePos.getZ()), EnumHand.MAIN_HAND);
        }
}
