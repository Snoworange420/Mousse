package com.snoworange.mousse.module;

public enum Category {

    COMBAT("Combat"), EXPLOIT("Exploit"), RENDER("Render"), MOVEMENT("Movement"), PLAYER("Player"), MISC("Misc"), SYSTEM("System");

    public String name;
    public boolean opened;

    Category(String name) {
        this.name = name;
        this.opened = true;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public void toggle() {
        if (!this.opened) {
            this.opened = true;
        } else if (this.opened) {
            this.opened = false;
        }
    }
}