package utils.graph;

import lombok.NonNull;
import lombok.val;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class Node<T> {

    public T value;
    public LinkedHashSet<Node<T>> adjacent;

    public Node(T value) {
        this(value, null);
    }

    public Node(T value, Collection<Node<T>> adjacent) {
        this.value = value;
        this.adjacent = new LinkedHashSet<>();
        if (nonNull(adjacent)) {
            this.adjacent.addAll(adjacent);
        }
    }


    /**
     * Creates an Iterable to iterate breadth first throughout the graph starting from this node.
     * @return An iterable object that iterates breadth first through the graph.
     */
    public Iterable<Node<T>> breadthFirst() {
        return breadthFirst(n -> true);
    }

    /**
     * Creates an Iterable to iterate breadth first throughout the graph starting from this node.
     * @param predicate A predicated to decide whether a node can be scanned.
     * @return An iterable object that iterates breadth first through the graph.
     */
    public Iterable<Node<T>> breadthFirst(Predicate<Node<T>> predicate) {
        return breadthFirst(predicate, null);
    }

    /**
     * Iterate breadth first throughout the graph starting from this node.
     * @param predicate A predicated to decide whether a node can be scanned.
     * @param preProcess A method to process a node (or its neighbours) before it's neighbors are scanned.
     */
    public void breadthFirstPrefiltered(Predicate<Node<T>> predicate, Consumer<Node<T>> preProcess) {
        breadthFirst(predicate, preProcess).forEach(n -> {});
    }

    /**
     * Creates an Iterable to iterate breadth first throughout the graph starting from this node.
     * @param predicate A predicated to decide whether a node can be scanned.
     * @param preProcess A method to process a node (or its neighbours) before it's neighbors are scanned.
     * @return An iterable object that iterates breadth first through the graph.
     */
    private Iterable<Node<T>> breadthFirst(Predicate<Node<T>> predicate, Consumer<Node<T>> preProcess) {
        Node<T> thiz = this;

        return () -> new Iterator<>() {

            final Queue<Node<T>> q = new LinkedList<>(singleton(thiz));
            final Set<Node<T>> scanned = new HashSet<>(singleton(thiz));

            @Override
            public boolean hasNext() {
                q.removeIf(n -> !predicate.test(n));
                return !q.isEmpty();
            }

            @Override
            public Node<T> next() {
                Node<T> curr = q.poll();
                if (isNull(curr)) {
                    return null;
                }

                if (nonNull(preProcess)) {
                    preProcess.accept(curr);
                }

                curr.adjacent.stream()
                        .filter(n -> !scanned.contains(n))
                        .filter(predicate)
                        .peek(scanned::add)
                        .forEach(q::add);

                return curr;
            }
        };
    }


    /**
     * Creates an Iterable to iterate depth first throughout the graph starting from this node.
     * @return An iterable object that iterates depth first through the graph.
     */
    public Iterable<Node<T>> depthFirst() {
        return depthFirst(n -> true);
    }

    /**
     * Creates an Iterable to iterate depth first throughout the graph starting from this node.
     * @param predicate A predicated to decide whether a node can be scanned.
     * @return An iterable object that iterates depth first through the graph.
     */
    public Iterable<Node<T>> depthFirst(Predicate<Node<T>> predicate) {
        return depthFirst(predicate, null);
    }

    /**
     * Creates an Iterable to iterate depth first throughout the graph starting from this node.
     * @param predicate A predicated to decide whether a node can be scanned.
     * @param preProcess A method to process a node (or its neighbours) before it's neighbors are scanned.
     */
    public void depthFirstPrefiltered(Predicate<Node<T>> predicate, Consumer<Node<T>> preProcess) {
        depthFirst(predicate, preProcess).forEach(n -> {});
    }

    /**
     * Creates an Iterable to iterate depth first throughout the graph starting from this node.
     * @param predicate A predicated to decide whether a node can be scanned.
     * @param preProcess A method to process a node (or its neighbours) before it's neighbors are scanned.
     * @return An iterable object that iterates depth first through the graph.
     */
    private Iterable<Node<T>> depthFirst(Predicate<Node<T>> predicate, Consumer<Node<T>> preProcess) {
        Node<T> thiz = this;

        return () -> new Iterator<>() {
            final Stack<Node<T>> s = new Stack<>() {{
                add(thiz);
            }};
            final Set<Node<T>> scanned = new HashSet<>(singleton(thiz));

            @Override
            public boolean hasNext() {
                s.removeIf(n -> !predicate.test(n));
                return !s.isEmpty();
            }

            @Override
            public Node<T> next() {
                Node<T> curr = s.pop();
                if (isNull(curr)) {
                    return null;
                }

                if (nonNull(preProcess)) {
                    preProcess.accept(curr);
                }

                curr.adjacent.stream()
                        .filter(n -> !scanned.contains(n))
                        .filter(predicate)
                        .peek(scanned::add)
                        .forEach(s::add);

                return curr;
            }
        };
    }


    /**
     * Find all possible paths from one node to another. Moves over each node once.
     * @param target The destination node.
     * @return An iterable object that iterates through all possible paths.
     */
    public Iterable<List<Node<T>>> walks(@NonNull Node<T> target) {
        return walks(target, (path, newNode) -> !path.contains(newNode));
    }

    /**
     * Find all possible paths from one node to another.
     * @param target The destination node.
     * @param predicate A predicated to decide whether a node can be moved to by the provided path.
     * @return An iterable object that iterates through all possible paths.
     */
    public Iterable<List<Node<T>>> walks(@NonNull Node<T> target, BiPredicate<List<Node<T>>, Node<T>> predicate) {
        Node<T> thiz = this;

        return () -> new Iterator<>() {

            private boolean isInit = false;
            private List<Node<T>> nextPath = new ArrayList<>(List.of(thiz));

            private void tryInit() {
                if (isInit) return;

                isInit = true;
                dive();
            }

            @Override
            public boolean hasNext() {
                tryInit();
                return nextPath != null;
            }

            @Override
            public List<Node<T>> next() {
                tryInit();
                while (true) {
                    List<Node<T>> selectedPath = new ArrayList<>(nextPath);

                    if (!incCurrent()) {
                        goUpAndInc();
                        if (nextPath == null) {
                            return selectedPath;
                        }
                    }

                    if (!nextPath.get(nextPath.size()-1).equals(target)) {
                        dive();
                    }

                    if (selectedPath.get(selectedPath.size()-1).equals(target)) {
                        return selectedPath;
                    }
                }
            }

            private boolean incCurrent() {
                Node<T> nextSibling = nextSibling(nextPath.size()-2);
                if (nonNull(nextSibling)) {
                    nextPath.set(nextPath.size()-1, nextSibling);
                    return true;
                }
                return false;
            }

            private void goUpAndInc() {
                while (nextPath.size() <= 1 || isNull(nextSibling(nextPath.size()-2))) {
                    if(nextPath.size() <= 1) {
                        nextPath = null;
                        return;
                    }
                    nextPath.remove(nextPath.size()-1);
                }

                nextPath.set(nextPath.size()-1, nextSibling(nextPath.size()-2));
            }

            private void dive() {
                while (true) {
                    Node<T> node = nextPath.get(nextPath.size()-1).adjacent.stream()
                            .filter(n -> predicate.test(new ArrayList<>(nextPath), n))
                            .findFirst().orElse(null);
                    if (node == null) return;
                    nextPath.add(node);
                    if (target.equals(node)) return;
                }
            }

            private Node<T> nextSibling(int depth) {
                Node<T> parent = nextPath.get(depth);
                Node<T> last = nextPath.get(depth+1);

                Iterator<Node<T>> i = parent.adjacent.iterator();
                while (true) {
                    if (i.next().equals(last)) break;
                }

                if (!i.hasNext()) return null;

                while (i.hasNext()) {
                    Node<T> n = i.next();
                    if (!nextPath.contains(n)) return n;
                }
                return null;
            }

        };
    }

    /**
     * Find all possible paths from one node to another.
     * @param target The destination node.
     * @param edgeWeight A function that calculates the weight of moving from the left node to the right node.
     * @return The shortest possible path from this not to the target.
     */
    public List<Node<T>> shortestWalk(@NonNull Node<T> target, BiFunction<Path<T>, Node<T>, Long> edgeWeight) {
        Map<Path<T>, Long> paths = new HashMap<>(Map.of(new Path<>(List.of(this)), 0L));

        while (true) {
            long minWeight = Long.MAX_VALUE;
            Path<T> minPath = null;
            for (val e : paths.entrySet()) {
                if (e.getValue() < minWeight) {
                    minWeight = e.getValue();
                    minPath = e.getKey();
                }
            }
            paths.remove(minPath);

            if (isNull(minPath)) return null;

            for (val neighbor : minPath.getPath().get(minPath.getPath().size()-1).adjacent) {
                if (neighbor.equals(target)) {
                    return Stream.concat(minPath.getPath().stream(), Stream.of(neighbor)).collect(toList());
                }
                Long newWeight = edgeWeight.apply(minPath, neighbor);
                if (isNull(newWeight)) continue;

                paths.put(new Path<>(minPath.getPath(), neighbor), minWeight + newWeight);
            }
        }
    }

    public static class Path<T> {
        private final List<Node<T>> path;
        private final Set<Node<T>> nodes;
        private Integer hashCache;
        public Path(List<Node<T>> path) {
            this.path = new ArrayList<>(path);
            this.nodes = new HashSet<>(path);
        }
        public Path(List<Node<T>> path, Node<T> tail) {
            this(Stream.concat(path.stream(), Stream.of(tail)).collect(toList()));
        }
        public List<Node<T>> getPath() {
            return path;
        }
        public boolean contains(Node<T> n) {
            return nodes.contains(n);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path<?> path1 = (Path<?>) o;
            return Objects.equals(path, path1.path);
        }
        @Override
        public int hashCode() {
            if (hashCache == null) {
                hashCache = Objects.hash(path);
            }
            return hashCache;
        }
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }

}
