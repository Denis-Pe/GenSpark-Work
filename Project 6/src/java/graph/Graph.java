package graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * An undirected graph represented as a section within the first quadrant
 * of the Cartesian Plane (rectangular grid indexed by numbers >= 0),
 * and it does not allow for diagonal moves
 * 
 */
// Keep in mind when reading the source that the coordinate systems is
// 0,0 at the top-left with y+ being downwards and x+ being to the right
public class Graph<T> {
    public record Position(int x, int y) {
        Position northOfThis() {
            return new Position(x, y - 1);
        }

        Position southOfThis() {
            return new Position(x, y + 1);
        }

        Position eastOfThis() {
            return new Position(x + 1, y);
        }

        Position westOfThis() {
            return new Position(x - 1, y);
        }

        public Position incX() {
            return new Position(x+1, y);
        }

        public Position incY() {
            return new Position(x, y+1);
        }

        public Position decX() {
            if (x-1 < 0) {
                throw new RuntimeException("Invalid position was going to be created while decrementing X");
            }
            return new Position(x-1, y);
        }

        public Position decY() {
            if (y-1 < 0) {
                throw new RuntimeException("Invalid position was going to be created while decrementing Y");
            }
            return new Position(x, y-1);
        }

        /**
         * It will assume the minimum value for both x and y is 0
         * 
         * @param limitX the upper bound of x, exclusive
         * @param limitY the upper boudn of y, exclusive
         * @return a list of the valid adjacent positions to this one
         */
        List<Position> adjacentPositions(int limitX, int limitY) {
            List<Position> output = new ArrayList<>();

            // check for greater than zero because northOfThis only subtracts from y
            if (this.northOfThis().y >= 0) {
                output.add(this.northOfThis());
            }

            // added to y
            if (this.southOfThis().y < limitY) {
                output.add(this.southOfThis());
            }

            // added to x
            if (this.eastOfThis().x < limitX) {
                output.add(this.eastOfThis());
            }

            // subtracted from x
            if (this.westOfThis().x >= 0) {
                output.add(this.westOfThis());
            }

            return output;
        }

        /**
         * @param other position to compare against
         * @return the absolute difference between the x component of this position and
         *         the other position
         */
        int deltaX(Position other) {
            return Math.abs(x - other.x);
        }

        /**
         * @param other position to compare against
         * @return the absolute difference between the y component of this position and
         *         the other position
         */
        int deltaY(Position other) {
            return Math.abs(y - other.y);
        }

        /**
         * @param other position to compare against
         * @return sum of this.deltaX(other) + this.deltaY(other), a.k.a. Manhattan
         *         Distance
         */
        public int deltaTotal(Position other) {
            return deltaX(other) + deltaY(other);
        }

        /**
         * This method will not take into account any width or height
         * 
         * @param other position to compare against
         * @return whether the position passed in is adjacent to this one
         */
        boolean isAdjacentTo(Position other) {
            return deltaTotal(other) == 1;
        }
    }

    HashMap<Position, T> nodes;
    int width, height;

