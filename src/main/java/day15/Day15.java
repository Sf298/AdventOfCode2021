package day15;

import lombok.val;
import utils.Utils;
import utils.graph.GridGraph;

import java.util.Arrays;

import static utils.graph.GraphUtils.boxed;

public class Day15 {

    private static final int DAY = Integer.parseInt(Day15.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        int[][] grid = Utils.streamLinesForDay(DAY)
                .map(l -> Arrays.stream(l.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        GridGraph<Integer> graph = new GridGraph<>(boxed(grid), 4);

        val shortestPath = graph.get(0,0).shortestWalk(graph.getWrap(-1, -1),
                (path,newNode) -> Long.valueOf(newNode.value));

        System.out.println(shortestPath);
        long ans = shortestPath.stream().mapToInt(n -> n.value).sum() - shortestPath.get(0).value;
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int[][] baseGrid = Utils.streamLinesForDay(DAY)
                .map(l -> Arrays.stream(l.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        int[][] grid = new int[baseGrid.length*5][baseGrid[0].length*5];
        for (int bigI = 0; bigI < grid.length; bigI++) {
            for (int bigJ = 0; bigJ < grid[bigI].length; bigJ++) {
                int smallI = bigI % baseGrid.length;
                int smallJ = bigJ % baseGrid[0].length;
                int mod = bigI / baseGrid.length + bigJ / baseGrid[0].length;
                int baseValue = baseGrid[smallI][smallJ];
                grid[bigI][bigJ] = ((baseValue + mod - 1)%9)+1;
            }
        }

        GridGraph<Integer> graph = new GridGraph<>(boxed(grid), 4);

        val shortestPath = graph.get(0,0).shortestWalk(graph.getWrap(-1, -1),
                (path,newNode) -> Long.valueOf(newNode.value));

        System.out.println(shortestPath);
        long ans = shortestPath.stream().mapToInt(n -> n.value).sum() - shortestPath.get(0).value;
        System.out.println("Part 2 ANS: " + ans);
    }

}
