package utils.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class GridGraph<T> {

    public List<List<GridNode>> grid;

    /**
     * Create a graph from an array of values.
     * @param shape The dimensions of the grid.
     * @param adjacency How the nodes link together. Can be 4 or 8.
     * @param generator A function that returns the initial value for the given coordinate.
     */
    public GridGraph(int[] shape, int adjacency, Function<Coord, T> generator) {
        if (adjacency != 4 && adjacency != 8) {
            throw new IllegalArgumentException("Invalid adjacency '" + adjacency + "'");
        }

        this.grid = new ArrayList<>();

        for (int i = 0; i < shape[0]; i++) {
            List<GridNode> gi = new ArrayList<>();
            this.grid.add(gi);
            for (int j = 0; j < shape[1]; j++) {
                Coord c = new Coord(new int[] {i, j});
                gi.add(new GridNode(generator.apply(c), c));
            }
        }

        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                if (isNull(get(i, j))) continue;

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

    /**
     * Create a graph from an array of values.
     * @param grid The grid of values.
     * @param adjacency How the nodes link together. Can be 4 or 8.
     */
    public GridGraph(T[][] grid, int adjacency) {
        this(new int[] {grid.length, grid[0].length}, adjacency, c -> grid[c.get(0)][c.get(1)]);
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

        public Coord(int[] coord) {
            this.coord = Arrays.copyOf(coord, coord.length);
        }

        public Coord(int dims, int initialValue) {
            this.coord = new int[dims];
            for (int i = 0; i < dims; i++) {
                coord[i] = initialValue;
            }
        }

        public int get(int index) {
            return coord[index];
        }

        public int dims() {
            return coord.length;
        }

        public Coord add(Coord c) {
            Coord newCoord = new Coord(coord);
            for (int i = 0; i < coord.length; i++) {
                newCoord.coord[i] = coord[i] + c.coord[i];
            }
            return newCoord;
        }

        public Coord subtract(Coord c) {
            Coord newCoord = new Coord(coord);
            for (int i = 0; i < coord.length; i++) {
                newCoord.coord[i] = coord[i] - c.coord[i];
            }
            return newCoord;
        }

        public Coord min(Coord c) {
            Coord newCoord = new Coord(coord);
            for (int i = 0; i < coord.length; i++) {
                newCoord.coord[i] = Math.min(coord[i], c.coord[i]);
            }
            return newCoord;
        }

        public Coord max(Coord c) {
            Coord newCoord = new Coord(coord);
            for (int i = 0; i < coord.length; i++) {
                newCoord.coord[i] = Math.max(coord[i], c.coord[i]);
            }
            return newCoord;
        }

        public Coord abs() {
            Coord newCoord = new Coord(coord);
            for (int i = 0; i < coord.length; i++) {
                newCoord.coord[i] = Math.abs(coord[i]);
            }
            return newCoord;
        }

        public int[] toArray() {
            return Arrays.copyOf(coord, coord.length);
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
