package utils.volume.set;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.IntStream.range;

public class Cuboid {

    private final Coordinate mins;
    private final Coordinate maxs;
    private final int hashCache;

    public static boolean surrounds(int p1Min, int p1Max, int p2Min, int p2Max) {
        return p1Min <= p2Min && p2Max <= p1Max;
    }
    public static boolean overlaps(int p1Min, int p1Max, int p2Min, int p2Max) {
        return (p1Min <= p2Min && p2Min <= p1Max) ^ (p1Min <= p2Max && p2Max <= p1Max);
    }
    public static boolean unconnected(int p1Min, int p1Max, int p2Min, int p2Max) {
        return p1Max < p2Min || p2Max < p1Min;
    }

    public Cuboid(Coordinate mins, Coordinate maxs) {
        if (mins.dims() != maxs.dims()) {
            throw new RuntimeException("mins dimensions dont match maxs dimensions");
        }
        if (range(0, mins.dims()).anyMatch(i -> mins.get(i) > maxs.get(i))) {
            throw new RuntimeException("mins values should be less than or equal to their maxs counterpart");
        }

        this.mins = mins;
        this.maxs = maxs;
        hashCache = Objects.hash(mins.hashCode(), maxs.hashCode());
    }

    public int min(int dim) {
        return mins.get(dim);
    }
    public int max(int dim) {
        return maxs.get(dim);
    }

    public int dims() {
        return mins.dims();
    }

    public BigInteger area() {
        return range(0, mins.dims())
                .map(i -> max(i)-min(i)+1)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);
    }


    public boolean contains(Cuboid c) {
        return range(0, mins.dims()).allMatch(i -> surrounds(min(i), max(i), c.min(i), c.max(i)));
    }

    public boolean contains(Coordinate c) {
        return range(0, mins.dims()).allMatch(i -> surrounds(min(i), max(i), c.get(i), c.get(i)));
    }

    public boolean intersects(Cuboid c) {
        if (contains(c) || c.contains(this)) return true;
        long overlapCount = range(0, mins.dims())
                .filter(i -> overlaps(min(i), max(i), c.min(i), c.max(i))
                        || surrounds(min(i), max(i), c.min(i), c.max(i))
                        || surrounds(c.min(i), c.max(i), min(i), max(i)))
                .limit(2)
                .count();
        return overlapCount > 1;
    }

    public boolean alignedInAxis(Cuboid r, int axis) {
        return min(axis) == r.min(axis) && max(axis) == r.max(axis);
    }

    public boolean alignedAdjacently(Cuboid r) {
        int axisNotAdjacent = -1;
        for (int i = 0; i < maxs.dims(); i++) {
            if (!alignedInAxis(r, i)) {
                if (axisNotAdjacent != -1) { // found a second axis not aligned, quit
                    return false;
                }
                axisNotAdjacent = i; // record the unaligned axis
            }
        }

        if (axisNotAdjacent == -1) return false;

        return max(axisNotAdjacent)==r.min(axisNotAdjacent)-1 || min(axisNotAdjacent)==r.max(axisNotAdjacent)+1;
    }


    public Cuboid merge(Cuboid cuboid) {
        if (!alignedAdjacently(cuboid)) return null;
        return new Cuboid(mins.min(cuboid.mins), maxs.max(cuboid.maxs));
    }

    public static List<Cuboid> mergeAll(Collection<Cuboid> cuboids) {
        List<Cuboid> cs = new ArrayList<>(cuboids);

        outerLoop: while (true) {
            for (int i = 0; i < cs.size(); i++) {
                Cuboid ci = cs.get(i);
                for (int j = 0; j < i; j++) {
                    Cuboid merged = ci.merge(cs.get(j));
                    if (nonNull(merged)) {
                        cs.remove(i);
                        cs.remove(j);
                        cs.add(merged);
                        continue outerLoop;
                    }
                }
            }
            break;
        }

        return cs;
    }

    public List<Cuboid> segment(Cuboid cuboid) {
        return segment(singletonList(this), cuboid);
    }

    public static List<Cuboid> segment(List<Cuboid> existingCuboids, Cuboid newCuboid) {
        List<Cuboid> out = new ArrayList<>(existingCuboids);

        // split
        for (int i = 0; i < newCuboid.dims(); i++) {
            int I = i;
            out = out.stream()
                    .flatMap(c -> c.slice(I, newCuboid.min(I)-1).stream())
                    .flatMap(c -> c.slice(I, newCuboid.max(I)).stream())
                    .collect(Collectors.toList());
        }

        return out;
    }

    /**
     * Slice this cube into 2 between position and position+1.
     * @param axis The axis perpendicular to which the slice is made.
     * @param position The position on the axis for which the slice should be made.
     * @return The newly created
     */
    public List<Cuboid> slice(int axis, int position) {
        if (position < min(axis) || max(axis) <= position) return List.of(this);

        int[] mins = this.mins.toArray();
        mins[axis] = position+1;
        int[] maxs = this.maxs.toArray();
        maxs[axis] = position;
        return List.of(
                new Cuboid(this.mins, new Coordinate(maxs)),
                new Cuboid(new Coordinate(mins), this.maxs)
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuboid rectangle = (Cuboid) o;
        return mins.equals(rectangle.mins) && maxs.equals(rectangle.maxs);
    }

    @Override
    public int hashCode() {
        return hashCache;
    }

    @Override
    public String toString() {
        return "Rectangle{min X,Y = " + mins +", max X,Y = " + maxs + '}';
    }


    public static void print2DLayers(List<Cuboid> cuboids) {
        int minX = cuboids.stream().mapToInt(c -> c.min(0)).min().orElse(0);
        int maxX = cuboids.stream().mapToInt(c -> c.max(0)).max().orElse(0);
        int minY = cuboids.stream().mapToInt(c -> c.min(1)).min().orElse(0);
        int maxY = cuboids.stream().mapToInt(c -> c.max(1)).max().orElse(0);

        for (int j = minY-2; j <= maxY+2; j++) {
            for (int i = minX-2; i <= maxX+2; i++) {
                int I = i;
                int J = j;
                long index = cuboids.stream()
                        .filter(c -> c.contains(new Coordinate(I,J)))
                        .count();
                System.out.print(index==0 ? "." : ""+index);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printByIndex(List<Cuboid> cuboids) {
        int minX = cuboids.stream().mapToInt(c -> c.min(0)).min().orElse(0);
        int maxX = cuboids.stream().mapToInt(c -> c.max(0)).max().orElse(0);
        int minY = cuboids.stream().mapToInt(c -> c.min(1)).min().orElse(0);
        int maxY = cuboids.stream().mapToInt(c -> c.max(1)).max().orElse(0);

        for (int j = minY-2; j <= maxY+2; j++) {
            for (int i = minX-2; i <= maxX+2; i++) {
                int I = i;
                int J = j;
                int index = IntStream.range(0, cuboids.size())
                        .filter(ii -> cuboids.get(ii).contains(new Coordinate(I,J)))
                        .findFirst().orElse(-1);
                System.out.print(index==-1 ? "." : ""+index);
            }
            System.out.println();
        }
        System.out.println();
    }

}
