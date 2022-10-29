package com.snoworange.mousse.module;

public enum Category {

    COMBAT("Combat"), EXPLOIT("Exploit"), RENDER("Render"), MOVEMENT("Movement"), PLAYER("Player"), MISC("Misc");

    public String name;
    public int moduleIndex;

    Category(String name) {
        this.name = name;
    }
}
