import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

class MainTest {
    // for a total of 1,000 checks in each test!
    final static int SAMPLES_LEN = 100;
    final static int REPETITIONS = 10;

    @DisplayName("Test every other is too high")
    @RepeatedTest(REPETITIONS)
    void everyOtherTooHigh() {
        ArrayList<String> samples = new ArrayList<>();

        for (int i = 0; i < SAMPLES_LEN; i++) {
            if (i % 2 == 0) {
                samples.add(Main.getResponseUnequal(1, 2));
            } else {
                samples.add(Main.getResponseUnequal(2, 1));
            }
        }

        for (int i = 0; i < SAMPLES_LEN; i++) {
            if (i % 2 == 0) {
                assertEquals(samples.get(i), Main.TOO_LOW);
            } else {
                assertEquals(samples.get(i), Main.TOO_HIGH);
            }
        }
    }

    @DisplayName("Test all samples are too high")
    @RepeatedTest(REPETITIONS)
    void allTooHigh() {
        ArrayList<String> samples = new ArrayList<>();

        for (int i = 0; i < SAMPLES_LEN; i++) {
            samples.add(Main.getResponseUnequal(2, 1));
        }

        for (String response : samples) {
            assertEquals(response, Main.TOO_HIGH);
        }
    }

    @DisplayName("Test all samples are too low")
    @RepeatedTest(REPETITIONS)
    void allTooLow() {
        ArrayList<String> samples = new ArrayList<>();

        for (int i = 0; i < SAMPLES_LEN; i++) {
            samples.add(Main.getResponseUnequal(1, 2));
        }

        for (String response : samples) {
            assertEquals(response, Main.TOO_LOW);
        }
    }
}