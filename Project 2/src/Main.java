import java.util.Random;
import java.util.Scanner;

class Main {
    private static void game(Scanner input, String name) {
        int answer = new Random().nextInt(1, 21);

        System.out.println("\nWell, " + name + ", I am thinking of a number between 1 and 20.\nTake a guess.\n");
        int guess = input.nextInt();
        int tries = 1;
        System.out.println();

        while (guess != answer && tries <= 6) {
            System.out.print("Your guess is too ");
            if (guess > answer) {
                System.out.println("high");
            } else {
                System.out.println("low");
            }
            System.out.println("Take a guess\n");

            guess = input.nextInt();
            System.out.println();

            tries++;
        }

        if (tries == 1) {
            System.out.println("Great! Congratulations " + name + "! You got it first try!");
        } else {
            System.out.printf("Good job, %s! You guessed my number in %d guesses.\n", name, tries);
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Hello! What is your name?\n");
        String name = input.nextLine();

        while (true) {
            game(input, name);
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