    /**
     * Create a graph of given dimensions with all nodes having nothing,
     * internally represented as null
     * 
     * @param width  of the graph
     * @param height of the graph
     */
    public Graph(int width, int height) {
        this.width = width;
        this.height = height;
        nodes = new HashMap<Position, T>(width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                nodes.put(new Position(x, y), null);
            }
        }
    }

    private void throwIfOutOfBounds(Position pos) {
        if (pos.x >= width || pos.x < 0 || pos.y >= height || pos.y < 0) {
            throw new IndexOutOfBoundsException("Tried to access a node which is outside the bounds of the graph");
        }
    }

    public T getAt(Position pos) {
        throwIfOutOfBounds(pos);

        return nodes.get(pos);
    }

    public void setAt(Position pos, T value) {
        throwIfOutOfBounds(pos);

        nodes.put(pos, value);
    }

    public void movePosToPos(Position from, Position to) {
        throwIfOutOfBounds(from);
        throwIfOutOfBounds(to);

        T temp = nodes.get(from);
        nodes.put(from, nodes.get(to));
        nodes.put(to, temp);
    }

    public Optional<Position> findMatches(T value) {
        for (Entry<Position, T> entry : nodes.entrySet()) {
            try {
                if (entry.getValue().equals(value)) {
                    return Optional.of(entry.getKey());
                }
            } catch (Exception ignored) {

            }
        }
        return Optional.empty();
    }

    public boolean containsValue(T val) {
        return nodes.values().contains(val);
    }

    public boolean anyValueMatches(Predicate<T> predicate) {
        try {
            return nodes.values().parallelStream().anyMatch(predicate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param src the node that we start from
     * @param dst our final destination
     * @return an optional list of the steps through the path, it excludes the node
     *         we start
     *         from but includes the final destination. If the optional is not
     *         present, it means there is no path from src to dst
     */
    public Optional<List<Position>> aStarPath(Position src, Position dst) {

        record NodeInfo(
                Position pos,
                // f
                int totalWeight,
                // g
                int distWeight,
                // h
                int heuristicWeight) {

            // automatically updates totalWeight
            NodeInfo withDistWeight(int newDistWeight) {
                return new NodeInfo(pos, newDistWeight + heuristicWeight, newDistWeight, heuristicWeight);
            }

            // this one also automatically updates totalWeight
            NodeInfo withHeuristicWeight(int newHeuristicWeight) {
                return new NodeInfo(pos, distWeight + newHeuristicWeight, distWeight, newHeuristicWeight);
            }
        }

        /// We compare nodes based on totalWeight
        class NodeInfoComparator implements Comparator<NodeInfo> {
            @Override
            public int compare(NodeInfo o1, NodeInfo o2) {
                return o1.totalWeight - o2.totalWeight;
            }
        }
        NodeInfoComparator comparator = new NodeInfoComparator();

        ArrayList<NodeInfo> open = new ArrayList<>(List.of(new NodeInfo(src, 0, 0, 0)));
        ArrayList<NodeInfo> closed = new ArrayList<>();

        while (!open.isEmpty()) {
            NodeInfo currentNode = open.stream().parallel().min(comparator).get();
            open.removeIf(node -> node.pos.equals(currentNode.pos)); // remove current node from the list

            closed.add(currentNode);

            if (currentNode.pos.equals(dst)) {
                // RECONSTRUCT PATH TO RETURN
                List<Position> path = new ArrayList<>();

                // remove the first element which is src automatically
                closed.remove(0);

                for (NodeInfo node : closed) {
                    if (path.isEmpty()) {
                        path.add(node.pos);
                    } else {
                        Position lastPos = path.get(path.size() - 1);

                        if (node.pos.isAdjacentTo(lastPos)) {
                            // not only is it adjacent, but it also needs to be getting closer to our goal
                            int lastPosHeurWeight = lastPos.deltaTotal(dst);
                            if (lastPosHeurWeight > node.heuristicWeight || node.pos.equals(dst)) {
                                path.add(node.pos);
                            }
                        }
                    }
                }

                return Optional.of(path);
            }

            List<NodeInfo> children = new ArrayList<>(
                    currentNode.pos.adjacentPositions(width, height).stream().parallel()
                            .map(p -> new NodeInfo(p, 0, 0, 0)).toList());
            for (int i = 0; i < children.size(); i++) {
                if (closed.contains(children.get(i)))
                    continue;

                int childDistWeight = currentNode.distWeight + children.get(i).pos.deltaTotal(currentNode.pos);
                int childHeuristic = children.get(i).pos.deltaTotal(dst);

                // records are immutable, so I have to set the whole element of list in order to
                // set a component or components
                children.set(i, children.get(i).withDistWeight(childDistWeight).withHeuristicWeight(childHeuristic));

                Optional<NodeInfo> inClosed;
                final Position childPos = children.get(i).pos;
                if ((inClosed = closed.stream().parallel().filter(n -> n.pos.equals(childPos)).findFirst())
                        .isPresent()) {
                    if (children.get(i).distWeight > inClosed.get().distWeight) {
                        continue;
                    }
                }

                open.add(children.get(i));
            }
        }
        return Optional.empty();
    }
}