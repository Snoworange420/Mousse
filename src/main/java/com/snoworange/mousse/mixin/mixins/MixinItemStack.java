package com.snoworange.mousse.mixin.mixins;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.modules.misc.Tooltip;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class MixinItemStack {

    //Impact skid lmfao

    @Redirect(
            method = {"getTooltip"},
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/item/ItemStack.isItemDamaged()Z"
            )
    )
    private boolean isItemDamaged(ItemStack itemStack) {
        try {
            if (Main.moduleManager.getModule("Tooltip").isEnabled() && Tooltip.alwaysDura.isEnable()) {
                return true;
            }
        } catch (NullPointerException nullPointerException) {
            Main.sendMessage(nullPointerException.toString());
            nullPointerException.printStackTrace();
        }

        return itemStack.isItemDamaged();
    }
}
//moice