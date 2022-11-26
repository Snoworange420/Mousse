package com.snoworange.mousse.module;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.event.listeners.PacketEvent;
import com.snoworange.mousse.event.listeners.TotemPopEvent;
import com.snoworange.mousse.module.modules.combat.*;
import com.snoworange.mousse.module.modules.exploit.*;
import com.snoworange.mousse.module.modules.misc.*;
import com.snoworange.mousse.module.modules.movement.*;
import com.snoworange.mousse.module.modules.player.*;
import com.snoworange.mousse.module.modules.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.snoworange.mousse.Main.mc;

public class ModuleManager {

    public static ModuleManager instance;

    public ArrayList<Module> modules;

    public ModuleManager() {
        (modules = new ArrayList<Module>()).clear();

        //COMBAT
        modules.add(new Dispenser32k());
        modules.add(new AutoXP());
        modules.add(new AutoEz());
        modules.add(new Criticals());
        modules.add(new AutoArmor());
        modules.add(new FastAura());
        modules.add(new AutoAuto32k());
        modules.add(new Grab32k());
        modules.add(new Auto32k2019());
        modules.add(new Notify32k());
        //modules.add(new Dispenser32kRewrite());
        modules.add(new ThreadAura());

        //EXPLOIT
        modules.add(new SecretClose());

        //JOKES

        //MOVEMENT
        modules.add(new ElytraFly());
        modules.add(new LiquidSpeed());
        modules.add(new Scaffold());
        modules.add(new AirJump());
        modules.add(new AutoSprint());
        modules.add(new EntityFly());
        modules.add(new Fly());
        modules.add(new ReverseStep());
        modules.add(new ShiftSpam());
        modules.add(new IceSpeed());

        //PLAYER
        modules.add(new NoFall());
        modules.add(new Jesus());
        modules.add(new Capes());
        modules.add(new HeadRotator());

        //MISC
        modules.add(new Disconnector());
        modules.add(new BetterChat());
        modules.add(new DiscordRPC());
        modules.add(new Announcer());
        modules.add(new GuiTheme());
        //modules.add(new TrueDurability());
        //modules.add(new DebugModule());

        //RENDER
        modules.add(new ShulkerPeek());
        modules.add(new NoPortal());
        modules.add(new CleanGUI());
        modules.add(new LightningBolt());
        modules.add(new HopperRadius());
        modules.add(new Particles());
        modules.add(new ShulkerPreview());

        //WORK IN PROGRESS

        instance = this;
    }

    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public Module getModule(String name) {
        for (Module m : this.modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
    public ArrayList<Module> getModuleList() {
        return this.modules;
    }

    public static List<Module> getModulesByCategory(Category c) {
        List<Module> modules = new ArrayList<Module>();

        for (Module m : Main.moduleManager.modules) {
            if (m.getCategory() == c) {
                modules.add(m);
            }
        }
        return modules;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        modules.forEach(module -> {
            if (module.isEnabled()) {
                module.onTick();
            }
        });
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        modules.forEach(module -> {
            if (module.isEnabled()) {
                module.onPlayerTick();
            }
        });
    }

    @SubscribeEvent
    public void onFastTick(TickEvent event) {
        modules.forEach(m -> {
            if (mc.world != null && mc.player != null) {
                try {
                    m.onFastTick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        modules.forEach(m -> {
            if (event.getEntityLiving() instanceof EntityPlayer) {
                if (mc.world != null && mc.player != null) {
                    try {
                        m.onUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (mc.world == null) return;
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(Minecraft.getMinecraft().world);
                if (!(entity instanceof EntityPlayer) || entity.getName().equalsIgnoreCase(Minecraft.getMinecraft().player.getName()))
                    return;
                MinecraftForge.EVENT_BUS.post(new TotemPopEvent((EntityPlayer) entity));
            }
        }
    }
}
