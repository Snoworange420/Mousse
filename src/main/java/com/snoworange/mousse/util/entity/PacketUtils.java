package com.snoworange.mousse.util.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class PacketUtils {

    public static void sendPacketDirectly(Packet<?> packet) {
        if (Minecraft.getMinecraft().getConnection() != null) {
            NetworkManager networkManager = Minecraft.getMinecraft().getConnection().getNetworkManager();
            networkManager.channel().writeAndFlush(packet);
        }
    }
}
