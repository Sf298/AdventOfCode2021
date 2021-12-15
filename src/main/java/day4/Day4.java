package day4;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class Day4 {

    private static final int DAY = Integer.parseInt(Day4.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part2();
    }

    private static void part1() {
        String text = Utils.streamLinesForDay(DAY).collect(Collectors.joining("\n"));
        String[] split = text.split("\n\n");
        List<Integer> drawSequence = Arrays.stream(split[0].split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Grid> grids = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            grids.add(new Grid(split[i]));
        }

        for (val draw : drawSequence) {
            grids.forEach(g -> g.setFound(draw));
            for (int i = 0; i < grids.size(); i++) {
                Grid g = grids.get(i);
                int scoreSum = g.calculateScoreSum();
                if (scoreSum != -1) {
                    System.out.println(scoreSum * draw);
                    return;
                }
            }
        }
    }

    private static void part2() {
        String text = Utils.streamLinesForDay(DAY).collect(Collectors.joining("\n"));
        String[] split = text.split("\n\n");
        List<Integer> drawSequence = Arrays.stream(split[0].split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Grid> grids = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            grids.add(new Grid(split[i]));
        }

        for (val draw : drawSequence) {
            List<Grid> incompletePre = grids.stream()
                    .filter(g -> g.calculateScoreSum() == -1)
                    .collect(Collectors.toList());

            grids.forEach(g -> g.setFound(draw));
            if (grids.stream().allMatch(g -> g.calculateScoreSum() >= 0)) {
                Grid g = incompletePre.get(incompletePre.size()-1);
                System.out.println(g.calculateScoreSum() * draw);
                return;
            }
        }
    }

    private static class Grid {

        Map<Integer, Pair<Integer, Integer>> index = new HashMap<>();
        boolean[][] found;

        public Grid(String s) {
            String[] rows = s.split("\n");
            for (int i=0; i<rows.length; i++) {
                String[] values = rows[i].trim().split("[ ]+");
                for (int j = 0; j < values.length; j++) {
                    index.put(Integer.parseInt(values[j]), Pair.of(i,j));
                }
            }

            int maxH = index.values().stream().mapToInt(Pair::getLeft).max().orElseThrow();
            int maxW = index.values().stream().mapToInt(Pair::getRight).max().orElseThrow();
            found = new boolean[maxH+1][maxW+1];
        }

        public void setFound(int value) {
            val coord = index.get(value);
            if (isNull(coord)) return;
            found[coord.getLeft()][coord.getRight()] = true;
        }

        public int calculateScoreSum() {
            boolean wasFound = false;
            for (int i = 0; i < found.length; i++) {
                boolean allTrue = true;
                for (int j = 0; j < found[i].length; j++) {
                    allTrue &= found[i][j];
                }
                if (allTrue) wasFound = true;
            }
            for (int i = 0; i < found[0].length; i++) {
                boolean allTrue = true;
                for (int j = 0; j < found.length; j++) {
                    allTrue &= found[j][i];
                }
                if (allTrue) wasFound = true;
            }
            if(!wasFound) {
                return -1;
            }

            val flipped = Utils.reverseMap(index);
            int sum = 0;
            for (int i = 0; i < found.length; i++) {
                for (int j = 0; j < found[0].length; j++) {
                    if (!found[i][j]) {
                        sum += flipped.get(Pair.of(i,j));
                    }
                }
            }
            return sum;
        }

        /*private int[] getRowOrColumn(int[] rcCoord) {
            Map<Pair<Integer, Integer>, Integer> flipped = Utils.reverseMap(index);
            if (rcCoord[0] == -1) {
                int[] out = new int[found[0].length];
                for (int i = 0; i < out.length; i++) {
                    out[i] = flipped.get(Pair.of(i, rcCoord[1]));
                }
                return out;
            } else if(rcCoord[1] == -1) {
                int[] out = new int[found.length];
                for (int i = 0; i < out.length; i++) {
                    out[i] = flipped.get(Pair.of(rcCoord[0], i));
                }
                return out;
            }
            return null;
        }*/

        @Override
        public String toString() {
            Map<Pair<Integer, Integer>, Integer> flipped = Utils.reverseMap(index);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < found.length; i++) {
                for (int j = 0; j < found[i].length; j++) {
                    sb.append(flipped.get(Pair.of(i,j))).append("\t");
                }
                sb.append("\n");
            }
            return sb.toString();
        }

    }
}
