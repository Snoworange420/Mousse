package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.EntityUtils;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LiquidSpeed extends Module {

    private float speed = 0.25f;

    public LiquidSpeed() {
        super("LiquidSpeed", "Bypasses liquid movement speed in some NCP servers", Category.MOVEMENT, 0);
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
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            if (EntityUtils.isInLiquid()) {
                mc.player.jumpMovementFactor = speed;
            }
        }
    }
}
