package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.entity.GravityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ElytraFly extends Module {

    private int timer;
    public int speed = 1;
    public static boolean autoTakeOff;

    public ElytraFly() {
        super("ElytraFly", "fly's with elytra", Category.MOVEMENT, 0);
        this.setKey(Keyboard.KEY_N);
    }

    /*
    @Override
    public void init() {
        //mode = new ModeSetting("Elytra Mode", "VanillaBoost", "VanillaBoost", "ControlVelocity", "ControlFreeze", "Packet");
        //autoTakeoff = new BooleanSetting("Auto Takeoff", true);
        autoTakeOff = true;
        super.init();
    }

     */

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
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.toggled) {
            if (autoTakeOff) {

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


            /*
            switch (mode.getMode()) {

                case "VanillaBoost":
                    if (mc.player.isElytraFlying()) {
                        MovementUtils.moveEntityWithSpeed(mc.player, 1, true);
                    }

                case "ControlVelocity":
                    if (mc.player.isElytraFlying()) {

                        mc.player.setVelocity(0 ,0 ,0);

                        MovementUtils.moveEntityWithSpeed(mc.player, 1, true);
                    }

                case "ControlFreeze":
                    if (mc.player.isElytraFlying()) {

                        MovementUtils.freeze();

                        MovementUtils.moveEntityWithSpeed(mc.player, 1, true);
                    }

                case "Packet":
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ, false));

                    MovementUtils.freeze();
                    MovementUtils.Strafe(1);
            }

            */

            /*
            if (mode.equals("VanillaBoost")) {
                if (mc.player.isElytraFlying()) {
                    MovementUtils.moveEntityWithSpeed(mc.player, speed, true);
                }
            }

            if (mode.equals("ControlVelocity")) {
                if (mc.player.isElytraFlying()) {

                    mc.player.setVelocity(0 ,0 ,0);

                    MovementUtils.moveEntityWithSpeed(mc.player, speed, true);
                }
            }

            if (mode.equals("ControlFreeze")) {
                if (mc.player.isElytraFlying()) {

                    MovementUtils.freeze();

                    MovementUtils.moveEntityWithSpeed(mc.player, speed, true);
                }
            }

            if (mode.equals("Packet")) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ, false));

                MovementUtils.freeze();
                MovementUtils.Strafe(1);
            }

             */

            // if (mode.equals("VanillaBoost")) {
            if (mc.player.isElytraFlying()) {
                GravityUtils.moveEntityWithSpeed(mc.player, speed, true);
            }

            //}
        }
    }
}
