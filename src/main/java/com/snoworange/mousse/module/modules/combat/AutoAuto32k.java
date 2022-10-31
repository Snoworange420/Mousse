package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoAuto32k extends Module {

    public AutoAuto32k() {
        super("Auto Auto32k", "Automatically toggles auto32k if enemy range is close to you", Category.COMBAT);
    }

    ModeSetting type;
    NumberSetting range;

    @Override
    public void init() {
        super.init();

        type = new ModeSetting("Type 32k", "Dispenser", "Dispenser", "Hopper");
        range = new NumberSetting("Range", 7, 1, 10, 1);
        addSetting(type, range);
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
    public void onTick(TickEvent.ClientTickEvent event) {

        if (mc.world == null || mc.player == null) return;

        if (this.toggled) {
            for (EntityPlayer target : mc.world.playerEntities) {
                if (!target.getName().equals(mc.getSession().getUsername()) && mc.player.getDistance(target) <= range.value && target.getHealth() > 0 && !target.isDead) {

                    if (type.is("Dispenser")) {
                        if (!Main.moduleManager.getModule("Dispenser32k").isToggled()) {
                            Main.moduleManager.getModule("Dispenser32k").toggle();
                        }
                    }

                    if (type.is("Hopper")) {
                        if (!Main.moduleManager.getModule("Auto32k").isToggled()) {
                            Main.moduleManager.getModule("Auto32k").toggle();
                        }
                    }
                }
            }
        }
    }
}
