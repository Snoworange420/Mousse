package com.snoworange.mousse.event.listeners;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PacketEvent$PacketReceiveEvent extends PacketEvent
{
    public PacketEvent$PacketReceiveEvent(final Packet packet) {
        super(packet);
    }
}