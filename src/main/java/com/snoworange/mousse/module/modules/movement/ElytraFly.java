package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;
import com.snoworange.mousse.util.entity.GravityUtils;
import com.snoworange.mousse.util.entity.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ElytraFly extends Module {

    private int timer;

    public ElytraFly() {
        super("ElytraFly", "Fly's with elytra.", Category.MOVEMENT);
    }

    ModeSetting mode;
    BooleanSetting autoTakeoff;
    NumberSetting speed;

    @Override
    public void init() {
        mode = new ModeSetting("Elytra Mode", "VanillaBoost", "VanillaBoost", "ControlVelocity", "ControlFreeze", "Packet");
        autoTakeoff = new BooleanSetting("Auto Takeoff", true);
        speed = new NumberSetting("Speed", 1, 1, 20, 0.1);
        super.init();

        addSetting(mode, autoTakeoff, speed);
    }

    @Override
    public void onEnable() {
        timer = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    //Jump = 44 ticks
    //Jump on a block = 32 ticks

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.toggled) {

            if (mc.player == null || mc.world == null) return;

            if (autoTakeoff.enable) {

                if (!mc.player.onGround) {
                    timer++;
                }

                if (!mc.player.onGround) {
                    timer++;
                } else if (mc.player.onGround) {
                    timer = 0;
                }

                if (!mc.player.isElytraFlying() && timer >= 34) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    timer = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.player == null || mc.world == null) return;


            if (mode.is("VanillaBoost")) {
                if (mc.player.isElytraFlying()) {
                    GravityUtils.moveEntityWithSpeed(mc.player, speed.getValue(), true);
                }
            }

            if (mode.is("ControlVelocity")) {
                if (mc.player.isElytraFlying()) {

                    mc.player.setVelocity(0 ,0 ,0);

                    GravityUtils.moveEntityWithSpeed(mc.player, speed.getValue(), true);
                }
            }

            if (mode.is("ControlFreeze")) {
                if (mc.player.isElytraFlying()) {

                    MovementUtils.freeze();

                    GravityUtils.moveEntityWithSpeed(mc.player, speed.getValue(), true);
                }
            }
        }
    }
}
