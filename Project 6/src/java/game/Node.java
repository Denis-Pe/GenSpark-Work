package game;

import player.Player;

class Node {
    private int x, y;
    private String icon;
    private static final String NULL_ICON = "=";
    private static final String BORDER_ICON = "*";
    private boolean border;

    // it's null or instanceof Human or Goblin or game.Treasure
    private Object state;

    Node(int x, int y, Object state, boolean border) {
        this.x = x;
        this.y = y;
        this.border = border;
        setState(state);
    }

    void setState(Object newState) {
        boolean player = newState instanceof Player;
        boolean treasure = newState instanceof Treasure;

        if (player) {
            Player theNewState = (Player) newState;
            icon = theNewState.getIcon();
            state = theNewState;
        } else if (treasure) {
            Treasure theNewState = (Treasure) newState;
            icon = theNewState.getIcon();
            state = theNewState;
        } else {
            icon = border ? BORDER_ICON : NULL_ICON;
            state = null;
        }
    }

    boolean isEmpty() {
        return state == null;
    }

    String getIcon() { return icon; }

    Object getState() { return state; }

    @Override
    public String toString() {
        return icon;
    }
}