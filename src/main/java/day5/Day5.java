package day5;

import utils.Utils;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

public class Day5 {

    //private static final int DAY = Integer.parseInt(Day2.class.getName().replaceAll("^0-9", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        List<String> lines = Utils.streamLinesForDay(5).collect(toList());

        List<int[]> coords = lines.stream()
                .flatMap(s -> Arrays.stream(s.split(" -> ")))
                .map(s -> s.split(","))
                .map(a -> new int[] {parseInt(a[0]), parseInt(a[1])})
                .collect(toList());

        int maxX = 0, maxY = 0;
        for (int[] coord : coords) {
            maxX = Math.max(maxX, coord[0]);
            maxY = Math.max(maxY, coord[1]);
        }

        int[][] grid = new int[maxX+1][maxY+1];
        for (String line : lines) {
            int[][] coord = convertLine(line);
            int x1=coord[0][0], y1=coord[0][1], x2=coord[1][0], y2=coord[1][1];
            if (x1 != x2 && y1 != y2) {
                continue;
            }

            for (int i = x1, j = y1; i != x2 || j != y2;) {
                grid[i][j]++;
                if (x1 < x2) i++;
                if (x1 > x2) i--;
                if (y1 < y2) j++;
                if (y1 > y2) j--;
            }
            grid[x2][y2]++;
        }

        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > 1) {
                    count++;
                }
            }
        }

        System.out.println("Part 1 ANS: " + count);
    }

    private static void part2() {
        List<String> lines = Utils.streamLinesForDay(5).collect(toList());

        List<int[]> coords = lines.stream()
                .flatMap(s -> Arrays.stream(s.split(" -> ")))
                .map(s -> s.split(","))
                .map(a -> new int[] {parseInt(a[0]), parseInt(a[1])})
                .collect(toList());

        int maxX = 0, maxY = 0;
        for (int[] coord : coords) {
            maxX = Math.max(maxX, coord[0]);
            maxY = Math.max(maxY, coord[1]);
        }

        int[][] grid = new int[maxX+1][maxY+1];
        for (String line : lines) {
            int[][] coord = convertLine(line);
            int x1=coord[0][0], y1=coord[0][1], x2=coord[1][0], y2=coord[1][1];

            for (int i = x1, j = y1; i != x2 || j != y2;) {
                grid[i][j]++;
                if (x1 < x2) i++;
                if (x1 > x2) i--;
                if (y1 < y2) j++;
                if (y1 > y2) j--;
            }
            grid[x2][y2]++;
        }


        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > 1) {
                    count++;
                }
            }
        }

        System.out.println("Part 2 ANS: " + count);
    }

    private static int[][] convertLine(String line) {
        String[] split = line.split(" -> ");
        return new int[][] {
                convertCoord(split[0]),
                convertCoord(split[1])
        };
    }
    private static int[] convertCoord(String c) {
        String[] split = c.split(",");
        return new int[] {
                parseInt(split[0]),
                parseInt(split[1])
        };
    }

    private static void printGrid(int[][] grid) {
        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                System.out.print(grid[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

}
