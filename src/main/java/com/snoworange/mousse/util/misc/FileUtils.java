package com.snoworange.mousse.util.misc;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.ModuleManager;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.KeyBindSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    public static File config;
    public static File mousse;

    public static void saveAll() {
        saveActiveModules(mousse);
        saveBinds(mousse);
        saveModuleSetting(mousse);
    }

    public static void loadAll() {
        loadActiveModules(mousse);
        loadBinds(mousse);
        loadModuleSetting(mousse);
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

    public static void saveModuleSetting(final File directory) {

        File setting = config;

        if(!directory.exists()){
            directory.mkdir();
        }
        if(!setting.exists()){
            setting.mkdir();
        }

        try{
            for (Module m : Main.moduleManager.getModuleList()){
                File module = new File(setting, m.getName());
                if (!module.exists()) {
                    module.createNewFile();
                }

                PrintWriter pw = new PrintWriter(module);

                final String[] str = {""};

                str[0] += m.isToggled()?"1":"0";
                str[0] += "\n";

                m.settings.forEach(s -> {
                    if(s instanceof KeyBindSetting){
                        str[0] += "0"+String.valueOf(((KeyBindSetting) s).getKeyCode());
                    }
                    if(s instanceof BooleanSetting){
                        str[0] += ((BooleanSetting)s).isEnable()?"11":"10";
                    }
                    if(s instanceof ModeSetting){
                        str[0] += "2"+ ((ModeSetting) s).index;
                    }
                    if(s instanceof NumberSetting){
                        str[0] += "3"+ String.valueOf(((NumberSetting) s).value);
                    }
                    str[0] += "\n";
                });

                pw.print(str[0]);
                pw.close();
            }
        }catch (IOException e){

        }
    }

    public static void loadModuleSetting(final File directory) {

        File setting = config;

        if (setting.isDirectory()){
            for (Module m : Main.moduleManager.getModuleList()) {
                File SettingFile = new File(setting, m.getName());
                try {
                    FileReader filereader = new FileReader(SettingFile);
                    int ch;
                    String str = "";
                    while((ch = filereader.read()) != -1){
                        str += String.valueOf((char)ch);
                    }
                    int i = 0;
                    for (String val : Arrays.asList(str.split("\n"))) {
                        if(i == 0) {
                            //m.setToggled(val.equals("1")?true:false);
                            System.out.println("Balls");
                        }else {

                            String dat = val.substring(1);
                            if (val.startsWith("0")) {
                                KeyBindSetting bind = (KeyBindSetting)m.settings.get(i-1);
                                bind.keyCode = Integer.parseInt(dat);
                            }
                            if (val.startsWith("1")) {
                                BooleanSetting bind = (BooleanSetting)m.settings.get(i-1);
                                bind.setEnable(val.equals("1"));
                            }
                            if (val.startsWith("2")) {
                                ModeSetting bind = (ModeSetting)m.settings.get(i-1);
                                bind.index = Integer.parseInt(dat);
                            }
                            if (val.startsWith("3")) {
                                NumberSetting bind = (NumberSetting)m.settings.get(i-1);
                                bind.value = Double.parseDouble(dat);
                            }
                        }
                        i++;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException | ClassCastException | StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    SettingFile.delete();
                }
            }
        }
    }
}
