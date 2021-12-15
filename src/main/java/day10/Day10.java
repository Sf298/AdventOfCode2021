package day10;

import lombok.val;
import utils.Utils;

import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class Day10 {

    private static final int DAY = Integer.parseInt(Day10.class.getSimpleName().replaceAll("[^0-9]", ""));

    private static final Set<Character> openChars = Set.of('(', '[', '{', '<');
    private static final Set<Character> closeChars = Set.of(')', ']', '}', '>');
    private static final Map<Character, Character> pairs = new HashMap<>();
    private static final Map<Character, Integer> points = Map.of(')', 3,
                                                                 ']', 57,
                                                                 '}', 1197,
                                                                 '>', 25137);
    private static final Map<Character, Integer> scores = Map.of(')', 1,
                                                                 ']', 2,
                                                                 '}', 3,
                                                                 '>', 4);

    public static void main(String[] args) {
        pairs.putAll(Map.of('(',')', ')','(',
                            '[',']', ']','['));
        pairs.putAll(Map.of('{','}', '}','{',
                            '<','>', '>','<'));

        part1();
        part2();
    }

    private static void part1() {
        val ans = Utils.streamLinesForDay(DAY)
                .map(Day10::findErrors)
                .filter(Error::isCorrupted)
                .mapToInt(e -> points.get(e.c))
                .sum();
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        val sorted = Utils.streamLinesForDay(DAY)
                .map(Day10::findErrors)
                .filter(Error::isIncomplete)
                .mapToLong(e -> calculateClosingScore(e.missingCharacters))
                .sorted()
                .boxed()
                .collect(toList());

        System.out.println("sorted size " + sorted.size());
        val ans = sorted.get(sorted.size()/2);

        System.out.println("Part 2 ANS: " + ans);
    }

    private static Error findErrors(String line) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (openChars.contains(c)) {
                stack.add(c);
            } else if (closeChars.contains(c)) {
                if (stack.peek() == pairs.get(c)) {
                    stack.pop();
                } else {
                    return new Error(c,i);
                }
            }
        }
        List<Character> missingCharacters = stack.stream().map(pairs::get).collect(toList());
        Collections.reverse(missingCharacters);
        return new Error(missingCharacters);
    }

    private static long calculateClosingScore(List<Character> missingCharacters) {
        long score = 0;
        for (int i = 0; i <missingCharacters.size(); i++) {
            score *= 5;
            score += scores.get(missingCharacters.get(i));
        }
        return score;
    }

    private static class Error {
        Character c;
        Integer position;
        List<Character> missingCharacters;

        public Error(char c, int position) {
            this.c = c;
            this.position = position;
        }

        public Error(List<Character> missingCharacters) {
            this.missingCharacters = missingCharacters;
        }

        @Override
        public String toString() {
            return "found '"+c+"' at position " + position;
        }

        public boolean isCorrupted() {
            return nonNull(c);
        }
        public boolean isIncomplete() {
            return nonNull(missingCharacters);
        }
    }

}
