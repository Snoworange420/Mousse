package com.snoworange.mousse.util.render;

import com.snoworange.mousse.misc.particles.ParticleGenerator;

public class ParticleUtils {

    private static final ParticleGenerator particleGenerator = new ParticleGenerator(0);

    public static void drawParticles(int mouseX, int mouseY) {
        particleGenerator.draw(mouseX, mouseY);
    }
}
