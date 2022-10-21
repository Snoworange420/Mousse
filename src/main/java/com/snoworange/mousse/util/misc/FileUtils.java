package com.snoworange.mousse.util.misc;

import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.ModuleManager;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileUtils {

    public static File config;
    public static File mousse;

    public static void saveAll() {
        saveActiveModules(mousse);
        saveBinds(mousse);
    }

    public static void loadAll() {
        loadActiveModules(mousse);
        loadBinds(mousse);
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

                if (m.getKey() != 0) {
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
}
