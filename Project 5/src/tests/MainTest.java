import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

class MainTest {
    // Python-generated random strings found in the .txt files in this tests folder
    // 1000 strings one for one line of each file
    // each string has length 100
    // and all strings are unique

    // they will go into five arrays:
    // one with lowercase letters,
    // one with uppercase,
    // one with numeric characters,
    // one with special characters,
    // one with all of them, i.e. no restrictions as to what the string can have
    final static String FILES_PREFIX = "src/tests/testStrings/";

    static String[] LOWER; final static String LOWER_FILENAME = FILES_PREFIX +"LOWER_STRINGS.txt";
    static String[] UPPER; final static String UPPER_FILENAME = FILES_PREFIX +"UPPER_STRINGS.txt";
    static String[] NUMERIC; final static String NUMERIC_FILENAME = FILES_PREFIX +"NUMERIC_STRINGS.txt";
    static String[] SPECIAL; final static String SPECIAL_FILENAME = FILES_PREFIX +"SPECIAL_STRINGS.txt";
    static String[] UNRESTRICTED; final static String UNRESTRICTED_FILENAME = FILES_PREFIX +"UNRESTRICTED_STRINGS.txt";

    // for a total of 1000*100*10 * 5 = 5,000,000 times
    // that isAlphabetic() will run.
    // if each string is 100 characters long,
    // that means 100,000,000 total checks of individual characters
    // even though it's pretty darn fast
    final static int REPETITIONS = 10;

    static String[] populateArray(String filename) {
        String[] arr;

        // catch block throws, I can't run my tests without the strings
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> strings = reader.lines().toList();

            arr = new String[strings.size()];
            for (int i = 0; i < strings.size(); i++) {
                arr[i] = strings.get(i);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return arr;
    }

    @BeforeAll
    static void setup() {
        LOWER = populateArray(LOWER_FILENAME);
        UPPER = populateArray(UPPER_FILENAME);
        NUMERIC = populateArray(NUMERIC_FILENAME);
        SPECIAL = populateArray(SPECIAL_FILENAME);
        UNRESTRICTED = populateArray(UNRESTRICTED_FILENAME);
    }

    @DisplayName("Lowercase Alphabetic Strings test")
    @RepeatedTest(REPETITIONS)
    void lower() {
        for (String str : LOWER) {
            assertTrue(Main.isAlphabetic(str), "String %s failed to pass the test".formatted(str));
        }
    }

    @DisplayName("Uppercase Alphabetic Strings test")
    @RepeatedTest(REPETITIONS)
    void upper() {
        for (String str : UPPER) {
            assertTrue(Main.isAlphabetic(str), "String %s failed to pass the test".formatted(str));
        }
    }

    @DisplayName("Numeric Strings test")
    @RepeatedTest(REPETITIONS)
    void numeric() {
        for (String str : NUMERIC) {
            assertFalse(Main.isAlphabetic(str), "String %s failed to pass the test".formatted(str));
        }
    }

    @DisplayName("Special Characters Strings test")
    @RepeatedTest(REPETITIONS)
    void special() {
        for (String str : SPECIAL) {
            assertFalse(Main.isAlphabetic(str), "String %s failed to pass the test".formatted(str));
        }
    }

    @DisplayName("Unrestricted Characters Strings test")
    @RepeatedTest(REPETITIONS)
    void unrestricted() {
        for (String str : UNRESTRICTED) {
            assertFalse(Main.isAlphabetic(str), "String %s failed to pass the test".formatted(str));
        }
    }
}