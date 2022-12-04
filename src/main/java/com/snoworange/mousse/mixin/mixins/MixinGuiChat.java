package com.snoworange.mousse.mixin.mixins;

import com.snoworange.mousse.command.CommandManager;
import com.snoworange.mousse.util.render.RenderUtils2;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiChat.class)
public class MixinGuiChat
{
    @Shadow
    protected GuiTextField field_146415_a;

    @Inject(method = { "drawScreen" }, at = { @At("HEAD") })
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
        if (this.field_146415_a.getText().startsWith(CommandManager.prefix)) {
            RenderUtils2.drawChatboxOutline();
        }
    }
}
