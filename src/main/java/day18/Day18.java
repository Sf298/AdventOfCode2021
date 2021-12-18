package day18;

import static java.util.Objects.nonNull;

public class Day18 {

    private static final int DAY = Integer.parseInt(Day18.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        SFNumber number;
        number = SFNumber.parse("[[[[[9,8],1],2],3],4]").reduce();
        test(number, "[[[[0,9],2],3],4]", 1);
        number = SFNumber.parse("[7,[6,[5,[4,[3,2]]]]]").reduce();
        test(number, "[7,[6,[5,[7,0]]]]", 2);
        number = SFNumber.parse("[[6,[5,[4,[3,2]]]],1]").reduce();
        test(number, "[[6,[5,[7,0]]],3]", 3);
        number = SFNumber.parse("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").reduce();
        test(number, "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", 4);

        number = SFNumber.add(SFNumber.parse("[[[[4,3],4],4],[7,[[8,4],9]]]"), SFNumber.parse("[1,1]"));
        number.reduceAll();
        test(number, "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", 5);

/*
[[[[4,0],[5,4]],[[7,7],[6,0]]],[[7,[5,5]],[[0,[11,3]],[[6,3],[8,8]]]]]
0123   2 3   21 23   2 3   210 12  3   21 23  4

[[[[4,0],[5,0]],[[[4,5],[2,6]],[9,5]]],[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]]
0123   2 3   21 234
 */

        // first of the long section
        number = SFNumber.add(SFNumber.parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"), SFNumber.parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"));
        System.out.println(number);
        while (nonNull(number.reduce())) {
            System.out.println(number);
        }
        if (!test(number, "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", 6)) {
            return;
        }

        /*SFNumber sum = chainAdd(
                SFNumber.parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"),
                SFNumber.parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"),
                SFNumber.parse("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"),
                SFNumber.parse("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"),
                SFNumber.parse("[7,[5,[[3,8],[1,4]]]]"),
                SFNumber.parse("[[2,[2,2]],[8,[8,1]]]")
        );
        test(sum, "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", 7);*/



        int ans = 0;
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
    }

    private static SFNumber chainAdd(SFNumber... numbers) {
        SFNumber sum = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            sum = SFNumber.add(sum, numbers[i]);
            sum.reduceAll();
            System.out.println(sum);
        }
        return sum;
    }

    private static boolean test(SFNumber actual, String expected, int testNumber) {
        String str = actual.toString().replaceAll("<", "[").replaceAll(">", "]");
        if (!str.equals(expected)) {
            System.out.println("Test " + testNumber + " failed: ");
            System.out.println("e: " + expected);
            System.out.println("a: " + actual);
            System.out.println();
            return false;
        } else {
            System.out.println("Test " + testNumber + " passed.");
            return true;
        }
    }

    private interface SFNumber {
        SFNumber reduce();
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

        default void reduceAll() {
            while (nonNull(reduce()));
        }
    }

    private static class SFNumberLiteral implements SFNumber {
        public SFNumberPair parent;
        public int literal;

        public SFNumberLiteral(int literal) {
            this.literal = literal;
        }

        @Override
        public SFNumber reduce() {
            if (literal <= 9) return null;

            int left = literal / 2;
            return new SFNumberPair(left, literal - left, parent);
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
        public SFNumber reduce() {
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
                    return this;
                }
            } else {
                SFNumber newLeft = left.reduce();
                if (nonNull(newLeft)) {
                    left = newLeft;
                    return this;
                }
            }
            if (right instanceof SFNumberPair) {
                SFNumber newRight = ((SFNumberPair) right).internalExplode(depth+1);
                if (nonNull(newRight)) {
                    right = newRight;
                    return this;
                }
            } else {
                SFNumber newRight = right.reduce();
                if (nonNull(newRight)) {
                    right = newRight;
                    return this;
                }
            }
            return null;
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
            if (depth() < 4) {
                return "[" + left + "," + right + ']';
            } else {
                return "<" + left + "," + right + '>';
            }
        }
    }

}
