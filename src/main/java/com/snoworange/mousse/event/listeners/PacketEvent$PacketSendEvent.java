package com.snoworange.mousse.event.listeners;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PacketEvent$PacketSendEvent extends PacketEvent
{
    public PacketEvent$PacketSendEvent(final Packet packet) {
        super(packet);
    }
}

