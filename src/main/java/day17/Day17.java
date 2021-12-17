package day17;

import lombok.val;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class Day17 {

    private static final int DAY = Integer.parseInt(Day17.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        //part1();
        part2();
    }

    /*
    S.....#....#...#..#.##
    6
        5
    11      -1
        4
    15      -1
        3
    18      -1
        2
    20      -1
        1
    21      -1
        0
    21
     */
    private static void part1() {
        //String input = "target area: x=20..30, y=-10..-5";
        String input = "target area: x=185..221, y=-122..-74";
        String[] split = input.split(": ")[1].split(", ");
        String[] arr1 = split[0].substring(2).split("\\.\\.");
        String[] arr2 = split[1].substring(2).split("\\.\\.");
        int x1=Integer.parseInt(arr1[0]), x2=Integer.parseInt(arr1[1]);
        int y1=Integer.parseInt(arr2[0]), y2=Integer.parseInt(arr2[1]);

        Rectangle target = new Rectangle(x1, y1, x2-x1+1, y2-y1+1);
        System.out.println(target);

        List<Integer> optionsX = new ArrayList<>();
        for (int i = 0; i < target.width; i++) {
            int x = target.x + i;
            double speed = calcSpeedX(x);
            if (speed % 1 == 0) {
                optionsX.add((int) speed);
            }
        }
        //System.out.println(optionsX);
        int minTime = optionsX.stream().reduce(Integer.MAX_VALUE, Math::min);

        Integer maxYSpeed = null;
        loop1: for (int initialV = 1000000; initialV > 0; initialV--) {
            for (int dDown = (int)target.getMinY(); dDown < target.getMaxY(); dDown++) {
                double res = calcTimeForY(initialV, -dDown);
                if (res % 1 == 0 && res >= minTime) {
                    //System.out.printf("%d, %d -> %f\n", initialV, -dDown, res);
                    maxYSpeed = initialV;
                    break loop1;
                }
            }
        }

        int maxHeight = sumOfRange(maxYSpeed);
        System.out.println("Part 1 ANS: (" + minTime + "," + maxYSpeed + "), " + maxHeight);
    }
    private static int sumOfRange(int i) {
        return (i * (i+1))/2;
    }
    private static double calcSpeedX(int target) {
        return 0.5 * (sqrt(8 * target + 1) - 1);
    }

    private static double calcTimeForY(int initialV, int distanceDown) {
        if (initialV > 0) {
            int timeDelay = 2 * initialV; // t = (v-u)/a, times 2 for up and down
            double dropTime = 0.5 * (sqrt(8 * distanceDown + Math.pow(2 * initialV + 1, 2)) - 2 * initialV - 1);
            return timeDelay + dropTime;
        } else {
            int v = initialV;
            int d = 0;
            int t = 0;
            while (d <= distanceDown) {
                if (d == distanceDown) return t;
                d -= v;
                v--;
                t++;
            }
            return 0.5;
        }
    }

    private static void part2() {
        //String input = "target area: x=20..30, y=-10..-5";
        String input = "target area: x=185..221, y=-122..-74";
        String[] split = input.split(": ")[1].split(", ");
        String[] arr1 = split[0].substring(2).split("\\.\\.");
        String[] arr2 = split[1].substring(2).split("\\.\\.");
        int x1=Integer.parseInt(arr1[0]), x2=Integer.parseInt(arr1[1]);
        int y1=Integer.parseInt(arr2[0]), y2=Integer.parseInt(arr2[1]);

        Rectangle target = new Rectangle(x1, y1, x2-x1+1, y2-y1+1);
        System.out.println(target);


        int count = 0;
        for (int i = 0; i < target.getMaxX(); i++) {
            for (int j = -10000; j < 10000; j++) {
                if (simulateOne(i, j, target) != null) {
                    count++;
                }
            }
        }

        System.out.println("Part 2 ANS: " + count);
    }
    private static Integer simulateOne(int vx, int vy, Rectangle target) {
        int x=0,y=0;
        int t = 0;
        while (x <= target.getMaxX() && y >= target.getMinY()) {
            if (target.contains(x,y))
                return t;
            x += vx; y += vy;
            if(vx > 0) vx--; vy--;
            t++;
        }
        return null;
    }

}
