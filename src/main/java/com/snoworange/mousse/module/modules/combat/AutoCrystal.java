package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.ModeSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;
import com.snoworange.mousse.util.entity.EntityUtils;
import com.snoworange.mousse.util.math.MathUtils;
import com.snoworange.mousse.util.math.Timer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


//why does this shit not work,
public class AutoCrystal extends Module {

    private final Timer placeTimer = new Timer();

    private final Timer breakTimer = new Timer();

    private final Timer preditTimer = new Timer();

    private final Timer manualTimer = new Timer();

    /*
    private final NumberSetting attackFactor = new NumberSetting("PredictDelay", 0, 0, 200, 1);

    private final NumberSetting red = new NumberSetting("Red", 140, 0, 255, 1);

    private final NumberSetting green = new NumberSetting("Green", 90, 0, 255, 1);

    private final NumberSetting blue = new NumberSetting("Blue", 140, 0, 255, 1);

    private final NumberSetting alpha = new NumberSetting("Alpha", 200, 0, 255, 1);

    private final NumberSetting boxAlpha = new NumberSetting("BoxAlpha", 125, 0, 255, 1);

    private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1, 1, 5, 0.1);

    public BooleanSetting place = new BooleanSetting("Place", true);

    public NumberSetting placeDelay = new NumberSetting("PlaceDelay", 4, 0, 300, 0.1);

    public NumberSetting placeRange = new NumberSetting("PlaceRange", 4, 0.1,7.0, 0.1);

    public BooleanSetting explode = new BooleanSetting("Break", true);

    public BooleanSetting packetBreak = new BooleanSetting("PacketBreak", true);

    public BooleanSetting predicts = new BooleanSetting("Predict", true);

    public BooleanSetting rotate = new BooleanSetting("Rotate", true);

    public NumberSetting breakDelay = new NumberSetting("BreakDelay", 4, 0, 300, 1);

    public NumberSetting breakRange = new NumberSetting("BreakRange", 4, 0.1, 7, 0.1);

    public NumberSetting breakWallRange = new NumberSetting("BreakWallRange", 4, 0.1, 7, 0.1);

    public BooleanSetting opPlace = new BooleanSetting("1.13 Place", true);

    public BooleanSetting suicide = new BooleanSetting("AntiSuicide", true);

    public BooleanSetting autoswitch = new BooleanSetting("AutoSwitch", true);

    public BooleanSetting ignoreUseAmount = new BooleanSetting("IgnoreUseAmount", true);

    public NumberSetting wasteAmount = new NumberSetting("UseAmount", 4, 1, 5, 1);

    public BooleanSetting facePlaceSword = new BooleanSetting("FacePlaceSword", true);

    public NumberSetting targetRange = new NumberSetting("TargetRange", 4, 1, 12, 1);

    public NumberSetting minDamage = new NumberSetting("MinDamage", 4, 0.1, 20, 0.1);

    public NumberSetting facePlace = new NumberSetting("FacePlaceHP", 4, 0, 36, 1);

    public NumberSetting breakMaxSelfDamage = new NumberSetting("BreakMaxSelf", 4, 0.1, 12, 0.1);

    public NumberSetting breakMinDmg = new NumberSetting("BreakMinDmg", 4, 0.1, 7, 0.1);

    public NumberSetting minArmor = new NumberSetting("MinArmor", 4, 0.1, 80, 0.1);

    //public Setting<SwingMode> swingMode = new Setting("Swing", SwingMode.MainHand);
    public ModeSetting swingMode = new ModeSetting("Swing", "MainHand", "MainHand", "OffHand", "None");

    public BooleanSetting render = new BooleanSetting("Render", true);

    public BooleanSetting renderDmg = new BooleanSetting("RenderDmg", true);

    public BooleanSetting box = new BooleanSetting("Box", true);

    public BooleanSetting outline = new BooleanSetting("Outline", true);
    public BooleanSetting antiKick = new BooleanSetting("AntiKick", true);

     */

    NumberSetting attackFactor;

    NumberSetting red;

    NumberSetting green;

    NumberSetting blue;

    NumberSetting alpha;

    NumberSetting boxAlpha;

    NumberSetting lineWidth;

    BooleanSetting place;

    NumberSetting placeDelay;

