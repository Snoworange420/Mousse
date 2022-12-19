package com.snoworange.mousse.event.listeners;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Cancelable
public class RenderPortalOverlayEvent extends Event {

    public float timeInPortal;
    public ScaledResolution scaledRes;
    public CallbackInfo ci;

    public RenderPortalOverlayEvent(float timeInPortal, ScaledResolution scaledRes, CallbackInfo ci) {
        this.timeInPortal = timeInPortal;
        this.scaledRes = scaledRes;
        this.ci = ci;
    }
}
