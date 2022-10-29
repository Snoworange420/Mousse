package com.snoworange.mousse.command.impl;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.Command;
import com.snoworange.mousse.util.misc.FileUtils;
import net.minecraft.client.Minecraft;

public class Save extends Command {

    public Save() {
        super("Save", "saves stuff", "save", "sv");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 0) {
            FileUtils.saveAll();
            Main.sendMessage("Saved configs!");
        }
    }
}
