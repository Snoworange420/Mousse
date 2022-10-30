package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class IceSpeed extends Module {

    public IceSpeed() {
        super("IceSpeed", "Modifes the slipperness of the ice blocks", Category.MOVEMENT, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Blocks.ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.toggled) {
            Blocks.ICE.slipperiness = 0.4f;
            Blocks.FROSTED_ICE.slipperiness = 0.4f;
            Blocks.PACKED_ICE.slipperiness = 0.4f;
        }
    }
}
