package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.sound.sampled.Line;

public class Info32k extends Module {

    public Info32k() {
        super("32kInfo", "info's you bout 32k wepons", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static boolean has32k() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack threetwokay = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (InventoryUtils.is32k(threetwokay)) {
                return true;
            }
        }
        return false;
    }

}
