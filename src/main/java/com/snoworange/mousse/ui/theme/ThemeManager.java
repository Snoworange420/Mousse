package com.snoworange.mousse.ui.theme;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.ui.theme.themes.Mousse;
import com.snoworange.mousse.ui.theme.themes.SummerOrange;

import java.util.concurrent.CopyOnWriteArrayList;

public class ThemeManager {

    public CopyOnWriteArrayList<Theme> themes = new CopyOnWriteArrayList<Theme>();

    public int index=0;

    public ThemeManager() {
        themes.add(new SummerOrange());
        //themes.add(new Mousse());
    }

    public Theme getCurrentTheme() {
        return themes.get(index);
    }

    public boolean setTheme(String name) {
        for(Theme theme : themes) {
            if(theme.getName() == name) {
                this.index = themes.indexOf(theme);
                return true;
            }
        }
        return false;
    }

    public static Theme getTheme() {
        return Main.themeManager.getCurrentTheme();
    }
}
