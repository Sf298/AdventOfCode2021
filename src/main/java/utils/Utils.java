package utils;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

public class Utils {

    public static String getStringForDay(int day) {
        return streamLinesForDay(day).collect(Collectors.joining("\n"));
    }

    public static Scanner createScannerForDay(int day) {
        val is = Utils.class.getClassLoader().getResourceAsStream("day"+day+"Input.txt");
        return new Scanner(is);
    }

    public static Stream<String> streamLinesForDay(int day) {
        val in = createScannerForDay(day);
        val iterator = new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return in.hasNextLine();
            }

            @Override
            public String next() {
                return in.nextLine();
            }
        };
        val splitItr = Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
        return StreamSupport.stream(splitItr, false);
    }
    public static Stream<String> streamLinesForDayTest(int day) {
        return streamLinesForDay(-day);
    }

    public static boolean betweenInc(long l, long min, long max) {
        return min <= l && l <= max;
    }
    public static boolean betweenInc(String s, long min, long max) {
        val l = Long.parseLong(s);
        return betweenInc(l, min, max);
    }

    public static <K,V> Map<V, List<K>> reverseMultiMap(Map<K, List<V>> map) {
        return map.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(v -> Pair.of(v, e.getKey())))
                .collect(Collectors.groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));
    }

    public static <K,V> Map<V,K> reverseMap(Map<K,V> map) {
        return map.entrySet().stream().collect(toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public static <T,R> List<R> aFor(Collection<T> c, Function<T,R> f) {
        return c.stream().map(f).collect(toList());
    }

    public static <T> int[] aForInt(T[] a, ToIntFunction<T> f) {
        return Arrays.stream(a).mapToInt(f).toArray();
    }

    public static <T> Stream<T> stream(Iterable<T> i) {
        return stream(i.iterator());
    }

    public static <T> Stream<T> stream(Iterator<T> i) {
        val splitItr = Spliterators.spliteratorUnknownSize(i, Spliterator.ORDERED);
        return StreamSupport.stream(splitItr, false);
    }

}
