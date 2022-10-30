package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.misc.Discord;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;

public class DiscordRPC extends Module {

    public DiscordRPC() {
        super("DiscordRPC", "", Category.MISC, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Discord.startRPC();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Discord.stopRPC();
    }
}
