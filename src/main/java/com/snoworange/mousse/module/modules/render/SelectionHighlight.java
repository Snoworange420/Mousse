package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.ui.Hud;
import com.snoworange.mousse.util.render.ColorUtils;
import com.snoworange.mousse.util.render.RenderUtils2;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class SelectionHighlight extends Module {

    BooleanSetting rainbow;

    public SelectionHighlight() {
        super("SelectionHighlight", "highlights block youre selecting", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void init() {
        super.init();

        rainbow = new BooleanSetting("Rainbow", false);
        addSetting(rainbow);
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        if (this.isEnabled()) {
            try {
                if (mc.world != null && mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null && mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getMaterial().isSolid()) {
                    if (rainbow.isEnable()) {
                        RenderUtils2.drawBlockOutline(mc.objectMouseOver.getBlockPos(), 0.0, new Color(ColorUtils.rainbow(0, 255)), new Color(ColorUtils.rainbow(0, 255)));
                    } else {
                        RenderUtils2.drawBlockOutline(mc.objectMouseOver.getBlockPos(), 0.0, new Color(255, 255, 255), new Color(255, 255, 255));
                    }
                }
            } catch (NullPointerException ignored) {
                //empty because yes
            }
        }
    }
}