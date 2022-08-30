package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.InventoryUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoTotem extends Module {

    public AutoTotem() {
        super("AutoTotem", "Automatically swaps totem to your offhand", Category.COMBAT, 0);
    }

    int totemcount;
    boolean move;
    boolean clickemptyslot;
    double minhealth = 12.0;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled) {

            if (mc.currentScreen instanceof GuiContainer) return;

            totemcount = InventoryUtils.amountInInventory(Items.TOTEM_OF_UNDYING);

            if (move) {
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
                move = false;

                if (!mc.player.inventory.getItemStack().isEmpty()) {
                    clickemptyslot = true;
                }

                return;
            }

            if (clickemptyslot) {
                int index = InventoryUtils.getBlank();

                if (index == -1) return;

                mc.playerController.windowClick(0, InventoryUtils.getSlotIndex(index), 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
                clickemptyslot = false;
            }

            if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && mc.player.getHealth() <= minhealth) {

                if (totemcount == 0) return;

                int index = InventoryUtils.findItem(Items.TOTEM_OF_UNDYING);

                if (index == -1) return;

                mc.playerController.windowClick(0, InventoryUtils.getSlotIndex(index), 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
                move = true;
            }
        }
    }
}
