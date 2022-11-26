package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastAura extends Module {

    public FastAura() {
        super("32kAura", "Attacks super fast players around you when you have 32k in your hand", Category.COMBAT);
    }

    ModeSetting attackMode;
    BooleanSetting rotate;
    BooleanSetting silent;

    private int superWeaponIndex = -1;

    @Override
    public void init() {
        super.init();

        attackMode = new ModeSetting("Attack Mode", "Vanilla", "Vanilla", "Packet", "Both");
        rotate = new BooleanSetting("Rotate", null, false);
        silent = new BooleanSetting("Silent Swap", null, true);

        addSetting(attackMode, silent);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        superWeaponIndex = -1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        superWeaponIndex = -1;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {

        if (mc.world == null || mc.player == null) return;

        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.world == null || mc.player == null) return;

            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                    superWeaponIndex = i;
                    break;
                }
            }

            if (superWeaponIndex != -1) {
                for (EntityPlayer target : mc.world.playerEntities) {
                    if (!target.getName().equals(mc.getSession().getUsername()) && mc.player.getDistance(target) <= 8.2 && target.getHealth() > 0 && !target.isDead) {

                        AutoEz.targets.put(target.getName(), (EntityPlayer) target);

                        //Silent
                        if (silent.enable) {

                            if (mc.player.inventory.getStackInSlot(superWeaponIndex).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.inventory.getStackInSlot(superWeaponIndex)) >= Short.MAX_VALUE) {
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(superWeaponIndex));
                                mc.playerController.updateController();

                                mc.player.connection.sendPacket(new CPacketUseEntity(target));

                                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                            }

                            //No Silent
                        } else if (!(silent.enable)) {

                            ItemStack crItemStack = mc.player.inventory.getCurrentItem();

                            if (crItemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, crItemStack) >= Short.MAX_VALUE) {

                                mc.player.connection.sendPacket(new CPacketUseEntity(target));

                                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                                mc.player.swingArm(EnumHand.MAIN_HAND);

                            }
                        }
                    }
                }
            }
        }
    }
}