package com.snoworange.mousse;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.snoworange.mousse.command.CommandManager;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.ModuleManager;
import com.snoworange.mousse.ui.ClickGui;
import com.snoworange.mousse.ui.Hud;
import com.snoworange.mousse.ui.theme.ThemeManager;
import com.snoworange.mousse.util.misc.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
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

//hi
@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class Main {

    public static ModuleManager moduleManager;
    public static Hud hud;
    public static KeyBinding ClickGUI;
    public static KeyBinding ShulkerPeek;
    public static ThemeManager themeManager;
    public static CommandManager commandManager;
    public static ClickGui clickgui;

    //

    public static final String MOD_ID = "mousse";
    public static final String NAME = "Mousse";
    public static final String VERSION = "v0.8.0";

    public static Minecraft mc = Minecraft.getMinecraft();

    //
    @Mod.Instance
    public Main instance;

    public void initFilesystem() {
        FileUtils.createDirectory();
        FileUtils.loadAll();
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.EVENT_BUS.register(new Hud());

        Display.setTitle(Main.NAME + " " + Main.VERSION);

        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        hud = new Hud();
        clickgui = new ClickGui();
        themeManager = new ThemeManager();

        ClickGUI = new KeyBinding("ClickGUI", Keyboard.KEY_NONE, "Mousse");
        ShulkerPeek = new KeyBinding("ShulkerPreview Peek Binding", Keyboard.KEY_NONE, "Mousse");

        ClientRegistry.registerKeyBinding(ClickGUI);
        ClientRegistry.registerKeyBinding(ShulkerPeek);

        this.initFilesystem();
    }

    @Mod.EventHandler
    public void post(FMLPostInitializationEvent event) {

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

    public static void sendMessage(String msg) {

        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) return;

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString( ChatFormatting.DARK_GREEN + "[" + Main.NAME + "] " + ChatFormatting.RESET + msg));
    }

    @SubscribeEvent
    public void displayGuiScreen(TickEvent.ClientTickEvent event) {
        if (Main.ClickGUI.isPressed()) {
            FileUtils.loadTheme(FileUtils.mousse);
            mc.displayGuiScreen(new ClickGui());
        }
    }
}
