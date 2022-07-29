import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Game {
    private String answer;
    private String failedLetters;
    private char[] correctAnswers;

    /// randomWord indicates whether it should attempt to get the secret word
    /// from randomWords.txt in this project's directory
    /// if not, the secret word will be "predictable"
    Game(boolean randomWord) {
        if (randomWord) {
            // default to "failure" if reading from randomWords.txt
            try {
                List<String> words = Files.readAllLines(Paths.get("randomWords.txt"));
                int listSize = words.size();
                Random random = new Random();

                answer = words.get(random.nextInt(0, listSize));
            } catch (Exception e) {
                answer = "failure";
            }
        } else {
            answer = "predictable";
        }

        failedLetters = "";
        correctAnswers = new char[answer.length()];
        Arrays.fill(correctAnswers, '_');
    }

    /// returns true if the input was successful whether correct or not
    /// else it returns false, i.e. if it was already guessed
    public boolean inputLetter(char letter) {
        boolean correct = answer.contains("" + letter);

        if (correct) {
            // place the letter
            char[] rightLetters = answer.toCharArray();

            for (int i = 0; i < rightLetters.length; i++) {
                if (
                        rightLetters[i] == letter
                        && correctAnswers[i] == '_'
                ) {
                    correctAnswers[i] = letter;
                    return true;
                }
            }

            return false;
        } else {
            failedLetters += "" + letter;
        }

        return true;
    }

    public String getFigure() {
        final String[] stages = {
                """
 +---+
     |
     |
     |
    ===""", """
 +---+
 0   |
     |
     |
    ===""", """
 +---+
 0   |
 |   |
     |
    ===""", """
 +---+
 0   |
 |   |
 |   |
    ===""", """
 +---+
 0   |
 |\\  |
 |   |
    ===""", """
 +---+
 0   |
/|\\  |
 |   |
    ===""", """
 +---+
 0   |
/|\\  |
 |   |
  \\ ===""","""
 +---+
 0   |
/|\\  |
 |   |
/ \\ ==="""
        };

        return stages[failedLetters.length()];
    }

    public boolean isGameFailed() {
        return failedLetters.length() >= 7;
    }

    public boolean isGameWon() {
        for (char c : correctAnswers)
            if (c == '_')
                return false;
        // it's not a victory if it's a defeat
        return !isGameFailed();
    }

    public String getWordState() {
        StringBuilder output = new StringBuilder();
        for (char c : correctAnswers)
            output.append(c);
        return output.toString();
    }

    public String getFailedLetters() {
        return failedLetters;
    }

    public String getOutcome() {
        if (isGameWon()) {
            return "Yes! The secret word is \"" + answer + "\"! You have won!";
        } else if (isGameFailed()) {
            return "The secret word is \"" + answer + "\". Thank you for playing!";
        } else {
            return "";
        }
    }
}