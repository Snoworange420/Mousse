package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.event.listeners.PacketEvent;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.misc.FileUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class AutoEz extends Module {

    //TODO: Rewrite

    public List<String> ezMessages;
    public static ConcurrentHashMap<String, EntityPlayer> targets;
    public int timeout;

    public AutoEz() {
        super("AutoEz", "Insults players in chat after killing them", Category.COMBAT);
        this.ezMessages = new ArrayList<String>();
    }

    public boolean loadEZMessages() {
        while (true) {
            try {
                final File config = new File(FileUtils.mousse.getAbsolutePath(), "AutoEz.txt");
                if (!config.exists()) {
                    config.createNewFile();
                }
                this.ezMessages = Files.readAllLines(config.toPath());
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public void onEnable() {
        if (this.loadEZMessages()) {
            Main.sendMessage("Successfully loaded Ez messages.");
        }
        else {
            Main.sendMessage("Failed to load Ez messages. Ensure that you have an AutoEz.txt in your Mousse folder.");
        }
    }

    /*
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction().equals(CPacketUseEntity.Action.ATTACK)) {
                final Entity target = packet.getEntityFromWorld((World) mc.world);
                if (target instanceof EntityPlayer) {
                    AutoEz.targets.put(target.getName(), (EntityPlayer)target);
                    Main.sendMessage("Found target!");
                }
            }
        }
    }

     */

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.world == null || mc.player == null) return;

            if (mc.player.getLastAttackedEntity() != null) {
                final Entity target = mc.player.getLastAttackedEntity();
                if (target instanceof EntityPlayer) {
                    AutoEz.targets.put(target.getName(), (EntityPlayer) target);
                }
            }

            mc.world.playerEntities.stream().filter(AutoEz::lambda$onUpdate$1).forEach(this::lambda$onUpdate$2);
        }
    }

    public void ez(final EntityPlayer entityPlayer) {
        final String name = entityPlayer.getName();

        if (this.ezMessages.size() == 0) {
            Main.sendMessage("The AutoEz.txt file is empty. No messages will be sent.");
            return;
        }

        final int rand = new Random().nextInt(this.ezMessages.size());
        AutoEz.targets.remove(name);

        final String ezMessage = this.ezMessages.get(rand).replace("$name$", name);

        mc.player.sendChatMessage(ezMessage);
    }

    public void lambda$onUpdate$2(final EntityPlayer entityPlayer) {
        if (entityPlayer != null && entityPlayer.getHealth() < 0.01f) {
            this.ez(entityPlayer);
        }
    }

    public static boolean lambda$onUpdate$1(final EntityPlayer entityPlayer) {
        return AutoEz.targets.containsKey(entityPlayer.getName());
    }

    static {
        AutoEz.targets = new ConcurrentHashMap<String, EntityPlayer>();
    }
}
