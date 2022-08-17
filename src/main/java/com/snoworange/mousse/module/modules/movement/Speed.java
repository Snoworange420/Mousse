package com.snoworange.mousse.module.modules.movement;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends Module {

    public Speed() {
        super("Speed", "Lets you fly", Category.MOVEMENT, 0);
    }

    float speed = 1.0f;
    boolean autojump = true;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (this.toggled) {

                if (mc.player.isInLava() || mc.player.isInWater()) {
                    return;
                }

                int angle;

                boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
                boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
                boolean right = mc.gameSettings.keyBindRight.isKeyDown();
                boolean back = mc.gameSettings.keyBindBack.isKeyDown();

                if (left && right) {
                    angle = forward ? 0 : back ? 180 : -1;
                } else if (forward && back) {
                    angle = left ? -90 : (right ? 90 : -1);
                } else {
                    angle = left ? -90 : (right ? 90 : 0);

                    if (forward) {
                        angle /= 2;
                    } else if (back) {
                        angle = 180 - (angle / 2);
                    }
                }

                if (angle != -1 && (forward || left || right || back)) {
                    float yaw = mc.player.rotationYaw + angle;

                    mc.player.motionX = EntityUtils.getRelativeX(yaw) * speed / 2.5;
                    mc.player.motionZ = EntityUtils.getRelativeZ(yaw) * speed / 2.5;
                }

                if (autojump) {
                    if (mc.player.onGround && EntityUtils.isMoving(mc.player)) {
                        mc.player.jump();
                    }
                }

                if (mc.player.collidedHorizontally) {
                    mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, 0, 0, false));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? 0.0622 : 0) - (mc.gameSettings.keyBindSneak.isKeyDown() ? 0.0622 : 0), mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
                }
            }
        }
    }
}
