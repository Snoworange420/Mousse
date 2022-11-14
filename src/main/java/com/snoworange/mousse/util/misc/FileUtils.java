package com.snoworange.mousse.util.misc;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.ModuleManager;
import com.snoworange.mousse.setting.Setting;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileUtils {

    public static File config;
    public static File mousse;

    public static void saveAll() {
        saveActiveModules(mousse);
        saveBinds(mousse);
        saveSettings(mousse);
        saveTheme(mousse);
    }

    public static void loadAll() {
        loadActiveModules(mousse);
        loadBinds(mousse);
        loadSettings(mousse);
        loadTheme(mousse);
    }

    public static void createDirectory() {
        FileUtils.mousse = new File("mousse");
        if (!FileUtils.mousse.exists()) {
            FileUtils.mousse.mkdirs();
        }
        FileUtils.config = new File("mousse/configs");
        if (!FileUtils.config.exists()) {
            FileUtils.config.mkdir();
        }
    }

    public static void saveActiveModules(final File file) {
        Exception ex;
        try {
            final File modules = new File(file.getAbsolutePath(), "ActiveModules.txt");
            final BufferedWriter bw = new BufferedWriter(new FileWriter(modules));
            for (final Module m : ModuleManager.instance.getModuleList()) {
                bw.write(m.getName() + ":");
                if (m.isToggled()) {
                    bw.write("true");
                }
                else {
                    bw.write("false");
                }
                bw.write("\r\n");
            }
            bw.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }

    public static void loadActiveModules(final File file) {
        Exception ex;
        try {
            final File modules = new File(file.getAbsolutePath(), "ActiveModules.txt");
            if (!modules.exists()) {
                modules.createNewFile();
                return;
            }
            final BufferedReader br = new BufferedReader(new FileReader(modules));
            final List<String> linezz = Files.readAllLines(modules.toPath());
            for (final String line : linezz) {
                final String[] regex = line.split(":");
                final Module m = ModuleManager.instance.getModule(regex[0]);
                if (Boolean.parseBoolean(regex[1])) {
                    m.disable();
                    m.enable();
                }
            }
            br.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }

    public static void saveBinds(final File file) {
        Exception ex;
        try {
            final File binds = new File(file.getAbsolutePath(), "Keybinds.txt");
            final BufferedWriter bw = new BufferedWriter(new FileWriter(binds));
            for (final Module m : ModuleManager.instance.getModuleList()) {
                bw.write(m.getName() + ":" + m.getKey());
                bw.write("\r\n");
            }
            bw.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }

    public static void loadBinds(final File file) {
        Exception ex;
        try {
            final File binds = new File(file.getAbsolutePath(), "Keybinds.txt");
            if (!binds.exists()) {
                binds.createNewFile();
                return;
            }
            final BufferedReader br = new BufferedReader(new FileReader(binds));
            final List<String> linezz = Files.readAllLines(binds.toPath());
            for (final String line : linezz) {
                final String[] regex = line.split(":");
                final Module m = ModuleManager.instance.getModule(regex[0]);

                if (!(Integer.parseInt(regex[1]) <= 0)) {
                    m.setKey(Integer.parseInt(regex[1]));
                }
            }
            br.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }

    public static void saveSettings(final File file) {
        Exception ex;
        try {
            final File settings = new File(file.getAbsolutePath(), "Settings.txt");
            final BufferedWriter bw = new BufferedWriter(new FileWriter(settings));
            for (final Module m : ModuleManager.instance.getModuleList()) {
                bw.write(m.getName() + ":");
                for (final Setting<?> s : m.settings) {
                    bw.write(s.name + "-" + s.value + ":");
                }
                bw.write("\r\n");
            }
            bw.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }

    public static void loadSettings(final File file) {
        Exception ex;
        try {
            final File settings = new File(file.getAbsolutePath(), "Settings.txt");
            if (!settings.exists()) {
                settings.createNewFile();
                return;
            }
            final BufferedReader br = new BufferedReader(new FileReader(settings));
            final List<String> linezz = Files.readAllLines(settings.toPath());
            for (final String line : linezz) {
                final String[] regex = line.split(":");
                for (final Module m : ModuleManager.instance.getModuleList()) {
                    for (int i = 1; i < regex.length; ++i) {
                        final String term = regex[i];
                        final String[] pair = term.split("-");
                        final Setting<?> s = (Setting<?>) m.settings;
                        if (s instanceof BooleanSetting) {
                            final BooleanSetting sb = (BooleanSetting) s;
                            sb.setEnable(Boolean.parseBoolean(pair[1]));
                        }
                        if (s instanceof NumberSetting) {
                            final NumberSetting sd = (NumberSetting) s;
                            sd.setValue(Double.parseDouble(pair[1]));
                        }
                        if (s instanceof ModeSetting) {
                            final ModeSetting sm = (ModeSetting) s;
                            sm.is(pair[1]);
                        }
                    }
                }
            }
            br.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }

    public static void saveTheme(final File file) {
        Exception ex;
        try {
            final File theme = new File(file.getAbsolutePath(), "Theme.txt");
            final BufferedWriter bw = new BufferedWriter(new FileWriter(theme));

            bw.write(String.valueOf(Main.themeManager.index));
            bw.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }

    public static void loadTheme(final File file) {
        Exception ex;
        try {
            final File theme = new File(file.getAbsolutePath(), "Theme.txt");
            if (!theme.exists()) {
                theme.createNewFile();
                return;
            }
            final BufferedReader br = new BufferedReader(new FileReader(theme));
            final List<String> linezz = Files.readAllLines(theme.toPath());
            for (final String line : linezz) {
                try {
                    if (Integer.parseInt(line) > -1) {
                        Main.themeManager.index = Integer.parseInt(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            br.close();
            return;
        }
        catch (Exception e) {
            ex = e;
        }
        ex.printStackTrace();
    }
}
