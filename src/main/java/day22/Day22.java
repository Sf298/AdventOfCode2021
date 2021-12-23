package day22;

import lombok.val;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Triple;
import utils.Permutations;
import utils.Utils;
import utils.graph.GridGraph;
import utils.volume.set.Coordinate;
import utils.volume.set.Cuboid;
import utils.volume.set.VolumeSet;

import java.math.BigInteger;

public class Day22 {

    private static final int DAY = Integer.parseInt(Day22.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) throws InterruptedException {
        part1();
        Thread.sleep(8000);
        part2();
    }

    private static void part1() {
        FocusGrid3D g = new FocusGrid3D(new Coordinate(-50,-50,-50), new Coordinate(50,50,50));

        Utils.streamLinesForDay(DAY)
                .map(Day22::parseLine)
                .forEachOrdered(t -> g.setAllInclusive(t.getMiddle(), t.getRight(), t.getLeft().equals("on")));

        long count = 0;
        GridGraph.Coord fifty = new GridGraph.Coord(3, 50);
        for (int[] c : Permutations.permuteCuboid(3, 101)) {
            if (g.get(new GridGraph.Coord(c).subtract(fifty))) {
                count++;
            }
        }

        val ans = count;
        System.out.println("Part 1 ANS: " + ans);
    }

    private static Triple<String, Coordinate, Coordinate> parseLine(String line) {
        line = line.replace("..", ".").replaceAll("[xyz]=", "");
        String[] split = line.split("[., ]");

        Coordinate from = new Coordinate(Integer.parseInt(split[1]),
                Integer.parseInt(split[3]),
                Integer.parseInt(split[5])
        );
        Coordinate to = new Coordinate(Integer.parseInt(split[2]),
                Integer.parseInt(split[4]),
                Integer.parseInt(split[6])
        );

        return Triple.of(split[0], from, to);
    }

    private static class FocusGrid3D {

        private final Coordinate from;
        private final Coordinate to;
        private final boolean[][][] grid;

        public FocusGrid3D(Coordinate min, Coordinate max) {
            from = min;
            to = max;

            Coordinate range = max.subtract(min).abs().add(Coordinate.of(3,1));
            grid = new boolean[range.get(0)][range.get(1)][range.get(2)];
        }

        public void set(Coordinate coord, boolean newValue) {
            if(coord.dims() != 3) {
                throw new RuntimeException("Incorrect number of dimenstions!");
            }
            if (coord.get(0) < from.get(0) || coord.get(0) > to.get(0)) return;
            if (coord.get(1) < from.get(1) || coord.get(1) > to.get(1)) return;
            if (coord.get(2) < from.get(2) || coord.get(2) > to.get(2)) return;

            grid[coord.get(0)-from.get(0)][coord.get(1)-from.get(1)][coord.get(2)-from.get(2)] = newValue;
        }

        public void setAllInclusive(Coordinate from, Coordinate to, boolean newValue) {
            for (int i = Math.max(this.from.get(0), from.get(0)); i <= Math.min(this.to.get(0), to.get(0)); i++) {
                for (int j = Math.max(this.from.get(1), from.get(1)); j <= Math.min(this.to.get(1), to.get(1)); j++) {
                    for (int k = Math.max(this.from.get(2), from.get(2)); k <= Math.min(this.to.get(2), to.get(2)); k++) {
                        set(new Coordinate(i,j,k), newValue);
                    }
                }
            }
        }

        public boolean get(GridGraph.Coord coord) {
            return grid[coord.get(0)-from.get(0)][coord.get(1)-from.get(1)][coord.get(2)-from.get(2)];
        }

    }

    private static void part2() {
        VolumeSet set = new VolumeSet();

        long start = System.currentTimeMillis();
        Utils.streamLinesForDay(DAY)
                .map(Day22::parseLine)
                .forEachOrdered(t -> {
                    if (t.getLeft().equals("on")) {
                        set.add(new Cuboid(t.getMiddle(), t.getRight()));
                    } else {
                        set.subtract(new Cuboid(t.getMiddle(), t.getRight()));
                    }
                });
        System.out.println(System.currentTimeMillis() - start);


        val ans = set.area();
        System.out.println("Part 2 ANS: " + ans);
    }

}
