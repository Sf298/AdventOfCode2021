package day3;

import utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Day3 {

    private static final int DAY = Integer.parseInt(Day3.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        List<String> lines = Utils.streamLinesForDay(DAY).collect(toList());

        int length = lines.get(0).length();

        int[] counts = new int[length];
        lines.stream()
                .map(String::toCharArray)
                .forEach(a -> {
                    for (int i = 0; i < counts.length; i++) {
                        if (a[i] == '1') {
                            counts[i]++;
                        }
                    }
                });

        int midPoint = lines.size()/2;
        List<String> gammaStrList = Arrays.stream(counts)
                .boxed()
                .map(i -> (i > midPoint) ? "1" : "0")
                .collect(toList());
        int gamma = Integer.parseInt(String.join("", gammaStrList), 2);
        int epsilon = (((~gamma) << (32-length)) >>> (32-length));

        System.out.println("Part 1 ANS: " + (gamma * epsilon));
    }

    private static void part2() {
        List<int[]> lines = Utils.streamLinesForDay(DAY)
                .map(String::toCharArray)
                .map(Day3::chToInt)
                .collect(toList());

        int length = lines.get(0).length;

        // O2Gen
        List<int[]> currentLines = lines;
        for (int i = 0; i < length; i++) {
            final int I = i;
            int[] mostCommon = findMostCommon(currentLines);
            currentLines = currentLines.stream()
                    .filter(l -> l[I] == mostCommon[I])
                    .collect(toList());
            if(currentLines.size() == 1) {
                break;
            }
        }
        String o2GenStr = Arrays.stream(currentLines.get(0))
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(""));
        int o2Gen = Integer.parseInt(o2GenStr, 2);

        // CO2Gen
        currentLines = lines;
        for (int i = 0; i < length; i++) {
            final int I = i;
            int[] leastCommon = findLeastCommon(currentLines);
            currentLines = currentLines.stream()
                    .filter(l -> l[I] == leastCommon[I])
                    .collect(toList());
            if(currentLines.size() == 1) {
                break;
            }
        }
        String co2GenStr = Arrays.stream(currentLines.get(0))
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(""));
        int co2Gen = Integer.parseInt(co2GenStr, 2);

        System.out.println("Part 2 ANS: " + (o2Gen * co2Gen));
    }

    private static int[] findMostCommon(List<int[]> inputs) {
        int length = inputs.get(0).length;

        int[] counts = new int[length];
        inputs.forEach(a -> {
            for (int i = 0; i < counts.length; i++) {
                if (a[i] == 1) {
                    counts[i]++;
                }
            }
        });

        double midPoint = inputs.size()/2.0;
        return Arrays.stream(counts)
                .map(i -> (i >= midPoint) ? 1 : 0)
                .toArray();
    }

    private static int[] findLeastCommon(List<int[]> inputs) {
        int length = inputs.get(0).length;

        int[] counts = new int[length];
        inputs.forEach(a -> {
            for (int i = 0; i < counts.length; i++) {
                if (a[i] == 1) {
                    counts[i]++;
                }
            }
        });

        double midPoint = inputs.size()/2.0;
        return Arrays.stream(counts)
                .map(i -> (i < midPoint) ? 1 : 0)
                .toArray();
    }

    private static int[] chToInt(char[] c) {
        int[] out = new int[c.length];
        for (int i = 0; i < c.length; i++) {
            out[i] = c[i] - '0';
        }
        return out;
    }

}
