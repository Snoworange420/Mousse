package com.snoworange.mousse.module.modules.player;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.NumberSetting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HeadRotator extends Module {

    NumberSetting speed;

    public HeadRotator() {
        super("HeadRotator", "", Category.PLAYER);
    }

    @Override
    public void init() {
        super.init();

        speed = new NumberSetting("Speed", 5, 1, 20, 1);
        addSetting(speed);
    }

    int yaw = 0;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (mc.world == null || mc.player == null) return;

        if (this.toggled) {
            yaw += speed.value;
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(MathHelper.wrapDegrees(yaw), 0, mc.player.onGround));
        }
    }
}
