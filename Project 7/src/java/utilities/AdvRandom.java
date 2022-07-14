package utilities;

import java.util.List;

// Advanced random capabilities!
public class AdvRandom {
    /// Precondition: arr is not empty
    public static <T> T randomItemArr(T[] arr) {
        return arr[(int)(Math.random()*arr.length)];
    }

    /// Precondition: list is not empty
    public static <T> T randomItemList(List<T> list) {
        return list.get((int)(Math.random()*list.size()));
    }
}