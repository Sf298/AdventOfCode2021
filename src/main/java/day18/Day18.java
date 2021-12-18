package day18;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import utils.PermutationIterable;
import utils.Utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Day18 {

    private static final int DAY = Integer.parseInt(Day18.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        SFNumber number;
        number = SFNumber.parse("[[[[[9,8],1],2],3],4]").tryExplode();
        test(number, "[[[[0,9],2],3],4]", 1);
        number = SFNumber.parse("[7,[6,[5,[4,[3,2]]]]]").tryExplode();
        test(number, "[7,[6,[5,[7,0]]]]", 2);
        number = SFNumber.parse("[[6,[5,[4,[3,2]]]],1]").tryExplode();
        test(number, "[[6,[5,[7,0]]],3]", 3);
        number = SFNumber.parse("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").tryExplode();
        test(number, "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", 4);

        number = SFNumber.add(SFNumber.parse("[[[[4,3],4],4],[7,[[8,4],9]]]"), SFNumber.parse("[1,1]"));
        number.reduceAll(false);
        test(number, "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", 5);

        SFNumberPair testSum = SFNumber.chainAdd(false,
                SFNumber.parse("[1,1]"),
                SFNumber.parse("[2,2]"),
                SFNumber.parse("[3,3]"),
                SFNumber.parse("[4,4]"),
                SFNumber.parse("[5,5]"),
                SFNumber.parse("[6,6]")
        );
        test(testSum, "[[[[5,0],[7,4]],[5,5]],[6,6]]", 6);


        number = SFNumber.add(SFNumber.parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"), SFNumber.parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"));
        number.reduceAll(false);
        test(number, "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", 7);


        long start = System.currentTimeMillis();
        testSum = SFNumber.chainAdd(false,
                SFNumber.parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"),
                SFNumber.parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"),
                SFNumber.parse("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"),
                SFNumber.parse("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"),
                SFNumber.parse("[7,[5,[[3,8],[1,4]]]]"),
                SFNumber.parse("[[2,[2,2]],[8,[8,1]]]"),
                SFNumber.parse("[2,9]"),
                SFNumber.parse("[1,[[[9,3],9],[[9,0],[0,7]]]]"),
                SFNumber.parse("[[[5,[7,4]],7],1]"),
                SFNumber.parse("[[[[4,2],2],6],[8,7]]")
        );
        System.out.println("Chain Add: " + (System.currentTimeMillis() - start));
        test(testSum, "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", 8);

        start = System.currentTimeMillis();
        SFNumber.add(SFNumber.parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"), SFNumber.parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"));
        System.out.println("Add: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        SFNumber.copy(testSum);
        System.out.println("Copy: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        testSum.magnitude();
        System.out.println("mag: " + (System.currentTimeMillis() - start));

        SFNumber[] numbers = Utils.streamLinesForDay(DAY)
                .map(SFNumber::parse)
                .toArray(SFNumber[]::new);
        SFNumberPair sum = SFNumber.chainAdd(false, numbers);
        val ans = sum.magnitude();
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        SFNumber[] numbers = Utils.streamLinesForDay(DAY)
                .map(SFNumber::parse)
                .toArray(SFNumber[]::new);
        long start = System.currentTimeMillis();
        long maxMag = 0;
        SFNumber maxNum = null;
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers.length; j++) {
                if(i==j) continue;
                SFNumber n1 = SFNumber.copy(numbers[i]);
                SFNumber n2 = SFNumber.copy(numbers[j]);
                SFNumberPair sum = SFNumber.add(n1, n2);
                sum.reduceAll(false);
                long mag = sum.magnitude();
                if (mag > maxMag) {
                    maxMag = mag;
                    maxNum = sum;
                }
            }
        }
        System.out.println(maxNum);
        System.out.println(System.currentTimeMillis() - start);

        System.out.println("Part 2 ANS: " + maxMag);
    }

    private static void test(SFNumber actual, String expected, int testNumber) {
        String actualStr = actual.toString().replace('<', '[').replace('>', ']');
        if (!actualStr.equals(expected)) {
            System.out.println("Test " + testNumber + " failed: ");
            System.out.println("e: " + expected);
            System.out.println("a: " + actual);
            System.out.println();
        } else {
            System.out.println("Test " + testNumber + " passed.");
        }
    }

    private interface SFNumber {
        SFNumber tryExplode();
        SFNumber trySplit();
        long magnitude();
        SFNumberPair getParent();
        void setParent(SFNumberPair p);

        static SFNumber parse(String str) {
            if (str.matches("[0-9]+")) {
                return new SFNumberLiteral(Integer.parseInt(str));
            } else if(str.startsWith("[")) {
                str = str.substring(1,str.length()-1);
                int openBracketsCount = 0;
                int splitPoint = 0;
                for (; splitPoint < str.length(); splitPoint++) {
                    if (str.charAt(splitPoint) == '[') openBracketsCount++;
                    if (str.charAt(splitPoint) == ']') openBracketsCount--;
                    if (openBracketsCount == 0 && str.charAt(splitPoint) == ',') break;
                }

                return new SFNumberPair(parse(str.substring(0, splitPoint)), parse(str.substring(splitPoint+1)));
            }
            throw new RuntimeException("failed to parse '"+str+"'");
        }

        static List<SFNumber> copy(List<SFNumber> numbers) {
            return numbers.stream().map(SFNumber::copy).collect(Collectors.toList());
        }
        static SFNumber copy(SFNumber number) {
            if (number instanceof SFNumberLiteral) {
                return new SFNumberLiteral(((SFNumberLiteral) number).literal);
            } else {
                SFNumberPair p = (SFNumberPair) number;
                return new SFNumberPair(copy(p.left), copy(p.right));
            }
        }

        static SFNumberPair chainAdd(boolean debug, SFNumber... numbers) {
            List<SFNumber> numbersList = copy(asList(numbers));
            SFNumber sum = numbersList.get(0);
            for (int i = 1; i < numbersList.size(); i++) {
                sum = SFNumber.add(sum, numbersList.get(i));
                sum.reduceAll(debug);
            }
            return (SFNumberPair) sum;
        }
        static SFNumberPair add(SFNumber left, SFNumber right) {
            return new SFNumberPair(left, right);
        }

        default int depth() {
            int depth = 0;
            SFNumber n = this;
            while (nonNull(n.getParent())) {
                n = n.getParent();
                depth++;
            }
            return depth;
        }

        default void reduceAll(boolean debug) {
            while (true) {
                if (debug) System.out.print(this);
                val explodeResult = tryExplode();
                if (nonNull(explodeResult)) {
                    if (debug) System.out.println(" Explodes into");
                    continue;
                }
                val splitResult = trySplit();
                if (debug) System.out.println(" Splits into");
                if (isNull(splitResult)) {
                    break;
                }
            }
        }
    }

    private static class SFNumberLiteral implements SFNumber {
        public SFNumberPair parent;
        public int literal;

        public SFNumberLiteral(int literal) {
            this.literal = literal;
        }

        @Override
        public SFNumber tryExplode() {
            return null;
        }

        @Override
        public SFNumber trySplit() {
            if (literal <=9)
                return null;

            int left = literal / 2;
            if (parent == null) System.out.println(this);
            return new SFNumberPair(left, literal - left, parent);
        }

        @Override
        public long magnitude() {
            return literal;
        }

        @Override
        public SFNumberPair getParent() {
            return parent;
        }

        @Override
        public void setParent(SFNumberPair p) {
            parent = p;
        }

        @Override
        public String toString() {
            return literal+"";
        }
    }

    private static class SFNumberPair implements SFNumber {
        public SFNumberPair parent;
        public SFNumber left;
        public SFNumber right;

        public SFNumberPair(SFNumber left, SFNumber right) {
            this(left, right, null);
        }
        public SFNumberPair(SFNumber left, int right) {
            this(left, right, null);
        }
        public SFNumberPair(int left, SFNumber right) {
            this(left, right, null);
        }
        public SFNumberPair(int left, int right) {
            this(left, right, null);
        }
        public SFNumberPair(SFNumber left, SFNumber right, SFNumberPair parent) {
            this.left = left;
            this.right = right;
            this.parent = parent;

            left.setParent(this);
            right.setParent(this);
        }
        public SFNumberPair(SFNumber left, int right, SFNumberPair parent) {
            this(left, new SFNumberLiteral(right), parent);
        }
        public SFNumberPair(int left, SFNumber right, SFNumberPair parent) {
            this(new SFNumberLiteral(left), right, parent);
        }
        public SFNumberPair(int left, int right, SFNumberPair parent) {
            this(new SFNumberLiteral(left), new SFNumberLiteral(right), parent);
        }

        @Override
        public SFNumber tryExplode() {
            return internalExplode(0);
        }
        private SFNumber internalExplode(int depth) {
            if (depth >= 4) {
                // left
                SFNumberPair p = firstParentWithLeft(this);
                if (nonNull(p)) {
                    SFNumberLiteral c = rightmostChild(p.left);
                    if (nonNull(c)) {
                        c.literal += ((SFNumberLiteral) this.left).literal;
                    }
                }
                // right
                p = firstParentWithRight(this);
                if (nonNull(p)) {
                    SFNumberLiteral c = leftmostChild(p.right);
                    if (nonNull(c)) {
                        c.literal += ((SFNumberLiteral) this.right).literal;
                    }
                }
                return new SFNumberLiteral(0);
            }

            if (left instanceof SFNumberPair) {
                SFNumber newLeft = ((SFNumberPair) left).internalExplode(depth+1);
                if (nonNull(newLeft)) {
                    left = newLeft;
                    left.setParent(this);
                    return this;
                }
            } else {
                SFNumber newLeft = left.tryExplode();
                if (nonNull(newLeft)) {
                    left = newLeft;
                    left.setParent(this);
                    return this;
                }
            }
            if (right instanceof SFNumberPair) {
                SFNumber newRight = ((SFNumberPair) right).internalExplode(depth+1);
                if (nonNull(newRight)) {
                    right = newRight;
                    right.setParent(this);
                    return this;
                }
            } else {
                SFNumber newRight = right.tryExplode();
                if (nonNull(newRight)) {
                    right = newRight;
                    right.setParent(this);
                    return this;
                }
            }
            return null;
        }

        @Override
        public SFNumber trySplit() {
            SFNumber leftResult = left.trySplit();
            if(nonNull(leftResult)) {
                left = leftResult;
                right.setParent(this);
                return this;
            }

            SFNumber rightResult = right.trySplit();
            if(nonNull(rightResult)) {
                right = rightResult;
                right.setParent(this);
                return this;
            }
            return null;
        }

        @Override
        public long magnitude() {
            return left.magnitude() * 3 + right.magnitude() * 2;
        }

        @Override
        public SFNumberPair getParent() {
            return parent;
        }

        @Override
        public void setParent(SFNumberPair p) {
            parent = p;
        }

        private static SFNumberPair firstParentWithLeft(SFNumber child) {
            SFNumberPair p = child.getParent();
            while (nonNull(p) && p.left.equals(child)) {
                child = p;
                p = p.parent;
            }
            return p;
        }

        private static SFNumberPair firstParentWithRight(SFNumber child) {
            SFNumberPair p = child.getParent();
            while (nonNull(p) && p.right.equals(child)) {
                child = p;
                p = p.parent;
            }
            return p;
        }

        private static SFNumberLiteral leftmostChild(SFNumber n) {
            while (nonNull(n)) {
                if(n instanceof SFNumberLiteral)
                    return (SFNumberLiteral) n;
                n = ((SFNumberPair) n).left;
            }
            return null;
        }

        private static SFNumberLiteral rightmostChild(SFNumber n) {
            while (nonNull(n)) {
                if(n instanceof SFNumberLiteral)
                    return (SFNumberLiteral) n;
                n = ((SFNumberPair) n).right;
            }
            return null;
        }

        @Override
        public String toString() {
            if (depth() >= 4) {
                return "<" + left + "," + right + '>';
            }
            return "[" + left + "," + right + ']';
        }
    }

}
