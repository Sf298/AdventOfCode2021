package day11;

import lombok.val;
import utils.graph.GridGraph;
import utils.Utils;
import utils.graph.Node;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day11 {

    private static final int DAY = Integer.parseInt(Day11.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        int[][] grid = Utils.streamLinesForDay(DAY)
                .map(l -> Arrays.stream(l.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        GridGraph<Integer> graph = new GridGraph<>(GridGraph.boxed(grid), 8);
        long flashCount = 0;
        for (int step = 0; step < 100; step++) {
            Set<GridGraph.Coord> flashedNodes = new HashSet<>();

            for (val node : graph.get(0, 0).breadthFirst()) {
                node.value++;
            }

            /*for (val node : graph.get(0, 0).breadthFirst()) {
                if (node.value <= 9) {
                    continue;
                }
                System.out.println(node);
                printCoords(graph);
                for (val flashed : node.depthFirst(n -> n.value > 9 && !flashedNodes.contains(toGN(n).coord))) {
                    flashedNodes.add(toGN(flashed).coord);
                    flashed.adjacent.forEach(n -> n.value++);
                }
            }*/
            while (true) {
                boolean anyFlashed = false;
                for (val node : graph.get(0, 0).breadthFirst()) {
                    if (node.value <= 9 || flashedNodes.contains(toGN(node).coord)) {
                        continue;
                    }

                    flashedNodes.add(toGN(node).coord);
                    node.adjacent.forEach(n -> n.value++);
                    anyFlashed = true;
                }
                if (!anyFlashed) {
                    break;
                }
            }

            for (val node : graph.get(0, 0).breadthFirst()) {
                if (node.value > 9) node.value = 0;
            }

            flashCount += flashedNodes.size();
        }
        System.out.println(flashCount);
    }

    private static void part2() {
        int[][] grid = Utils.streamLinesForDay(DAY)
                .map(l -> Arrays.stream(l.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        GridGraph<Integer> graph = new GridGraph<>(GridGraph.boxed(grid), 8);
        for (int step = 0; step < 100000; step++) {
            Set<GridGraph.Coord> flashedNodes = new HashSet<>();

            for (val node : graph.get(0, 0).breadthFirst()) {
                node.value++;
            }

            while (true) {
                boolean anyFlashed = false;
                for (val node : graph.get(0, 0).breadthFirst()) {
                    if (node.value <= 9 || flashedNodes.contains(toGN(node).coord)) {
                        continue;
                    }

                    flashedNodes.add(toGN(node).coord);
                    node.adjacent.forEach(n -> n.value++);
                    anyFlashed = true;
                }
                if (!anyFlashed) {
                    break;
                }
            }

            for (val node : graph.get(0, 0).breadthFirst()) {
                if (node.value > 9) node.value = 0;
            }

            System.out.println(flashedNodes.size());
            if (flashedNodes.size() == graph.size()) {
                System.out.println("ans = " + (step+1));
                return;
            }
        }
    }

    private static void printCoords(GridGraph<Integer> graph) {
        for (int i = 0; i < graph.grid.size(); i++) {
            for (int j = 0; j < graph.grid.get(i).size(); j++) {
                System.out.print("\t" + graph.get(i,j).value);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static GridGraph<Integer>.GridNode toGN(Node<Integer> n) {
        return (GridGraph<Integer>.GridNode) n;
    }

}
