package com.snoworange.mousse.command.impl;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.Command;
import com.snoworange.mousse.module.modules.combat.Dispenser32k;

public class Dispenser32kAutoClose extends Command {

    public Dispenser32kAutoClose() {
        super("Dispenser32kAutoClose", "exits automatically hopper gui when you have 32k", "d32kac", "d32kac");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 1) {

            if (args[0].equals("true") || Boolean.parseBoolean(args[0])) {
                Dispenser32k.autoClose = true;
                Main.sendMessage("Set autoclose to true");
            } else if (args[0].equals("false")) {
                Dispenser32k.autoClose = false;
                Main.sendMessage("Set autoclose to false");
            } else {
                Main.sendMessage("?");
            }
        }
    }
}
