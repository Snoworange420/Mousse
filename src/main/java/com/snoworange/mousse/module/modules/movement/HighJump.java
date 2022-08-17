package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.w3c.dom.Entity;

public class HighJump extends Module {

    public HighJump() {
        super("HighJump", "Lets you jump higher", Category.MOVEMENT, 0);
    }

    public int jumpstrength = 1;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    double tick = jumpstrength * 2;

    int timer = 0;
    int jumped = 0;

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.gameSettings.keyBindJump.isPressed()) {
                jumped = 1;
            }

            if (timer <= tick && jumped == 1) {
                timer++;
                mc.player.motionY += 0.0585;
            }

            if (timer > tick && mc.player.onGround) {
                timer = 0;
                jumped = 0;
            }

            mc.player.sendChatMessage("Timer: " + timer + "tick: " + tick + "jumped: " + jumped);
        }
    }
}
