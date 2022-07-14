package utilities;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static utilities.AdvRandom.*;

// So how do we test randomness?
// I think that the proper way to do it
// is run things a zillion times and
// see how the odds came out.
// If there's, in theory, a 50% chance
// of something, the number of times it
// happens should be at least closer and
// closer to 50% the more times it happens
class AdvRandomTest {
    // feel free to tweak any of these values if you got a few minutes and/or a faster VM or PC

    final static long REPETITIONS = 100_000_000L;
    final static double WIGGLE_ROOM = 0.001; // plus minus 0.1%
    final static int MAX_COLLECTION_SIZE = 100; // for tests relating to collections like lists and arrays

    // ----PERCENTAGE BOOLEAN TESTING----

    void testPercent(int percent) {
        long numTrue = 0;
        for (long i = 0L; i < REPETITIONS; i++) {
            numTrue += nextBoolPercentage(percent) ? 1 : 0;
        }

        double percTrue = (double) numTrue / REPETITIONS;
        double shouldBePerc = (double) percent / 100;

        assertTrue(shouldBePerc <= percTrue + WIGGLE_ROOM
                         && shouldBePerc >= percTrue - WIGGLE_ROOM,
                "Failed test, %f is not %d%%".formatted(percTrue, percent));

        System.out.println("Result: " + percTrue);
    }

    @DisplayName("50% chance testing")
    @Test
    void percentage50() {
        testPercent(50);
    }

    @DisplayName("Test bool from 0% to 100%, incrementing by 5")
    @Test
    void percentageIncreasing() {
        for (int perc = 0; perc <= 100; perc+=5) {
            testPercent(perc);
        }
    }

    // ----COLLECTIONS & STRINGS----
    // to test collections, I will pick the middle element
    // of a collection of a range of integers from 1 to the collection size,
    // and check if that element is picked the % amount of times
    // that it technically has a chance to be picked
    //
    // for strings, I will do something similar; we got a string of unique letters,
    // check if the middle letter is picked the % amount of times that it
    // has a chance to be picked

    void testItemArr(Integer[] arr) {
        int middle = arr[arr.length/2];
        long timesPicked = 0;
        for (long i = 0; i < REPETITIONS; i++) {
            timesPicked += randomItemArr(arr) == middle ? 1 : 0;
        }

        double percTrue = (double) timesPicked / REPETITIONS;
        double shouldBePerc = 1.0 / arr.length;

        assertTrue(shouldBePerc <= percTrue + WIGGLE_ROOM
                        && shouldBePerc >= percTrue - WIGGLE_ROOM,
                "Failed test for %d, %f is not %03f%%".formatted(middle, percTrue, shouldBePerc*100));

        System.out.println("Result: " + percTrue);
    }

    void testItemList(List<Integer> list) {
        int middle = list.get(list.size()/2);
        long timesPicked = 0;
        for (long i = 0; i < REPETITIONS; i++) {
            timesPicked += randomItemList(list) == middle ? 1 : 0;
        }

        double percTrue = (double) timesPicked / REPETITIONS;
        double shouldBePerc = 1.0 / list.size();

        assertTrue(shouldBePerc <= percTrue + WIGGLE_ROOM
                        && shouldBePerc >= percTrue - WIGGLE_ROOM,
                "Failed test for %d, %f is not %03f%%".formatted(middle, percTrue, shouldBePerc*100));

        System.out.println("Result: " + percTrue);
    }

    void testCharStr(String str) {
        int middle = str.charAt(str.length()/2);
        long timesPicked = 0;
        for (long i = 0; i < REPETITIONS; i++) {
            timesPicked += randomCharStr(str) == middle ? 1 : 0;
        }

        double percTrue = (double) timesPicked / REPETITIONS;
        double shouldBePerc = 1.0 / str.length();

        assertTrue(shouldBePerc <= percTrue + WIGGLE_ROOM
                        && shouldBePerc >= percTrue - WIGGLE_ROOM,
                "Failed test for %c, %f is not %03f%%".formatted(middle, percTrue, shouldBePerc*100));

        System.out.println("Result: " + percTrue);
    }

    @DisplayName("Test array from size 10 to MAX_COLLECTION_SIZE, incrementing by 10")
    @Test
    void testRandomItemArrIncLen() {
        for (int i = 10; i <= MAX_COLLECTION_SIZE; i+=10) {
            Integer[] test = Arrays
                    .stream(IntStream.rangeClosed(1, i).boxed().toArray())
                    .map(obj -> (Integer) obj)
                    .toList()
                    .toArray(new Integer[0]);

            testItemArr(test);
        }
    }

    @DisplayName("Test list from size 10 to MAX_COLLECTION_SIZE, incrementing by 10")
    @Test
    void testRandomItemListIncLen() {
        for (int i = 10; i <= MAX_COLLECTION_SIZE; i+=10) {
            List<Integer> test = IntStream.rangeClosed(1, MAX_COLLECTION_SIZE)
                    .boxed()
                    .toList();

            testItemList(test);
        }
    }

    @DisplayName("Testing different strings")
    @Test
    void testRandomCharStr() {
        String[] tests = {
                "abcdefghijklmnopqrstuvwxyz",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "0123456789",
                "`~!@#$%^&*()-_=+[{]};:'\",<.>/?",
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789`~!@#$%^&*()-_=+[{]};:'\",<.>/?"
        };

        for (String test : tests) {
            testCharStr(test);
        }
    }

    // since I do not ensure unique letter strings coming from randomStr() and that is its intended behavior, I do not test it
}