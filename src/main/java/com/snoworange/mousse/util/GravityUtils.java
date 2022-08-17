package com.snoworange.mousse.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GravityUtils {

    public static void moveEntityWithSpeed(Entity entity, double speed, boolean shouldMoveY) {

        Minecraft mc = Minecraft.getMinecraft();

        float yaw = (float) Math.toRadians(mc.player.rotationYaw);

        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            entity.motionX = -(MathHelper.sin(yaw) * speed);
            entity.motionZ = MathHelper.cos(yaw) * speed;
        } else if (mc.gameSettings.keyBindBack.isKeyDown()) {
            entity.motionX = MathHelper.sin(yaw) * speed;
            entity.motionZ = -(MathHelper.cos(yaw) * speed);
        }

        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            entity.motionZ = MathHelper.sin(yaw) * speed;
            entity.motionX = MathHelper.cos(yaw) * speed;
        } else if (mc.gameSettings.keyBindRight.isKeyDown()) {
            entity.motionZ = -(MathHelper.sin(yaw) * speed);
            entity.motionX = -(MathHelper.cos(yaw) * speed);
        }

        if (shouldMoveY) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                entity.motionY = speed;
            } else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                entity.motionY = -speed;
            }
        }

        //strafe
        if (mc.gameSettings.keyBindForward.isKeyDown() && mc.gameSettings.keyBindLeft.isKeyDown()) {
            entity.motionX = (MathHelper.cos(yaw) * speed) - (MathHelper.sin(yaw) * speed);
            entity.motionZ = (MathHelper.cos(yaw) * speed) + (MathHelper.sin(yaw) * speed);
        } else if (mc.gameSettings.keyBindLeft.isKeyDown() && mc.gameSettings.keyBindBack.isKeyDown()) {
            entity.motionX = (MathHelper.cos(yaw) * speed) + (MathHelper.sin(yaw) * speed);
            entity.motionZ = -(MathHelper.cos(yaw) * speed) + (MathHelper.sin(yaw) * speed);
        } else if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown()) {
            entity.motionX = -(MathHelper.cos(yaw) * speed) + (MathHelper.sin(yaw) * speed);
            entity.motionZ = -(MathHelper.cos(yaw) * speed) - (MathHelper.sin(yaw) * speed);
        } else if (mc.gameSettings.keyBindRight.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown()) {
            entity.motionX = -(MathHelper.cos(yaw) * speed) - (MathHelper.sin(yaw) * speed);
            entity.motionZ = (MathHelper.cos(yaw) * speed) - (MathHelper.sin(yaw) * speed);
        }
    }
}
