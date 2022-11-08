package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LightningBolt extends Module {

    public LightningBolt() {
        super("LightningBolt", "summons lightning when player dies", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void summonLightning(World world, double x, double y, double z) {

        EntityLightningBolt lightningBolt = new EntityLightningBolt(world, x, y, z, false);

        world.addWeatherEffect(lightningBolt);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.world == null || mc.player == null) return;

            for (EntityPlayer target : mc.world.playerEntities) {
                if ((target.getHealth() <= 0.069420f && target.deathTime == 0) || target.isDead) {
                    summonLightning(mc.world, target.posX, target.posY, target.posZ);
                }
            }
        }
    }
}