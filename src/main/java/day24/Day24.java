package day24;

import lombok.val;
import utils.Utils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day24 {

    private static final int DAY = Integer.parseInt(Day24.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        val lines = Utils.streamLinesForDay(DAY).collect(Collectors.toList());
        Function<String, Integer> compiled = compile(lines);
        for (long i = 99999999999999L; i > 0L; i--) {
            val result = compiled.apply(String.valueOf(i));
            if (result == 0) {
                System.out.println(i + ", " + result);
                break;
            }
        }

        int ans = 0;
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
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
                int value = Integer.parseInt(split[2]);
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
            final int parsed = Integer.parseInt(str);
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
