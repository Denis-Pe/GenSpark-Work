import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

class GameResultTest {
    // for a total of 250 results to check
    final static int RESULTS_LEN = 50;
    final static int TEST_REPETITION = 5;

    @DisplayName("Test every other result is correct")
    @RepeatedTest(TEST_REPETITION)
    void everyOther() {
        ArrayList<GameResult> results = new ArrayList<>();

        for (int i = 0; i < RESULTS_LEN; i++) {
            if (i%2 == 0) {
                results.add(new GameResult(true));
            } else {
                results.add(new GameResult(false));
            }
        }

        for (int i = 0; i < RESULTS_LEN; i++) {
            if (i%2 == 0) {
                assertEquals(results.get(i).getOutcomeMessage(), GameResult.Outcome.VICTORY_MESSAGE);
            } else {
                assertEquals(results.get(i).getOutcomeMessage(), GameResult.Outcome.DEATH_MESSAGE);
            }
        }
    }

    @DisplayName("Test every result is correct")
    @RepeatedTest(TEST_REPETITION)
    void allOfThem() {
        ArrayList<GameResult> results = new ArrayList<>();

        for (int i = 0; i < RESULTS_LEN; i++) {
            results.add(new GameResult(true));
        }

        for (GameResult result : results) {
            assertEquals(result.getOutcomeMessage(), GameResult.Outcome.VICTORY_MESSAGE);
        }
    }

    @DisplayName("Test every result is incorrect")
    @RepeatedTest(TEST_REPETITION)
    void noneOfThem() {
        ArrayList<GameResult> results = new ArrayList<>();

        for (int i = 0; i < RESULTS_LEN; i++) {
            results.add(new GameResult(false));
        }

        for (GameResult result : results) {
            assertEquals(result.getOutcomeMessage(), GameResult.Outcome.DEATH_MESSAGE);
        }
    }
}