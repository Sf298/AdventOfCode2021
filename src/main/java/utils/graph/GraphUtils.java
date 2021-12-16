package utils.graph;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GraphUtils {

    public static Integer[][] boxed(int[][] grid) {
        return Arrays.stream(grid)
                .map(a -> Arrays.stream(a).boxed().toArray(Integer[]::new))
                .toArray(Integer[][]::new);
    }
    public static Long[][] boxed(long[][] grid) {
        return Arrays.stream(grid)
                .map(a -> Arrays.stream(a).boxed().toArray(Long[]::new))
                .toArray(Long[][]::new);
    }
    public static Double[][] boxed(double[][] grid) {
        return Arrays.stream(grid)
                .map(a -> Arrays.stream(a).boxed().toArray(Double[]::new))
                .toArray(Double[][]::new);
    }

    public static <E> Stream<E> stream(Iterable<E> i) {
        Spliterator<E> splitItr = Spliterators.spliteratorUnknownSize(i.iterator(), Spliterator.ORDERED);
        return StreamSupport.stream(splitItr, false);
    }

}
