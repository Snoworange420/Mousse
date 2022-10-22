package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.util.misc.FileUtils;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetterChat extends Module {

    public List<String> chatModifiers;

    public BetterChat() {
        super("BetterChat", "", Category.MISC, 0);
        chatModifiers = new ArrayList<String>();
    }

    public static boolean customPrefix = false;
    public static boolean customSuffix = false;

    public boolean loadStuff() {
        while (true) {
            try {
                final File config = new File(FileUtils.mousse.getAbsolutePath(), "BetterChat.txt");
                if (!config.exists()) {
                    config.createNewFile();
                }
                chatModifiers = Files.readAllLines(config.toPath());
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (!customPrefix || !customSuffix) {
            return;
        }

        if (this.loadStuff()) {
            Main.sendMessage("Successfully loaded prefix and suffix.");
        } else {
            Main.sendMessage("Failed to load prefix and suffix. Ensure that you have an BetterChat.txt in your Mousse folder.");
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onChat(final ClientChatEvent event) {

        for (final String s : Arrays.asList("/", ".", "-", ",", ":", ";", "'", "\"", "+", "@", "#", "&", "_")) {
            if (event.getMessage().startsWith(s)) return;
        }

        if (customPrefix) {

            if (this.chatModifiers.size() == 0) {
                Main.sendMessage("The BetterChat.txt file is empty. No prefix will be sent.");
                return;
            }

            final String prefix = this.chatModifiers.get(0);

            event.setMessage(prefix + event.getMessage());
        } else if (!customPrefix) {
            event.setMessage("> " + event.getMessage());
        }

        if (customSuffix) {

            if (this.chatModifiers.size() <= 1) {
                Main.sendMessage("The suffix text in BetterChat.txt is empty. No suffix will be sent.");
                return;
            }

            final String suffix = this.chatModifiers.get(1);

            event.setMessage(event.getMessage() + suffix);

        } else if (!customSuffix) {
            event.setMessage(event.getMessage() + " / Mousse");
        }
    }
}
