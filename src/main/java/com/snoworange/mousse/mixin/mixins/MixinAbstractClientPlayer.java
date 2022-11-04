package com.snoworange.mousse.mixin.mixins;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.modules.player.Capes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(value={AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method={"getLocationCape"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        NetworkPlayerInfo info = this.getPlayerInfo();
        UUID uuid = null;
        if (info != null) {
            uuid = this.getPlayerInfo().getGameProfile().getId();
        }

        if (Main.moduleManager.getModule("Capes").isToggled()) {
            ResourceLocation cape = Capes.getCapeResource((AbstractClientPlayer) (Object) this);
            if (uuid != null && Minecraft.getMinecraft().getSession().getUsername().equals(this.getPlayerInfo().getGameProfile().getName())) {
                callbackInfoReturnable.setReturnValue(cape);
            }
        }
    }
}