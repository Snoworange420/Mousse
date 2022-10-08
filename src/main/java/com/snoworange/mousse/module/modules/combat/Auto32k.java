package com.snoworange.mousse.module.modules.combat;

import com.snoworange.mousse.Main;
import com.snoworange.mousse.module.Category;
import com.snoworange.mousse.module.Module;
import com.snoworange.mousse.setting.settings.BooleanSetting;
import com.snoworange.mousse.setting.settings.NumberSetting;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Auto32k extends Module {
    //public BooleanSetting useKillaura = new BooleanSetting("useKillaura", true, true);
    //public NumberSetting cps = new NumberSetting("CPS", true, 0, 1, 20, 1);

    public boolean useKillaura = true;

    public int cps = 12;

    public BlockPos placedHopperPos;//this isent needed.. i thought i would need it, but i dident and i dident remove it //actually you reference it a dozen times
    public boolean shouldKillaura;
    public boolean hasPlacedStuff;
    public EntityPlayer entityPlayer;
    public int cpsTick;//i guess ill do it this way, not with system time

    public Auto32k() {
        super("Auto32k", "automatically sets up the 32k bypass for you", Category.COMBAT, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.placedHopperPos = null;
        this.shouldKillaura = false;
        this.hasPlacedStuff = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.toggled) {

        int hopperIndex = -1;
        int shulkerIndex = -1;
        int enchantedSwordIndex = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
            if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.HOPPER))) {
                hopperIndex = i;
            }

            if (itemStack.getItem() instanceof ItemShulkerBox) {
                shulkerIndex = i;
            }
            if (itemStack.getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= Short.MAX_VALUE) {
                enchantedSwordIndex = i;
            }
        }

        if (!this.hasPlacedStuff && (hopperIndex == -1 || shulkerIndex == -1)) {
            Main.sendMessage("Blocks not found in your hotbar!");
            disable();
            return;
        }

        if (enchantedSwordIndex == -1 && !hasPlacedStuff) {

            double closestBlockPosDistance = 4;//maybe?
            BlockPos closestBlockPos = null;

            for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(mc.player.posX - 3, mc.player.posY - 1, mc.player.posZ - 3), new BlockPos(mc.player.posX + 3, mc.player.posY + 2, mc.player.posZ + 3))) {
                if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < closestBlockPosDistance && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && !(mc.world.getBlockState(blockPos.down()).getBlock() instanceof BlockAir) && mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir && mc.world.getBlockState(blockPos.up().up()).getBlock() instanceof BlockAir) {
                    closestBlockPosDistance = mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    closestBlockPos = blockPos;
                }
            }

            if (closestBlockPos != null) {
                this.placeStuff(hopperIndex, shulkerIndex, closestBlockPos.down(), EnumFacing.UP, new Vec3d(closestBlockPos.getX(), closestBlockPos.getY(), closestBlockPos.getZ()));
            }

        }

        if (enchantedSwordIndex != -1) {
            //we have a 32k
            this.shouldKillaura = true;

            //wow this was a fricking pain to figure out
            if (mc.player.inventory.currentItem != enchantedSwordIndex) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(enchantedSwordIndex));
                mc.player.inventory.currentItem = enchantedSwordIndex;
                mc.playerController.updateController();
            }

        } else {
            this.shouldKillaura = false;
        }

        if (enchantedSwordIndex == -1 && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerHopper && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {
            //this is very weird.. but i dont have to get the hopperInventory from GuiHopper
            for (int i = 0; i < 5; i++) {
                if (mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i).getItem().equals(Items.DIAMOND_SWORD) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= Short.MAX_VALUE) {
                    enchantedSwordIndex = i;
                    break;
                }
            }

            if (enchantedSwordIndex == -1) {
                return;
            }

            if (mc.player.inventory.mainInventory.get(mc.player.inventory.currentItem).getItem() instanceof ItemAir) {
                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                    if (itemStack.getItem() instanceof ItemAir) {
                        if (mc.player.inventory.currentItem != i) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                            mc.player.inventory.currentItem = i;
                            mc.playerController.updateController();
                        }
                        break;
                    }
                }
            }

            mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

        }

        if (this.shouldKillaura && useKillaura) {

            double closestEntityDistance = 8;//range

            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityPlayer) || entity instanceof EntityPlayerSP || entity.isDead) {
                    continue;
                }

                if (mc.player.getDistance(entity) < closestEntityDistance && ((EntityPlayer) entity).getHealth() > 0/*this doesnt seem to do anything */) {
                    this.entityPlayer = (EntityPlayer) entity;
                    closestEntityDistance = mc.player.getDistance(entity);
                }

            }

            if (this.entityPlayer != null) {

                this.cpsTick++;

                if (this.cpsTick >= (20 / (cps))) {

                    mc.playerController.attackEntity(mc.player, this.entityPlayer);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.cpsTick = 0;
                }
            }
        }
    }
}

    public void placeStuff(int hopperIndex, int shulkerIndex, BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        //place hopper
        if (mc.world.getBlockState(blockPos.up()).getBlock() instanceof BlockAir) {//shouldent happen
            mc.player.connection.sendPacket(new CPacketHeldItemChange(hopperIndex));
            mc.player.inventory.currentItem = hopperIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        this.placedHopperPos = blockPos.up();

        boolean placedShulker = false;

        //place shulker
        if (mc.world.getBlockState(this.placedHopperPos).getBlock().equals(Blocks.HOPPER) && mc.world.getBlockState(this.placedHopperPos.up()).getBlock() instanceof BlockAir) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(shulkerIndex));
            mc.player.inventory.currentItem = shulkerIndex;
            mc.playerController.updateController();
            mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedHopperPos, EnumFacing.UP/* we are placing on the top? */, new Vec3d(this.placedHopperPos.getX(), this.placedHopperPos.getY(), this.placedHopperPos.getZ()), EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            placedShulker = true;
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        if (placedShulker) {
            //open hopper
            mc.playerController.processRightClickBlock(mc.player, mc.world, this.placedHopperPos, enumFacing/*....*/, new Vec3d(this.placedHopperPos.getX(), this.placedHopperPos.getY(), this.placedHopperPos.getZ()), EnumHand.MAIN_HAND);
        }
        this.hasPlacedStuff = true;
    }
}
