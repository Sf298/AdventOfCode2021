package day8;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Day8 {

    private static final int DAY = Integer.parseInt(Day8.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        long ans = Utils.streamLinesForDay(DAY)
                .map(l -> l.split(" \\| "))
                .map(a -> a[1].split(" "))
                .flatMap(Arrays::stream)
                .map(String::length)
                .filter(i -> i==2 || i==4 || i==3 || i==7)
                .count();

        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        val rows = Utils.streamLinesForDay(DAY)
                .map(Day8::parseRow)
                .collect(Collectors.toList());

        /*
        Segments
             zzzz
            y    x
            y    x
             wwww
            v    u
            v    u
             tttt
         */

        long outputSum = 0;
        for (val row : rows) {
            val uniques = row.getKey();
            val outputs = row.getValue();

            NumberSegments[] nums = new NumberSegments[10];
            Map<String, String> segmentsFound = new HashMap<>();

            // 1,4,7,8
            nums[1] = uniques.stream().filter(n -> n.size() == 2).findFirst().orElseThrow();
            nums[4] = uniques.stream().filter(n -> n.size() == 4).findFirst().orElseThrow();
            nums[7] = uniques.stream().filter(n -> n.size() == 3).findFirst().orElseThrow();
            nums[8] = uniques.stream().filter(n -> n.size() == 7).findFirst().orElseThrow();

            // z
            segmentsFound.put("z", findOne(subtract(nums[7], nums[1])));

            // 3
            val fiveSegmentNums = uniques.stream().filter(n -> n.size() == 5).collect(Collectors.toList());
            if (subtract(fiveSegmentNums.get(0), fiveSegmentNums.get(1)).size() == 1 &&
                    subtract(fiveSegmentNums.get(0), fiveSegmentNums.get(2)).size() == 1) {
                nums[3] = fiveSegmentNums.get(0);
            } else if (subtract(fiveSegmentNums.get(1), fiveSegmentNums.get(0)).size() == 1 &&
                    subtract(fiveSegmentNums.get(1), fiveSegmentNums.get(2)).size() == 1) {
                nums[3] = fiveSegmentNums.get(1);
            } else if (subtract(fiveSegmentNums.get(2), fiveSegmentNums.get(0)).size() == 1 &&
                    subtract(fiveSegmentNums.get(2), fiveSegmentNums.get(1)).size() == 1) {
                nums[3] = fiveSegmentNums.get(2);
            }

            // y
            segmentsFound.put("y", findOne(subtract(nums[4], nums[3])));

            // 9
            nums[9] = union(nums[3], nums[4]);

            //2, 5
            fiveSegmentNums.remove(nums[3]);
            if (subtract(fiveSegmentNums.get(0), nums[9]).isEmpty()) {
                nums[2] = fiveSegmentNums.get(1);
                nums[5] = fiveSegmentNums.get(0);
            } else {
                nums[2] = fiveSegmentNums.get(0);
                nums[5] = fiveSegmentNums.get(1);
            }

            // t,u,v,w,x
            segmentsFound.put("x", findOne(subtract(nums[3], nums[5])));
            segmentsFound.put("v", findOne(subtract(nums[8], nums[9])));
            segmentsFound.put("u", findOne(subtract(nums[3], nums[2])));
            segmentsFound.put("t", findOne(subtract(nums[9], union(nums[4],nums[7]) )));
            segmentsFound.put("w", findOne(subtractSegments(nums[8], segmentsFound.values())));

            // 0
            nums[0] = subtractSegments(nums[8], List.of(segmentsFound.get("w")));
            nums[6] = new NumberSegments(nums[5]); nums[6].add(segmentsFound.get("v"));


            // calculate output
            val valuesMap = new HashMap<NumberSegments, Integer>(nums.length);
            for (int i = 0; i < nums.length; i++) {
                valuesMap.put(nums[i], i);
            }

            int outputInt = 0;
            for (val outputNum : outputs) {
                outputInt *= 10;
                if (!valuesMap.containsKey(outputNum)) {
                    throw new RuntimeException();
                }
                outputInt += valuesMap.get(outputNum);
            }

            outputSum += outputInt;
        }

        System.out.println("Part 2 ANS: " + outputSum);
    }

    private static Pair<List<NumberSegments>, List<NumberSegments>> parseRow(String row) {
        val split1 = row.split(" \\| ");
        val uniques = split1[0].split(" ");
        val outputs = split1[1].split(" ");
        return Pair.of(
                Arrays.stream(uniques).map(NumberSegments::new).collect(Collectors.toList()),
                Arrays.stream(outputs).map(NumberSegments::new).collect(Collectors.toList())
        );
    }

    private static NumberSegments union(NumberSegments a, NumberSegments b) {
        val out = new NumberSegments(a);
        out.addAll(b);
        return out;
    }

    private static NumberSegments subtract(NumberSegments a, NumberSegments b) {
        val out = new NumberSegments(a);
        out.removeAll(b);
        return out;
    }

    private static NumberSegments subtractSegments(NumberSegments a, Collection<String> b) {
        val out = new NumberSegments(a);
        out.removeAll(b);
        return out;
    }

    private static <T> T findOne(Set<T> s) {
        if (s.size() > 1) {
            throw new RuntimeException("more than one found: " + s);
        }
        return s.stream().findFirst().orElseThrow();
    }

    private static class NumberSegments extends HashSet<String> {
        public NumberSegments(NumberSegments numberSegments) {
            super(numberSegments);
        }
        public NumberSegments(String s) {
            super(asList(s.split("")));
        }
    }

}
