package io.github.thatsmusic99.headsplus.managers;

import java.util.HashMap;

public class SellableHeadsManager {

    private final HashMap<String, Double> prices = new HashMap<>();
    private static SellableHeadsManager instance;

    public SellableHeadsManager() {
        instance = this;
    }

    public static SellableHeadsManager get() {
        return instance;
    }
}
