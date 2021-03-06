package day19;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import utils.Utils;
import utils.graph.EdgeGraph;
import utils.graph.Node;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static utils.Combinations.combinations;
import static utils.Utils.stream;

public class Day19 {

    private static final int DAY = Integer.parseInt(Day19.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        /*var coordsIn = List.of(
                new Coord(-2, 0, 1),
                new Coord(-34, 34, 1),
                new Coord(-3, 0, 24)
        );
        /*var coordsOut = List.of(
                new Coord(-2, -1, 2),
                new Coord(-34, 33, 2),
                new Coord(-3, -1, 25)
        );
        val transform = Triple.of(CoordTransformation.FORWARDS, 3, new Coord(10, 45, -6));
        System.out.println("in " + coordsIn);
        val res = CoordTransformation.transform(transform, coordsIn);
        System.out.println("out " + res);
        val undo = CoordTransformation.transformInv(transform, res);
        System.out.println("undo " + undo);*/


        part1();
        //part2();
    }

    private static void part1() {
        String rawInput = Utils.getStringForDay(-DAY);

        // parse coords from String
        // [section1[coord1, coor2, ...], section2[coord1, coor2, ...]]
        System.out.println("Reading file");
        List<List<Coord>> coords = Arrays.stream(rawInput.split("\n\n"))
                .map(s -> s.replaceAll("--- [a-z0-9 ]+ ---\n", ""))
                .map(Coord::parseSection)
                .collect(toList());

        // map each coord in a section to every other coord in the same section
        // {<section1, section2>, [[coord1, coor2], [coord1, coor3], ...]}
        System.out.println("pairing coord pairs");
        Map<Integer, Set<Set<Coord>>> sectionToAllPairs = new HashMap<>();
        for (int i = 0; i < coords.size(); i++) {
            Set<Set<Coord>> sectionPairs = stream(combinations(coords.get(i), 2))
                    .map(HashSet::new)
                    .collect(toSet());
            sectionToAllPairs.put(i, sectionPairs);
        }

        sectionToAllPairs.entrySet().forEach(System.out::println);

        // compare coord pairs to pairs in every other section by distance between pairs
        // {<section1, section2>, [DistancePair1, DistancePair2, ...]}
        System.out.println("calculating distances between pairs");
        Map<Pair<Integer,Integer>, List<DistancePair>> sectionsToAllDistancePairs = new HashMap<>();
        for (List<Map.Entry<Integer, Set<Set<Coord>>>> entry : combinations(sectionToAllPairs.entrySet())) {
            //sectionsToAllDistancePairs.put(entry, calculateDistances(sectionToAllPairs.get(i), sectionToAllPairs.get(j), i, j));
        }

        sectionsToAllDistancePairs.entrySet().forEach(System.out::println);

/*        // loop through each combination of 3 DistancePairs and filter out triples of pairs that dont loop
        // reduce the triples of pairs into 3 coordinates (keep the mapping of sections)
        // {<section1, section2>, [CoordTriplet1, CoordTriplet2, ...]}
        System.out.println("finding loops of triples");
        Map<Pair<Integer,Integer>, List<CoordTriplet>> sectionsToCoordTriplets = new HashMap<>();
        for (Map.Entry<Pair<Integer, Integer>, List<DistancePair>> e1 : sectionsToAllDistancePairs.entrySet()) {
            List<CoordTriplet> coordTriplets = new ArrayList<>();
            for (List<DistancePair> pairsForSubsection : combinations(e1.getValue(), 3)) {
                if (!DistancePair.isLooped(pairsForSubsection.get(0).pair1, pairsForSubsection.get(1).pair1, pairsForSubsection.get(2).pair1)) continue;
                if (!DistancePair.isLooped(pairsForSubsection.get(0).pair2, pairsForSubsection.get(1).pair2, pairsForSubsection.get(2).pair2)) continue;

                coordTriplets.add(new CoordTriplet(pairsForSubsection, e1.getKey().getLeft(), e1.getKey().getRight()));
            }
            if (!coordTriplets.isEmpty())
                sectionsToCoordTriplets.put(e1.getKey(), coordTriplets);
        }


        // loop through each triple with its neighbours, using an intersection to determine what coordinate maps to what.
        // {<section1, section2>, [MappedPair1, MappedPair2, ...]}
        System.out.println("connect triple, with neighbor section");
        Map<Pair<Integer,Integer>, Set<MappedPair>> sectionsToMappedPairs = new HashMap<>();
        for (Map.Entry<Pair<Integer, Integer>, List<CoordTriplet>> e1 : sectionsToCoordTriplets.entrySet()) {
            List<CoordTriplet> triplets = e1.getValue();
            Set<MappedPair> mappedPairs = new HashSet<>();
            for (int i = 0; i < triplets.size(); i++) {
                for (int j = 0; j < triplets.size(); j++) {
                    if (i == j) continue;
                    CoordTriplet intersection = triplets.get(i).intersect(triplets.get(j));
                    if (intersection.triplet1.size() != 1) continue;
                    if (intersection.triplet2.size() != 1) continue;

                    mappedPairs.add(new MappedPair(e1.getKey().getLeft(), e1.getKey().getRight(),
                            intersection.triplet1.iterator().next(),
                            intersection.triplet2.iterator().next()
                    ));
                }
            }
            if (!mappedPairs.isEmpty())
                sectionsToMappedPairs.put(e1.getKey(), mappedPairs);
        }

        /*Set<Integer> sectionsFound = new HashSet<>();
        for (val e1 : sectionsToMappedPairs.entrySet()) {
            if (e1.getValue().size() < 12) continue;
            sectionsFound.add(e1.getKey().getLeft());
            sectionsFound.add(e1.getKey().getRight());
            for (val t : e1.getValue()) {
                System.out.println(t);
            }
            System.out.println();
        }
        System.out.println(sectionsFound);*

        // find the transformation for each mapped section.
        // {<section1, section2>, (rotation, numberOfClockwiseRotations, translation)}
        System.out.println("calculating transforms");
        Map<Pair<Integer,Integer>, Triple<CoordTransformation, Integer, Coord>> sectionsToTransformations = new HashMap<>();
        for (Map.Entry<Pair<Integer, Integer>, Set<MappedPair>> e1 : sectionsToMappedPairs.entrySet()) {
            List<MappedPair> mappedPairs = new ArrayList<>(e1.getValue());
            List<Coord> inputs = mappedPairs.stream().map(mp -> mp.pair.getLeft()).collect(toList());
            List<Coord> outputs = mappedPairs.stream().map(mp -> mp.pair.getRight()).collect(toList());
            val transformation = CoordTransformation.detectTransformation(inputs, outputs);
            sectionsToTransformations.put(e1.getKey(), transformation);
        }
        /*for (val e1 : sectionsToTransformations.entrySet()) {
            System.out.println(e1);
        }*

        // map all existing coordinates back to section 0
        val edges = sectionsToTransformations.keySet().stream()
                .flatMap(p -> Stream.of(List.of(p.getKey(), p.getValue()), List.of(p.getValue(), p.getKey())))
                .collect(toList());
        EdgeGraph<Integer> graph = new EdgeGraph<>(edges);

        // debug print
        Node<Integer> node0 = graph.getByValue(0);
        for (int i = 1; i < coords.size(); i++) {
            val walk = graph.getByValue(i).shortestWalk(node0/*,
                    (n1,n2) -> (sectionsToTransformations.containsKey(Pair.of(n1.value, n2.value)) ? 1 : 99L)*);
            if (walk.size() != 2) continue;
            val t = sectionsToTransformations.get(Pair.of(walk.get(0).value, 0));
            val r = CoordTransformation.transform(t, coords.get(walk.get(0).value));
            System.out.println(coords.get(walk.get(0).value));
            System.out.println(coords.get(0));
            System.out.println(r);
            System.out.println();
            System.out.println(i + " " + walk);
        }*/


        int ans = 0;
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
    }

