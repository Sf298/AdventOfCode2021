package utils.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class GridGraph<T> {

    public List<List<GridNode>> grid;

    /**
     * Create a graph from an array of values.
     * @param grid The grid of values.
     * @param adjacency How the nodes link together. Can be 4 or 8.
     */
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
    public int size(int dimension) {
        List<?> l = grid;
        for (int i = 0; i < dimension; i++) {
            l = grid.get(0);
        }
        return l.size();
    }

    public GridNode get(Coord c) {
        return get(c.get(0), c.get(1));
    }
    public GridNode get(int i, int j) {
        if (i < 0 || j < 0 || i >= grid.size() || j >= grid.get(i).size()) {
            return null;
        }
        List<GridNode> gridNode = grid.get(i);
        return gridNode.get(j);
    }
    public GridNode getWrap(Coord c) {
        return getWrap(c.get(0), c.get(1));
    }
    public GridNode getWrap(int i, int j) {
        if (i < 0) i = size(0) + i;
        if (j < 0) j = size(1) + j;
        return get(i,j);
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
            return get(coord.get(0)+di, coord.get(1)+dj);
        }

        @Override
        public String toString() {
            return coord + ":" + value;
        }

    }

    public static class Coord {

        private final int[] coord;
        private Integer hashCache;

        public Coord(int... coord) {
            this.coord = Arrays.copyOf(coord, coord.length);
        }

        public int get(int index) {
            return coord[index];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord1 = (Coord) o;
            return Arrays.equals(coord, coord1.coord);
        }

        @Override
        public int hashCode() {
            if (isNull(hashCache)) {
                hashCache = Arrays.hashCode(coord);
            }
            return hashCache;
        }

        @Override
        public String toString() {
            return "(" + stream(coord).mapToObj(Integer::toString).collect(Collectors.joining(",")) + ')';
        }

    }

}
