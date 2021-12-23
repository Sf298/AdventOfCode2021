package utils.volume.set;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class VolumeSet {

    HashSet<Cuboid> cuboids = new HashSet<>();

    public BigInteger area() {
        return cuboids.stream().map(Cuboid::area).reduce(BigInteger.ZERO, BigInteger::add);
    }

    public void add(Cuboid toAdd) {
        List<Cuboid> affected = cuboids.stream()
                .filter(toAdd::intersects)
                .collect(toList());
        if (affected.isEmpty()) {
            cuboids.add(toAdd);
            return;
        }

        cuboids.removeAll(affected);

        List<Cuboid> newAffected = affected.stream()
                .flatMap(c -> c.segment(toAdd).stream())
                .filter(c -> !toAdd.contains(c))
                .collect(toList());

        newAffected.add(toAdd);

        List<Cuboid> merged = Cuboid.mergeAll(newAffected);

        cuboids.addAll(merged);
    }

    public void subtract(Cuboid toAdd) {
        List<Cuboid> affected = cuboids.stream()
                .filter(toAdd::intersects)
                .collect(toList());
        if (affected.isEmpty()) {
            return;
        }

        cuboids.removeAll(affected);

        List<Cuboid> newAffected = affected.stream()
                .flatMap(c -> c.segment(toAdd).stream())
                .filter(c -> !toAdd.contains(c))
                .collect(toList());

        List<Cuboid> merged = Cuboid.mergeAll(newAffected);

        cuboids.addAll(merged);
    }

}
