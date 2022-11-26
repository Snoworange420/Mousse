package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.event.listeners.PacketEvent;
import com.snoworange.mousse.event.listeners.TotemPopEvent;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.util.entity.InventoryUtils;
import com.snoworange.mousse.util.entity.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.snoworange.mousse.Main.mc;

public class ThreadAura extends Module {

    public ThreadAura() {
        super("ThreadAura", "32k aura that works on seperate threads", Category.COMBAT);
    }
    
    public EntityPlayer target;
    public int superWeaponIndex;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.toggled) {

            if (mc.player == null || mc.world == null) return;

            superWeaponIndex = -1;

            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    superWeaponIndex = i;
                    break;
                }
            }

            if (superWeaponIndex < 0) return;

            for (EntityPlayer player : mc.world.playerEntities) {

                if (!InventoryUtils.is32k(mc.player.inventory.getStackInSlot(superWeaponIndex))) return;

                if (player == mc.player) continue;

                if (mc.player.getDistance(player) <= 8.2 && player.getHealth() > 0 && !player.isDead) {

                    target = player;

                    mc.player.connection.sendPacket(new CPacketHeldItemChange(superWeaponIndex));
                    mc.playerController.attackEntity(mc.player, player);
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                } else {
                    target = null;
                }
            }
        }
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {

        if (target == null) return;
        
        if (event.getPlayer() == target) {

            if (!(mc.player.inventory.getStackInSlot(superWeaponIndex).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.inventory.getStackInSlot(superWeaponIndex)) >= Short.MAX_VALUE)) {
                return;
            }

            for (int threads = 0; threads < 5; ++threads) {
                new Thread(() -> {
                    for (int iterations = 0; iterations < 8; ++iterations) {
                        PacketUtils.sendPacketDirectly(new CPacketHeldItemChange(superWeaponIndex));
                        PacketUtils.sendPacketDirectly(new CPacketUseEntity(this.target));
                        PacketUtils.sendPacketDirectly(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }).start();
            }
        }
    }
}
