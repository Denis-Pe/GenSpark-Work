import game.Game;
import utilities.Input;

public class Main {
    public static void main(String[] args) {
        Game g = new Game("pedro");
        Input input = new Input();

        while(true) {
            g.printMap();
            g.nextTurn(input);
        }
    }
}
