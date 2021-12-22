package utils;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Based on the loop implementation of heap's algorithm on <a href="https://en.wikipedia.org/wiki/Heap's_algorithm#Details_of_the_algorithm">wikipedia</a>.
 */
public class Permutations {

    public static <T> Iterable<List<T>> permuteNoRepetition(List<T> items) {
        return () -> new Iterator<List<T>>() {
            final List<T> itemsCopy = new ArrayList<>(items);
            final int[] c = new int[itemsCopy.size()];
            int i = 0;
            List<T> next = new ArrayList<>(itemsCopy);

            @Override
            public boolean hasNext() {
                return i < itemsCopy.size();
            }

            @Override
            public List<T> next() {
                List<T> toReturn = next;
                next = internalNext();
                return toReturn;
            }

            public List<T> internalNext() {
                List<T> output = null;
                while (i < itemsCopy.size()) {
                    if (c[i] >= i) {
                        c[i] = 0;
                        i += 1;
                        continue;
                    }

                    if ((i & 1) == 0) {
                        T temp = itemsCopy.get(0);
                        itemsCopy.set(0, itemsCopy.get(i));
                        itemsCopy.set(i, temp);
                    } else {
                        T temp = itemsCopy.get(c[i]);
                        itemsCopy.set(c[i], itemsCopy.get(i));
                        itemsCopy.set(i, temp);
                    }
                    output = new ArrayList<>(itemsCopy);
                    c[i] += 1;
                    i = 0;
                    break;
                }
                return output;
            }
        };
    }

    public static <T> Iterable<List<T>> permuteWithRepetition(List<T> items) {
        return () -> new Iterator<>() {
            final List<T> itemsCopy = new ArrayList<>(items);
            final Iterator<int[]> i = permuteCuboid(items.size(), items.size()).iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public List<T> next() {
                return Arrays.stream(i.next()).mapToObj(itemsCopy::get).collect(toList());
            }

        };
    }

    public static Iterable<int[]> permuteCoordinates(int... dimensionSizes) {
        final int[] currentIndex = new int[dimensionSizes.length];
        currentIndex[currentIndex.length-1] = -1;

        return () -> new Iterator<>() {

            @Override
            public boolean hasNext() {
                for (int i = 0; i < currentIndex.length; i++) {
                    if (currentIndex[i] < dimensionSizes[i] - 1) return true;
                }
                return false;
            }

            @Override
            public int[] next() {
                for (int i = currentIndex.length - 1; i >= 0; i--) {
                    currentIndex[i]++;
                    if (currentIndex[i] < dimensionSizes[i]) {
                        break;
                    }
                    currentIndex[i] = 0;
                }

                return Arrays.copyOf(currentIndex, currentIndex.length);
            }

        };
    }

    public static Iterable<int[]> permuteCuboid(int dims, int sideLength) {
        final int[] currentIndex = new int[dims];
        currentIndex[currentIndex.length-1] = -1;

        return () -> new Iterator<>() {

            @Override
            public boolean hasNext() {
                for (int i = 0; i < currentIndex.length; i++) {
                    if (currentIndex[i] < sideLength - 1) return true;
                }
                return false;
            }

            @Override
            public int[] next() {
                for (int i = currentIndex.length - 1; i >= 0; i--) {
                    currentIndex[i]++;
                    if (currentIndex[i] < sideLength) {
                        break;
                    }
                    currentIndex[i] = 0;
                }

                return Arrays.copyOf(currentIndex, currentIndex.length);
            }

        };
    }

}
