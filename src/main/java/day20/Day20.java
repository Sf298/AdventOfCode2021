package day20;

import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Day20 {

    private static final int DAY = Integer.parseInt(Day20.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part1() {
        Scanner in = Utils.createScannerForDay(DAY);
        Enhancement enhancementAlgorithm = new Enhancement(in.nextLine().split(""));
        in.nextLine();
        Img image = new Img(Utils.stream(in)
                .map(l -> l.split(""))
                .toArray(String[][]::new));
        image.print();
        int step = 0;

        image = image.convolve(enhancementAlgorithm.isInfiniteFieldTrue(step++), enhancementAlgorithm);
        image = image.convolve(enhancementAlgorithm.isInfiniteFieldTrue(step++), enhancementAlgorithm);

        int ans = image.count();
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int ans = 0;
        System.out.println("Part 2 ANS: " + ans);
    }

    private static class Img {

        Set<Pair<Integer, Integer>> image = new HashSet<>();
        int minX, maxX, minY, maxY;

        public Img(String[][] image) {
            for (int j = 0; j < image.length; j++) {
                for (int i = 0; i < image[j].length; i++) {
                    if ("#".equals(image[j][i])) {
                        this.image.add(Pair.of(i,j));
                    }
                }
            }
            minX = minY = 0;
            maxX = image.length-1;
            maxY = image[0].length-1;
        }
        public Img(Img image) {
            this.image.addAll(image.image);
            updateMinMax();
        }

        /*public boolean get(int x, int y) {
            return image.contains(Pair.of(x,y));
        }*/

        public boolean get(boolean isInfiniteFieldTrue, int x, int y) {
            if (isInfiniteFieldTrue && !isCoordStored(x,y)) {
                return true;
            }
            return image.contains(Pair.of(x, y));
        }

        private void updateMinMax() {
            minX = this.image.stream().mapToInt(Pair::getLeft).min().orElse(0);
            maxX = this.image.stream().mapToInt(Pair::getLeft).max().orElse(0);
            minY = this.image.stream().mapToInt(Pair::getRight).min().orElse(0);
            maxY = this.image.stream().mapToInt(Pair::getRight).max().orElse(0);
        }
        public int minX() {
            return minX;
        }
        public int maxX() {
            return maxX;
        }
        public int minY() {
            return minY;
        }
        public int maxY() {
            return maxY;
        }
        public boolean isCoordStored(int x, int y) {
            return !(x<minX() || x>maxX() || y<minY() || y>minY());
        }

        public void set(int x, int y, boolean value) {
            if (value) {
                image.add(Pair.of(x,y));
            } else {
                image.remove(Pair.of(x,y));
            }
        }

        public int count() {
            return image.size();
        }

        private Img convolve(boolean isInfiniteFieldTrue, Enhancement enhancementAlgorithm) {
            System.out.println("conv " + isInfiniteFieldTrue);
            Img out = new Img(this);
            int buffer = 2;
            for (int j = minY()-buffer; j <= maxY()+buffer; j++) {
                for (int i = minX()-buffer; i <= maxX()+buffer; i++) {
                    BigInteger newValuePos = BigInteger.ZERO;
                    for (int y = -1; y <= 1; y++) {
                        for (int x = -1; x <= 1; x++) {
                            newValuePos = newValuePos.shiftLeft(1);
                            if (get(isInfiniteFieldTrue, i+x, j+y)) {
                                newValuePos = newValuePos.setBit(0);
                            }
                        }
                    }
                    out.set(i,j, enhancementAlgorithm.get(newValuePos.intValue()));
                }
            }
            out.updateMinMax();
            return out;
        }

        public void print() {
            for (int j = minY(); j <= maxY(); j++) {
                for (int i = minX(); i <= maxX(); i++) {
                    System.out.print(get(false, i,j) ? "#" : ".");
                }
                System.out.println();
            }
            System.out.println();
        }

        public String subToString(boolean isInfiniteFieldTrue, int i, int j) {
            StringBuilder sb = new StringBuilder();
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    sb.append(get(isInfiniteFieldTrue, i+x, j+y) ? "#" : ".");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    private static class Enhancement {

        final boolean[] algorithm;

        public Enhancement(String[] algorithm) {
            this.algorithm = new boolean[algorithm.length];
            for (int i = 0; i < algorithm.length; i++) {
                this.algorithm[i] = "#".equals(algorithm[i]);
            }
        }

        public boolean get(int i) {
            return algorithm[i];
        }

        public boolean isInfiniteFieldTrue(int step) {
            return (step&1) == 1 && algorithm[0];
        }

    }

}
