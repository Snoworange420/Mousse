package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.Sub;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Auto32kSkidHopper extends Module {

    public int moduleKeybind = Keyboard.KEY_UNLABELED;

    public Auto32kSkidHopper() {
        super("Hopper32k", "", Category.COMBAT, 0);
    }
    public BlockPos placedHopperPos;//this isent needed.. i thought i would need it, but i dident and i dident remove it //actually you reference it a dozen times
    public boolean shouldKillaura;
    public boolean hasPlacedStuff;
    public EntityPlayer entityPlayer;
    public int cpsTick;//i guess ill do it this way, not with system time


    @SubscribeEvent
    public void init(TickEvent.PlayerTickEvent event) {

        if (Sub.auto32kKeybind.isPressed()) {
           Main.is32kEnabled = !Main.is32kEnabled;
           if (Main.is32kEnabled) {
               this.onEnable();
           }
        }


        if (!Main.is32kEnabled) {
            return;
        }

        int hopperIndex = -1;
        int shulkerIndex = -1;
        int enchantedSwordIndex = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = Main.mc.player.inventory.mainInventory.get(i);
            if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.HOPPER))) {
                hopperIndex = i;
            }

            if (itemStack.getItem() instanceof ItemShulkerBox) {
                shulkerIndex = i;
            }
            if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                enchantedSwordIndex = i;
            }
        }

        if (!this.hasPlacedStuff && (hopperIndex == -1 || shulkerIndex == -1)) {
            Main.is32kEnabled = false;
            return;
        }

        /* if (enchantedSwordIndex == -1 && Main.mc.objectMouseOver !=
         * null && Main.mc.objectMouseOver.getBlockPos() != null &&
         * !(Main.mc.world.getBlockState(Minecraft.getMinecraft
         * ().objectMouseOver. getBlockPos()).getBlock() instanceof BlockAir) &&
         * !hasPlacedStuff) { this.placeStuff(hopperIndex, shulkerIndex,
         * Main.mc.objectMouseOver.getBlockPos(),
         * Main.mc.objectMouseOver.sideHit,
         * Main.mc.objectMouseOver.hitVec); } else */ if (enchantedSwordIndex == -1 && !hasPlacedStuff) {

            double closestBlockPosDistance = 4;//maybe?
            BlockPos closestBlockPos = null;


            for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(Main.mc.player.posX - 3, Main.mc.player.posY - 1, Main.mc.player.posZ - 3), new BlockPos(Main.mc.player.posX + 3, Main.mc.player.posY + 2, Main.mc.player.posZ + 3))) {
                if (Main.mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && Main.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0)), null).isEmpty() && !(Main.mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && Main.mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && Main.mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && Main.mc.world.getBlockState(blockPos.up().up()).getBlock() instanceof BlockAir) {
                    closestBlockPosDistance = Main.mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    closestBlockPos = blockPos;
                }
            }

            if (closestBlockPos != null) {
                this.placeStuff(hopperIndex, shulkerIndex, closestBlockPos.down(), EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
            }

        }

        if (enchantedSwordIndex != -1) {
            //we have a 32k
            this.shouldKillaura = true;

            //wow this was a fricking pain to figure out
            if (Main.mc.player.inventory.currentItem != enchantedSwordIndex) {
                Main.mc.player.connection.sendPacket(new CPacketHeldItemChange(enchantedSwordIndex));
                Main.mc.player.inventory.currentItem = enchantedSwordIndex;
                Main.mc.playerController.updateController();
            }

        } else {
            this.shouldKillaura = false;
        }

        if (enchantedSwordIndex == -1 && Main.mc.player.openContainer != null && Main.mc.player.openContainer instanceof ContainerHopper && Main.mc.player.openContainer.inventorySlots != null && !Main.mc.player.openContainer.inventorySlots.isEmpty()) {
            //this is very weird.. but i dont have to get the hopperInventory from GuiHopper
            for (int i = 0; i < 5; i++) {
                if (Main.mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, Main.mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= Short.MAX_VALUE) {
                    enchantedSwordIndex = i;
                    break;
                }
            }

            if (enchantedSwordIndex == -1) {
                return;
            }

            if (Main.mc.player.inventory.mainInventory.get(Main.mc.player.inventory.currentItem).getItem() instanceof ItemAir) {
                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = Main.mc.player.inventory.mainInventory.get(i);
                    if (itemStack.getItem() instanceof ItemAir) {
                        if (Main.mc.player.inventory.currentItem != i) {
                            Main.mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                            Main.mc.player.inventory.currentItem = i;
                            Main.mc.playerController.updateController();
                        }
                        break;
                    }
                }
            }

            Main.mc.playerController.windowClick(Main.mc.player.openContainer.windowId, enchantedSwordIndex, Main.mc.player.inventory.currentItem, ClickType.SWAP, Main.mc.player);

        }

        if (this.shouldKillaura) {

            double closestEntityDistance = 8;//range

            for (Entity entity : Main.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityPlayer) || entity instanceof EntityPlayerSP || entity.isDead) {
                    continue;
                }

                if (Main.mc.player.getDistance(entity) < closestEntityDistance && ((EntityPlayer) entity).getHealth() > 0/*this doesnt seem to do anything */) {
                    this.entityPlayer = (EntityPlayer) entity;
                    closestEntityDistance = Main.mc.player.getDistance(entity);
                }

            }

            if (this.entityPlayer != null) {

                this.cpsTick++;

                if (this.cpsTick >= (20 / (Main.cps))) {

                    Main.mc.playerController.attackEntity(Main.mc.player, this.entityPlayer);
                    Main.mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.cpsTick = 0;
                }
            }
        }

    }

    public void placeStuff(int hopperIndex, int shulkerIndex, BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {
        Main.mc.player.connection.sendPacket(new CPacketEntityAction(Main.mc.player, CPacketEntityAction.Action.START_SNEAKING));

        //place hopper
        if (Main.mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) {//shouldent happen
            Main.mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
            Main.mc.player.inventory.currentItem = hopperIndex;
            Main.mc.playerController.updateController();
            Main.mc.playerController.processRightClickBlock(Main.mc.player, Main.mc.world, blockPos, enumFacing, vec3d, EnumHand.MAIN_HAND);
            Main.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        this.placedHopperPos = blockPos.up();

        boolean placedShulker = false;

        //place shulker
        if (Main.mc.world.getBlockState(this.placedHopperPos).getBlock().equals(Blocks.HOPPER) && Main.mc.world.getBlockState(this.placedHopperPos.up()).getBlock() instanceof BlockAir) {
            Main.mc.player.connection.sendPacket(new CPacketHeldItemChange(shulkerIndex));
            Main.mc.player.inventory.currentItem = shulkerIndex;
            Main.mc.playerController.updateController();
            Main.mc.playerController.processRightClickBlock(Main.mc.player, Main.mc.world, this.placedHopperPos, EnumFacing.UP/* we are placing on the top? */, new Vec3d(this.placedHopperPos.getX(), this.placedHopperPos.getY(), this.placedHopperPos.getZ()), EnumHand.MAIN_HAND);
            Main.mc.player.swingArm(EnumHand.MAIN_HAND);
            placedShulker = true;
        }
        Main.mc.player.connection.sendPacket(new CPacketEntityAction(Main.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        if (placedShulker) {
            //open hopper
            Main.mc.playerController.processRightClickBlock(Main.mc.player, Main.mc.world, this.placedHopperPos, enumFacing/*....*/, new Vec3d(this.placedHopperPos.getX(), this.placedHopperPos.getY(), this.placedHopperPos.getZ()), EnumHand.MAIN_HAND);
        }

        this.hasPlacedStuff = true;
    }


    @Override
    public void onEnable() {

        super.onEnable();

        this.placedHopperPos = null;
        this.shouldKillaura = false;
        this.hasPlacedStuff = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
