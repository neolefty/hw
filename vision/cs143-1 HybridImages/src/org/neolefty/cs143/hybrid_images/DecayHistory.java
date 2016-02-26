package org.neolefty.cs143.hybrid_images;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/** An exponentially decaying history. Good for files? */
public class DecayHistory<T extends Serializable> implements Externalizable {
    // largest first
    private SortedMap<Double, T> map = new TreeMap<>(Collections.reverseOrder());
    private static final int VERSION = 1;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        out.writeInt(map.size());
        for (Map.Entry<Double, T> entry : map.entrySet()) {
            out.writeDouble(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int version = in.readInt();
        int n = in.readInt();
        for (int i = 0; i < n; ++i)
            //noinspection unchecked
            map.put(in.readDouble(), (T) in.readObject());
    }
}
