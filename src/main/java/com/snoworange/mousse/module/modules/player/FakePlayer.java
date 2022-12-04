package com.snoworange.mousse.module.modules.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FakePlayer extends Module {

    private final String name = "Fit";

    private EntityOtherPlayerMP _fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns a FakePlayer for testing", Category.PLAYER);
    }

    public static String getUuid(String name) {
        JsonParser parser = new JsonParser();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            if (UUIDJson.isEmpty())
                return "invalid name";
            JsonObject UUIDObject = (JsonObject) parser.parse(UUIDJson);
            return reformatUuid(UUIDObject.get("id").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private static String reformatUuid(String uuid) {
        String longUuid = "";
        longUuid = longUuid + uuid.substring(1, 9) + "-";
        longUuid = longUuid + uuid.substring(9, 13) + "-";
        longUuid = longUuid + uuid.substring(13, 17) + "-";
        longUuid = longUuid + uuid.substring(17, 21) + "-";
        longUuid = longUuid + uuid.substring(21, 33);
        return longUuid;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        this._fakePlayer = null;
        if (mc.player != null) {
            try {
                getClass();
                getClass();
                this._fakePlayer = new EntityOtherPlayerMP((World) mc.world, new GameProfile(UUID.fromString(getUuid("Fit")), "Fit"));
            } catch (Exception e) {
                getClass();
                this._fakePlayer = new EntityOtherPlayerMP((World) mc.world, new GameProfile(UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03"), "Fit"));
                Main.sendMessage("Failed to load uuid, setting another one.");
            }
            getClass();
            Main.sendMessage(String.format("%s has been spawned.", new Object[]{"Fit"}));
            this._fakePlayer.copyLocationAndAnglesFrom((Entity) mc.player);
            this._fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(-100, (Entity) this._fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        if (mc.world != null && mc.player != null) {
            super.onDisable();
            try {
                mc.world.removeEntity((Entity) this._fakePlayer);
            } catch (Exception exception) {
                Main.sendMessage(exception.toString());
                exception.printStackTrace();
            }
        }
    }
}