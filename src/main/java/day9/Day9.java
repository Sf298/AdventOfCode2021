package day9;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;
import utils.graph.GridGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.graph.GraphUtils.boxed;
import static utils.graph.GraphUtils.stream;

public class Day9 {

    private static final int DAY = Integer.parseInt(Day9.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        int[][] grid = Utils.streamLinesForDay(DAY)
                .map(l -> Arrays.stream(l.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        long ans = IntStream.range(0, grid.length * grid[0].length)
                .mapToObj(i -> Pair.of(i % grid.length, i / grid.length))
                .filter(c -> {
                    int v = grid[c.getLeft()][c.getRight()];
                    return (c.getLeft()+1 >= grid.length || grid[c.getLeft()+1][c.getRight()] > v)
                            && (c.getLeft()-1 < 0 || grid[c.getLeft()-1][c.getRight()] > v)
                            && (c.getRight()+1 >= grid[c.getLeft()].length || grid[c.getLeft()][c.getRight()+1] > v)
                            && (c.getRight()-1 < 0 || grid[c.getLeft()][c.getRight()-1] > v);
                })
                .mapToInt(c -> grid[c.getLeft()][c.getRight()])
                .map(i -> i+1)
                .sum();

        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int[][] grid = Utils.streamLinesForDay(DAY)
                .map(l -> Arrays.stream(l.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        GridGraph<Integer> graph = new GridGraph<>(boxed(grid), 4);

        val minimums = stream(graph.get(0,0).breadthFirst())
                .map(n -> (GridGraph<Integer>.GridNode) n)
                .map(gn -> minimumPoint(gn))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<Set<GridGraph<Integer>.GridNode>> basins = new ArrayList<>();
        for (val min : minimums) {
            if (!basins.stream().anyMatch(b -> b.contains(min))) {
                basins.add(stream(min.breadthFirst(gn -> gn.value < 9))
                        .map(n -> (GridGraph<Integer>.GridNode) n)
                        .collect(Collectors.toSet()));
            }
        }

        //basins.forEach(b -> printCoords(graph, b));
        val prod = basins.stream()
                .map(Set::size)
                .sorted((f1, f2) -> Integer.compare(f2, f1))
                .limit(3)
                .reduce((integer, integer2) -> integer*integer2).orElseThrow();

        System.out.println("Part 2 ANS: " + prod);
    }

    private static GridGraph<Integer>.GridNode minimumPoint(GridGraph<Integer>.GridNode c) {
        while (true) {
            val C = c;
            val minNodes = c.adjacent.stream()
                    .filter(n -> n.value < C.value)
                    .sorted(Comparator.comparingInt(n -> n.value))
                    .collect(Collectors.toList());

            if (minNodes.size() > 1) return null;
            if (minNodes.isEmpty()) return c;

            c = (GridGraph<Integer>.GridNode) minNodes.get(0);
        }
    }

    private static void printCoords(GridGraph<Integer> graph, Set<GridGraph<Integer>.GridNode> nodes) {
        for (int i = 0; i < graph.grid.size(); i++) {
            for (int j = 0; j < graph.grid.get(i).size(); j++) {
                if (nodes.contains(graph.get(i,j))) {
                    System.out.print(graph.get(i,j).value);
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}
