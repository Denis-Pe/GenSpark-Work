import java.util.Scanner;

// Same functionality as a Scanner,
// but with added checks
public class Input {
    Scanner input;

    Input() {
        input = new Scanner(System.in);
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