package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.InventoryUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Notify32k extends Module {

    public ItemStack oldStack;
    public ItemStack lastHoldingStack;

    public Notify32k() {
        super("32kNotify", "among sussy 32k ppl", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            for (EntityPlayer target : mc.world.playerEntities) {
                if (!target.getName().equals(mc.getSession().getUsername()) && target.getHealth() > 0 && !target.isDead) {

                    ItemStack itemStack = target.inventory.getCurrentItem();

                    oldStack = itemStack;

                    if (lastHoldingStack == null) return;

                    if (!oldStack.equals(lastHoldingStack)) {

                        if (!InventoryUtils.is32k(oldStack) && !InventoryUtils.is32k(lastHoldingStack)) return;

                        if (InventoryUtils.is32k(itemStack)) {
                            Main.sendMessage("[" + this.name + "] " + target.getName() + " is now holding a 32k.");
                        } else if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && !(EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE)) {
                            Main.sendMessage("[" + this.name + "] " + target.getName() + " is not longer holding a 32k.");
                        }

                        lastHoldingStack = oldStack;
                    }

                    lastHoldingStack = target.inventory.getCurrentItem();
                }
            }
        }
    }
}