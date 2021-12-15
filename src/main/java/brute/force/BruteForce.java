package brute.force;

public class BruteForce {

    public static void main(String[] args) {
        Comparer actualValue = new Comparer();

        long currentGuess = 100;
        while (actualValue.compareTo(currentGuess) > 0) {
            currentGuess *= 100;
        }
        System.out.println(currentGuess);

        long result = runBinarySearchIteratively(actualValue, currentGuess/100, currentGuess);
        System.out.println(result + " in " + actualValue.getComparisons() + " steps");
    }

    public static long runBinarySearchIteratively(Comparer c, long low, long high) {
        long index = -1;

        while (low <= high) {
            long mid = low  + ((high - low) / 2);
            if (c.compareTo(mid) > 0) {
                low = mid + 1;
            } else if (c.compareTo(mid) < 0) {
                high = mid - 1;
            } else if (c.compareTo(mid) == 0) {
                index = mid;
                break;
            }
        }
        return index;
    }

    private static class Comparer implements Comparable<Long> {

        private static final long ACTUAL = 1631647919273L;
        private long comparisons = 0;

        @Override
        public int compareTo(Long o) {
            comparisons++;
            if (ACTUAL < o) {
                return -1;
            } else if (ACTUAL > o) {
                return 1;
            } else {
                return 0;
            }
        }

        public long getComparisons() {
            return comparisons;
        }

    }

}
