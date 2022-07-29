import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import graph.Graph;

import java.util.List;
import java.util.Optional;

class GraphTest {
    static int GRID_MAX_WIDTH = 250;
    static int GRID_MIN_WIDTH = 50;
    static int GRID_MAX_HEIGHT = 250;
    static int GRID_MIN_HEIGHT = 50;

    // Minimal Dummy Class
    // see usage below
    class MDC {
    }

    /*
     * Notes about tests:
     * - The source is always the top-left corner of the grid
     * - The destination is always the bottom-right corner of the grid
     * - To keep tests from being memory hogs, I use my Minimal Dummy Class as the
     * type the Graph's nodes store so I can have a minimal footprint
     * when testing things that are not related to what the Graph contains at all
     */
    void testAStarOutput(int width, int height) {
        Graph<MDC> test = new Graph<>(width, height);

        Graph.Position src = new Graph.Position(0, 0);
        Graph.Position dst = new Graph.Position(width - 1, height - 1);

        Optional<List<Graph.Position>> path = test.aStarPath(src, dst);

        assertTrue(path.isPresent());

        assertNotEquals(path.get().get(0), src);

        // to test our path searching algorithm works,
        // we only make sure that we are getting closer
        // and closer to our destination or at least not closer but equal and not
        // getting further away
        for (int i = 1; i < path.get().size(); i++) {
            assertTrue(path.get().get(i).deltaTotal(dst) <= path.get().get(i - 1).deltaTotal(dst));
        }

        System.out.println("Finished testing a grid with width " + width + " and height " + height);
    }

    // Short test only tests from the minimum dimensions
    // to 5 times the minimum (let's call it n) increasing by n/25,
    // both dimensions increasing at the same time
    @DisplayName("Testing A* Algorithm: Short Test")
    @Test
    void testAStarPathShort() {
        int width = GRID_MIN_WIDTH, height = GRID_MIN_HEIGHT;

        while (width < GRID_MIN_WIDTH * 5 && height < GRID_MIN_HEIGHT * 5) {
            testAStarOutput(width, height);
            width += GRID_MIN_WIDTH * 5 / 25;
            height += GRID_MIN_HEIGHT * 5 / 25;
        }

    }

    // The long test starts from the minimum dimensions and first increase the width
    // by max/4 until it reaches the max, then increases the height by max/4 and
    // restarts the width to do the same thing.
    // So on and so forth until it gets to the max dimensions in both axes
    @DisplayName("Testing A* Algorithm: Long Test")
    @Test
    void testAStarPathLong() {
        int height = GRID_MIN_HEIGHT;

        while (height < GRID_MAX_HEIGHT) {
            int width = GRID_MIN_WIDTH;
            while (width < GRID_MAX_WIDTH) {
                testAStarOutput(width, height);

                width += GRID_MAX_WIDTH/4;
            }

            height += GRID_MAX_HEIGHT/4;
        }
    }
}