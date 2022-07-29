import utilities.Input;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utilities.AdvRandom.*;

// scoring system:
// you get 100 points for each right letter,
// and this gets multiplied by 7 - # of wrong guesses

public class Main {
    final static String RES = "resources/";
    final static String HMAN_FILENAME = RES + "hangman_art.txt";
    final static String WORDS_FILENAME = RES + "random_words.txt";
    final static String SCORES_FILENAME = RES + "scores.txt";

    static List<String> getHangmanArt() {
        List<String> hangmanArt;
        try {
            hangmanArt = Files.readAllLines(Paths.get(HMAN_FILENAME)).stream()
                    // reduce from lines to an actual list of the graphics
                    .reduce(new ArrayList<>(), (acc, str) -> {
                        if (acc.isEmpty()) {
                            acc.add(str + '\n');
                        } else if (str.isEmpty()) {
                            acc.add("");
                        } else {
                            acc.set(acc.size()-1, acc.get(acc.size()-1) + str + '\n');
                        }
                        return acc;
                    }, (acc, str) -> acc);
        } catch (Exception e) {
            // can't function without the graphics
            throw new RuntimeException(e);
        }

        return hangmanArt;
    }

    static String getSecretWord() {
        String secretWord;
        try {
            secretWord = randomItemList(Files.readAllLines(Paths.get(WORDS_FILENAME)));
        } catch (Exception ignored) {
            System.out.println("Was not able to open file and get a random word." +
                    "\nDefaulting to choice from 12 words...");
            secretWord = randomItemArr(new String[]{"cat", "bat", "hat", "dad",
                    "blast", "fast", "fact", "start",
                    "tag", "clan", "snack", "crack"});
        }

        return secretWord;
    }

    public static void main(String[] args) {
        var hangmanArt = getHangmanArt();
        var secretWord = getSecretWord();

        Input input = new Input();

        System.out.println("H A N G M A N");

        int currScore = game(hangmanArt, input, secretWord, "");

        // scoring system

        ArrayList<Integer> scores;
        try {
            scores = new ArrayList<> (
                    Files.readAllLines(Paths.get(SCORES_FILENAME)).stream()
                    .map(Integer::parseInt)
                    .toList()
            );
        } catch (Exception e) {
            // create a new one / wipe out the old one if it was corrupt and that caused the error
            // if even this fails then we got bigger problems to worry about
            try {
                Files.write(Paths.get(SCORES_FILENAME), new ArrayList<String>(), StandardCharsets.UTF_8);

                scores = new ArrayList<>();
            } catch (Exception anotherOne) {
                throw new RuntimeException(anotherOne);
            }
        }

        if (currScore == 0) {
            System.out.println("You lose!");
        } else {
            int maxScore = scores
                    .stream()
                    .reduce(0, Math::max, (acc, val) -> acc);

            System.out.print("You win! ");
            if (currScore > maxScore) {
                System.out.println("New high score of " + currScore + "!");
            } else {
                System.out.println("But, you did not beat your high score of " + maxScore + ".");
            }

            scores.add(currScore);
            try {

                Files.write(
                        Paths.get(SCORES_FILENAME),
                        scores.stream().map(score -> String.valueOf(score) + '\n').toList(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static Stream<String> strStream(final String input) {
        return Arrays.stream(input.split(""));
    }

    static int getGameScore(int corrects, int wrongs) {
        return corrects*100*(7-wrongs);
    }

    /// Returns whether the score of the game, which is 0 if the player loses
    static int game(final List<String> hangmanArt, final Input input, final String secretWord, String guesses) {
        // for wrongs, we need to go over each unique letter in *guesses* and determine if it's in *secretWord* or not
        int wrongs = new LinkedHashSet<>(strStream(guesses).toList())
                .stream()                                                 // NOTICE:  ZERO ONE
                .reduce(0, (acc, letter) -> acc + (secretWord.contains(letter)? 0 : 1), (acc, letter) -> acc);

        // for corrects, we need to go over each letter in *secretWord* and determine if *guesses* has it or not
        int corrects = strStream(secretWord)                       // NOTICE:  ONE  ZERO
                .reduce(0, (acc, letter) -> acc + (guesses.contains(letter) /*&& !guesses.isEmpty()*/? 1 : 0), (acc, letter) -> acc);

        String str2display = strStream(secretWord)
                .map(letter -> guesses.contains(letter)? letter : "_")
                .collect(Collectors.joining());

        System.out.println(hangmanArt.get(wrongs));
        System.out.println("Guesses: " + guesses);
        System.out.println("Progress: " + str2display);

        if (wrongs < 7) {
            if (corrects >= secretWord.length()) {
                return getGameScore(corrects, wrongs);
            } else { // you got another chance
                System.out.println("Guess a letter:");
                return game(hangmanArt, input, secretWord, guesses + input.nextLineNonempty().charAt(0));
            }
        } else {
            return 0;
        }
    }
}
