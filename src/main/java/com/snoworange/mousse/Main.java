package com.snoworange.mousse;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.snoworange.mousse.command.CommandManager;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.ModuleManager;
import com.snoworange.mousse.ui.ClickGui;
import com.snoworange.mousse.ui.Hud;
import com.snoworange.mousse.ui.theme.ThemeManager;
import com.snoworange.mousse.util.misc.FileUtils;
import com.snoworange.mousse.util.render.JColor;
import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;


@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION)
public class Main {

    public static final Logger log = LogManager.getLogger("mousse");
    public static final EventBus EVENT_BUS = new EventManager();
    public static ThemeManager themeManager = new ThemeManager();

    public static ModuleManager moduleManager;

    public static Hud hud;
    public static KeyBinding ClickGUI;
    public static CommandManager commandManager;
    private ClickGui clickgui;
    //public ClickGui clickGui;

    //

    public static final String MOD_ID = "mousse";
    public static final String NAME = "Mousse";
    public static final String VERSION = "v0.3";

    public static Minecraft mc = Minecraft.getMinecraft();

    public static final JColor MOUSSE_COLOR = new JColor(131, 141, 59);

    //
    @Mod.Instance
    public Main instance;


    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
    }

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

        ClickGUI = new KeyBinding("ClickGUI", Keyboard.KEY_NONE, "Mousse");
        ClientRegistry.registerKeyBinding(ClickGUI);

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

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString( ChatFormatting.RESET + "[" + Main.NAME + "] " + msg));
    }

    @SubscribeEvent
    public void displayGuiScreen(TickEvent.ClientTickEvent event) {
        if (Main.ClickGUI.isPressed()) {
            mc.displayGuiScreen(new ClickGui());
        }
    }
}
