package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class TickAura extends Module {

    public TickAura() {
        super("TickAura", ".", Category.COMBAT, 0);
    }

    public EntityPlayer entityPlayer;
    private int cpsticks;
    private int cps = 10;

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
            double closestEntityDistance = 8;//range

            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityPlayer) || entity instanceof EntityPlayerSP || entity.isDead) {
                    continue;
                }

                if (mc.player.getDistance(entity) < closestEntityDistance && ((EntityPlayer) entity).getHealth() > 0/*this doesnt seem to do anything */) {
                    this.entityPlayer = (EntityPlayer) entity;
                    closestEntityDistance = mc.player.getDistance(entity);
                }
            }

            if (this.entityPlayer != null) {

                int tick = 20;
                this.cpsticks++;

                if (this.cpsticks >= (tick / (cps))) {

                    mc.playerController.attackEntity(Main.mc.player, this.entityPlayer);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.cpsticks = 0;
                }
            }
        }
    }
}
