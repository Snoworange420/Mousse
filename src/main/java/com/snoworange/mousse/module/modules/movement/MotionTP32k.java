package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MotionTP32k extends Module {

    public MotionTP32k() {
        super("32kMotionTP", "just modifies your motionY and stuff so you rubberband", Category.MOVEMENT);
    }

    public boolean startFlag;
    public int delay = 0;

    BooleanSetting invalidpacket;
    NumberSetting teleportDelay;

    @Override
    public void onEnable() {
        super.onEnable();

        startFlag = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        startFlag = true;
        delay = 0;
    }

    @Override
    public void init() {
        super.init();

        teleportDelay = new NumberSetting("Motion Length Ticks", 5, 0, 1, 1);
        invalidpacket = new BooleanSetting("Invalid Packet", false);
        addSetting(teleportDelay, invalidpacket);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (startFlag) {
            mc.player.motionY = 4;

            ++delay;

            if (delay >= teleportDelay.getValue()) {
                startFlag = false;

                if (invalidpacket.isEnable()) mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 1337.0, mc.player.posZ, mc.player.onGround));
            }
        }
    }
}