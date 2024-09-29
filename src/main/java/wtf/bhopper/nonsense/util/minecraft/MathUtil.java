package wtf.bhopper.nonsense.util.minecraft;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class MathUtil {

    public static double distanceTo(double x1, double y1, double z1, double x2, double y2, double z2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double z = z1 - z2;
        return MathHelper.sqrt_double(x * x + y + y + z * z);
    }

    public static double distanceTo(Vec3 pos1, Vec3 pos2) {
        return pos1.distanceTo(pos2);
    }

    public static Vec3 closestPoint(AxisAlignedBB aabb, double x, double y, double z) {
        double closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
        double closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
        double closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
        return new Vec3(closestX, closestY, closestZ);
    }

}
