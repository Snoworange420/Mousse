package com.snoworange.mousse.command.impl;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.Command;
import com.snoworange.mousse.module.modules.combat.Dispenser32k;

public class Dispenser32kRedstoneDelay extends Command {

    public Dispenser32kRedstoneDelay() {
        super("Dispenser32kRedstoneDelay", "Temp command to set dispenser32k's redstone delay", "dispenser32kredstonedelay", "32krd");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 1) {
            Dispenser32k.redstoneDelay = Integer.parseInt(args[0]);
            Main.sendMessage("Set delay to " + args[0]);
        }
    }
}
