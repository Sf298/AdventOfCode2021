package utils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

public class MultiMapWrapper<K, V, M extends Map<K,C>, C extends Collection<V>> implements Map<K,C> {

    private final M map;
    private final Supplier<C> collectionType;

    public MultiMapWrapper(Supplier<M> mapType, Supplier<C> collectionType) {
        map = mapType.get();
        this.collectionType = collectionType;
    }

    public M map() {
        return map;
    }

    @Override
    public int size() {
        return map.values().stream().mapToInt(Collection::size).sum();
    }

    @Override
    public boolean isEmpty() {
        return map.values().stream().allMatch(Collection::isEmpty);
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.values().stream().anyMatch(c -> c.contains(value));
    }

    @Override
    public C get(Object key) {
        return map.get(key);
    }

    @Override
    public C put(K key, C value) {
        return map.put(key, value);
    }

    public void putValue(K key, V value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            C c = collectionType.get();
            c.add(value);
            map.put(key, c);
        }
    }

    @Override
    public C remove(Object key) {
        return map.remove(key);
    }

    public boolean removeValue(V value) {
        Entry<K, C> e = find(value);
        if (isNull(e)) return false;
        return nonNull(remove(e, value));
    }

    public boolean removeValues(Collection<?> values) {
        return removeMulti(vs -> vs.removeAll(values));
    }

    public boolean retainValues(Collection<?> values) {
        return removeMulti(vs -> vs.retainAll(values));
    }

    public boolean removeValuesIf(Predicate<? super V> filter) {
        return removeMulti(vs -> vs.removeIf(filter));
    }

    @Override
    public void putAll(Map<? extends K, ? extends C> m) {
        for (Entry<? extends K, ? extends C> e : m.entrySet()) {
            e.getValue().forEach(v -> putValue(e.getKey(), v));
        }
    }

    public void putAllValues(Map<? extends V, ? extends K> m) {
        for (Entry<? extends V, ? extends K> e : m.entrySet()) {
            putValue(e.getValue(), e.getKey());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<C> values() {
        return map.values();
    }

    public Collection<V> actualValues() {
        return actualValuesStream().collect(toSet());
    }

    public Stream<V> actualValuesStream() {
        return map.values().stream().flatMap(Collection::stream);
    }

    @Override
    public Set<Entry<K, C>> entrySet() {
        return map.entrySet();
    }

    private Map.Entry<K, C> find(V t) {
        for (Map.Entry<K, C> e : map.entrySet()) {
            if (e.getValue().contains(t)) {
                return e;
            }
        }
        return null;
    }

    private V remove(Map.Entry<K, C> e, V t) {
        boolean wasRemoved = e.getValue().remove(t);
        if (e.getValue().isEmpty()) {
            map.remove(e.getKey());
        }
        return wasRemoved ? t : null;
    }

    private boolean removeMulti(Predicate<C> function) {
        boolean changed = false;
        List<K> toRemove = new ArrayList<>();
        for (Entry<K, C> e : map.entrySet()) {
            changed |= function.test(e.getValue());
            if (e.getValue().isEmpty()) {
                toRemove.add(e.getKey());
            }
        }
        toRemove.forEach(map::remove);
        return changed;
    }

}
