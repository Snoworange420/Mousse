package com.snoworange.mousse.module.modules.player;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class Capes extends Module {

    public static List<UUID> UUIDs = new ArrayList<>();

    public static Capes instance;

    public Capes() {
        super("Capes", "", Category.PLAYER, 0);

        UUIDs.add(UUID.fromString("fe1ad3a4-9e5d-4f68-a04c-2e74bd07df42")); //Snoworange6919
        UUIDs.add(UUID.fromString("05b09c32-fe1b-4563-bbc6-db9968070d94")); //Jonakip
        UUIDs.add(UUID.fromString("42069420-6969-6969-6969-420420420420")); //huu_bo
        UUIDs.add(UUID.fromString("7e31f4df-d166-44ef-9065-6231420226ab")); //HGkidudeski
        UUIDs.add(UUID.fromString("e2857f53-ffa6-468e-bc7a-9aa43c143e45")); //Pseudonymous000

        instance = this;
    }

    public static final ResourceLocation MOUSSE_CAPE = new ResourceLocation("textures/mousse_cape.png");
    public static final ResourceLocation SNOWORANGE_CAPE = new ResourceLocation("textures/snoworange_cape.png");
    public static final ResourceLocation JONAKIP_CAPE = new ResourceLocation("textures/jonakip_cape.png");
    public static final ResourceLocation HUUB_CAPE = new ResourceLocation("textures/huub_cape.png");
    public static final ResourceLocation ELMO_CAPE = new ResourceLocation("textures/elmo_cape.png");
    public static final ResourceLocation PSEUDO_CAPE = new ResourceLocation("textures/pseudo_cape.png");

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

        if (player.getUniqueID().toString().equals("fe1ad3a4-9e5d-4f68-a04c-2e74bd07df42")) {
            return SNOWORANGE_CAPE;
        }
        if (player.getUniqueID().toString().equals("05b09c32-fe1b-4563-bbc6-db9968070d94")) {
            return JONAKIP_CAPE;
        }
        if (player.getUniqueID().toString().equals("42069420-6969-6969-6969-420420420420")) {
            Main.sendMessage("Sorry Huub ik heb nog geen texture");
            return HUUB_CAPE;
        }
        if (player.getUniqueID().toString().equals("7e31f4df-d166-44ef-9065-6231420226ab")) {
            return ELMO_CAPE;
        }
        if (player.getUniqueID().toString().equals("e2857f53-ffa6-468e-bc7a-9aa43c143e45")) {
            return PSEUDO_CAPE;
        }

        if (player.getName().equals(Minecraft.getMinecraft().getSession().getUsername())) {
            return MOUSSE_CAPE;
        }

        return null;
    }
}
