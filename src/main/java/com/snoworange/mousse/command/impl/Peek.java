package com.snoworange.mousse.command.impl;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.Command;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
public class Peek extends Command {

    private static Block[] SHULKERS;
    private static Minecraft mc = Minecraft.getMinecraft();

    public Peek() {
        super("peek", "Peeks inside shulker box", "peek", "pe");
        SHULKERS = new Block[]{Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX};
    }

    private static boolean isShulkerBox(Block block) {
        for (Block b : SHULKERS) {
            if (b != block) continue;
            return true;
        }
        return false;
    }

    private static InventoryBasic getFromItemNBT(NBTTagCompound tag) {
        NonNullList items = NonNullList.withSize((int) 27, (Object) ItemStack.EMPTY);
        String customName = "Shulker Box";
        if (tag.hasKey("Items", 9)) {
            ItemStackHelper.loadAllItems((NBTTagCompound) tag, (NonNullList) items);
        }
        if (tag.hasKey("CustomName", 8)) {
            customName = tag.getString("CustomName");
        }
        InventoryBasic inventoryBasic = new InventoryBasic(customName, true, items.size());
        for (int i = 0; i < items.size(); ++i) {
            inventoryBasic.setInventorySlotContents(i, (ItemStack)items.get(i));
        }
        return inventoryBasic;
    }

    @Override
    public void onCommand(String[] args, String command) {

        ItemStack stack = null;
        if (!Peek.mc.player.getHeldItemOffhand().isEmpty()) {
            stack = Peek.mc.player.getHeldItemOffhand();
        }
        if (!Peek.mc.player.getHeldItemMainhand().isEmpty()) {
            stack = Peek.mc.player.getHeldItemMainhand();
        }
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();if (Peek.isShulkerBox(block)) {
                    if (stack.hasTagCompound()) {
                        mc.displayGuiScreen((GuiScreen) new GuiChest((IInventory) Peek.mc.player.inventory, (IInventory) Peek.getFromItemNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"))));
                    } else {
                        mc.displayGuiScreen((GuiScreen) new GuiChest((IInventory) Peek.mc.player.inventory, (IInventory) new InventoryBasic("Shulker Box", true, 27)));
                    }
                    return;
                }
                Main.sendMessage("You are not holding a shulker box!");
        }
    }
}
