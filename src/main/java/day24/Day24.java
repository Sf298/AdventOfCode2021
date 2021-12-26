package day24;

import lombok.val;
import utils.Utils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Day24 {

    private static final int DAY = parseInt(Day24.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        long start = System.currentTimeMillis();

        int[] a = {1, 1, 1, 1, 1, 26, 1, 26, 26, 1, 26, 26, 26, 26};
        int[] b = {13, 11, 12, 10, 14, -1, 14, -16, -8, 12, -16, -13, -6, -6};
        int[] c = {6, 11, 5, 6, 8, 14, 9, 4, 7, 13, 11, 11, 6, 1};
        for (long i = 99999999999999L; i > 0L; i--) {
            String s = String.valueOf(i);
            if(s.contains("0")) continue;

            val result = s.split("");
            long z = 0;
            for (int j = 0; j < 14; j++) {
                z = manualStep(parseInt(result[j]), z, a[j], b[j], c[j], j);
            }
            if (z == 0) {
                System.out.println(i + ", " + z);
                System.out.println(System.currentTimeMillis() - start);
                break;
            }
        }
        for (long i = 11111111111111L; i <= 99999999999999L; i++) {
            String s = String.valueOf(i);
            if(s.contains("0")) continue;

            val result = s.split("");
            long z = 0;
            for (int j = 0; j < 14; j++) {
                z = manualStep(parseInt(result[j]), z, a[j], b[j], c[j], j);
            }
            if (z == 0) {
                System.out.println(i + ", " + z);
                System.out.println(System.currentTimeMillis() - start);
            }
        }

        int ans = 0;
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
    }


    private static long manualStep(int w, long z, int a, int b, int c,    int j) {
        int x;
        //System.out.println("\t\t\t"+j+" " + (26 - (z % 26)));
        //x = ((z % 26 + b) != w) ? 1 : 0;
        //z = ((z / a) * (25 * x + 1)) + ((w + c)*x);
        //return z;
        return ((z % 26 + b) != w) ? (((z / a) * 26) + (w + c)) : (z / a);
    }

    private static Function<String, Integer> compile(List<String> lines) {
        val compiledOps = new ArrayList<BiFunction<int[], Iterator<Integer>, Integer>>();
        for (val line : lines) {
            val op = getOperation(line);
            if (Objects.nonNull(op)) {
                compiledOps.add(op);
            }
        }

        return (modelNum) -> {
            Iterator<Integer> modelNumberIterator = Arrays.stream(modelNum.split("")).map(Integer::parseInt).iterator();
            int[] mem = new int[4];

            for (int i = 0; i < compiledOps.size(); i++) {
                compiledOps.get(i).apply(mem, modelNumberIterator);
            }

            return mem[3];
        };
    }

    private static BiFunction<int[], Iterator<Integer>, Integer> getOperation(String str) {
        String[] split = str.split(" ");

        if(split[0].startsWith("d") && split[2].equals("1")) {
            return null;
        }

        final int leftVariableIndex;
        switch (split[1]) {
            case "w" -> leftVariableIndex = 0;
            case "x" -> leftVariableIndex = 1;
            case "y" -> leftVariableIndex = 2;
            case "z" -> leftVariableIndex = 3;
            default -> leftVariableIndex = -1;
        }

        if (split[0].equals("inp"))
            return (mem, modelDigitI) -> mem[leftVariableIndex] = modelDigitI.next();

        Function<int[], Integer> rightVariable;
        switch (split[2]) {
            case "w" -> rightVariable = mem -> mem[0];
            case "x" -> rightVariable = mem -> mem[1];
            case "y" -> rightVariable = mem -> mem[2];
            case "z" -> rightVariable = mem -> mem[3];
            default -> {
                int value = parseInt(split[2]);
                rightVariable = mem -> value;
            }
        }

        switch (split[0]) {
            case "add" -> { return (mem, md) -> mem[leftVariableIndex] += rightVariable.apply(mem); }
            case "mul" -> { return (mem, md) -> mem[leftVariableIndex] *= rightVariable.apply(mem); }
            case "div" -> { return (mem, md) -> mem[leftVariableIndex] /= rightVariable.apply(mem); }
            case "mod" -> { return (mem, md) -> mem[leftVariableIndex] %= rightVariable.apply(mem); }
            case "eql" -> { return (mem, md) -> mem[leftVariableIndex] = (mem[leftVariableIndex]==rightVariable.apply(mem) ? 1 : 0); }
        }
        throw new RuntimeException(String.format("Unsuported literal '%s'", str));
    }

    private static Function<int[], Integer> getLiteral(String str) {
        try {
            final int parsed = parseInt(str);
            return mem -> parsed;
        } catch (Exception e) {
            switch (str) {
                case "w" -> { return mem -> mem[0]; }
                case "x" -> { return mem -> mem[1]; }
                case "y" -> { return mem -> mem[2]; }
                case "z" -> { return mem -> mem[3]; }
            }
        }
        throw new RuntimeException(String.format("Unsuported literal '%s'", str));
    }

}
