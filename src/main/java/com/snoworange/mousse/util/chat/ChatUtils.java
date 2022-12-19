package com.snoworange.mousse.util.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.snoworange.mousse.Main;
import com.snoworange.mousse.command.Command;
import com.snoworange.mousse.command.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Pattern;

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

    public static final String prefix;
    public static final Minecraft mc;

    public static void sendMessage(Object message, Object... arguments) {

        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) return;

        sendMessageWithDeletionID(message, 420420, arguments);
    }

    public static void sendMessageWithDeletionID(Object message, int id, Object... arguments) {
        String stringMessage = message.toString();
        for (Object argument : arguments) {
            String regex = Pattern.quote("{}");
            stringMessage = stringMessage.replaceFirst(regex, argument.toString());
        }
        TextComponentString textComponent = new TextComponentString(prefix + stringMessage);
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(textComponent, id);
    }

    static {
        mc = Minecraft.getMinecraft();
        prefix = TextFormatting.DARK_GREEN + "[" + Main.NAME + "] " + TextFormatting.RESET;
    }
}