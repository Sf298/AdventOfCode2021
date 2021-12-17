package day7;

import lombok.val;
import utils.Utils;

import java.util.Arrays;

public class Day7 {

    private static final int DAY = Integer.parseInt(Day7.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
        /*long start = System.currentTimeMillis();
        long n = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            n = x(i);
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(n);*/
    }

    private static void part1() {
        int[] numbers = Utils.streamLinesForDay(DAY)
                .flatMap(l -> Arrays.stream(l.split(",")))
                .mapToInt(Integer::parseInt)
                .toArray();

        System.out.println(Arrays.toString(numbers));
        System.out.println("mean: " + (Arrays.stream(numbers).sum() / (double) numbers.length));
        Arrays.sort(numbers);
        System.out.println(Arrays.toString(numbers));
        int median = numbers[numbers.length/2];

        int fuel = 0;
        for (int number : numbers) {
            fuel += Math.abs(number - median);
        }
        System.out.println("Part 1 ANS: " + fuel);
    }

    private static void part2() {
        int[] nums = Utils.streamLinesForDay(DAY)
                .flatMap(l -> Arrays.stream(l.split(",")))
                .mapToInt(Integer::parseInt)
                .toArray();
        int maxInput = Arrays.stream(nums).max().orElseThrow();

        int minDistIndex = -1;
        long minDist = Long.MAX_VALUE;
        for (int i = 0; i <= maxInput; i++) {
            long nowDist = dist(nums, i);
            if (nowDist < minDist) {
                minDist = nowDist;
                minDistIndex = i;
            }
        }
        System.out.println("Part 2 ANS: " + minDistIndex + " " + minDist);
    }
    private static long dist(int[] nums, int center) {
        long fuel = 0;
        for (int number : nums) {
            int dist = Math.abs(number - center);
            fuel += x(dist);
        }
        return fuel;
    }

    private static long x(int i) {
        return (i * (i+1L)) >>> 1;
    }

}
