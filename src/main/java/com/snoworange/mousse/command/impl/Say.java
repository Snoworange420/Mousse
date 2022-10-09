package com.snoworange.mousse.command.impl;

import com.snoworange.mousse.command.Command;
import net.minecraft.client.Minecraft;

public class Say extends Command {

    public Say() {
        super("Say", "Says thing is the chat", "say", "sa");
    }
    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 1) {
            StringBuilder msg = new StringBuilder();

            boolean flag = true;
            for (String string : args) {
                if (flag) {
                    flag = false;
                    continue;
                }
                msg.append(string).append(" ");
            }

            Minecraft.getMinecraft().player.sendChatMessage(args[0] + " " + msg.toString());
        }
    }
}
