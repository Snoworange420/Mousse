package com.snoworange.mousse.command.impl;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.Command;
import com.snoworange.mousse.util.misc.FileUtils;

public class Load extends Command {

    public Load() {
        super("Load", "loads stuff", "load", "sv");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 0) {
            FileUtils.loadAll();
            Main.sendMessage("Loaded configs! (may prob not work)");
        }
    }
}