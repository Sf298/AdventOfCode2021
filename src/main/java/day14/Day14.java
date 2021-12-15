package day14;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

public class Day14 {

    private static final int DAY = Integer.parseInt(Day14.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        String lines = Utils.streamLinesForDay(DAY).collect(joining("\n"));
        String[] split1 = lines.split("\n\n");
        Map<String, Character> mappings = Arrays.stream(split1[1].split("\n"))
                .map(l -> l.split(" -> "))
                .collect(toMap(a -> a[0], a -> a[1].charAt(0)));
        char[] sequence = split1[0].toCharArray();

        System.out.println("Initial: " + new String(sequence));
        for (int step = 0; step < 10; step++) {
            char[] newSequence = new char[sequence.length*2-1];
            for (int i = 0; i < sequence.length; i++) {
                int mappedIndex = i << 1;
                newSequence[mappedIndex] = sequence[i];
            }

            for (int i = 0; i < sequence.length-1; i++) {
                char l = sequence[i];
                char r = sequence[i+1];
                char newChar = mappings.get(""+l+r);

                int mappedIndex = i << 1;
                newSequence[mappedIndex+1] = newChar;
            }
            sequence = newSequence;
            //System.out.println("After step "+(step+1)+": " + new String(sequence));
        }

        char[] finalSequence = sequence;
        Map<Character, Long> totals = IntStream.range(0, sequence.length)
                .mapToObj(i -> finalSequence[i])
                .collect(groupingBy(Function.identity(), counting()));
        System.out.println(totals);

        long ans = ansFromTotals(totals);
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        String lines = Utils.streamLinesForDay(DAY).collect(joining("\n"));
        String[] split1 = lines.split("\n\n");
        Map<String, Character> mappings = Arrays.stream(split1[1].split("\n"))
                .map(l -> l.split(" -> "))
                .collect(toMap(a -> a[0], a -> a[1].charAt(0)));

        Map<String, Long> pairs = new HashMap<>();
        for (int i = 0; i < split1[0].length()-1; i++) {
            String pair = split1[0].substring(i, i+2);
            pairs.put(pair, pairs.getOrDefault(pair,0L) + 1L);
        }


        for (int step = 0; step < 40; step++) {
            pairs = pairs.entrySet().stream()
                    .flatMap(e -> {
                        char newChar = mappings.get(e.getKey());
                        String[] arr = e.getKey().split("");
                        return Stream.of(
                                Pair.of(arr[0]+newChar, e.getValue()),
                                Pair.of(newChar+arr[1], e.getValue())
                        );
                    })
                    .collect(groupingBy(Pair::getKey, summingLong(Pair::getValue)));
        }

        Set<String> chars = pairs.keySet().stream().flatMap(k -> Arrays.stream(k.split(""))).collect(toSet());
        Map<String, Long> finalPairs = pairs;
        Map<Character, Long> counts = chars.stream()
                .map(c -> {
                    long startsWithCount = finalPairs.entrySet().stream().filter(k -> k.getKey().startsWith(c)).mapToLong(Map.Entry::getValue).sum();
                    return Pair.of(c.charAt(0), startsWithCount);
                })
                .collect(toMap(Pair::getKey, Pair::getValue));

        // correction for missing end letter
        char endLetter = split1[0].charAt(split1[0].length()-1);
        counts.put(endLetter, counts.get(endLetter) + 1L);

        System.out.println(counts);
        long ans = ansFromTotals(counts);
        System.out.println("Part 2 ANS: " + ans);
    }

    private static Long ansFromTotals(Map<Character, Long> totals) {
        Character min = null;
        Character max = null;
        for (val e : totals.entrySet()) {
            if (isNull(min) || totals.get(min) > e.getValue()) {
                min = e.getKey();
            }
            if (isNull(max) || totals.get(max) < e.getValue()) {
                max = e.getKey();
            }
        }
        return totals.get(max) - totals.get(min);
    }

}
