public class Main {
    // whether the string only has characters of the alphabet
    static boolean isAlphabetic(String str) {
        boolean output = true;
        for (char c : str.toCharArray()) {
            if (!"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".contains("" + c)) {
                output = false;
            }
        }
        return output;
    }

    static char getGameInput(Input input) {
        String in = input.nextLine().toLowerCase();
        System.out.println();
        while (in.length() != 1 || !isAlphabetic(in)) {
            System.out.println("Make sure your input is a single letter. Try again:\n");
            in = input.nextLine().toLowerCase();
            System.out.println();
        }
        return in.charAt(0);
    }

    // returns the outcome of the game in a string
    static String game(Input input) {
        Game game = new Game(true);

        while (!game.isGameFailed() && !game.isGameWon()) {
            System.out.println(game.getFigure());
            System.out.println("Missed letters: " + game.getFailedLetters());
            System.out.println(game.getWordState());
            System.out.println("Guess a letter.\n");

            char gameInput = getGameInput(input);

            while (!game.inputLetter(gameInput)) {
                System.out.println("You have already guessed that letter. Choose again.\n");
                gameInput = getGameInput(input);
            }
        }

        if (game.isGameFailed())
            System.out.println(game.getFigure());

        return game.getOutcome();
    }

    public static void main(String[] args) {
        Input input = new Input();

        System.out.println("H A N G M A N");

        while (true) {
            String outcome = game(input);
            System.out.println(outcome);
            System.out.println("Do you want to play again? (yes or no)\n");

            String answer = input.nextLine().toLowerCase();
            System.out.println();
            while (!(answer.equals("no") || answer.equals("yes"))) {
                System.out.println("That was not a valid input. Choose again:\n");
                answer = input.nextLine().toLowerCase();
                System.out.println();
            }

            if (answer.equals("no")) {
                break;
            }
        }
    }
}
