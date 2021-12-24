package day24;

import lombok.val;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Day24 {

    private static final int DAY = Integer.parseInt(Day24.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        val lines = Utils.streamLinesForDay(DAY).collect(Collectors.toList());
        val expression = toExpression(lines);
        System.out.println("finished expression");
        val compiled = simplify(expression);
        System.out.println("finished simplify");
        for (long i = 99999999999999L; i > 0; i--) {
            String exp = compiled;
            String[] split = String.valueOf(i).split("");
            Collections.reverse(Arrays.asList(split));
            for (int j = 0; j < split.length; j++) {
                exp = exp.replace("{"+j+"}", split[j]);
            }
            String result = simplify(simplify(exp));
            System.out.println(result);
        }

        int ans = 0;
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
    }

    private static String toExpression(List<String> lines) {
        int inputIndex = 0;
        String expression = "z";
        Collections.reverse(lines);
        for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
            String line = lines.get(i);
            if (line.startsWith("a") && line.endsWith(" 0")) continue;
            if (line.startsWith("m") && line.endsWith(" 1")) continue;
            if (line.startsWith("d") && line.endsWith(" 1")) continue;


            String[] split = line.split(" ");
            if (line.startsWith("m") && line.endsWith(" 0")) {
                expression = expression.replace(split[1], "0");
            }

            switch (split[0]) {
                case "inp" -> expression = expression.replace(split[1], "{" + inputIndex++ + "}");
                case "add" -> expression = expression.replace(split[1], "(" + split[1] + "+" + split[2] + ")");
                case "mul" -> expression = expression.replace(split[1], "(" + split[1] + "*" + split[2] + ")");
                case "div" -> expression = expression.replace(split[1], "(" + split[1] + "/" + split[2] + ")");
                case "mod" -> expression = expression.replace(split[1], "(" + split[1] + "%" + split[2] + ")");
                case "eql" -> expression = expression.replace(split[1], "(" + split[1] + "=" + split[2] + ")");
            }
        }
        return expression.replaceAll("[wxyz]", "0");
    }
    private static String simplify(String expression) {
        if(!expression.startsWith("(")) return expression;

        expression = expression.substring(1, expression.length()-1);
        int depth = 0;
        int i = 0;
        for (; i < expression.length(); i++) {
            if (expression.charAt(i) == '(')
                depth++;
            else if (expression.charAt(i) == ')')
                depth--;
            else if (depth == 0 && (expression.charAt(i)=='+' || expression.charAt(i)=='*' || expression.charAt(i)=='/' || expression.charAt(i)=='%' || expression.charAt(i)=='='))
                break;
        }
        String operation = String.valueOf(expression.charAt(i));

        String left = expression.substring(0, i);
        String right = expression.substring(i+1);


        switch (operation) {
            case "+" -> {
                if(left.equals("0"))
                    return simplify(right);
                else if (right.equals("0"))
                    return left;
            }
            case "*" -> {
                if(left.equals("0") || right.equals("0"))
                    return "0";
                if(left.equals("1"))
                    return simplify(right);
                else if (right.equals("1"))
                    return simplify(left);
            }
            case "/" -> {
                if (right.equals("1"))
                    return simplify(left);
            }
            case "%" -> {
                if(left.equals("0"))
                    return "0";
            }
            case "=" -> {
                if(left.equals(right))
                    return "1";
            }
        }

        String leftSimplified = simplify(left);
        String rightSimplified = simplify(right);

        switch (operation) {
            case "+" -> {
                if(leftSimplified.equals("0"))
                    return rightSimplified;
                else if (rightSimplified.equals("0"))
                    return leftSimplified;
            }
            case "*" -> {
                if(leftSimplified.equals("0") || rightSimplified.equals("0"))
                    return "0";
                if(leftSimplified.equals("1"))
                    return rightSimplified;
                else if (rightSimplified.equals("1"))
                    return leftSimplified;
            }
            case "/" -> {
                if (rightSimplified.equals("1"))
                    return leftSimplified;
            }
            case "%" -> {
                if(leftSimplified.equals("0"))
                    return "0";
            }
            case "=" -> {
                if(leftSimplified.equals(rightSimplified))
                    return "1";
            }
        }

        if (!leftSimplified.startsWith("(") && !rightSimplified.startsWith("(")) {
            if (leftSimplified.matches("[0-9-]+") && rightSimplified.matches("[0-9-]+")) {
                switch (operation) {
                    case "+" -> { return String.valueOf(Integer.parseInt(leftSimplified) + Integer.parseInt(rightSimplified)); }
                    case "*" -> { return String.valueOf(Integer.parseInt(leftSimplified) * Integer.parseInt(rightSimplified)); }
                    case "/" -> { return String.valueOf(Integer.parseInt(leftSimplified) / Integer.parseInt(rightSimplified)); }
                    case "%" -> { return String.valueOf(Integer.parseInt(leftSimplified) % Integer.parseInt(rightSimplified)); }
                }
            }
        }

        return "(" + leftSimplified + operation + rightSimplified + ")";
    }

/*
    private static Function<String, Integer> compile(List<String> lines) {
        val compiledOps = new ArrayList<BiFunction<int[], Iterator<Integer>, Integer>>();
        for (val line : lines) {
            compiledOps.add(getOperation(line));
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

        final int variableIndex;
        switch (split[1]) {
            case "w" -> variableIndex = 0;
            case "x" -> variableIndex = 1;
            case "y" -> variableIndex = 2;
            case "z" -> variableIndex = 3;
            default -> variableIndex = -1;
        }

        Function<int[], Integer> literalSupplier = (split.length > 2) ? getLiteral(split[2]) : null;
        switch (split[0]) {
            case "inp" -> { return (mem, modelDigitI) -> mem[variableIndex] = modelDigitI.next(); }
            case "add" -> { return (mem, md) -> mem[variableIndex] += literalSupplier.apply(mem); }
            case "mul" -> { return (mem, md) -> mem[variableIndex] *= literalSupplier.apply(mem); }
            case "div" -> { return (mem, md) -> mem[variableIndex] /= literalSupplier.apply(mem); }
            case "mod" -> { return (mem, md) -> mem[variableIndex] %= literalSupplier.apply(mem); }
            case "eql" -> { return (mem, md) -> mem[variableIndex] = (mem[variableIndex]==literalSupplier.apply(mem) ? 1 : 0); }
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
*/
}
