package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class Disconnector extends Module {

    public Disconnector() {
        super("Disconnector", "", Category.MISC, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.world.sendQuittingDisconnectingPacket();
        mc.player.connection.onDisconnect(new TextComponentString("Disconnecting!"));
        onDisable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
