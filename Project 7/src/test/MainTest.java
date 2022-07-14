import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import utilities.Input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MainTest {
    final static String WORDS_FILENAME = "resources/random_words.txt";
    static List<String> words;

    @BeforeAll
    static void setup() {
        try (BufferedReader reader = new BufferedReader(new FileReader(WORDS_FILENAME))) {
            words = reader.lines().toList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot start test due to error opening the random words file.", e);
        }
    }

    @DisplayName("Testing the strStream() method")
    @Test
    void testStrStream() {
        for (String word : words) {
            List<String> wordList = Arrays.stream(word.split("")).toList();
            List<String> strStreamList = Main.strStream(word).toList();

            assertEquals(wordList, strStreamList,
                    wordList.toString() + " failed to assert equality with " + strStreamList.toString());
        }
    }

    @DisplayName("Testing the game() method")
    @Test
    void testGame() {
        List<String> hangmanArt = Main.getHangmanArt();
        var input = new Input();

        for (String word : words) {
            int corrects = word.length();
            int wrongs = 0;
            int score = Main.game(hangmanArt, input, word, word);

            assertEquals(score, Main.getGameScore(corrects, wrongs),
                    "Failed to assert equality between test score and game score");
        }
    }
}