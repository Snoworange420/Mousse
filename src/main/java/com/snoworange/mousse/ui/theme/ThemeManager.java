package com.snoworange.mousse.ui.theme;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.ui.theme.themes.Mousse;
import com.snoworange.mousse.ui.theme.themes.Spring;
import com.snoworange.mousse.ui.theme.themes.SummerOrange;

import java.util.concurrent.CopyOnWriteArrayList;

public class ThemeManager {

    public CopyOnWriteArrayList<Theme> themes = new CopyOnWriteArrayList<Theme>();

    public int index = 0;

    public ThemeManager() {
        themes.add(new Mousse());
        themes.add(new Spring());
        themes.add(new SummerOrange());
    }

    public Theme getCurrentTheme() {
        return themes.get(index);
    }

    public boolean setTheme(String name) {
        for (Theme theme : themes) {
            if (theme.getName() == name) {
                this.index = themes.indexOf(theme);
                return true;
            }
        }
        return false;
    }

    public void cycle() {
        if(index < themes.size() - 1) {
            index++;
        } else {
            index = 0;
        }
    }

    public static Theme getTheme() {
        return Main.themeManager.getCurrentTheme();
    }
}
