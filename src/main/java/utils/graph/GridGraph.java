package utils.graph;

import java.util.*;

import static java.util.Objects.nonNull;

public class GridGraph<T> {

    public List<List<GridNode>> grid;

    public static Integer[][] boxed(int[][] grid) {
        return Arrays.stream(grid)
                .map(a -> Arrays.stream(a).boxed().toArray(Integer[]::new))
                .toArray(Integer[][]::new);
    }

    public GridGraph(T[][] grid, int adjacency) {
        if (adjacency != 4 && adjacency != 8) {
            throw new IllegalArgumentException("Invalid adjacency '" + adjacency + "'");
        }

        this.grid = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            List<GridNode> gi = new ArrayList<>();
            this.grid.add(gi);
            for (int j = 0; j < grid[i].length; j++) {
                gi.add(new GridNode(grid[i][j], new Coord(i,j)));
            }
        }

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (nonNull(get(i-1, j))) get(i, j).adjacent.add(get(i-1, j));
                if (nonNull(get(i+1, j))) get(i, j).adjacent.add(get(i+1, j));
                if (nonNull(get(i, j-1))) get(i, j).adjacent.add(get(i, j-1));
                if (nonNull(get(i, j+1))) get(i, j).adjacent.add(get(i, j+1));

                if (adjacency == 8) {
                    if (nonNull(get(i-1, j-1))) get(i, j).adjacent.add(get(i-1, j-1));
                    if (nonNull(get(i+1, j-1))) get(i, j).adjacent.add(get(i+1, j-1));
                    if (nonNull(get(i-1, j+1))) get(i, j).adjacent.add(get(i-1, j+1));
                    if (nonNull(get(i+1, j+1))) get(i, j).adjacent.add(get(i+1, j+1));
                }
            }
        }
    }

    public int size() {
        return grid.size() * grid.get(0).size();
    }

    public GridNode get(Coord c) {
        return get(c.i, c.j);
    }
    public GridNode get(int i, int j) {
        if (i < 0 || j < 0 || i >= grid.size() || j >= grid.get(i).size()) {
            return null;
        }
        List<GridNode> gridNode = grid.get(i);
        return gridNode.get(j);
    }

    public class GridNode extends Node<T> {

        public Coord coord;

        public GridNode(T value, Coord coord) {
            this(value, coord, null);
        }

        public GridNode(T value, Coord coord, Collection<Node<T>> adjacent) {
            super(value, adjacent);
            this.coord = coord;
        }

        public GridNode getAdjacent(int di, int dj) {
            return get(coord.i+di, coord.i+dj);
        }

        @Override
        public String toString() {
            return "{" + coord + ":" + value + "}";
        }

    }

    public static class Coord {
        public int i;
        public int j;

        public Coord(int i, int j) {
            this.i = i;
            this.j = j;
        }
        public Coord(Coord c) {
            this(c.i, c.j);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return i == coord.i && j == coord.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }

        @Override
        public String toString() {
            return "(" + i + "," + j + ')';
        }
    }

}
