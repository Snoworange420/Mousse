package com.snoworange.mousse.module;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.modules.combat.*;
import com.snoworange.mousse.module.modules.misc.*;
import com.snoworange.mousse.module.modules.movement.*;
import com.snoworange.mousse.module.modules.player.NoFall;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    public ArrayList<Module> modules;

    public ModuleManager() {
        (modules = new ArrayList<Module>()).clear();

        //COMBAT

        //modules.add(new Auto32k());
        modules.add(new TickAura());
        modules.add(new AutoTotem());

        //EXPLOIT

        //JOKES

        //MOVEMENT
        //modules.add(new PacketShift());
        modules.add(new EntityFly());
        modules.add(new HighJump());
        modules.add(new LongJump());
        modules.add(new AirJump());
        modules.add(new Speed());
        modules.add(new Fly());

        //PLAYER
        modules.add(new NoFall());

        //MISC
        modules.add(new Disconnector());
        modules.add(new AutoSwing());
        modules.add(new AntiBv8());
        modules.add(new BetterChat());

        //WORK IN PROGRESS

        //
    }

    public Module getModule (String name) {
        for(Module m : this.modules) {
            if(m.getName().equalsIgnoreCase(name)) {
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
