package utilities;

import java.util.List;

// Advanced random capabilities!
public class AdvRandom {
    /**
     * 
     * @param percent
     * @return a boolean, percent chance of being true
     */
    public static boolean nextBoolPercentage(int percent) {
        return (int) (Math.random() * 100) + 1 - percent <= 0;
    }

    /**
     * 
     * @param min minimum value of the integer
     * @param max one plus the max value of the integer
     * @return an integer between {@code min} inclusive and {@code max} exclusive
     */
    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * 
     * @param max one plus the max value of the integer
     * @return an integer between 0 inclusive and {@code max} exclusive
     */
    public static int randomInt(int max) {
        return randomInt(0, max);
    }

    /**
     * @param <T>         the type of the elements of the array
     * @param nonEmptyArr the array to pick an element from
     * @return a random element of the array
     * @throws IllegalArgumentException whenever the array given was empty
     */
    public static <T> T randomItemArr(T[] nonEmptyArr) {
        try {
            return nonEmptyArr[(int) (Math.random() * nonEmptyArr.length)];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("The collection given was empty");
        }
    }

    /**
     * @param <T>          the type of the elements of the list
     * @param nonEmptyList the list to pick an element from
     * @return a random element of the list
     * @throws IllegalArgumentException whenever the list given was empty
     */
    public static <T> T randomItemList(List<T> nonEmptyList) {
        try {
            return nonEmptyList.get((int) (Math.random() * nonEmptyList.size()));
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("The collection given was empty");
        }
    }

    /**
     * @param nonEmptyStr the string to pick an element from
     * @return a random element of the string
     * @throws IllegalArgumentException whenever the string given was empty
     */
    public static char randomCharStr(String nonEmptyStr) {
        try {
            return nonEmptyStr.charAt((int) (Math.random() * nonEmptyStr.length()));
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("The collection given was empty");
        }
    }

    /**
     * @param len length of the string returned
     * @return a random string of {@code len} size
     */
    public static String randomStr(int len) {
        String chars = "`~1234567890!@#$%^&*()-_=+[{]};:'\",<.>/?qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < len; i++) {
            output.append(randomCharStr(chars));
        }
        return output.toString();
    }
}
