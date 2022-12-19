package com.snoworange.mousse.mixin.mixins;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.modules.misc.Tooltip;
import com.snoworange.mousse.util.render.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    @Shadow
    public int width;
    @Shadow
    public int height;

    @Shadow
    public void updateScreen() {
    }

    @Inject(method = "drawWorldBackground(I)V", at = @At("HEAD"), cancellable = true)
    private void drawWorldBackgroundWrapper(final int tint, final CallbackInfo ci) {
        if (Minecraft.getMinecraft().world != null && Main.moduleManager.getModule("CleanGUI").isToggled()) {
            ci.cancel();
        }

        if (Minecraft.getMinecraft().world != null) {

            if (Main.moduleManager.getModule("Particles").isToggled()) {
                final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
                final int width = scaledResolution.getScaledWidth();
                final int height = scaledResolution.getScaledHeight();
                ParticleUtils.drawParticles(Mouse.getX() * width / Minecraft.getMinecraft().displayWidth, height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1);
            }
        }
    }

    @Inject(method = { "renderToolTip" }, at = { @At("HEAD") }, cancellable = true)
    public void renderToolTip(final ItemStack itemStack, final int x, final int y, final CallbackInfo ci) {
        if (Main.moduleManager.getModule("Tooltip").isEnabled() && Tooltip.shulker.isEnable()) {
            Tooltip.renderShulker(itemStack, x, y, ci);
        }
    }
}