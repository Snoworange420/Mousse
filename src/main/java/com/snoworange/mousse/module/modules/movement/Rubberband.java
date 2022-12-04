package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Rubberband extends Module {

    public Rubberband() {
        super("Rubberband", "", Category.MOVEMENT);
    }

    public boolean end;
    public int timer = 0;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        end = true;

        timer = 0;
    }

    @SubscribeEvent
    public void onDisableStuff(TickEvent.ClientTickEvent event) {

        if (mc.player == null || mc.world == null) return;

        if (end) {
            timer++;
        }

        if (timer < 35 && end) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY * timer / 1.6, mc.player.posZ, false));
        }

        if (timer >= 35) {
            end = false;
            timer = 0;
        }
    }
}
