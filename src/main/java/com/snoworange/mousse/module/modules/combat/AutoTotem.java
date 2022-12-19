package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.InventoryUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoTotem extends Module {

    public AutoTotem() {
        super("AutoTotem", "totem", Category.COMBAT);
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
    public void onFastTick(TickEvent event) {
        if (this.isEnabled()) {

            if (mc.world != null && mc.player != null && mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && !(mc.currentScreen instanceof GuiContainer)) {
                int totemSlot = InventoryUtils.findInv(Items.TOTEM_OF_UNDYING);
                if (totemSlot != -1) {
                    InventoryUtils.swapItem(totemSlot, 45);
                }
            }
        }
    }
}
