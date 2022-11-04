package com.snoworange.mousse.module.modules.player;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

public class Capes extends Module {

    public Capes() {
        super("Capes", "", Category.PLAYER, 0);
    }
    public static Capes instance;
    public static final ResourceLocation MOUSSE_CAPE = new ResourceLocation("textures/cape.png");

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Capes getInstance() {
        if (instance == null) {
            instance = new Capes();
        }
        return instance;
    }

    public static ResourceLocation getCapeResource(AbstractClientPlayer player) {
        return MOUSSE_CAPE;
    }
}
