package day18;

import lombok.val;
import utils.Utils;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class Day18 {

    private static final int DAY = parseInt(Day18.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        //part2();
    }

    private static void part1() {
        String testNumber;
        String testSum;

        testNumber = explode("[[[[[9,8],1],2],3],4]");
        test(testNumber, "[[[[0,9],2],3],4]", 1);
        testNumber = explode("[7,[6,[5,[4,[3,2]]]]]");
        test(testNumber, "[7,[6,[5,[7,0]]]]", 2);
        testNumber = explode("[[6,[5,[4,[3,2]]]],1]");
        test(testNumber, "[[6,[5,[7,0]]],3]", 3);
        testNumber = explode("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]");
        test(testNumber, "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", 4);

        testNumber = reduceAll(add("[[[[4,3],4],4],[7,[[8,4],9]]]", "[1,1]"), false);
        test(testNumber, "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", 5);

        testSum = chainAdd(false, "[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]", "[6,6]");
        test(testSum, "[[[[5,0],[7,4]],[5,5]],[6,6]]", 6);

/*
[[[[4,0],[5,4]],[[7,7],[6,0]]],[[7,[5,5]],[[0,[11,3]],[[6,3],[8,8]]]]]
0123   2 3   21 23   2 3   210 12  3   21 23  4

[[[[4,0],[5,0]],[[[4,5],[2,6]],[9,5]]],[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]]
0123   2 3   21 234
 */

        // 1st of the long section
        testNumber = add("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]", "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]");
        testNumber = reduceAll(testNumber, true);
        test(testNumber, "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", 7);

        // 2nd of the long section
        testNumber = add("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]");
        testNumber = reduceAll(testNumber, false);
        test(testNumber, "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]", 8);

        testSum = chainAdd(false,
                "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
                "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
                "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
                "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
                "[7,[5,[[3,8],[1,4]]]]",
                "[[2,[2,2]],[8,[8,1]]]",
                "[2,9]",
                "[1,[[[9,3],9],[[9,0],[0,7]]]]",
                "[[[5,[7,4]],7],1]",
                "[[[[4,2],2],6],[8,7]]"
        );
        test(testSum, "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", 9);

        String[] numbers = Utils.streamLinesForDay(DAY).toArray(String[]::new);
        String sum = chainAdd(false, numbers);
        val ans = magnitude(sum);
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
    }

    private static String chainAdd(boolean debug, String... numbers) {
        String sum = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            sum = reduceAll(add(sum, numbers[i]), debug);
        }
        return sum;
    }
    private static String add(String number1, String number2) {
        return "[" + number1 + "," + number2 + "]";
    }
    private static String reduceAll(String number, boolean debug) {
        while (true) {
            if (debug) System.out.print(number);
            int firstExplodeIndex = firstExplodeIndex(number);
            int firstSplitIndex = firstSplitIndex(number);

            if (firstExplodeIndex != -1) {
                number = explode(number);
                if (debug) System.out.println(" Explodes into");
            } else if (firstSplitIndex != -1) {
                number = split(number);
                if (debug) System.out.println(" Splits into");
            } else {
                break;
            }
        }
        if (debug) System.out.println();
        return number;
        /*while (true) {
            if (debug) System.out.print(number);
            int firstExplodeIndex = firstExplodeIndex(number);
            int firstSplitIndex = firstSplitIndex(number);
            if (firstExplodeIndex == -1 && firstSplitIndex == -1)
                break;

            if (firstSplitIndex == -1 || (firstExplodeIndex != -1 && firstExplodeIndex <= firstSplitIndex)) {
                number = explode(number);
                if (debug) System.out.println(" Explodes into");
            } else if (firstExplodeIndex == -1 || firstSplitIndex < firstExplodeIndex) {
                number = split(number);
                if (debug) System.out.println(" Splits into");
            }
        }
        if (debug) System.out.println();
        return number;*/
    }
    private static String split(String number) {
        int firstTooBigIdx = indexOfFirstTooBig(number, 0);
        String firstTooBigStr = literal(number, firstTooBigIdx);
        int firstTooBig = parseInt(firstTooBigStr);

        int t = firstTooBig / 2;
        String newValue = "[" + t + "," + (firstTooBig-t) + "]";

        number = replaceFirst(number, firstTooBigStr, newValue, firstTooBigIdx);
        return number;
    }
    private static String explode(String number) {
        int firstTooDeepIdx = indexOfFirstDepth(number, 4, 0);

        // replace with 0
        String firstTooDeep = subNumber(number, firstTooDeepIdx);
        number = replaceFirst(number, firstTooDeep, "0", firstTooDeepIdx);
        int[] subNumbers = {
                parseInt(firstTooDeep.split(",")[0].replaceAll("[^0-9]", "")),
                parseInt(firstTooDeep.split(",")[1].replaceAll("[^0-9]", ""))
        };

        // update previous number
        int previousNumIdx = indexOfPreviousNumber(number, firstTooDeepIdx-1);
        if (previousNumIdx != -1) {
            String previousNumStr = literal(number, previousNumIdx);
            int previousNum = parseInt(previousNumStr);
            number = replaceFirst(number, previousNumStr, String.valueOf(previousNum + subNumbers[0]), previousNumIdx);
        }

        // update previous number
        int nextNumIdx = indexOfNextNumber(number, firstTooDeepIdx+1);
        if (nextNumIdx != -1) {
            String nextNumStr = literal(number, nextNumIdx);
            int nextNum = parseInt(nextNumStr);
            number = replaceFirst(number, nextNumStr, String.valueOf(nextNum + subNumbers[1]), nextNumIdx);
        }

        return number;
    }
    private static int firstSplitIndex(String number) {
        return indexOfFirstTooBigParent(number, 0);
    }
    private static int firstExplodeIndex(String number) {
        return indexOfFirstDepth(number, 4, 0);
    }
    private static int indexOfFirstTooBigParent(String number, int startIndex) {
        int idx = indexOfFirstTooBig(number, startIndex);
        return indexOflastDepth(number, 0, idx);
    }
    private static int indexOfFirstTooBig(String number, int startIndex) {
        Pattern pattern = Pattern.compile("[0-9]{2,}");
        Matcher matcher = pattern.matcher(number);
        if (matcher.find(startIndex)) {
            return matcher.start();
        }
        return -1;
    }
    private static int indexOfFirstDepth(String number, int depth, int startIndex) {
        int bracketsCount = -1;
        for (int i = startIndex; i < number.length(); i++) {
            if (number.charAt(i) == '[') bracketsCount++;
            if (number.charAt(i) == ']') bracketsCount--;
            if (bracketsCount == depth) return i;
        }
        return -1;
    }
    private static int indexOflastDepth(String number, int depth, int startIndex) {
        int bracketsCount = -1;
        for (int i = startIndex; i >= 0; i--) {
            if (number.charAt(i) == '[') bracketsCount++;
            if (number.charAt(i) == ']') bracketsCount--;
            if (bracketsCount == -depth) return i;
        }
        return -1;
    }
    private static int indexOfNextNumber(String number, int startIndex) {
        int i = startIndex;
        while ('0' <= number.charAt(i) && number.charAt(i) <='9') {
            i++;
        }
        for (; i < number.length(); i++) {
            if ('0' <= number.charAt(i) && number.charAt(i) <='9') return i;
        }
        return -1;
    }
    private static int indexOfPreviousNumber(String number, int startIndex) {
        int i = startIndex;
        while ('0' <= number.charAt(i) && number.charAt(i) <='9') {
            i--;
        }
        i--;
        for (; i >= 0; i--) {
            if ('0' <= number.charAt(i) && number.charAt(i) <='9') {
                while ('0' <= number.charAt(i) && number.charAt(i) <='9') {
                    i--;
                }
                return i + 1;
            }
        }
        return -1;
    }
    private static int getDepth(String number, int startIndex) {
        int bracketsCount = -1;
        for (int i = startIndex; i >= 0; i--) {
            if (number.charAt(i) == '[') bracketsCount++;
            if (number.charAt(i) == ']') bracketsCount--;
        }
        return bracketsCount;
    }
    private static String subNumber(String number, int startIndex) {
        if (startIndex == -1) return null;
        int end = indexOfFirstDepth(number, -1, startIndex) + 1;
        return number.substring(startIndex, end);
    }
    private static String literal(String number, int startIndex) {
        if (startIndex == -1) return null;

        int end = startIndex;
        while ('0' <= number.charAt(end) && number.charAt(end) <='9') {
            end++;
        }
        return number.substring(startIndex, end);
    }
    private static String replaceFirst(String number, String substring, String replacement, int startIndex) {
        int index = number.indexOf(substring, startIndex);
        String left = number.substring(0, index);
        String right = number.substring(index);

        String pattern = substring.replace("[", "\\[");
        right = right.replaceFirst(pattern, replacement);
        return left + right;
    }
    private static long magnitude(String number) {
        if (number.matches("[0-9]+")) {
            return Integer.parseInt(number);
        } else if(number.startsWith("[")) {
            number = number.substring(1,number.length()-1);
            int openBracketsCount = 0;
            int splitPoint = 0;
            for (; splitPoint < number.length(); splitPoint++) {
                if (number.charAt(splitPoint) == '[') openBracketsCount++;
                if (number.charAt(splitPoint) == ']') openBracketsCount--;
                if (openBracketsCount == 0 && number.charAt(splitPoint) == ',') break;
            }

            long leftMag = magnitude(number.substring(0, splitPoint));
            long rightMag = magnitude(number.substring(splitPoint+1));

            return  3*leftMag + 2*rightMag;
        }
        throw new RuntimeException("failed to parse '"+number+"'");
    }

    private static boolean test(String actual, String expected, int testNumber) {
        String str = actual.toString().replaceAll("<", "[").replaceAll(">", "]");
        if (!str.equals(expected)) {
            System.out.println("Test " + testNumber + " FAILED: ");
            System.out.println("e: " + expected);
            System.out.println("a: " + actual);
            System.out.println();
            return false;
        } else {
            System.out.println("Test " + testNumber + " passed.");
            return true;
        }
    }

}
