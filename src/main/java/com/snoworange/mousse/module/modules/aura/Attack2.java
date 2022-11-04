package com.snoworange.mousse.module.modules.aura;

import com.snoworange.mousse.setting.settings.ModeSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
public class Attack2 {
    static Minecraft mc = Minecraft.getMinecraft();

    public static void attack(EntityPlayer target, ModeSetting attackMode) {
        if (attackMode.is("Vanilla")) {
            mc.playerController.attackEntity(mc.player, target);
        } else if (attackMode.is("Packet")) {
            mc.player.connection.sendPacket((Packet) new CPacketUseEntity(target));
        } else if (attackMode.is("Both")) {
            mc.playerController.attackEntity(mc.player, target);
            mc.player.connection.sendPacket((Packet) new CPacketUseEntity(target));
        }
    }
}
