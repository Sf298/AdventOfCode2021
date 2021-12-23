package utils.volume.set;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

public class Coordinate {

    private final int[] coord;
    private Integer hashCache;

    public static Coordinate of(int dims, int initialValue) {
        int[] coord = new int[dims];
        for (int i = 0; i < dims; i++) {
            coord[i] = initialValue;
        }
        return new Coordinate(coord);
    }

    public Coordinate(int... coord) {
        this.coord = Arrays.copyOf(coord, coord.length);
    }

    public int get(int index) {
        return coord[index];
    }

    public int dims() {
        return coord.length;
    }

    public Coordinate add(Coordinate c) {
        Coordinate newCoord = new Coordinate(coord);
        for (int i = 0; i < coord.length; i++) {
            newCoord.coord[i] = coord[i] + c.coord[i];
        }
        return newCoord;
    }

    public Coordinate subtract(Coordinate c) {
        Coordinate newCoord = new Coordinate(coord);
        for (int i = 0; i < coord.length; i++) {
            newCoord.coord[i] = coord[i] - c.coord[i];
        }
        return newCoord;
    }

    public Coordinate min(Coordinate c) {
        Coordinate newCoord = new Coordinate(coord);
        for (int i = 0; i < coord.length; i++) {
            newCoord.coord[i] = Math.min(coord[i], c.coord[i]);
        }
        return newCoord;
    }

    public Coordinate max(Coordinate c) {
        Coordinate newCoord = new Coordinate(coord);
        for (int i = 0; i < coord.length; i++) {
            newCoord.coord[i] = Math.max(coord[i], c.coord[i]);
        }
        return newCoord;
    }

    public Coordinate abs() {
        Coordinate newCoord = new Coordinate(coord);
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
        Coordinate coord1 = (Coordinate) o;
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
