package com.snoworange.mousse.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.snoworange.mousse.Main;
import net.minecraft.client.Minecraft;

public class Discord {

    private static String discordID = "1013887562357678148";
    private static DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    private static DiscordRPC discordRPC = DiscordRPC.INSTANCE;

    public static void startRPC() {
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2));
        discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);

        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().world != null) {
            discordRichPresence.state = "Logged in as " + Minecraft.getMinecraft().player.getName();
        } else discordRichPresence.state = "In the memu";

        discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        discordRichPresence.largeImageKey = "matchamousse";
        discordRichPresence.details = "Enjoying " + Main.NAME + " " + Main.VERSION;;
        discordRPC.Discord_UpdatePresence(discordRichPresence);
    }

    public static void stopRPC() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();
    }
}
