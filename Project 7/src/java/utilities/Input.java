package utilities;

import java.util.Scanner;

public class Input {
    Scanner input;

    public Input() {
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

    public String nextLineNonempty() {
        String output = nextLine();
        while (output.isEmpty()) {
            System.out.println("Input a nonempty string please.");
            output = nextLine();
        }
        return output;
    }
}