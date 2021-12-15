package day13;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

public class Day13 {

    private static final int DAY = Integer.parseInt(Day13.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        //part1();
        part2();
    }

    private static void part1() {
        String lines = Utils.streamLinesForDay(DAY).collect(joining("\n"));
        String[] split1 = lines.split("\n\n");
        Set<Pair<Integer, Integer>> coords = Arrays.stream(split1[0].split("\n"))
                .map(Day13::toCoord)
                .collect(toSet());
        List<Pair<String, Integer>> folds = Arrays.stream(split1[1].split("\n"))
                .map(l -> l.split(" ")[2])
                .map(l -> l.split("="))
                .map(a -> Pair.of(a[0], Integer.parseInt(a[1])))
                .collect(toList());

        int v = folds.get(0).getValue();
        if (folds.get(0).getKey().equals("y")) {
            coords = coords.stream()
                    .map(c -> {
                        if (c.getRight() < v) return c;
                        return Pair.of(c.getLeft(), v-(c.getRight()-v));
                    })
                    .collect(toSet());
        } else {
            coords = coords.stream()
                    .map(c -> {
                        if (c.getLeft() < v) return c;
                        return Pair.of(v-(c.getLeft()-v), c.getRight());
                    })
                    .collect(toSet());
        }

        System.out.println("Part 1 ANS: " + coords.size());
    }

    private static void part2() {
        String lines = Utils.streamLinesForDay(DAY).collect(joining("\n"));
        String[] split1 = lines.split("\n\n");
        Set<Pair<Integer, Integer>> coords = Arrays.stream(split1[0].split("\n"))
                .map(Day13::toCoord)
                .collect(toSet());
        List<Pair<String, Integer>> folds = Arrays.stream(split1[1].split("\n"))
                .map(l -> l.split(" ")[2])
                .map(l -> l.split("="))
                .map(a -> Pair.of(a[0], Integer.parseInt(a[1])))
                .collect(toList());

        for (val fold : folds) {
            int v = fold.getValue();

            if (fold.getKey().equals("y")) {
                coords = coords.stream()
                        .map(c -> {
                            if (c.getRight() < v) return c;
                            return Pair.of(c.getLeft(), v-(c.getRight()-v));
                        })
                        .collect(toSet());
            } else {
                coords = coords.stream()
                        .map(c -> {
                            if (c.getLeft() < v) return c;
                            return Pair.of(v-(c.getLeft()-v), c.getRight());
                        })
                        .collect(toSet());
            }
        }
        print(coords);

        System.out.println("Part 2 ANS: " + coords.size());
    }

    private static Pair<Integer, Integer> toCoord(String line) {
        val split = line.split(",");
        return Pair.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private static void print(Set<Pair<Integer, Integer>> coords) {
        int xMax=0, yMax=0;
        for (val coord : coords) {
            xMax = Math.max(xMax, coord.getLeft());
            yMax = Math.max(yMax, coord.getRight());
        }

        for (int j = 0; j <= yMax; j++) {
            for (int i = 0; i <= xMax; i++) {
                if (coords.contains(Pair.of(i,j))) {
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}
