import java.util.Random;
import java.util.Scanner;

class GameResult {
    enum Outcome {
        Victory, Death;

        final static String DEATH_MESSAGE = "Gobbles you down in one bite!";
        final static String VICTORY_MESSAGE = "Spits out a chests of gold right in front of you!";
    }

    private final Outcome outcome;

    public String getOutcomeMessage() {
        if (outcome == Outcome.Victory) {
            return Outcome.VICTORY_MESSAGE;
        } else {
            return Outcome.DEATH_MESSAGE;
        }
    }

    GameResult(boolean victory) {
        if (victory) {
            outcome = Outcome.Victory;
        } else {
            outcome = Outcome.Death;
        }
    }
}

class MyInput {
    Scanner input;

    MyInput() {
        input = new Scanner(System.in);
    }

    public int nextInt() {
        int out;

        try {
            out = input.nextInt();
        } catch (Exception err) {
            System.out.println("Your input was not a valid integer. Try again:");
            while (true) {
                input.nextLine();
                try {
                    out = input.nextInt();
                    break;
                } catch (Exception secondErr) {
                    System.out.println("Try once again:");
                }
            }
        }

        return out;
    }
}

public class Main {
    final static String PROLOGUE = """
            You are in a land full of dragons. In front of you,
            you see two caves. In one cave, the dragon is friendly
            and will share his treasure with you. The other dragon
            is greedy and hungry and will eat you on sight.
            Which cave will you go into? (1 or 2)""";

    // an array, each line printed with a delay for
    // added suspense!
    final static String[] EPILOGUE = {
            "You approach the cave...\n",
            "It is dark and spooky...\n",
            "A large dragon jumps out in front of you!",
            " He opens his jaws and...\n"
    };

    public static void main(String[] args) {
        MyInput input = new MyInput();

        System.out.println(PROLOGUE);
        System.out.println();

        int usrAns = input.nextInt();
        while (usrAns < 1 || usrAns > 2) {
            System.out.println("Choose 1 or 2. Try again:");
            usrAns = input.nextInt();
        }
        int rightAns = new Random().nextInt(1, 2+1);
        GameResult result = new GameResult(usrAns == rightAns);

        System.out.println();
        for (String line : EPILOGUE) {
            System.out.print(line);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException err) {
                // no problem if sleeping does not work
            }
        }

        System.out.println(result.getOutcomeMessage());
    }
}
