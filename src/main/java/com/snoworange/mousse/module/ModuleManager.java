package com.snoworange.mousse.module;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.modules.combat.*;
import com.snoworange.mousse.module.modules.exploit.SecretClose;
import com.snoworange.mousse.module.modules.misc.*;
import com.snoworange.mousse.module.modules.movement.*;
import com.snoworange.mousse.module.modules.player.Capes;
import com.snoworange.mousse.module.modules.player.NoFall;
import com.snoworange.mousse.module.modules.movement.Scaffold;
import com.snoworange.mousse.module.modules.render.ShulkerPeek;
import com.snoworange.mousse.module.modules.render.Test3DRenderer;

import java.util.*;

public class ModuleManager {

    public static ModuleManager instance;

    public ArrayList<Module> modules;

    public ModuleManager() {
        (modules = new ArrayList<Module>()).clear();

        //COMBAT

        modules.add(new Auto32k());
        modules.add(new Dispenser32k());
        modules.add(new Surround());
        modules.add(new AutoXP());
        //modules.add(new AutoTotem());

        //EXPLOIT
        modules.add(new SecretClose());

        //JOKES

        //MOVEMENT
        //modules.add(new PacketShift());
        modules.add(new EntityFly());
        //modules.add(new AirJump());
        //modules.add(new Fly());
        modules.add(new WaterSpeed());
        modules.add(new Scaffold());

        //PLAYER
        modules.add(new NoFall());
        modules.add(new Capes());

        //MISC
        modules.add(new Disconnector());
        modules.add(new AutoSwing());
        modules.add(new BetterChat());
        modules.add(new DiscordRPC());
        modules.add(new Test3DRenderer());
        modules.add(new Announcer());

        //RENDER
        modules.add(new ShulkerPeek());

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
}
