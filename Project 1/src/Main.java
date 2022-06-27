import java.util.Random;
import java.util.Scanner;

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

    final static String[] EPILOGUE = {
            "You approach the cave...\n",
            "It is dark and spooky...\n",
            "A large dragon jumps out in front of you!",
            " He opens his jaws and...\n"
    };

    final static String DEATH = "Gobbles you down in one bite!";
    final static String VICTORY = "Spits out a chests of gold right in front of you!";

    public static void main(String[] args) {
        MyInput input = new MyInput();

        System.out.println(PROLOGUE);
        System.out.println();

        int usrAns = input.nextInt();
        while (usrAns < 1 || usrAns > 2) {
            System.out.println("Choose a number between 1 and 2 inclusive. Try again:");
            usrAns = input.nextInt();
        }
        int rightAns = new Random().nextInt(1, 2+1);

        System.out.println();
        for (String line : EPILOGUE) {
            System.out.print(line);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException err) {
                // no problem if sleeping does not work
            }
        }

        if (usrAns == rightAns) {
            System.out.println(VICTORY);
        } else {
            System.out.println(DEATH);
        }
    }
}
