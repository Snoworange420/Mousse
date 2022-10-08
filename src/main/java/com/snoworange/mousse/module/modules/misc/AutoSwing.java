package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoSwing extends Module {

    public AutoSwing() {
        super("InfiniteSwing", "", Category.MISC, 0);
    }

    private int tick;
    private int delay = 80;
    private boolean mainhand = true;

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

            tick++;

            if (tick >= delay) {
                if (mainhand) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                } else if (!mainhand) {
                    mc.player.swingArm(EnumHand.OFF_HAND);
                }
                tick = 0;
            }
        }
    }
}