    NumberSetting placeRange;

    BooleanSetting explode;

    BooleanSetting packetBreak;

    BooleanSetting predicts;

    BooleanSetting rotate;

    NumberSetting breakDelay;

    NumberSetting breakRange;

    NumberSetting breakWallRange;

    BooleanSetting opPlace;

    BooleanSetting suicide;

    BooleanSetting autoswitch;
    BooleanSetting silent;

    BooleanSetting ignoreUseAmount;

    NumberSetting wasteAmount;

    BooleanSetting facePlaceSword;

    NumberSetting targetRange;

    NumberSetting minDamage;

    NumberSetting facePlace;

    NumberSetting breakMaxSelfDamage;

    NumberSetting breakMinDmg;

    NumberSetting minArmor;
    ModeSetting swingMode;

    BooleanSetting render;

    BooleanSetting renderDmg;

    BooleanSetting box;

    BooleanSetting outline;
    BooleanSetting antiKick;

    EntityEnderCrystal crystal;

    private EntityLivingBase target;

    private BlockPos pos;

    private int hotBarSlot;

    private boolean armor;

    private boolean armorTarget;

    private int crystalCount;

    private int predictWait;

    private int predictPackets;

    private boolean packetCalc;

    private float yaw = 0.0F;

    private EntityLivingBase realTarget;

    private int predict;

    private float pitch = 0.0F;

    private boolean rotating = false;

    public AutoCrystal() {
        super("AutoCrystal", "settings broken", Category.COMBAT);
    }

