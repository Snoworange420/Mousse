package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.event.listeners.PacketEvent;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.NumberSetting;
import com.snoworange.mousse.util.render.RenderUtils2;
import com.snoworange.mousse.util.render.RenderUtils3;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class HopperRadius extends Module {

    public HopperRadius() {
        super("HopperRadius", "idk", Category.RENDER);
    }

    NumberSetting height;
    public BlockPos oldHopperPos;
    public double radius;
    public double hitradius;
    public BlockPos hopperPos;
    public double wallHeight;

    @Override
    public void init() {
        super.init();
        height = new NumberSetting("Height", 0.5, 0, 6, 0.1);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.hopperPos = null;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.world == null || mc.player == null) return;

            if (this.hopperPos == null) {
                return;
            }

            if (!(mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper) || mc.player.getDistanceSqToCenter(this.hopperPos) > 65.0) {
                this.hopperPos = null;
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
                final BlockPos packetPos = packet.getPos();
                if (mc.world.getBlockState(packetPos).getBlock() instanceof BlockHopper) {
                    this.wallHeight = Double.longBitsToDouble(Double.doubleToLongBits(2.226615095116189E307) ^ 0x7FBFB542DC55A837L);
                    this.hopperPos = packetPos;
                }
            }
            if (event.getPacket() instanceof CPacketCloseWindow) {
                this.hopperPos = null;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            if (!(event.getPacket() instanceof SPacketCloseWindow)) {
                if (event.getPacket() instanceof SPacketOpenWindow) {
                    if (((SPacketOpenWindow) event.getPacket()).getWindowTitle().getUnformattedText().equalsIgnoreCase("Item Hopper")) {
                        return;
                    }
                    this.hopperPos = null;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {

        if (this.toggled) {

            if (mc.world == null || mc.player == null) return;

            if (this.wallHeight < (double) height.getValue() / 2) {
                this.wallHeight += 0.005 * (double) height.getValue();
            } else if (this.wallHeight > (double) height.getValue() / 2) {
                this.wallHeight -= 0.01;
            }

            if (this.hopperPos != null) {
                RenderUtils2.drawCircle(this.hopperPos, 7.5, this.wallHeight, new Color(131, 141, 59), new Color(131, 141, 59));
                RenderUtils3.drawCircle(this.hopperPos, 7.5 + 7, this.wallHeight, new Color(131, 141, 59), new Color(131, 141, 59));

                this.oldHopperPos = this.hopperPos;

                this.radius = 7.5;
                this.hitradius = radius + 7;

                return;
            }

            if (this.hopperPos == null && this.oldHopperPos != null) {
                RenderUtils2.drawCircle(this.oldHopperPos, this.radius, this.wallHeight, new Color(131, 141, 59), new Color(131, 141, 59));
                RenderUtils3.drawCircle(this.oldHopperPos, this.hitradius, this.wallHeight, new Color(131, 141, 59), new Color(131, 141, 59));

                if (this.wallHeight > 0) {
                    this.wallHeight -= 0.1;
                    return;
                }

                if (this.radius > 0) {
                    this.radius -= 0.1;
                } else {
                    this.radius = 0;
                }

                if (this.hitradius > 0) {
                    this.hitradius -= 0.2;
                } else {
                    this.hitradius = 0;
                }
            }
        }
    }
}