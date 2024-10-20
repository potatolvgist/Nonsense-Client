package wtf.bhopper.nonsense.util.minecraft.pathfinding;

import net.minecraft.util.Vec3;

import java.util.List;

public interface IPathFinder {

    List<Vec3> getPath();
    void compute(int maxLoops);

    default void compute() {
        compute(1000);
    }

}
