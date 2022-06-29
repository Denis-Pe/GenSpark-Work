import java.util.Random;
import java.util.Scanner;

// Same functionality as a Scanner,
// but with added checks
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
        input.nextLine(); // consume the \n for safe input next!
        return out;
    }

    public String nextLine() {
        String out;
        try {
            out = input.nextLine();
        } catch (Exception err) {
            System.out.println("Your input was not valid. Try again:");
            while (true) {
                input.nextLine();
                try {
                    out = input.nextLine();
                    break;
                } catch (Exception secondErr) {
                    System.out.println("Try once again:");
                }
            }
        }

        return out;
    }
}

class Main {
    final static String TOO_HIGH = "Your guess is too high";
    final static String TOO_LOW = "Your guess is too low";

    static String getResponseUnequal(int guess, int answer) {
        return (guess > answer? TOO_HIGH : TOO_LOW);
    }

    private static String game(MyInput input, String name) {
        int answer = new Random().nextInt(1, 20+1);

        System.out.println("\nWell, " + name + ", I am thinking of a number between 1 and 20.\nTake a guess.\n");
        int guess = input.nextInt();
        int tries = 1;
        System.out.println();

        while (guess != answer && tries < 6) {
            System.out.println(getResponseUnequal(guess, answer));
            System.out.println("Take a guess\n");

            guess = input.nextInt();
            System.out.println();

            tries++;
        }

        if (tries == 1) {
             return String.format("Great! Congratulations %s! You got it first try!\n", name);
        } else if (tries <= 6) {
             return String.format("Good job, %s! You guessed my number in %d guesses.\n", name, tries);
        } else {
             return String.format("Oh no %s, you weren't able to guess the number in 6 tries or less.\n", name);
        }
    }

    public static void main(String[] args) {
        MyInput input = new MyInput();
        System.out.println("Hello! What is your name?\n");
        String name = input.nextLine();

        while (true) {
            String outcomeMessage = game(input, name);
            System.out.println(outcomeMessage);

            System.out.println("Would you like to play again? (y or n)\n");

            String repeat = input.nextLine().toLowerCase();
            System.out.println();

            while (!repeat.equals("n") && !repeat.equals("y")) {
                System.out.println("That is not a valid answer.\nAnswer again.\n");
                repeat = input.nextLine().toLowerCase();
                System.out.println();
            }

            if (repeat.equals("n")) {
                break;
            }
        }
    }
}
