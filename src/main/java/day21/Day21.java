package day21;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

public class Day21 {

    private static final int DAY = Integer.parseInt(Day21.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) {
        //part1();
        part2();
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
        BigInteger[][] player1Track = newTrack2D(21, 10, player1Pos-1);

        for (int i = 0; i < 7; i++) {
            printTrack(player1Track);
            player1Track = roll(player1Track);
        }

        //BigInteger sum = Arrays.stream(player1Track).reduce(BigInteger.ZERO, BigInteger::add);
        //System.out.println(sum);
    }
    private static BigInteger[][] roll(BigInteger[][] track) {
        BigInteger[][] trackOut = newTrack2D(track.length, track[0].length, -1);

        for (int score = 0; score < track.length; score++) { // scores
            for (int pos = 1; pos <= track[score].length; pos++) {  // positions
                if (track[score][pos-1].equals(BigInteger.ZERO)) continue;

                int newPos = modStart1(pos+1, track[0].length);
                int newScore = score + newPos;
                if (newScore < track.length) {
                    trackOut[newScore][newPos-1] = trackOut[newScore][newPos-1].add(track[score][pos-1]);
                }
                newPos = modStart1(pos+2, track[0].length);
                newScore = score + newPos;
                if (newScore < track.length) {
                    trackOut[newScore][newPos-1] = trackOut[newScore][newPos-1].add(track[score][pos-1]);
                }
                newPos = modStart1(pos+3, track[0].length);
                newScore = score + newPos;
                if (newScore < track.length) {
                    trackOut[newScore][newPos-1] = trackOut[newScore][newPos-1].add(track[score][pos-1]);
                }
            }
        }

        return trackOut;
    }
    private static BigInteger[] roll(BigInteger[] track) {
        BigInteger[] trackOut = newTrack(track.length, -1);

        for (int i = 0; i < track.length; i++) {
            int pos = (i+1)%track.length;
            trackOut[pos] = trackOut[pos].add(track[i]);
            pos = (i+2)%track.length;
            trackOut[pos] = trackOut[pos].add(track[i]);
            pos = (i+3)%track.length;
            trackOut[pos] = trackOut[pos].add(track[i]);
        }

        return trackOut;
    }
    private static BigInteger[] newTrack(int size, int startPos) {
        BigInteger[] out = new BigInteger[size];
        for (int i = 0; i < size; i++) {
            out[i] = BigInteger.ZERO;
        }
        if (startPos > 0) {
            out[startPos] = BigInteger.ONE;
        }
        return out;
    }
    private static BigInteger[][] newTrack2D(int maxScore, int length, int startPos) {
        BigInteger[][] out = new BigInteger[maxScore][length];
        for (int i = 0; i < maxScore; i++) {
            for (int j = 0; j < length; j++) {
                out[i][j] = BigInteger.ZERO;
            }
        }
        if (startPos >= 0) {
            out[0][startPos] = BigInteger.ONE;
        }
        return out;
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
