package com.snoworange.mousse.module.modules.player;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class Capes extends Module {

    public static Map<String, String[]> UUIDs = new HashMap<String, String[]>();

    public Capes() {
        super("Capes", "", Category.PLAYER, 0);

        UUIDs.put("Jonakip", new String[]{"05b09c32-fe1b-4563-bbc6-db9968070d94"});
        UUIDs.put("Snoworange2b2t", new String[]{"fe1ad3a4-9e5d-4f68-a04c-2e74bd07df42"});
    }
    private static Capes instance;
    public static final ResourceLocation JONAKIP_CAPE = new ResourceLocation("assets/textures/CapeJonakip.png");
    public static final ResourceLocation SNOWORANGE_CAPE = new ResourceLocation("assets/textures/CapeSnoworange.png");
    public static final ResourceLocation MOUSSE_CAPE = new ResourceLocation("assets/textures/CapeMousse.png");

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

        for (String name : UUIDs.keySet()) {
            for (String uuid : UUIDs.get(name)) {
                if (name.equalsIgnoreCase("Jonakip") && player.getUniqueID().toString().equals(uuid)) {
                    return JONAKIP_CAPE;
                }

                if (name.equalsIgnoreCase("Snoworange2b2t") && player.getUniqueID().toString().equals(uuid)) {
                    return SNOWORANGE_CAPE;
                }

                if (!player.getUniqueID().toString().equals(uuid)) continue;
                //return MOUSSE_CAPE;
                //up is correct, but for testing we return SNOWORANGE_CAPE (minecon2011 lol)
                return SNOWORANGE_CAPE;
            }
        }
        return null;
    }

    public static boolean hasCape(UUID uuid) {
        Iterator<String> iterator = UUIDs.keySet().iterator();
        if (iterator.hasNext()) {
            String name = iterator.next();
            return Arrays.asList((Object[]) UUIDs.get(name)).contains(uuid.toString());
        }
        return false;
    }
}
