package com.snoworange.mousse.util.math;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

public class MathUtils {

    public static double squaredDistanceBetween(double x1, double y1, double z1, double x2, double y2, double z2) {
        double
                x = x1 - x2,
                y = y1 - y2,
                z = z1 - z2;

        return x * x + y * y + z * z;
    }

    public static double squaredDistanceBetween(Vec3d pos1, Vec3d pos2) {
        double
                x = pos1.x - pos2.x,
                y = pos1.y - pos2.y,
                z = pos1.z - pos2.z;

        return x * x + y * y + z * z;
    }

    public static Vec3d ofVec3i(Vec3i vec3i) {
        return new Vec3d(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public static Vec3d ofCenterVec3i(Vec3i vec3i) {
        return new Vec3d(vec3i.getX() + 0.5, vec3i.getY() + 0.5, vec3i.getZ() + 0.5);
    }

    public static Vec3d getClosestPointOfBox(Vec3d pos, AxisAlignedBB box) {
        double[]
                a = new double[] {pos.x, pos.y, pos.z},
                b = new double[] {box.maxX, box.maxY, box.maxZ},
                c = new double[] {box.minX, box.minY, box.minZ};

        for(int i = 0; i <= 2; i++) {
            if(a[i] > b[i]) a[i] = b[i];
            if(a[i] < c[i]) a[i] = c[i];
        }

        return new Vec3d(a[0], a[1], a[2]);
    }

    public static Vec3d getClosestClickPos(Vec3d pos, AxisAlignedBB box) {
        double[]
                a = new double[] {pos.x, pos.y, pos.z},
                b = new double[] {box.maxX, box.maxY, box.maxZ},
                c = new double[] {box.minX, box.minY, box.minZ};

        for(int i = 0; i <= 2; i++) {
            if(a[i] > b[i]) a[i] = b[i];
            if(a[i] < c[i]) a[i] = c[i];
        }

        for(int i = 0; i <= 2; i++) {
            a[i] = a[i] - c[i];
        }

        return new Vec3d(a[0], a[1], a[2]);
    }

    public static boolean isInRange(Vec3d pos, Vec3d pos2, double range) {
        return squaredDistanceBetween(pos, pos2) <= range * range;
    }

    public static boolean isInRangeClosestPoint(Vec3d pos, AxisAlignedBB box, double range) {
        return squaredDistanceBetween(pos, getClosestPointOfBox(pos, box)) <= range * range;
    }

    public static double[] calculateAngle(Vec3d a, Vec3d b) {
        double
                x = a.x - b.x,
                y = a.y - b.y,
                z = a.z - b.z,
                d = Math.sqrt(x * x + y * y + z * z),
                pitch = Math.toDegrees(Math.asin(y / d)),
                yaw = Math.toDegrees(Math.atan2(z / d, x / d) + Math.PI / 2);

        return new double[] {
                yaw,
                pitch
        };
    }

    public static double[] getMovement(final double speed, float forwards, float sideways, float yawDegrees) {
        return getMovement(speed, forwards, sideways, Math.toRadians(yawDegrees));
    }

    public static double[] getMovement(final double speed, float forwards, float sideways, double yaw) {
        if(forwards != 0) {
            if(sideways > 0) yaw += forwards > 0 ? - Math.PI / 4 : Math.PI / 4;
            else if(sideways < 0) yaw += forwards > 0 ? Math.PI / 4 : - Math.PI / 4;

            sideways = 0;

            if(forwards > 0) forwards = 1;
            else if(forwards < 0) forwards = -1;
        }

        yaw += Math.PI / 2;

        return new double[] {
                forwards * speed * Math.cos(yaw) + sideways * speed * Math.sin(yaw),
                forwards * speed * Math.sin(yaw) - sideways * speed * Math.cos(yaw)
        };
    }

    public enum DmgCalcMode {
        DAMAGE,
        DISTANCE
    }

    // Calculate score based on the mode of calculation


    // damage calculations

    // get blast reduction off armor and potions
    public static float getReduction(EntityPlayer player, float damage, Explosion explosion) {
        // armor
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) player.getTotalArmorValue(), (float) player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        // enchantment
        damage *= (1.0F - (float) EnchantmentHelper.getEnchantmentModifierDamage(player.getArmorInventoryList(), DamageSource.causeExplosionDamage(explosion)) / 25.0F);

        // potions
        if(player.isPotionActive(Potion.getPotionById(11))) damage -= damage / 4;

        return damage;
    }

    // raytracing
    public static RayTraceResult.Type rayTrace(Vec3d start, Vec3d end) {
        double minX = Math.min(start.x, end.x);
        double minY = Math.min(start.y, end.y);
        double minZ = Math.min(start.z, end.z);
        double maxX = Math.max(start.x, end.x);
        double maxY = Math.max(start.y, end.y);
        double maxZ = Math.max(start.z, end.z);

        for(double x = minX; x > maxX; x += 1) {
            for(double y = minY; y > maxY; y += 1) {
                for(double z = minZ; z > maxZ; z += 1) {
                    IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(new BlockPos(x, y, z));

                    if(blockState.getBlock() == Blocks.OBSIDIAN
                            || blockState.getBlock() == Blocks.BEDROCK
                            || blockState.getBlock() == Blocks.BARRIER)
                        return RayTraceResult.Type.BLOCK;
                }
            }
        }

        return RayTraceResult.Type.MISS;
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0D;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }
}
