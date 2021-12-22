package day21;

import java.math.BigInteger;
import java.util.*;

import static utils.Permutations.permuteCoordinates;
import static utils.Permutations.permuteCuboid;

public class Day21 {

    private static final int DAY = Integer.parseInt(Day21.class.getSimpleName().replaceAll("[^0-9]", ""));

    public static void main(String[] args) throws InterruptedException {
        part1();
        part2();
    }

    private static void part1() {
        int player1Pos = 7;
        int player1Score = 0;
        int player2Pos = 4;
        int player2Score = 0;

        int nextRoll = 0;
        int timesRolled = 0;

        int target = 1000;
        while (true) {
            nextRoll = modStart1(nextRoll + 1,100); timesRolled++;
            player1Pos = modStart1(player1Pos+nextRoll, 10);
            nextRoll = modStart1(nextRoll + 1,100); timesRolled++;
            player1Pos = modStart1(player1Pos+nextRoll, 10);
            nextRoll = modStart1(nextRoll + 1,100); timesRolled++;
            player1Pos = modStart1(player1Pos+nextRoll, 10);
            player1Score += player1Pos;
            if (player1Score >= target) break;
            nextRoll = modStart1(nextRoll + 1,100); timesRolled++;
            player2Pos = modStart1(player2Pos+nextRoll, 10);
            nextRoll = modStart1(nextRoll + 1,100); timesRolled++;
            player2Pos = modStart1(player2Pos+nextRoll, 10);
            nextRoll = modStart1(nextRoll + 1,100); timesRolled++;
            player2Pos = modStart1(player2Pos+nextRoll, 10);
            player2Score += player2Pos;
        }

        System.out.println(timesRolled+" * "+Math.min(player1Score, player2Score));
        int ans = timesRolled * Math.min(player1Score, player2Score);
        System.out.println("Part 1 ANS: " + ans);
    }

    private static void part2() {
        int player1Pos = 7;
        int player2Pos = 4;
        BigInteger[][][][] track = newTrack4D(21, 10);
        track[0][0][player1Pos-1][player2Pos-1] = BigInteger.ONE;

        BigInteger p1Sum = BigInteger.ZERO;
        BigInteger p2Sum = BigInteger.ZERO;
        for (int i = 0; i < 20; i++) {
            p1Sum = p1Sum.add( roll(track, 1) );
            p2Sum = p2Sum.add( roll(track, 2) );
        }

        BigInteger ans = p1Sum.max(p2Sum);
        System.out.println("Part 2 ANS: " + ans);
    }
    private static BigInteger roll(BigInteger[][][][] track, int player) {
        BigInteger[][][][] trackOut = newTrack4D(track.length, track[0][0].length);

        BigInteger sum = BigInteger.ZERO;
        for (int[] c : permuteCoordinates(21, 21, 10, 10)) {
            int p1Score = c[0], p2Score = c[1], p1Pos = c[2], p2Pos = c[3];

            if (track[p1Score][p2Score][p1Pos][p2Pos].equals(BigInteger.ZERO))
                continue;

            if (player == 1) {
                for (int[] p1Rolls : permuteCuboid(3, 3)) {
                    int newP1Pos = modStart1(p1Pos + 1 + Arrays.stream(p1Rolls).map(i->i+1).sum(), track[0][0].length);
                    int newP1Score = p1Score + newP1Pos;
                    if (newP1Score < track.length) {
                        trackOut[newP1Score][p2Score][newP1Pos - 1][p2Pos] =
                                trackOut[newP1Score][p2Score][newP1Pos - 1][p2Pos].add(track[p1Score][p2Score][p1Pos][p2Pos]);
                    } else {
                        sum = sum.add(track[p1Score][p2Score][p1Pos][p2Pos]);
                    }
                }
            } else if (player == 2) {
                for (int[] p2Rolls : permuteCuboid(3, 3)) {
                    int newP2Pos = modStart1(p2Pos + 1 + Arrays.stream(p2Rolls).map(i->i+1).sum(), track[0][0].length);
                    int newP2Score = p2Score + newP2Pos;
                    if (newP2Score < track.length) {
                        trackOut[p1Score][newP2Score][p1Pos][newP2Pos - 1] =
                                trackOut[p1Score][newP2Score][p1Pos][newP2Pos - 1].add(track[p1Score][p2Score][p1Pos][p2Pos]);
                    } else {
                        sum = sum.add(track[p1Score][p2Score][p1Pos][p2Pos]);
                    }
                }
            }
        }

        copyTrack(trackOut, track);

        return sum;
    }
    private static BigInteger[][][][] newTrack4D(int maxScore, int length) {
        BigInteger[][][][] out = new BigInteger[maxScore][maxScore][length][length];
        for (int i = 0; i < maxScore; i++) {
            for (int j = 0; j < maxScore; j++) {
                for (int k = 0; k < length; k++) {
                    for (int l = 0; l < length; l++) {
                        out[i][j][k][l] = BigInteger.ZERO;
                    }
                }
            }
        }
        return out;
    }
    private static void copyTrack(BigInteger[][][][] from, BigInteger[][][][] to) {
        for (int i = 0; i < from.length; i++) {
            for (int j = 0; j < from[i].length; j++) {
                for (int k = 0; k < from[i][j].length; k++) {
                    for (int l = 0; l < from[i][j][k].length; l++) {
                        to[i][j][k][l] = from[i][j][k][l];
                    }
                }
            }
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

}