    @Override
    public void init() {
        super.init();

        attackFactor = new NumberSetting("PredictDelay", 0, 0, 200, 1);

        red = new NumberSetting("Red", 140, 0, 255, 1);

        green = new NumberSetting("Green", 90, 0, 255, 1);

        blue = new NumberSetting("Blue", 140, 0, 255, 1);

        alpha = new NumberSetting("Alpha", 200, 0, 255, 1);

        boxAlpha = new NumberSetting("BoxAlpha", 125, 0, 255, 1);

        lineWidth = new NumberSetting("LineWidth", 1, 1, 5, 0.1);

        place = new BooleanSetting("Place", true);

        placeDelay = new NumberSetting("PlaceDelay", 3, 0, 300, 0.1);

        placeRange = new NumberSetting("PlaceRange", 5, 0.1,7.0, 0.1);

        explode = new BooleanSetting("Break", true);

        packetBreak = new BooleanSetting("PacketBreak", true);

        predicts = new BooleanSetting("Predict", true);

        rotate = new BooleanSetting("Rotate", true);

        breakDelay = new NumberSetting("BreakDelay", 3, 0, 300, 1);

        breakRange = new NumberSetting("BreakRange", 5, 0.1, 7, 0.1);

        breakWallRange = new NumberSetting("BreakWallRange", 5, 0.1, 7, 0.1);

        opPlace = new BooleanSetting("1.13 Place", false);

        suicide = new BooleanSetting("AntiSuicide", true);

        autoswitch = new BooleanSetting("AutoSwitch", true);

        silent = new BooleanSetting("SilentSwap", true);

        ignoreUseAmount = new BooleanSetting("IgnoreUseAmount", true);

        wasteAmount = new NumberSetting("UseAmount", 4, 1, 5, 1);

        facePlaceSword = new BooleanSetting("FacePlaceSword", true);

        targetRange = new NumberSetting("TargetRange", 4, 1, 12, 1);

        minDamage = new NumberSetting("MinDamage", 4, 0.1, 20, 0.1);

        facePlace = new NumberSetting("FacePlaceHP", 4, 0, 36, 1);

        breakMaxSelfDamage = new NumberSetting("BreakMaxSelf", 4, 0.1, 12, 0.1);

        breakMinDmg = new NumberSetting("BreakMinDmg", 4, 0.1, 7, 0.1);

        minArmor = new NumberSetting("MinArmor", 4, 0.1, 80, 0.1);

        //public Setting<SwingMode> swingMode = new Setting("Swing", SwingMode.MainHand);
        swingMode = new ModeSetting("Swing", "MainHand", "MainHand", "OffHand", "None");

        render = new BooleanSetting("Render", false);

        renderDmg = new BooleanSetting("RenderDmg", false);

        box = new BooleanSetting("Box", false);

        outline = new BooleanSetting("Outline", false);
        antiKick = new BooleanSetting("AntiKick", true);

        addSetting(attackFactor, red, green, blue, alpha, boxAlpha, lineWidth, place, placeDelay, placeRange, explode, packetBreak, predicts, rotate, breakDelay, breakRange, breakWallRange, opPlace, suicide, autoswitch, silent, ignoreUseAmount, wasteAmount, facePlaceSword, targetRange, minDamage, facePlace, breakMaxSelfDamage, breakMinDmg, minArmor, swingMode, render, renderDmg, box, outline, antiKick);
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int)r;
        while (x <= cx + r) {
            int z = cz - (int)r;
            while (z <= cz + r) {
                int y = sphere ? (cy - (int)r) : cy;
                while (true) {
                    float f = sphere ? (cy + r) : (cy + h), f2 = f;
                    if (y >= f)
                        break;
                    double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
                    if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    y++;
                }
                z++;
            }
            x++;
        }
        return circleblocks;
    }

    /*
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage == 0 && ((Boolean) this.rotate.isEnable()) && this.rotating && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            //packet.yaw = this.yaw;
            //packet.pitch = this.pitch;

            this.rotating = false;
        }
        if (((Boolean)this.antiKick.isEnable()) && event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation)
            event.setCanceled(true);
    }

     */


    private void rotateTo(Entity entity) {
        if (((Boolean)this.rotate.isEnable())) {
            float[] angle = MathUtils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }

    private void rotateToPos(BlockPos pos) {
        if (((Boolean)this.rotate.isEnable())) {
            float[] angle = MathUtils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }

    @Override
    public void onEnable() {

        super.onEnable();

        this.placeTimer.reset();
        this.breakTimer.reset();
        this.predictWait = 0;
        this.hotBarSlot = -1;
        this.pos = null;
        this.crystal = null;
        this.predict = 0;
        this.predictPackets = 1;
        this.target = null;
        this.packetCalc = false;
        this.realTarget = null;
        this.armor = false;
        this.armorTarget = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.rotating = false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.toggled) {
            if (mc.world == null || mc.player == null) return;

            onCrystal();
        }
    }

    public String getDisplayInfo() {
        if (this.realTarget != null)
            return this.realTarget.getName();
        return null;
    }

    public void onCrystal() {
        if (mc.world == null || mc.player == null)
            return;
        this.realTarget = null;
        manualBreaker();
        this.crystalCount = 0;
        if (!((Boolean)this.ignoreUseAmount.isEnable()))
            for (Entity crystal : mc.world.loadedEntityList) {
                if (!(crystal instanceof EntityEnderCrystal) || !IsValidCrystal(crystal))
                    continue;
                boolean count = false;
                double damage = calculateDamage(this.target.getPosition().getX() + 0.5D, this.target.getPosition().getY() + 1.0D, this.target.getPosition().getZ() + 0.5D, (Entity)this.target);
                if (damage >= this.minDamage.getValue())
                    count = true;
                if (!count)
                    continue;
                this.crystalCount++;
            }
        this.hotBarSlot = -1;
        if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            int crystalSlot = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? mc.player.inventory.currentItem : -1, n = crystalSlot;
            if (crystalSlot == -1)
                for (int l = 0; l < 9; l++) {
                    if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                        crystalSlot = l;
                        this.hotBarSlot = l;
                    }
                }
            if (crystalSlot == -1) {
                this.pos = null;
                this.target = null;
                return;
            }
        }
        if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
            this.pos = null;
            this.target = null;
            return;
        }
        if (this.target == null)
            this.target = (EntityLivingBase) getTarget();
        if (this.target == null) {
            this.crystal = null;
            return;
        }
        if (this.target.getDistance((Entity)mc.player) > 12.0F) {
            this.crystal = null;
            this.target = null;
        }
        this.crystal = mc.world.loadedEntityList.stream().filter(this::IsValidCrystal).map(p_Entity -> (EntityEnderCrystal)p_Entity).min(Comparator.comparing(p_Entity -> Float.valueOf(this.target.getDistance((Entity)p_Entity)))).orElse(null);
        if (this.crystal != null && ((Boolean)this.explode.isEnable()) && this.breakTimer.passedMs(((long) this.breakDelay.getValue()))) {
            this.breakTimer.reset();
            if (((Boolean) this.packetBreak.isEnable())) {
                rotateTo((Entity)this.crystal);
                mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)this.crystal));
            } else {
                rotateTo((Entity)this.crystal);
                mc.playerController.attackEntity((EntityPlayer)mc.player, (Entity)this.crystal);
            }
            if (this.swingMode.is("MainHand")) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            } else if (this.swingMode.is("OffHand")) {
                mc.player.swingArm(EnumHand.OFF_HAND);
            }
        }
        if (this.placeTimer.passedMs((long) this.placeDelay.getValue()) && ((Boolean) this.place.isEnable())) {
            this.placeTimer.reset();
            double damage = 0.5D;
            for (BlockPos blockPos : placePostions(((float) this.placeRange.getValue()))) {
                double targetRange;
                if (blockPos == null || this.target == null || !mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty() || (targetRange = this.target.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ())) > ((float)this.targetRange.getValue()) || this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0F)
                    continue;
                double targetDmg = calculateDamage(blockPos.getX() + 0.5D, blockPos.getY() + 1.0D, blockPos.getZ() + 0.5D, (Entity)this.target);
                this.armor = false;
                for (ItemStack is : this.target.getArmorInventoryList()) {
                    if (is.getMaxDamage() != 0) {
                        float green = Math.round((is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage());
                        float red = 1.0F - green;
                        int dmg = 100 - (int) (red * 100.0F);
                        if (dmg > ((float) this.minArmor.getValue()))
                            continue;
                        this.armor = true;
                    }
                }
                if (targetDmg < ((float)this.minDamage.getValue()) && (((Boolean)this.facePlaceSword.isEnable()) ? (this.target.getAbsorptionAmount() + this.target.getHealth() > ((float)this.facePlace.getValue())) : (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword || this.target.getAbsorptionAmount() + this.target.getHealth() > ((float)this.facePlace.getValue()))) && (((Boolean)this.facePlaceSword.isEnable()) ? !this.armor : (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword || !this.armor)))
                    continue;
                double selfDmg;
                if (((selfDmg = calculateDamage(blockPos.getX() + 0.5D, blockPos.getY() + 1.0D, blockPos.getZ() + 0.5D, (Entity)mc.player)) + (((Boolean)this.suicide.isEnable()) ? 2.0D : 0.5D) >= (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && selfDmg >= targetDmg && targetDmg < (this.target.getHealth() + this.target.getAbsorptionAmount())) || damage >= targetDmg)
                    continue;
                this.pos = blockPos;
                damage = targetDmg;
            }
            if (damage == 0.5D) {
                this.pos = null;
                this.target = null;
                this.realTarget = null;
                return;
            }

            this.realTarget = this.target;

            if (this.hotBarSlot != -1 && this.autoswitch.isEnable() && !mc.player.isPotionActive(MobEffects.WEAKNESS)) {

                if (silent.isEnable()) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.hotBarSlot));
                } else {
                    mc.player.inventory.currentItem = this.hotBarSlot;
                }

                mc.playerController.updateController();
            }

            if (!((Boolean) this.ignoreUseAmount.isEnable())) {
                int crystalLimit = (int) this.wasteAmount.getValue();
                if (this.crystalCount >= crystalLimit)
                    return;
                if (damage < this.minDamage.getValue())
                    crystalLimit = 1;
                if (this.crystalCount < crystalLimit && this.pos != null) {
                    rotateToPos(this.pos);
                    mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                }
            } else if (this.pos != null) {
                rotateToPos(this.pos);
                mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
            }

            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }

    /*
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSpawnObject packet;
        if (event.getPacket() instanceof SPacketSpawnObject && (packet = (SPacketSpawnObject)event.getPacket()).getType() == 51 && ((Boolean)this.predicts.isEnable()) && this.preditTimer.passedMs(((int) this.attackFactor.getValue())) && ((Boolean)this.predicts.isEnable()) && ((Boolean)this.explode.isEnable()) && ((Boolean)this.packetBreak.isEnable()) && this.target != null) {
            if (!isPredicting(packet))
                return;
            CPacketUseEntity predict = new CPacketUseEntity();

            //predict.entityId = packet.getEntityID();
            //predict.action = CPacketUseEntity.Action.ATTACK;

            mc.player.connection.sendPacket((Packet) predict);
        }
    }

     */

    /*
    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (this.pos != null && ((Boolean)this.render.isEnable()) && this.target != null) {
            RenderUtil.drawBoxESP(this.pos, ((Boolean)(ClickGui.getInstance()).rainbow.isEnable()) ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.outline.isEnable()), ((Boolean)(ClickGui.getInstance()).rainbow.isEnable()) ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((float)this.lineWidth.getValue()), ((Boolean)this.outline.isEnable()), ((Boolean)this.box.isEnable()), ((Integer)this.boxAlpha.getValue()).intValue(), true);
            if (((Boolean)this.renderDmg.isEnable())) {
                double renderDamage = calculateDamage(this.pos.getX() + 0.5D, this.pos.getY() + 1.0D, this.pos.getZ() + 0.5D, (Entity)this.target);
                RenderUtil.drawText(this.pos, ((Math.floor(renderDamage) == renderDamage) ? (int)renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(renderDamage) })) + "");
            }
        }
    }
          */

    private boolean isPredicting(SPacketSpawnObject packet) {
        BlockPos packPos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        if (mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > ((float)this.breakRange.getValue()))
            return false;
        if (!canSeePos(packPos) && mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > ((float)this.breakWallRange.getValue()))
            return false;
        double targetDmg = calculateDamage(packet.getX() + 0.5D, packet.getY() + 1.0D, packet.getZ() + 0.5D, (Entity)this.target);
        if (EntityUtils.isInHole((Entity)mc.player) && targetDmg >= 1.0D)
            return true;
        double selfDmg = calculateDamage(packet.getX() + 0.5D, packet.getY() + 1.0D, packet.getZ() + 0.5D, (Entity)mc.player);
        double d = ((Boolean)this.suicide.isEnable()) ? 2.0D : 0.5D;
        if (selfDmg + d < (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && targetDmg >= (this.target.getAbsorptionAmount() + this.target.getHealth()))
            return true;
        this.armorTarget = false;
        for (ItemStack is : this.target.getArmorInventoryList()) {
            if (is.getMaxDamage() != 0) {
                float green = Math.round((is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage());
                float red = 1.0F - green;
                int dmg = 100 - (int) (red * 100.0F);
                if (dmg > ((float) this.minArmor.getValue()))
                    continue;
                this.armorTarget = true;
            }
        }
        if (targetDmg >= ((float)this.breakMinDmg.getValue()) && selfDmg <= ((float)this.breakMaxSelfDamage.getValue()))
            return true;
        return (EntityUtils.isInHole((Entity)this.target) && this.target.getHealth() + this.target.getAbsorptionAmount() <= ((float)this.facePlace.getValue()));
    }

    private boolean IsValidCrystal(Entity p_Entity) {
        if (p_Entity == null)
            return false;
        if (!(p_Entity instanceof EntityEnderCrystal))
            return false;
        if (this.target == null)
            return false;
        if (p_Entity.getDistance((Entity)mc.player) > ((float)this.breakRange.getValue()))
            return false;
        if (!mc.player.canEntityBeSeen(p_Entity) && p_Entity.getDistance((Entity)mc.player) > ((float)this.breakWallRange.getValue()))
            return false;
        if (this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0F)
            return false;
        double targetDmg = calculateDamage(p_Entity.getPosition().getX() + 0.5D, p_Entity.getPosition().getY() + 1.0D, p_Entity.getPosition().getZ() + 0.5D, (Entity)this.target);
        if (EntityUtils.isInHole((Entity)mc.player) && targetDmg >= 1.0D)
            return true;
        double selfDmg = calculateDamage(p_Entity.getPosition().getX() + 0.5D, p_Entity.getPosition().getY() + 1.0D, p_Entity.getPosition().getZ() + 0.5D, (Entity)mc.player);
        double d = ((Boolean)this.suicide.isEnable()) ? 2.0D : 0.5D;
        if (selfDmg + d < (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && targetDmg >= (this.target.getAbsorptionAmount() + this.target.getHealth()))
            return true;
        this.armorTarget = false;
        for (ItemStack is : this.target.getArmorInventoryList()) {
            if (is.getMaxDamage() != 0) {
                float green = Math.round((is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage());
                float red = 1.0F - green;
                int dmg = 100 - (int) (red * 100.0F);
                if (dmg > ((float) this.minArmor.getValue()))
                    continue;
                this.armorTarget = true;
            }
        }
        if (targetDmg >= ((float) this.breakMinDmg.getValue()) && selfDmg <= ((float) this.breakMaxSelfDamage.getValue()))
            return true;
        return (EntityUtils.isInHole((Entity)this.target) && this.target.getHealth() + this.target.getAbsorptionAmount() <= ((float)this.facePlace.getValue()));
    }

    EntityPlayer getTarget() {
        EntityPlayer closestPlayer = null;
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (mc.player == null || mc.player.isDead || entity.isDead || entity == mc.player || /*FriendUtils.isFriend(entity.getName()) || */ entity.getDistance((Entity)mc.player) > 12.0F)
                continue;
            this.armorTarget = false;
            for (ItemStack is : entity.getArmorInventoryList()) {
                if (is.getMaxDamage() != 0) {
                    float green = Math.round((is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage());
                    float red = 1.0F - green;
                    int dmg = 100 - (int) (red * 100.0F);
                    if (dmg > ((float) this.minArmor.getValue()))
                        continue;
                    this.armorTarget = true;
                }
            }
            if (EntityUtils.isInHole((Entity)entity) && entity.getAbsorptionAmount() + entity.getHealth() > ((float)this.facePlace.getValue()) && !this.armorTarget && ((float)this.minDamage.getValue()) > 2.2F)
                continue;
            if (closestPlayer == null) {
                closestPlayer = entity;
                continue;
            }
            if (closestPlayer.getDistance((Entity)mc.player) <= entity.getDistance((Entity)mc.player))
                continue;
            closestPlayer = entity;
        }
        return closestPlayer;
    }

    private void manualBreaker() {
        RayTraceResult result;
        if (this.manualTimer.passedMs(200L) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.BOW && mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && (result = mc.objectMouseOver) != null)
            if (result.typeOfHit.equals(RayTraceResult.Type.ENTITY)) {
                Entity entity = result.entityHit;
                if (entity instanceof EntityEnderCrystal) {
                    if (((Boolean)this.packetBreak.isEnable())) {
                        mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
                    } else {
                        mc.playerController.attackEntity((EntityPlayer)mc.player, entity);
                    }
                    this.manualTimer.reset();
                }
            } else if (result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                BlockPos mousePos = new BlockPos(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY() + 1.0D, mc.objectMouseOver.getBlockPos().getZ());
                for (Entity target : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(mousePos))) {
                    if (!(target instanceof EntityEnderCrystal))
                        continue;
                    if (((Boolean)this.packetBreak.isEnable())) {
                        mc.player.connection.sendPacket((Packet)new CPacketUseEntity(target));
                    } else {
                        mc.playerController.attackEntity((EntityPlayer)mc.player, target);
                    }
                    this.manualTimer.reset();
                }
            }
    }

    private boolean canSeePos(BlockPos pos) {
        return (mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true, false) == null);
    }

    private NonNullList<BlockPos> placePostions(float placeRange) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, true)).collect(Collectors.toList()));
        return positions;
    }

    private boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (!((Boolean)this.opPlace.isEnable())) {
                if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
                    return false;
                if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR)
                    return false;
                if (!specialEntityCheck)
                    return (mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty());
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal)
                        continue;
                    return false;
                }
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity instanceof EntityEnderCrystal)
                        continue;
                    return false;
                }
            } else {
                if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
                    return false;
                if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR)
                    return false;
                if (!specialEntityCheck)
                    return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal)
                        continue;
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    private float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0D;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0D;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception exception) {
            Main.sendMessage(exception.toString());
            exception.printStackTrace();
        }
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D);
        double finald = 1.0D;
        if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion((World)mc.world, null, posX, posY, posZ, 6.0F, false, true));
        return (float)finald;
    }

    private float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception exception) {}
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(MobEffects.RESISTANCE))
                damage -= damage / 4.0F;
            damage = Math.max(damage, 0.0F);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
    }
}