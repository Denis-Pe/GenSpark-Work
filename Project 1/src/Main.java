import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

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
        Scanner input = new Scanner(System.in);

        System.out.println(PROLOGUE);
        System.out.println();

        int usrAns;
        try {
            usrAns = input.nextInt();
        } catch (Exception err) {
            System.out.println("That was not a valid input. Try again:");
            while(true) {
                try {
                    usrAns = input.nextInt();
                    break;
                } catch (Exception anotherErr) {
                    System.out.println("Try once again:");
                }
            }
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