    private static List<DistancePair> calculateDistances(Set<Set<Coord>> l1Pairs, Set<Set<Coord>> l2Pairs, int s1, int s2) {
        List<DistancePair> out = new ArrayList<>();
        for (Set<Coord> pair1 : l1Pairs) {
            Iterator<Coord> i1 = pair1.iterator();
            Coord pair1Diff = i1.next().subtract(i1.next());
            for (Set<Coord> pair2 : l2Pairs) {
                Iterator<Coord> i2 = pair2.iterator();
                Coord pair2Normal = i2.next().subtract(i2.next());
                // comparing by hamiltonian then euclidean increases match chance, also runs faster
                if(pair1Diff.hamming() == pair2Normal.hamming() && pair1Diff.magnitudeSq() == pair2Normal.magnitudeSq()) {
                    out.add(new DistancePair(s1, pair1, s2, pair2, Math.sqrt(pair1Diff.magnitudeSq())));
                }
            }
        }
        return out;
    }

    private static class CoordTriplet {
        int section1;
        Set<Coord> triplet1;
        int section2;
        Set<Coord> triplet2;

        public CoordTriplet(int section1, int section2, Set<Coord> triplet1, Set<Coord> triplet2) {
            this.section1 = section1;
            this.triplet1 = triplet1;
            this.section2 = section2;
            this.triplet2 = triplet2;
        }

