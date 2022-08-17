package com.snoworange.mousse;

import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.ModuleManager;
import com.snoworange.mousse.module.modules.combat.Auto32kSkidHopper;
import com.snoworange.mousse.ui.ClickGui;
import com.snoworange.mousse.ui.Hud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION)
public class Main {

    public static ModuleManager moduleManager;
    public static Hud hud;
    public static KeyBinding ClickGUI;

    //

    public static boolean is32kEnabled;
    public static int cps = 13;

    //

    public static final String MOD_ID = "mousse";
    public static final String NAME = "Mousse";
    public static final String VERSION = "v0.1";

    public static Minecraft mc = Minecraft.getMinecraft();

    //
    private ClickGui clickgui;

    @Mod.Instance
    public Main instance;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.EVENT_BUS.register(new Hud());

        Display.setTitle(Main.NAME + " " + Main.VERSION);

        moduleManager = new ModuleManager();
        hud = new Hud();
        clickgui = new ClickGui();

        ClickGUI = new KeyBinding("ClickGUI", Keyboard.KEY_NONE, "Mousse");
        ClientRegistry.registerKeyBinding(ClickGUI);
    }

    @Mod.EventHandler
    public void PostInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void post(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Auto32kSkidHopper());
    }


    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        if(Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;
        try {
            if (Keyboard.isCreated()) {
                if (Keyboard.getEventKeyState()) {
                    int keyCode = Keyboard.getEventKey();
                    if (keyCode <= 0)
                        return;
                    for (Module m : moduleManager.modules) {
                        if (m.getKey() == keyCode && keyCode > 0) {
                            m.toggle();
                        }
                    }
                }
            }
        } catch (Exception ex) {ex.printStackTrace();}
    }

    @SubscribeEvent
    public void showGuiScreen(TickEvent.PlayerTickEvent event) {
        if (Main.ClickGUI.isPressed()) {
            mc.displayGuiScreen(new ClickGui());
        }
    }
}
