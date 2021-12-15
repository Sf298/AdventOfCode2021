package day1;

import utils.Utils;

import java.util.Scanner;

public class Day1 {

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        Scanner in = Utils.createScannerForDay(1);
        int previous = in.nextInt();

        int increaseCount = 0;
        while (in.hasNextInt()) {
            int current = in.nextInt();
            if (current > previous) {
                increaseCount++;
            }
            previous = current;
        }

        System.out.println("Part 1 ANS: " + increaseCount);
    }

    private static void part2() {
        int[] list = Utils.streamLinesForDay(1)
                .mapToInt(Integer::parseInt)
                .toArray();

        int increaseCount = 0;
        for (int i = 4; i < list.length+1; i++) {
            int previous = sum(list, 3, i-4);
            int current = sum(list, 3, i-3);
            if (current > previous) {
                increaseCount++;
            }
        }

        System.out.println("Part 2 ANS: " + increaseCount);
    }

    private static int sum(int[] arr, int count, int from) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += arr[from + i];
        }
        return sum;
    }

}