        public CoordTriplet(List<DistancePair> pairsForSubsection, int section1, int section2) {
            this.section1 = section1;
            this.section2 = section2;
            triplet1 = pairsForSubsection.stream().flatMap(p -> p.pair1.stream()).collect(toSet());
            triplet2 = pairsForSubsection.stream().flatMap(p -> p.pair2.stream()).collect(toSet());
        }

        public CoordTriplet intersect(CoordTriplet triplet) {
            Set<Coord> t1 = new HashSet<>(triplet1);
            t1.removeAll(triplet.triplet1);
            Set<Coord> t2 = new HashSet<>(triplet2);
            t2.removeAll(triplet.triplet2);
            return new CoordTriplet(section1, section2, t1, t2);
        }

        @Override
        public String toString() {
            return section1 + " " + section2 + " " + triplet1 + " " + triplet2;
        }
    }

    private static class DistancePair {
        double dist;
        int section1;
        Set<Coord> pair1;
        int section2;
        Set<Coord> pair2;

        public static boolean isLooped(List<Coord> e1, List<Coord> e2, List<Coord> e3) {
            // AB AC BC
            int i1;
            if ((i1 = e2.indexOf(e1.get(0))) != -1) {           // check A in AB
                int otherI1 = (i1+1) & 1;                       // get index of C in AC
                int i2;
                if ((i2 = e3.indexOf(e2.get(otherI1))) != -1) { // check C in BC
                    int otherI2 = (i2 + 1) & 1;                 // get index of A in AC
                    return e3.get(otherI2).equals(e1.get(1));   // ensure A in AC == AB[0]
                }
            } else if ((i1 = e2.indexOf(e1.get(1))) != -1) {    // check A in AB
                int otherI1 = (i1+1) & 1;                       // get index of C in AC
                int i2;
                if ((i2 = e3.indexOf(e2.get(otherI1))) != -1) { // check C in BC
                    int otherI2 = (i2 + 1) & 1;                 // get index of A in AC
                    return e3.get(otherI2).equals(e1.get(0));   // ensure A in AC == AB[0]
                }
            }
            return false;
        }

        public DistancePair(int section1, Set<Coord> pair1, int section2, Set<Coord> pair2, double dist) {
            this.dist = dist;
            this.section1 = section1;
            this.pair1 = pair1;
            this.section2 = section2;
            this.pair2 = pair2;
        }

        public boolean contains(Coord c) {
            return pair1.contains(c) || pair2.contains(c);
        }

        public boolean contains(Collection<Coord> cs) {
            return cs.stream().anyMatch(c -> pair1.contains(c) || pair2.contains(c));
        }

        public boolean containsCommon(DistancePair p) {
            Iterator<Coord> i1 = p.pair1.iterator();
            Iterator<Coord> i2 = p.pair2.iterator();
            return contains(i1.next()) || contains(i1.next()) || contains(i2.next()) || contains(i2.next());
        }

        @Override
        public String toString() {
            return section1 + " " + section2 + " " + pair1 + " " + pair2 + " " + dist;
        }
    }

    private static class MappedPair {
        int section1;
        int section2;
        Pair<Coord,Coord> pair;

        public MappedPair(int section1, int section2, Coord c1, Coord c2) {
            this.section1 = section1;
            this.section2 = section2;
            this.pair = Pair.of(c1, c2);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MappedPair that = (MappedPair) o;
            return section1 == that.section1 && section2 == that.section2 && Objects.equals(pair, that.pair);
        }

        @Override
        public int hashCode() {
            return Objects.hash(section1, section2, pair);
        }

        @Override
        public String toString() {
            return section1 + " " + section2 + " " + pair;
        }

    }

