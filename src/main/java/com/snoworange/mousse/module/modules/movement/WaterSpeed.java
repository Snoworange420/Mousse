package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.EntityUtils;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WaterSpeed extends Module {

    private float speed = 1.0f;

    public WaterSpeed() {
        super("WaterSpeed", "Bypasses liquid movement speed in some NCP servers", Category.MOVEMENT, 0);
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
            if (EntityUtils.isInLiquid()) {
                mc.player.jumpMovementFactor = speed;
            }
        }
    }
}
