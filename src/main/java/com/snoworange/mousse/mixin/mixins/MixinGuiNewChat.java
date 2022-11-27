package com.snoworange.mousse.mixin.mixins;

import com.snoworange.mousse.Main;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {
    @Shadow
    @Final
    private List<ChatLine> chatLines;
    @Shadow @Final private List<ChatLine> drawnChatLines;

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void drawRectBackgroundClean(int left, int top, int right, int bottom, int color, CallbackInfo callbackInfo) {
        if (Main.moduleManager.getModule("CleanGUI").isEnabled()) {
            callbackInfo.cancel();
        }
    }
}
