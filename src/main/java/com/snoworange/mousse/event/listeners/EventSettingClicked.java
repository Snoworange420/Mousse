package com.snoworange.mousse.event.listeners;

import com.snoworange.mousse.event.Event;
import com.snoworange.mousse.setting.Setting;

public class EventSettingClicked extends Event<EventSettingClicked> {

    Setting setting;

    public EventSettingClicked(Setting setting) {
        this.setting = setting;
    }

    public Setting getMessage() {
        return setting;
    }

    public void setMessage(Setting setting) {
        this.setting = setting;
    }
}