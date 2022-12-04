package com.snoworange.mousse.util.chat;

import com.snoworange.mousse.command.Command;
import com.snoworange.mousse.command.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ChatUtils {

    public static String handleChatPrediction(final String s) {
        final Command possibleCommand = (Command) CommandManager.commands.stream().filter(ChatUtils::lambda$handleChatPrediction$0).findFirst().orElse(null);
        if (possibleCommand == null) {
            return null;
        }
        final String commandName = possibleCommand.getName().toLowerCase();
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft.getMinecraft().fontRenderer.drawString(CommandManager.prefix + commandName, 4, sr.getScaledHeight() - 12, -570425345);
        return commandName;
    }

    private static boolean lambda$handleChatPrediction$0(Command command) {
        return command.getName().toLowerCase().startsWith(command.getName().replaceAll(CommandManager.prefix, "").toLowerCase());
    }
}
