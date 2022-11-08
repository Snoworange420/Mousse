package com.snoworange.mousse.module.modules.render;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.event.listeners.PacketEvent;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.module.modules.exploit.SecretClose;
import com.snoworange.mousse.module.modules.exploit.XCarry;
import com.snoworange.mousse.setting.settings.NumberSetting;
import com.snoworange.mousse.util.render.ColorUtils;
import com.snoworange.mousse.util.render.RenderUtils2;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.gui.inventory.GuiInventory;
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
    public BlockPos hopperPos;
    public double wallHeight;

    @Override
    public void init() {
        super.init();
        height = new NumberSetting("Height", 1, 0, 6, 0.1);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (this.toggled && event.getEntityLiving() instanceof EntityPlayer) {

            if (mc.world == null || mc.player == null) return;

            if (this.hopperPos == null) {
                return;
            }
            if (!(mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper) || mc.player.getDistanceSq(this.hopperPos) > Double.longBitsToDouble(Double.doubleToLongBits(0.925710585628769) ^ 0x7FBDDF6BCE5AC52FL)) {
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

            if (this.wallHeight < (double) height.getValue()) {
                this.wallHeight += Double.longBitsToDouble(Double.doubleToLongBits(3751.2945874163925) ^ 0x7FD9347793877A0BL) * (double) height.getValue();
            } else if (this.wallHeight > (double) height.getValue()) {
                this.wallHeight -= Double.longBitsToDouble(Double.doubleToLongBits(114.68668265750983) ^ 0x7FD8D113DC7F3B77L);
            }
            if (this.hopperPos != null) {
                RenderUtils2.drawCircle(this.hopperPos, Double.longBitsToDouble(Double.doubleToLongBits(0.14553988619673233) ^ 0x7FE2A10D0DBD4061L), this.wallHeight, new Color(ColorUtils.BESTCOLOR(0, 255)), new Color(ColorUtils.BESTCOLOR(10, 255)));
                this.oldHopperPos = this.hopperPos;
                this.radius = Double.longBitsToDouble(Double.doubleToLongBits(0.14070361133713452) ^ 0x7FE20293708FA091L);
                return;
            }
            if (this.hopperPos == null && this.oldHopperPos != null) {
                RenderUtils2.drawCircle(this.oldHopperPos, this.radius, this.wallHeight, new Color(ColorUtils.BESTCOLOR(0, 255)), new Color(ColorUtils.BESTCOLOR(10, 255)));
                if (this.wallHeight > Double.longBitsToDouble(Double.doubleToLongBits(1.1989844897406259E308) ^ 0x7FE557B6C1188A7BL)) {
                    this.wallHeight -= Double.longBitsToDouble(Double.doubleToLongBits(219.7551656050837) ^ 0x7FD2E1B3C896855BL);
                    return;
                }
                if (this.radius > Double.longBitsToDouble(Double.doubleToLongBits(6.522680943073321E306) ^ 0x7FA293C429F2655FL)) {
                    this.radius -= Double.longBitsToDouble(Double.doubleToLongBits(90.1592080629349) ^ 0x7FEF13A9EE9A7DBDL);
                } else {
                    this.radius = Double.longBitsToDouble(Double.doubleToLongBits(7.96568863695466E307) ^ 0x7FDC5BD9D9AD2AC5L);
                }
            }
        }
    }
}
