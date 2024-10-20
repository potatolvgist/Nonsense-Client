package wtf.bhopper.nonsense.util.minecraft.pathfinding;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

import java.util.ArrayList;
import java.util.List;

public class PathFinding implements MinecraftInstance {

    public static List<Vec3> getBlinkPath(BlockPos goal, double dashDistance, int maxLoops) {

        IPathFinder pathFinder = new BlinkPathFinder(mc.thePlayer.getPositionVector(), goal);
        pathFinder.compute(maxLoops);
        List<Vec3> path = pathFinder.getPath();

        List<Vec3> blinkPath = new ArrayList<>();

        Vec3 prev = null;
        for (int i = 0; i < path.size(); i++) {

            Vec3 current = path.get(i);

            if (i == 0 || i == path.size() - 1) {
                blinkPath.add(current);
                continue;
            }

            Vec3 last = blinkPath.get(blinkPath.size() - 1);
            if (current.squareDistanceTo(last) >= dashDistance * dashDistance && prev != null) {
                blinkPath.add(prev);
                prev = null;
            } else {
                prev = current;
            }

        }

        return blinkPath;

    }

    public static List<Vec3> getBlinkPath(BlockPos goal, int maxLoops) {
        IPathFinder pathFinder = new BlinkPathFinder(mc.thePlayer.getPositionVector(), goal);
        pathFinder.compute(maxLoops);
        return pathFinder.getPath();
    }

}
