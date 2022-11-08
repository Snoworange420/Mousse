package com.snoworange.mousse.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    public List<Command> commands = new ArrayList<Command>();
    public String prefix = "&";

    public CommandManager() {
        MinecraftForge.EVENT_BUS.register(this);

        commands.add(new Peek());
        commands.add(new Say());
        //commands.add(new Dispenser32kRedstoneDelay());
        //commands.add(new Dispenser32kAutoClose());
        commands.add(new Load());
        commands.add(new Save());
    }

    @SubscribeEvent
    public void onChat(final ClientChatEvent event) {
        String message = event.getMessage();

        if (!message.startsWith(prefix)) {
            return;
        }

        event.setCanceled(true);
        message = message.substring(prefix.length());

        if (message.split(" ").length > 0) {

            boolean commandFound = false;
            String commandName = message.split(" ")[0];

            if (commandName.equals("") || commandName.equals("help")) {
                sendCommandDescriptions();
            } else {
                for (Command c : commands) {
                    if (c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
                        c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
                        commandFound = true;
                        break;
                    }
                }
                if (!commandFound) {
                    sendClientChatMessage(ChatFormatting.DARK_RED + "command does not exist, use " + ChatFormatting.ITALIC + prefix + "help " + ChatFormatting.RESET + "" + ChatFormatting.DARK_RED + "for help.", true);
                }
            }
        }
    }

    /*
    @EventHandler
    public Listener<ClientChatEvent> listener = new Listener<>(event -> {

        String message = event.getMessage();

        if (!message.startsWith(prefix)) {
            return;
        }

        event.setCanceled(true);
        message = message.substring(prefix.length());

        if(message.split(" ").length > 0) {
            boolean commandFound = false;
            String commandName = message.split(" ")[0];
            if(commandName.equals("") || commandName.equals("help")) {
                ChatFormatting GRAY = ChatFormatting.GRAY;
                ChatFormatting BOLD = ChatFormatting.BOLD;
                ChatFormatting RESET = ChatFormatting.RESET;
                sendCommandDescriptions();
            } else {
                for (Command c : commands) {
                    if (c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
                        c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
                        commandFound = true;
                        break;
                    }
                }
                if (!commandFound) {
                    sendClientChatMessage(ChatFormatting.DARK_RED + "command does not exist, use " + ChatFormatting.ITALIC + prefix + "help " + ChatFormatting.RESET + "" + ChatFormatting.DARK_RED + "for help.", true);
                }
            }
        }
    });
     */
    private void sendCommandDescriptions() {
        sendClientChatMessage("\n", false);
        for (Command c : Main.commandManager.commands) {
            sendClientChatMessage(c.name + " - " + c.description + " [" + c.syntax + "]", false);
        }
    }

    public void setCommandPrefix(String pre) {
        prefix = pre;

        //if (Main.saveLoad != null) {
        //    Main.saveLoad.save();
        //}
    }

    public void sendClientChatMessage(String message, boolean prefix) {
        String messageWithPrefix = ChatFormatting.WHITE + "" + ChatFormatting.ITALIC + "[" + Main.NAME + ": " + ChatFormatting.RESET + ChatFormatting.GRAY + message;

        if (prefix)
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(messageWithPrefix));
        else
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }

    public void sendCorrectionMessage(String name, String syntax) {
        String correction = "correct usage of " + ChatFormatting.WHITE + name + ChatFormatting.GRAY + " command -> " + ChatFormatting.WHITE + prefix + syntax + ChatFormatting.GRAY + ".";
        sendClientChatMessage(correction, true);
    }

}
