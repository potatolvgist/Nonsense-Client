package wtf.bhopper.nonsense.util.minecraft.pathfinding;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlinkPathFinder implements IPathFinder, MinecraftInstance {

    private final BlockPos start;
    private final BlockPos goal;

    private final List<Vec3> path = new ArrayList<>();

    private final Map<Long, Node> nodes = new HashMap<>();

    public BlinkPathFinder(Vec3 start, BlockPos goal) {
        this.start = new BlockPos(start.xCoord, start.yCoord, start.zCoord);
        this.goal = goal;
    }

    @Override
    public List<Vec3> getPath() {
        return this.path;
    }

    @Override
    public void compute(int maxLoops) {
        this.nodes.clear();
        this.nodes.put(this.start.toLong(), new Node(this.start, null));

        for (int i = 0; i < maxLoops; i++) {
            Node lowestCost = this.getLowestCostUnsearchedNode();
            lowestCost.search = true;
            BlockPos pos = lowestCost.pos;

            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos newPos = pos.offset(facing);

                if (!isValidPosition(newPos)) {
                    continue;
                }

                Long hash = newPos.toLong();
                Node newNode = new Node(newPos, lowestCost);

                if (nodes.containsKey(hash)) {
                    Node oldNode = this.nodes.get(hash);
                    if (newNode.sCost() < oldNode.sCost()) {
                        this.nodes.put(hash, newNode);
                    }
                } else {
                    if (newNode.pos == goal) {
                        this.updatePath(newNode);
                        break;
                    }
                    this.nodes.put(hash, newNode);
                }
            }

        }

    }

    public Node getLowestCostUnsearchedNode() {
        return this.nodes.values()
                .stream()
                .filter(node -> !node.search)
                .sorted()
                .findFirst()
                .orElse(null);
    }

    private static boolean isValidPosition(BlockPos blockPos) {
        return !BlockUtil.isSolid(blockPos) && !BlockUtil.isSolid(blockPos.up());
    }

    private void updatePath(Node goalNode) {
        this.path.clear();

        Node node = goalNode;
        while (node != null) {
            this.path.add(0, new Vec3(node.pos.getX() + 0.5, node.pos.getY(), node.pos.getZ() + 0.5));
            node = node.parent;
        }
    }

    private class Node implements Comparable<Node> {
        public BlockPos pos;
        public Node parent;
        public double gCost;
        public double hCost;
        public boolean search;

        public Node(BlockPos pos, Node parent) {
            this.pos = pos;
            this.parent = parent;
            this.gCost = this.parent == null ? Double.MAX_VALUE : this.parent.gCost + this.pos.distanceSq(this.parent.pos);
            this.hCost = this.pos.distanceSq(BlinkPathFinder.this.goal);
            this.search = false;
        }

        public double sCost() {
            return this.gCost + this.hCost;
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(this.sCost(), o.sCost());
        }
    }


}
