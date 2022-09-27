package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.impl.Peek;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShulkerPeek extends Module {

    private static Block[] SHULKERS;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public ShulkerPeek() {
        super("ShulkerPeek", "", Category.RENDER, 0);
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
        NonNullList items = NonNullList.withSize((int)27, (Object) ItemStack.EMPTY);
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
    public void onEnable() {
        super.onEnable();

        ItemStack stack = null;
        if (!mc.player.getHeldItemOffhand().isEmpty()) {
            stack = mc.player.getHeldItemOffhand();
        }
        if (!mc.player.getHeldItemMainhand().isEmpty()) {
            stack = mc.player.getHeldItemMainhand();
        }
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (isShulkerBox(block)) {
                if (stack.hasTagCompound()) {
                    mc.displayGuiScreen((GuiScreen) new GuiChest((IInventory) mc.player.inventory, (IInventory) getFromItemNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"))));
                } else {
                    mc.displayGuiScreen((GuiScreen) new GuiChest((IInventory) mc.player.inventory, (IInventory) new InventoryBasic("Shulker Box", true, 27)));
                }

                mc.world.playSound(mc.player, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, 1.0f);

                return;
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled) {
            disable();
        }
    }
}
