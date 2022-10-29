package com.snoworange.mousse.util.easing;

import com.snoworange.mousse.util.render.AnimationUtils;
public abstract class EaseValue {

    public EaseValue() {
        this.timer = new Time();
    }

    public float duration;
    public AnimationUtils.Mode easeMode;
    public Time timer;

    public abstract void updateEase();

}