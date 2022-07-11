package game;

import player.Goblin;

import java.util.ArrayList;
import java.util.List;

public class Map {;
    // contains all of the information, including the player & goblins
    private final ArrayList<Node> content = new ArrayList<>();
    // one of them is counting the borders of the map
    // and one of them is the actual content
    private final int totalX, totalY;
    private final int mapX, mapY;

    Map(int xDim, int yDim) {
        totalX = xDim+2;
        totalY = yDim+2;
        this.mapX = xDim;
        this.mapY = yDim;

        for (int y = 0; y < totalY; y++) {
            for (int x = 0; x < totalX; x++) {
                boolean border =
                        x == 0 || y == 0
                                || x == totalX - 1
                                || y == totalY - 1;
                content.add(new Node(x, y, null, border));
            }
        }
    }

    private Map(ArrayList<Node> content, int totalX, int totalY, int mapX, int mapY) {
        this.content.addAll(content);
        this.totalX = totalX;
        this.totalY = totalY;
        this.mapX = mapX;
        this.mapY = mapY;
    }

    private int getIndex(int x, int y) {
        if (x >= mapX || y >= mapY
            || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException("Invalid indexing into the map");
        }

        int actX = x+1;
        int actY = y+1;

        // our row * width of a row, and then move the index
        // into the part of the row we want
        return actY*totalX + actX;
    }

    public void setAt(int x, int y, Object state) {
        content.get(getIndex(x, y)).setState(state);
    }

    public Node getAt(int x, int y) {
        return content.get(getIndex(x, y));
    }

    public List<String> getGoblinNames() {
        return ((List<Node>) content.clone())
                .stream()
                .filter(n -> n.getState() instanceof Goblin)
                .map(n -> ((Goblin) n.getState()).getName())
                .toList();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < content.size(); i++) {
            if (i%totalX == 0)
                output.append('\n');

            output.append(content.get(i).getIcon()); // content.get(i).getIcon()
        }

        return output.toString();
    }
}
