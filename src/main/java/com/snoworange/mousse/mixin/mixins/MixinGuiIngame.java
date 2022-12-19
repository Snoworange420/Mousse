package com.snoworange.mousse.mixin.mixins;

import com.snoworange.mousse.event.listeners.RenderPortalOverlayEvent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public class MixinGuiIngame {

    @Inject(at = @At("HEAD"), method = {"renderPortal"}, cancellable = true)
    public void onRenderPortalOverlay(float timeInPortal, ScaledResolution scaledRes, CallbackInfo ci) {
        final RenderPortalOverlayEvent event = new RenderPortalOverlayEvent(timeInPortal, scaledRes, ci);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
