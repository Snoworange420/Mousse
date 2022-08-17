package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class BetterChat extends Module {

    public BetterChat() {
        super("BetterChat", "", Category.MISC, 0);
    }

    public boolean greentext;
    boolean customPrefix;
    boolean customSuffix;
    boolean moussesuffix;
    public String prefix;
    public String suffix;

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onChat(final ClientChatEvent event) {

        for (final String s : Arrays.asList("/", ".", "-", ",", ":", ";", "'", "\"", "+", "@", "#", "&")) {
            if (event.getMessage().startsWith(s)) return;
        }

        greentext = true;
        moussesuffix = true;

        if (greentext) {
            event.setMessage("> " + event.getMessage());
        }

        if (moussesuffix) {
            event.setMessage(event.getMessage() + " / Mousse");
        }

        if (customPrefix) {
            event.setMessage(prefix + event.getMessage());
        }

        if (customSuffix) {
            event.setMessage(event.getMessage() + suffix);
        }
    }
}
