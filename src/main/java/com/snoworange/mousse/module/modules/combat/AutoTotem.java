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
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.toggled) {

            if (!mc.player.inventory.getItemStack().isEmpty()) {
                if (mc.currentScreen instanceof GuiContainer) {
                    return;
                }
            }

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

                int i = InventoryUtils.getBlank();

                if (i == -1) {
                    return;
                }

                mc.playerController.windowClick(0, InventoryUtils.getSlotIndex(i), 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
                clickemptyslot = false;
            }

            if ((mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && mc.player.getHealth() < minhealth) || mc.player.fallDistance > 3) {

                int ei = InventoryUtils.getHotbarBlank();

                if (ei == -1) {
                    return;
                }

                if (!mc.player.getHeldItemOffhand().isEmpty()) {
                    mc.playerController.windowClick(0,45, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.updateController();
                }

                if (totemcount <= 0) {
                    return;
                }

                int i = InventoryUtils.findItem(Items.TOTEM_OF_UNDYING);

                if (i == -1) {
                    return;
                }

                mc.playerController.windowClick(0, InventoryUtils.getSlotIndex(i), 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
                move = true;
            }
        }
    }
}
