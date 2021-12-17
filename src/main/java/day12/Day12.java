package day12;

import lombok.val;
import utils.Utils;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

public class Day12 {

    private static final int DAY = Integer.parseInt(Day12.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        //part1();
        part2();
    }

    private static void part1() {
        Map<String, Set<String>> links = Utils.streamLinesForDay(DAY)
                .map(l -> l.split("-"))
                .flatMap(a -> Stream.of(a, new String[] {a[1],a[0]}))
                .collect(groupingBy(a -> a[0], mapping(a->a[1], toCollection(HashSet::new))));
        links.values().forEach(s -> s.remove("start"));

        int count = recursive(links, new ArrayList<>(List.of("start")));
        System.out.println(count);
    }
    private static int recursive(Map<String, Set<String>> remainingLinks, List<String> visited) {
        String currentNode = visited.get(visited.size()-1);
        if (currentNode.equals("end")) {
            System.out.println(visited);
            return 1;
        }

        int count = 0;
        if (remainingLinks.containsKey(currentNode)) {
            for (val toFollow : remainingLinks.get(currentNode)) {
                if (toFollow.charAt(0) >= 'a') {
                    if (visited.contains(toFollow)) continue;
                }
                val l = cloneAndAdd(visited, toFollow);
                count += recursive(remainingLinks, l);
            }
        }
        return count;
    }
    private static ArrayList<String> cloneAndAdd(List<String> list, String toAdd) {
        val out = new ArrayList<>(list);
        out.add(toAdd);
        return out;
    }
    private static boolean isLowercase(String s) {
        return s.charAt(0) >= 'a';
    }

    private static void part2() {
        Map<String, Set<String>> links = Utils.streamLinesForDay(DAY)
                .map(l -> l.split("-"))
                .flatMap(a -> Stream.of(a, new String[] {a[1],a[0]}))
                .collect(groupingBy(a -> a[0], mapping(a->a[1], toCollection(HashSet::new))));
        links.values().forEach(s -> s.remove("start"));

        Set<String> smallCaves = new HashSet<>(links.keySet());
        smallCaves.removeAll(List.of("start","end"));
        smallCaves.removeIf(u -> u.charAt(0) <= 'Z');

        Set<List<String>> routes = new HashSet<>();
        for (val cave : smallCaves) {
            recursive(links, new ArrayList<>(List.of("start")), routes, cave);
        }
        System.out.println(routes.size());
    }
    private static void recursive(Map<String, Set<String>> remainingLinks, List<String> visited, Set<List<String>> routes, String smallCaveTwice) {
        String currentNode = visited.get(visited.size()-1);
        if (currentNode.equals("end")) {
            routes.add(visited);
            return;
        }

        if (remainingLinks.containsKey(currentNode)) {
            for (val toFollow : remainingLinks.get(currentNode)) {
                if (toFollow.equals(smallCaveTwice)) {
                    if (Collections.frequency(visited, smallCaveTwice) >= 2) continue;
                } else {
                    if (isLowercase(toFollow) && visited.contains(toFollow)) continue;
                }
                val l = cloneAndAdd(visited, toFollow);
                recursive(remainingLinks, l, routes, smallCaveTwice);
            }
        }
    }

}
