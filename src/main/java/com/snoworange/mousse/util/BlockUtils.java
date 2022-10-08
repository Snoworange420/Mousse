package com.snoworange.mousse.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtils {

    public static final List blackList;
    public static final List shulkerList;
    static Minecraft mc = Minecraft.getMinecraft();

    public static IBlockState getState(BlockPos pos)
    {
        return mc.world.getBlockState(pos);
    }

    public static boolean checkForNeighbours(BlockPos blockPos){
        // check if we don't have a block adjacent to blockpos
        if (!hasNeighbour(blockPos)){
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()){
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)){
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean hasNeighbour(BlockPos blockPos){
        for (EnumFacing side : EnumFacing.values()){
            BlockPos neighbour = blockPos.offset(side);
            if (Minecraft.getMinecraft().world.getBlockState(neighbour).getMaterial().isReplaceable()){
                return true;
            }
        }
        return false;
    }


    public static Block getBlock(BlockPos pos)
    {
        return getState(pos).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos)
    {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec){
        float[] rotations = getNeededRotations2(vec);

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
                rotations[1], mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec){
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw
                        + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper
                        .wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static Vec3d getEyesPos(){
        return new Vec3d(mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ);
    }

    public static List<BlockPos> getCircle(final BlockPos loc, final int y, final float r, final boolean hollow){
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; x++){
            for (int z = cz - (int)r; z <= cz + r; z++){
                final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))){
                    final BlockPos l = new BlockPos(x, y, z);
                    circleblocks.add(l);
                }
            }
        }
        return circleblocks;
    }

    static{
        blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER);
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        mc = Minecraft.getMinecraft();
    }

    public static EnumFacing getPlaceableSide(BlockPos pos){

        for (EnumFacing side : EnumFacing.values()){

            BlockPos neighbour = pos.offset(side);

            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)){
                continue;
            }

            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()){
                return side;
            }
        }

        return null;
    }

    public static boolean placeBlock(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        EnumFacing side = BlockUtils.getPlaceableSide(pos);

        if (side == null){
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!BlockUtils.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add((new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5)));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();


        if (BlockUtils.blackList.contains(neighbourBlock) || BlockUtils.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        BlockUtils.faceVectorPacketInstant(hitVec);

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);

        return false;
    }
}
