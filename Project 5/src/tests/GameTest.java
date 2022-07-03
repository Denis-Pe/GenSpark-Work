import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class GameTest {
    final static String ANSWER = "predictable";
    final static String ANSWER_UNDERSCORE = "___________";

    void testPrelude(Game game) {
        assertEquals("", game.getOutcome());
        assertEquals(ANSWER_UNDERSCORE, game.getWordState());

        assertFalse(game.isGameFailed());
        assertFalse(game.isGameWon());
    }

    @DisplayName("Test Victory")
    @Test
    void testGameWin() {
        Game game = new Game(false);

        testPrelude(game);

        for (char c : ANSWER.toCharArray()) {
            game.inputLetter(c);
            assertFalse(game.isGameFailed(), "Unexpected game failure");

        }

        assertTrue(game.isGameWon(), "Victory Unsuccessful - isGameWon");
        assertFalse(game.isGameFailed(), "Victory Unsuccessful - isGameFailed");
    }

    @DisplayName("Test Defeat")
    @Test
    void testGameFailed() {
        Game game = new Game(false);

        testPrelude(game);

        //
        for (char c : "absoluteRubbishHHHpredictable".toCharArray()) {
            game.inputLetter(c);
            assertFalse(game.isGameWon(), "Unexpected game victory");
        }

        assertTrue(game.isGameFailed(), "Failure Unsuccessful - isGameWon");
        assertFalse(game.isGameWon(), "Failure Unsuccessful - isGameFailed");
    }
}