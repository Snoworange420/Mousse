package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShulkerPeek extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public ShulkerPeek() {
        super("ShulkerPeek", "", Category.RENDER, 1);
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
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled) {
            ItemStack stack = null;

            if (mc.player.getHeldItemMainhand() == ItemStack.EMPTY) {
                if (!mc.player.getHeldItemOffhand().isEmpty() && mc.player.getHeldItemOffhand().getItem() instanceof ItemShulkerBox) {
                    stack = mc.player.getHeldItemOffhand();
                } else {
                    Main.sendMessage("Coudn't find shulker box in your hands!");
                }
            } else {
                stack = mc.player.getHeldItemMainhand();
            }

            //Open shulker box
            if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemShulkerBox) {
                if (stack.hasTagCompound()) {
                    mc.displayGuiScreen((GuiScreen) new GuiChest((IInventory) mc.player.inventory, (IInventory) getFromItemNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"))));
                } else {
                    mc.displayGuiScreen((GuiScreen) new GuiChest((IInventory) mc.player.inventory, (IInventory) new InventoryBasic("Shulker Box", true, 27)));
                }

                mc.world.playSound(mc.player, mc.player.posX, mc.player.posY, mc.player.posZ, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, 1.0f);
            } else {
                Main.sendMessage("Coudn't find shulker box in your hands!");
            }
            disable();
        }
    }
}
