package com.snoworange.mousse.module;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.modules.combat.*;
import com.snoworange.mousse.module.modules.misc.*;
import com.snoworange.mousse.module.modules.movement.*;
import com.snoworange.mousse.module.modules.player.Capes;
import com.snoworange.mousse.module.modules.player.Jesus;
import com.snoworange.mousse.module.modules.player.NoFall;
import com.snoworange.mousse.module.modules.movement.Scaffold;
import com.snoworange.mousse.module.modules.render.NoPortal;
import com.snoworange.mousse.module.modules.render.ShulkerPeek;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        modules.add(new AutoEz());
        modules.add(new Criticals());
        modules.add(new AutoArmor());
        modules.add(new FastAura());

        //EXPLOIT

        //JOKES

        //MOVEMENT
        modules.add(new EntityFly());
        modules.add(new WaterSpeed());
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

        //MISC
        modules.add(new Disconnector());
        modules.add(new AutoSwing());
        modules.add(new BetterChat());
        modules.add(new DiscordRPC());
        modules.add(new Announcer());
        modules.add(new SaveTest());

        //RENDER
        modules.add(new ShulkerPeek());
        modules.add(new NoPortal());

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

    public static Module getModuleP(final Predicate predicate) {
        return (Module)getModulesP().stream().filter(predicate).findFirst().orElse(null);
    }

    public static List getModulesP(final Predicate predicate) {
        return (List)getModulesP().stream().filter(predicate).collect(Collectors.toList());
    }

    public static List getModulesP() {
        return getInstance().modules;
    }
}
