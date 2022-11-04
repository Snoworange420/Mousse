package com.snoworange.mousse.command.impl;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.Command;
public class Peek extends Command {
    public Peek() {
        super("Peek", "Peeks inside shulker box", "peek", "pe");
    }

    @Override
    public void onCommand(String[] args, String command) {
        Main.moduleManager.getModule("ShulkerPeek").toggle();
        Main.sendMessage("Opened shulker box.");
    }
}
