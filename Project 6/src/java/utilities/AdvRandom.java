package utilities;

import java.util.List;

// Advanced random capabilities!
public class AdvRandom {
    /// Will return a boolean with percent probability
    public static boolean nextBoolPercentage(int percent) {
        return (int)(Math.random()*100)+1 - percent <= 0;
    }

    /// Precondition: arr is not empty
    public static <T> T randomItemArr(T[] arr) {
        return arr[(int)(Math.random()*arr.length)];
    }

    /// Precondition: list is not empty
    public static <T> T randomItemList(List<T> list) {
        return list.get((int)(Math.random()*list.size()));
    }

    // Read as a random Char from the Str
    /// Precondition: str is not empty
    public static char randomCharStr(String str) {
        return str.charAt((int)(Math.random()*str.length()));
    }

    public static String randomStr(int len) {
        String chars = "`~1234567890!@#$%^&*()-_=+[{]};:'\",<.>/?qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < len; i++) {
            output.append(randomCharStr(chars));
        }
        return output.toString();
    }
}