    private static class Coord {
        int x, y, z;
        public static List<Coord> parseSection(String section) {
            return Arrays.stream(section.trim().split("\n"))
                    .map(Coord::new)
                    .collect(toList());
        }
        public Coord(String row) {
            String[] vals = row.split(",");
            this.x = Integer.parseInt(vals[0]);
            this.y = Integer.parseInt(vals[1]);
            this.z = Integer.parseInt(vals[2]);
        }
        public Coord(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Coord add(Coord c) {
            return new Coord(x+c.x, y+c.y, z+c.z);
        }
        public Coord subtract(Coord c) {
            return new Coord(x-c.x, y-c.y, z-c.z);
        }
        public int magnitudeSq() {
            return x*x + y*y + z*z;
        }
        public int hamming() {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return x == coord.x && y == coord.y && z == coord.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public String toString() {
            return "{" + x + "," + y + "," + z + '}';
        }
    }

    private enum CoordTransformation {

        FORWARDS(c -> new Coord(c.x, c.y, c.z)), // coords dont change
        UP(c -> new Coord(c.x, c.z, -c.y)), // coords move up from perspective of observer
        DOWN(c -> new Coord(c.x, -c.z, c.y)), // coords move down from perspective of observer
        LEFT(c -> new Coord(-c.z, c.y, c.x)), // coords move left from perspective of observer
        RIGHT(c -> new Coord(c.z, c.y, -c.x)), // coords move right from perspective of observer
        BACKWARDS(c -> new Coord(-c.x, c.y, -c.z)), // coords move right from perspective of observer

        CLOCKWISE(c -> new Coord(c.y, -c.x, c.z)), // coords rotate clockwise from perspective of observer
        ANTICLOCKWISE(c -> new Coord(-c.y, c.x, c.z)); // coords rotate anti-clockwise from perspective of observer

        public static final Map<CoordTransformation, CoordTransformation> INV_ROTATION = Map.of(
                FORWARDS, FORWARDS,
                UP, DOWN,
                DOWN, UP,
                LEFT, RIGHT,
                RIGHT, LEFT,
                BACKWARDS, BACKWARDS
        );

        Function<Coord, Coord> transformLogic;

        CoordTransformation(Function<Coord, Coord> transformLogic) {
            this.transformLogic = transformLogic;
        }

        public Coord rotate(Coord inputCoordinate) {
            return transformLogic.apply(inputCoordinate);
        }

        public List<Coord> rotate(List<Coord> inputCoordinates) {
            return inputCoordinates.stream().map(this::rotate).collect(toList());
        }

        public static Coord findTranslation(List<Coord> inputCoords, List<Coord> outputCoords){
            Coord firstTranslation = outputCoords.get(0).subtract(inputCoords.get(0));
            for (int i = 1; i < inputCoords.size(); i++) {
                if (!firstTranslation.equals(outputCoords.get(1).subtract(inputCoords.get(1)))) {
                    return null;
                }
            }
            return firstTranslation;
        }

        public static Triple<CoordTransformation, Integer, Coord> detectTransformation(List<Coord> inputCoords, List<Coord> outputCoords){
            for (CoordTransformation t : CoordTransformation.values()) {
                List<Coord> transformed = t.rotate(inputCoords);

                for (int i = 0; i < 4; i++) {
                    transformed = CLOCKWISE.rotate(transformed);
                    Coord translation = findTranslation(transformed, outputCoords);
                    if (nonNull(translation))
                        return Triple.of(t, i, translation);
                }

            }
            return null;
        }

        public static List<Coord> transform(Triple<CoordTransformation, Integer, Coord> transform, List<Coord> coords) {
            List<Coord> rotated = transform.getLeft().rotate(coords);
            for (int i = 0; i < transform.getMiddle(); i++) {
                rotated = CLOCKWISE.rotate(rotated);
            }
            return rotated.stream()
                    .map(c -> transform.getRight().add(c))
                    .collect(toList());
        }

        public static List<Coord> transformInv(Triple<CoordTransformation, Integer, Coord> transform, List<Coord> coords) {
            List<Coord> transformed = coords.stream()
                    .map(c -> c.subtract(transform.getRight()))
                    .collect(toList());
            for (int i = 0; i < transform.getMiddle(); i++) {
                transformed = ANTICLOCKWISE.rotate(transformed);
            }
            return INV_ROTATION.get(transform.getLeft()).rotate(transformed);
        }

    }

}
