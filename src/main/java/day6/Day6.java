package day6;

import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Day6 {

    private static final int DAY = Integer.parseInt(Day6.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        String text = Utils.streamLinesForDay(DAY).findFirst().orElseThrow();
        String[] initial = text.trim().split(",");
        List<Integer> itemsList = Arrays.stream(initial).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));

        System.out.println("Initial state: " + itemsList);
        for (int i = 0; i < 18; i++) {
            int initialSize = itemsList.size();
            for (int j = 0; j < initialSize; j++) {
                if (itemsList.get(j) == 0) {
                    itemsList.set(j, 6);
                    itemsList.add(8);
                } else {
                    itemsList.set(j, itemsList.get(j) - 1);
                }
            }
            //System.out.printf("After %2d days: %s\n", i+1, itemsList);
        }
        System.out.println("Part 1 ANS: " + itemsList.size());
    }

    private static final int MATURATION_PERIOD = 2;
    private static final int GESTATION_PERIOD = 6;
    private static final Map<Integer, Long> resultCache = new HashMap<>();
    private static void part2() {
        String text = Utils.streamLinesForDay(DAY).findFirst().orElseThrow();
        String[] initial = text.trim().split(",");
        List<Integer> itemsList = Arrays.stream(initial).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));

        itemsList.stream()
                .distinct()
                .parallel()
                .forEach(i -> {
                    long result = simulate(i, 256);
                    resultCache.put(i, result);
                });

        long sum = itemsList.stream()
                .mapToLong(resultCache::get)
                .map(i -> i+1)
                .peek(System.out::println)
                .sum();

        System.out.println("Part 2 ANS: " + sum);
    }
    private static long simulate(int initial, int daysRemaining) {
        daysRemaining -= (initial+1);

        long descendants = 0;
        for (; daysRemaining >= 0; daysRemaining -= (GESTATION_PERIOD+1)) {
            descendants += 1 + simulate(GESTATION_PERIOD + MATURATION_PERIOD, daysRemaining);
        }
        return descendants;
    }

}
