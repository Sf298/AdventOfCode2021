package utils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class PriorityQueue<T> implements Queue<T>  {

    private final MultiMapWrapper<Long, T, TreeMap<Long, LinkedHashSet<T>>, LinkedHashSet<T>> tree = new MultiMapWrapper<>(TreeMap::new, LinkedHashSet::new);

    public PriorityQueue() {}
    public PriorityQueue(Map<T, Long> map) {
        putAll(map);
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException("Cannot add, use put()");
    }

    public void put(T t, long priority) {
        tree.putValue(priority, t);
    }

    @Override
    public boolean remove(Object o) {
        if (isNull(o)) return false;
        return tree.removeValue((T) o);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return tree.removeValuesIf(filter);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return tree.actualValues().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Cannot addAll, use putAll()");
    }

    public void putAll(Map<T, Long> map) {
        tree.putAllValues(map);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return tree.removeValues(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return tree.retainValues(c);
    }

    @Override
    public boolean offer(T t) {
        throw new UnsupportedOperationException("Cannot offer, use put()");
    }

    @Override
    public T remove() {
        T t = tree.actualValuesStream().findFirst().orElseThrow();
        tree.removeValue(t);
        return t;
    }

    @Override
    public T poll() {
        T t = tree.actualValuesStream().findFirst().orElse(null);
        if (isNull(t)) return null;

        tree.removeValue(t);
        return t;
    }

    @Override
    public T element() {
        return tree.actualValuesStream().findFirst().orElseThrow();
    }

    @Override
    public T peek() {
        return tree.actualValuesStream().findFirst().orElse(null);
    }

    public Long getPriority(T t) {
        return tree.entrySet().stream()
                .filter(e -> e.getValue().contains(t))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    @Override
    public int size() {
        return tree.size();
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return tree.containsValue(o);
    }

    @Override
    public Iterator<T> iterator() {
        return tree.actualValuesStream().iterator();
    }

    @Override
    public Object[] toArray() {
        return tree.actualValuesStream().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        tree.clear();
    }

    @Override
    public String toString() {
        String s = tree.entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                        .map(v -> "{" + v + ":" + e.getKey() + "}"))
                .collect(Collectors.joining(", "));
        return "[" + s + "]";
    }

}
