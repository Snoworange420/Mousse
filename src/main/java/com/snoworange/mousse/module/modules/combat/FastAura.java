package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FastAura extends Module {

    public FastAura() {
        super("32kAura", "Attacks super fast players around you when you have 32k in your hand", Category.COMBAT);
    }

    private int superWeaponIndex = -1;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            for (int i = 8; i > -1; i--) {
                ItemStack itemStack = mc.player.inventory.getCurrentItem();

                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    superWeaponIndex = i;
                } else {
                    superWeaponIndex = -1;
                }
            }

            if (superWeaponIndex != -1) {
                for (EntityPlayer target : mc.world.playerEntities) {
                    if (!target.getName().equals(mc.getSession().getUsername()) && mc.player.getDistance(target) <= 7 && target.getHealth() > 0 && !target.isDead) {
                        mc.player.connection.sendPacket((Packet) new CPacketUseEntity(target));
                        //mc.playerController.attackEntity(target, mc.player);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }
            }
        }
    }
}
