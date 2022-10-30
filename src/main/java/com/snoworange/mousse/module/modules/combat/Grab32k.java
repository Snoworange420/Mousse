package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Grab32k extends Module {

    public Grab32k() {
        super("32kGrab", "grabs 32k lol.", Category.COMBAT);
    }

    BooleanSetting autoClose;

    boolean clickedHopper;

    @Override
    public void init() {
        super.init();

        autoClose = new BooleanSetting("Auto Close", true);
        addSetting(autoClose);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        clickedHopper = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        clickedHopper = false;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {

        if (mc.world == null || mc.player == null) return;

        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (Main.moduleManager.getModule("Dispenser32k").isToggled()) return;

            int enchantedSwordIndex = -1;
            int shitIndex;

            for (int i = 0; i < 9; i++) {

                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    enchantedSwordIndex = i;
                }
            }

            double hopperBlockDistance = 4;
            BlockPos closestHopperPos = null;

            for (BlockPos hopperPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 2, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY + 2, mc.player.posZ + 3))) {

                if (mc.player.getDistance(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ()) < hopperBlockDistance && mc.world.getBlockState(hopperPos.up()).getBlock() instanceof BlockShulkerBox && mc.world.getBlockState(hopperPos).getBlock() instanceof BlockHopper) {
                    hopperBlockDistance = mc.player.getDistance(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ());
                    closestHopperPos = hopperPos;
                }
            }

            if (closestHopperPos != null && enchantedSwordIndex == -1 && !clickedHopper) {

                //Click hopper
                if (mc.world.getBlockState(closestHopperPos).getBlock() instanceof BlockHopper && mc.world.getBlockState(closestHopperPos.up()).getBlock() instanceof BlockShulkerBox) {
                    Main.sendMessage("Shulker box and hopper detected!");
                    mc.playerController.processRightClickBlock(mc.player, mc.world, closestHopperPos, EnumFacing.UP, new Vec3d(closestHopperPos.getX(), closestHopperPos.getY(), closestHopperPos.getZ()), EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);

                    clickedHopper = true;
                }
            }

            if (enchantedSwordIndex == -1 && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerHopper && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {

                for (int i = 0; i < 5; i++) {
                    if (mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= Short.MAX_VALUE) {
                        enchantedSwordIndex = i;
                        break;
                    } else if (mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i).getItem().equals(Items.DIAMOND_SWORD) && !(EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= Short.MAX_VALUE)) {
                        shitIndex = i;
                        mc.playerController.windowClick(mc.player.openContainer.windowId, shitIndex, shitIndex, ClickType.THROW, mc.player);
                        break;
                    }
                }

                if (enchantedSwordIndex == -1) {
                    return;
                }

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

                mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                Main.sendMessage("Grabbed 32k in slot " + enchantedSwordIndex);

                if (autoClose.enable) {
                    mc.player.closeScreen();
                }
            }

            if (enchantedSwordIndex != -1) {
                disable();
            }
        }
    }
}