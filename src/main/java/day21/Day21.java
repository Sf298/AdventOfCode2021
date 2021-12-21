package day21;

import java.math.BigInteger;
import java.util.Iterator;

public class Day21 {

    private static final int DAY = Integer.parseInt(Day21.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) throws InterruptedException {
        //part1();
        part2();
        Thread.sleep(100);
    }

    private static void part1() {
        DeterministicDice dice = new DeterministicDice();
        int player1Pos = 7;
        int player1Score = 0;
        int player2Pos = 4;
        int player2Score = 0;

        int target = 1000;
        while (true) {
            player1Pos = (player1Pos-1+dice.next()) % 10 + 1;
            player1Pos = (player1Pos-1+dice.next()) % 10 + 1;
            player1Pos = (player1Pos-1+dice.next()) % 10 + 1;
            System.out.println("P1: " + (player1Score += player1Pos));
            if (player1Score >= target) break;
            player2Pos = (player2Pos-1+dice.next()) % 10 + 1;
            player2Pos = (player2Pos-1+dice.next()) % 10 + 1;
            player2Pos = (player2Pos-1+dice.next()) % 10 + 1;
            System.out.println("P2: " + (player2Score += player2Pos));
            if (player2Score >= target) break;
        }

        System.out.println(dice.timesRolled+" * "+Math.min(player1Score, player2Score));
        int ans = dice.timesRolled * Math.min(player1Score, player2Score);
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int player1Pos = 1;
        int player2Pos = 4;
        BigInteger[][] player1Track = newTrack2D(21, 10);
        for (int i = 1; i < 2; i++) {
            player1Track[0][i] = BigInteger.ONE;
        }

        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < 11; i++) {
            System.out.println("Before step "+(i+1));
            printTrack(player1Track);
            sum = sum.add(roll(player1Track));
            System.out.println(sum);
        }

    }
    private static BigInteger roll(BigInteger[][] track) {
        BigInteger[][] trackOut = newTrack2D(track.length, track[0].length);

        BigInteger sum = BigInteger.ZERO;
        for (int score = 0; score < track.length; score++) { // scores
            for (int pos = 1; pos <= track[score].length; pos++) {  // positions
                if (track[score][pos-1].equals(BigInteger.ZERO)) continue;
                for (int roll1 = 1; roll1 <= 3; roll1++) {
                    for (int roll2 = 1; roll2 <= 3; roll2++) {
                        for (int roll3 = 1; roll3 <= 3; roll3++) {
                            int newPos = modStart1(pos + roll1+roll2+roll3, track[0].length);
                            int newScore = score + newPos;
                            if (newScore < track.length) {
                                trackOut[newScore][newPos-1] = trackOut[newScore][newPos-1].add(track[score][pos-1]);
                            } else {
                                sum = sum.add(track[score][pos-1]);
                            }
                        }
                    }
                }
            }
        }

        copyTrack(trackOut, track);

        return sum;
    }
    private static BigInteger[][] newTrack2D(int maxScore, int length) {
        BigInteger[][] out = new BigInteger[maxScore][length];
        for (int i = 0; i < maxScore; i++) {
            for (int j = 0; j < length; j++) {
                out[i][j] = BigInteger.ZERO;
            }
        }
        return out;
    }
    private static void copyTrack(BigInteger[][] from, BigInteger[][] to) {
        for (int i = 0; i < from.length; i++) {
            System.arraycopy(from[i], 0, to[i], 0, from[i].length);
        }
    }
    private static int modStart1(int i, int j) {
        return ((i-1)%j)+1;
    }

    private static void printTrack(BigInteger[][] track) {
        System.out.print("   ");
        for (int i = 0; i < track.length; i++) {
            System.out.printf("%4d", i);
        }
        System.out.println();
        for (int i = 0; i < track[0].length; i++) {
            System.out.printf("%3d", i+1); // row number
            for (int j = 0; j < track.length; j++) {
                System.out.printf("|%3s", (track[j][i].equals(BigInteger.ZERO)) ? " " : track[j][i]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static class DeterministicDice implements Iterator<Integer> {
        int internalVal = 0;
        int timesRolled = 0;

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            timesRolled++;
            return (internalVal++) + 1;
        }
    }

    private static class DiracDice implements Iterator<Integer> {
        int internalVal = 0;
        int timesRolled = 0;

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            timesRolled++;
            return (internalVal++) + 1;
        }
    }

}
