package com.snoworange.mousse.mixin.mixins;

import com.snoworange.mousse.event.listeners.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = { "sendPacket(Lnet/minecraft/network/Packet;)V" }, at = { @At("HEAD") }, cancellable = true)
    public void onPacketSend(final Packet<?> packet, final CallbackInfo ci) {
        final PacketEvent.Send event = new PacketEvent.Send(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = { "channelRead0" }, at = { @At("HEAD") }, cancellable = true)
    public void onPacketReceive(final ChannelHandlerContext chc, final Packet<?> packet, final CallbackInfo ci) {
        final PacketEvent.Receive event = new PacketEvent.Receive(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}