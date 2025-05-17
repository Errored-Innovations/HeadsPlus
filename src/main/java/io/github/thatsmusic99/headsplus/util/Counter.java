package io.github.thatsmusic99.headsplus.util;

import java.util.HashMap;

public class Counter<T> extends HashMap<T, Integer> {

    public int increment(T entry) {
        return add(entry, 1);
    }

    public int add(T entry, int amount) {
        int value = getOrDefault(entry, 0);
        put(entry, value + amount);
        return value + amount;
    }
}
