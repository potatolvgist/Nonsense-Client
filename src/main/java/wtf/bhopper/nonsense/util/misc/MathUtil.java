package wtf.bhopper.nonsense.util.misc;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.misc.EquationParser;

import java.util.*;

public class MathUtil {

    private static final Random RANDOM = new Random();

    public static int lerp(int a, int b, float f) {
        return a + (int)(f * (float)(b - a));
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    public static double distanceTo(double x1, double y1, double z1, double x2, double y2, double z2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double z = z1 - z2;
        return MathHelper.sqrt_double(x * x + y + y + z * z);
    }

    public static double distanceTo(Vec3 pos1, Vec3 pos2) {
        return pos1.distanceTo(pos2);
    }

    public static boolean rayIntersection(AxisAlignedBB aabb, Vec3 pos, float yaw, float pitch, float range) {
        Vec3 look = RotationUtil.getRotationVec(yaw, pitch);
        Vec3 end = look.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
        return rayIntersection(aabb, pos, end);
    }

    public static boolean rayIntersection(AxisAlignedBB aabb, Vec3 start, Vec3 end) {
        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        if (end.xCoord != 0) {
            double t1 = (aabb.minX - start.xCoord) / end.xCoord;
            double t2 = (aabb.maxX - start.xCoord) / end.xCoord;

            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        } else {
            if (start.xCoord < aabb.minX || start.xCoord > aabb.maxX) {
                return false;
            }
        }

        if (end.yCoord != 0) {
            double t1 = (aabb.minY - start.yCoord) / end.yCoord;
            double t2 = (aabb.maxY - start.yCoord) / end.yCoord;

            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        } else {
            if (start.yCoord < aabb.minY || start.yCoord > aabb.maxY) {
                return false;
            }
        }

        if (end.zCoord != 0) {
            double t1 = (aabb.minZ - start.zCoord) / end.zCoord;
            double t2 = (aabb.maxZ - start.zCoord) / end.zCoord;

            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        } else {
            if (start.zCoord < aabb.minZ || start.zCoord > aabb.maxZ) {
                return false;
            }
        }

        return tMin <= tMax && tMax >= 0;
    }

    public static Vec3 closestPoint(AxisAlignedBB aabb, double x, double y, double z) {
        double closestX = MathHelper.clamp_double(x, aabb.minX, aabb.maxX);
        double closestY = MathHelper.clamp_double(y, aabb.minY, aabb.maxY);
        double closestZ = MathHelper.clamp_double(z, aabb.minZ, aabb.maxZ);
        return new Vec3(closestX, closestY, closestZ);
    }

    public static Vec3 closestPoint(AxisAlignedBB aabb, Vec3 pos) {
        return closestPoint(aabb, pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, double x, double y, double z) {
        double closestX, closestY, closestZ;

        switch (face) {
            case DOWN:
            case UP:
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = face == EnumFacing.DOWN ? aabb.minY : aabb.maxY;
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
                break;

            case NORTH:
            case SOUTH:
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = face == EnumFacing.NORTH ? aabb.minZ : aabb.maxZ;
                break;

            case WEST:
            case EAST:
                closestX = face == EnumFacing.WEST ? aabb.minX : aabb.maxX;
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
                break;

            default:
                throw new IllegalArgumentException("Invalid face: " + face);
        }

        return new Vec3(closestX, closestY, closestZ);
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, Vec3 vec) {
        return closestPointOnFace(aabb, face, vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static Vec3 randomPoint(AxisAlignedBB aabb, double x, double y, double z, double range) {

        if (!boxIntersectsSphere(aabb, x, y, z, range)) {
            return null;
        }

        // An intersection is sampled to help improve efficiency
        AxisAlignedBB intersection = calculateBoxIntersectionWithSphere(aabb, x, y, z, range);

        Vec3 point;
        do {
            double randX = intersection.minX + (intersection.maxX - intersection.minX) * RANDOM.nextDouble();
            double randY = intersection.minY + (intersection.maxY - intersection.minY) * RANDOM.nextDouble();
            double randZ = intersection.minZ + (intersection.maxZ - intersection.minZ) * RANDOM.nextDouble();

            point = new Vec3(randX, randY, randZ);
        } while (!isPointInsideSphere(point, x, y, z, range));

        return point;
    }

    public static Vec3 randomPointOnFace(AxisAlignedBB aabb, EnumFacing face, double x, double y, double z, double range) {

        if (!boxIntersectsSphere(aabb, x, y, z, range)) {
            return null;
        }

        // An intersection is sampled to help improve efficiency
        AxisAlignedBB intersection = calculateBoxIntersectionWithSphere(aabb, x, y, z, range);

        switch (face) {
            case DOWN:
            case UP:
                double randomX = intersection.minX + (intersection.maxX - intersection.minX) * RANDOM.nextDouble();
                double randomY = (face == EnumFacing.UP) ? intersection.maxY : intersection.minY;
                double randomZ = intersection.minZ + (intersection.maxZ - intersection.minZ) * RANDOM.nextDouble();
                return new Vec3(randomX, randomY, randomZ);

            case NORTH:
            case SOUTH:
                double randomX2 = intersection.minX + (intersection.maxX - intersection.minX) * RANDOM.nextDouble();
                double randomY2 = intersection.minY + (intersection.maxY - intersection.minY) * RANDOM.nextDouble();
                double randomZ2 = (face == EnumFacing.SOUTH) ? intersection.maxZ : intersection.minZ;
                return new Vec3(randomX2, randomY2, randomZ2);

            case WEST:
            case EAST:
                double randomX3 = (face == EnumFacing.EAST) ? intersection.maxX : intersection.minX;
                double randomY3 = intersection.minY + (intersection.maxY - intersection.minY) * RANDOM.nextDouble();
                double randomZ3 = intersection.minZ + (intersection.maxZ - intersection.minZ) * RANDOM.nextDouble();
                return new Vec3(randomX3, randomY3, randomZ3);

            default:
                throw new IllegalArgumentException("Invalid face: " + face);
        }
    }

    public static boolean isPointInsideSphere(Vec3 point, double centerX, double centerY, double centerZ, double radius) {
        double dx = point.xCoord - centerX;
        double dy = point.yCoord - centerY;
        double dz = point.zCoord - centerZ;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    public static boolean boxIntersectsSphere(AxisAlignedBB aabb, double centerX, double centerY, double centerZ, double radius) {
        Vec3 closestPoint = closestPoint(aabb, centerX, centerY, centerZ);

        double dx = closestPoint.xCoord - centerX;
        double dy = closestPoint.yCoord - centerY;
        double dz = closestPoint.zCoord - centerZ;
        double distanceSquared = dx * dx + dy * dy + dz * dz;

        return distanceSquared <= radius * radius;
    }

    public static AxisAlignedBB calculateBoxIntersectionWithSphere(AxisAlignedBB aabb, double centerX, double centerY, double centerZ, double radius) {
        double minX = MathHelper.clamp_double(centerX - radius, aabb.minX, aabb.maxX);
        double maxX = MathHelper.clamp_double(centerX + radius, aabb.minX, aabb.maxX);
        double minY = MathHelper.clamp_double(centerY - radius, aabb.minY, aabb.maxY);
        double maxY = MathHelper.clamp_double(centerY + radius, aabb.minY, aabb.maxY);
        double minZ = MathHelper.clamp_double(centerZ - radius, aabb.minZ, aabb.maxZ);
        double maxZ = MathHelper.clamp_double(centerZ + radius, aabb.minZ, aabb.maxZ);

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static Set<Vec3> samplePointsOnSurface(AxisAlignedBB aabb, double distance) {
        Set<Vec3> uniquePoints = new HashSet<>(); // A hash set is used to prevent repeat points

        sampleFacePoints(aabb.minX, aabb.maxX, aabb.minY, aabb.minZ, aabb.maxZ, distance, uniquePoints);
        sampleFacePoints(aabb.minX, aabb.maxX, aabb.maxY, aabb.minZ, aabb.maxZ, distance, uniquePoints);
        sampleFacePoints(aabb.minX, aabb.maxX, aabb.minY, aabb.minZ, aabb.maxY, distance, uniquePoints);
        sampleFacePoints(aabb.minX, aabb.maxX, aabb.maxY, aabb.minZ, aabb.maxY, distance, uniquePoints);
        sampleFacePoints(aabb.minY, aabb.maxY, aabb.minZ, aabb.minX, aabb.maxZ, distance, uniquePoints);
        sampleFacePoints(aabb.minY, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxZ, distance, uniquePoints);

        return uniquePoints;
    }

    public static void sampleFacePoints(double minCoord1, double maxCoord1, double fixedCoord, double minCoord2, double maxCoord2, double distance, Set<Vec3> uniquePoints) {
        for (double coord1 = minCoord1; coord1 <= maxCoord1; coord1 += distance) {
            for (double coord2 = minCoord2; coord2 <= maxCoord2; coord2 += distance) {
                Vec3 point = new Vec3(coord1, fixedCoord, coord2);
                uniquePoints.add(point);
            }
        }
    }

    public static double randomInRange(double min, double max) {
        return min + (max - min) * RANDOM.nextDouble();
    }

    public static double solveMathProblem(String problem) {
        return new EquationParser(problem).parse();
    }

    public static int sumOf(int... values) {
        int result = 0;
        for (int i : values) {
            result += i;
        }
        return result;
    }

    public static Vec3 floor(Vec3 vec3) {
        return new Vec3(Math.floor(vec3.xCoord), Math.floor(vec3.yCoord), Math.floor(vec3.zCoord));
    }

    public static boolean vecEquals(Vec3 vecA, Vec3 vecB) {
        return vecA.xCoord == vecB.xCoord &&
                vecA.yCoord == vecB.yCoord &&
                vecA.zCoord == vecB.zCoord;
    }

}
