package com.snoworange.mousse.module.modules.misc;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Disconnector extends Module {

    public Disconnector() {
        super("Disconnector", "", Category.MISC, 0);
    }

    @Override
    public void onEnable() {
        mc.world.sendQuittingDisconnectingPacket();
        mc.player.connection.onDisconnect(new TextComponentString("Disconnecting!"));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (this.isEnabled()) disable();
    }
}
