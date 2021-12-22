package utils.graph;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class EdgeGraph<T> {

    public List<Node<T>> nodes;
    public List<T> values;

    /**
     * Create a graph from a list of values, linked together by an edge list of indexes.
     * @param values The key of values.
     * @param edges The list of index pairs pointing to the values.
     */
    public EdgeGraph(List<T> values, List<int[]> edges) {
        this.values = new ArrayList<>(values);
        nodes = values.stream()
                .map(Node::new)
                .collect(toList());

        for (int[] edge : edges) {
            Node<T> from = nodes.get(edge[0]);
            Node<T> to = nodes.get(edge[1]);
            from.adjacent.add(to);
        }
    }

    /**
     * Create a graph from a list of node pairs (edges).
     * @param values The list of edges.
     */
    public EdgeGraph(List<List<T>> values) {
        this.values = values.stream()
                .flatMap(List::stream)
                .distinct()
                .collect(toList());

        Map<T, Integer> indexes = new HashMap<>();
        for (int i = 0; i < this.values.size(); i++) {
            indexes.put(this.values.get(i), i);
        }

        nodes = this.values.stream()
                .map(Node::new)
                .collect(toList());

        for (List<T> edge : values) {
            Node<T> from = nodes.get(indexes.get(edge.get(0)));
            Node<T> to = nodes.get(indexes.get(edge.get(1)));
            from.adjacent.add(to);
        }
    }

    public Node<T> getByValue(T t) {
        return get(values.indexOf(t));
    }

    public Node<T> get(int i) {
        return nodes.get(i);
    }

}
