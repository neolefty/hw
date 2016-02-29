package org.neolefty.cs143.hybrid_images.util;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.neolefty.cs143.hybrid_images.test.TestKit;

import java.io.*;
import java.util.*;

// TODO send change events via JavaFX's way
/** An exponentially decaying history. Good for files?
 *  When you add an item, all current weights decay, and the new item's weight is increased by 1. */
public class DecayHistory<T extends Comparable & Serializable> implements Externalizable {
    private static final int VERSION = 1;
    private static final double DECAY = 0.9;

    // largest first
    private TreeMultimap<Double, T> weightMap = createWeightMap();
    // value to weight
    private HashMap<T, Double> valueMap = new HashMap<>();

    public void add(T value) {
        decay(); // update all weights
        Double oldWeight = remove(value); // remove old weight
        Double newWeight = oldWeight == null ? 1. : oldWeight + 1.; // add 1 to the weight
        put(newWeight, value); // insert this one at the top
    }

    /** A list of values in weighted order, heaviest first. */
    public List<T> values() {
        return new ArrayList<>(weightMap.values());
    }

    private static <T extends Comparable & Serializable> TreeMultimap<Double, T> createWeightMap() {
        return TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
    }

    // OPTIMIZATION: ramp up max by 1/DECAY each time instead, and only occasionally full-decay
    private void decay() {
        TreeMultimap<Double, T> tmpWeightMap = createWeightMap();
        valueMap.clear();
        for (Map.Entry<Double, T> entry : weightMap.entries())
            put(tmpWeightMap, entry.getKey() * DECAY, entry.getValue());
        weightMap = tmpWeightMap;
    }

    public Double remove(T value) {
        Double weight = valueMap.remove(value);
        if (weight != null)
            weightMap.remove(weight, value);
        return weight;
    }

    public int size() {
        return weightMap.size();
    }

    public T getTop() {
        if (size() == 0)
            return null;
        else
            return weightMap.values().iterator().next();
    }

    private void put(Double weight, T value) { put(weightMap, weight, value); }

    private void put(TreeMultimap<Double, T> map, Double weight, T value) {
        map.put(weight, value);
        valueMap.put(value, weight);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION); // version
        out.writeInt(weightMap.size()); // entry count
        for (Map.Entry<Double, T> entry : weightMap.entries()) { // entry pairs: double, value
            out.writeDouble(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readInt(); // version
        int n = in.readInt(); // entry count
        for (int i = 0; i < n; ++i) { // entry pairs: double, value
            Double d = in.readDouble();
            //noinspection unchecked
            T t = (T) in.readObject();
            weightMap.put(d, t);
            valueMap.put(t, d);
        }
    }

    @Override
    public String toString() {
        return weightMap.toString();
    }

    public static void main(String[] args) {
        TestKit.checkAssert();

        DecayHistory<String> h = new DecayHistory<>();
        h.add("a");
        h.add("c");
        h.add("b");
        System.out.println(h);
        System.out.println(h.values());
        assert h.values().equals(new ArrayList<>(Arrays.asList("b", "c", "a")));
        h.add("a");
        System.out.println(h);
        System.out.println(h.values());
        assert h.values().equals(new ArrayList<>(Arrays.asList("a", "b", "c")));
    }
}
