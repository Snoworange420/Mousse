package com.snoworange.mousse.util.entity;

import com.snoworange.mousse.Main;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryUtils {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static int amountInInventory(Item item) {
        int quantity = 0;

        for(int i = 44; i > -1; i--) {
            ItemStack stackInSlot = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if(stackInSlot.getItem() == item) quantity += stackInSlot.getCount();
        }
        if(Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() == item) quantity += Minecraft.getMinecraft().player.getHeldItemOffhand().getCount();

        return quantity;
    }

    public static int getWeapon() {
        int index = -1;
        double best = 0;
        for(int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            double damage = -1;
            Item item = stack.getItem();
            if(item instanceof ItemTool)
                damage = (float) (ReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool) item, "attackDamage", "field_77865_bY")) + (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
            if(item instanceof ItemSword)
                damage = ((ItemSword) item).getAttackDamage() + (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
            if(damage > best) {
                index = i;
                best = damage;
            }
        }
        return index;
    }

    public static int getTool(BlockPos pos) {
        int index = -1;
        double best = 0;
        for(int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if(stack.isEmpty()) continue;

            float speed = stack.getDestroySpeed(Minecraft.getMinecraft().world.getBlockState(pos));
            if(speed <= 1) continue;

            int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
            if(efficiency > 0) speed += Math.pow(efficiency, 2) + 1;

            if(speed > best) {
                index = i;
                best = speed;
            }
        }
        return index;
    }

    public static boolean canHarvestWithItemInSlot(IBlockState state, int slot) {
        String tool = state.getBlock().getHarvestTool(state);
        if(tool == null) return false;
        return Minecraft.getMinecraft().player.inventory.getStackInSlot(slot).getItem().getHarvestLevel(Minecraft.getMinecraft().player.inventory.getStackInSlot(slot), tool, null, null) >= state.getBlock().getHarvestLevel(state);
    }

    public static boolean is32k(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return false;
        }

        if (stack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= Short.MAX_VALUE) {
            return true;
        }

        return false;
    }

    public static int findInv(Item item) {
        for (int i = 0; i < 36; ++i) {
            Item slot = mc.player.inventory.getStackInSlot(i).getItem();
            if (slot.equals(item)) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

    public static int findShulker() {
        for (int i = 0; i < 36; ++i) {
            Item slot = mc.player.inventory.getStackInSlot(i).getItem();
            if (slot instanceof ItemShulkerBox) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

    public static void swapItem(int from, int to) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, to, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
    }
}