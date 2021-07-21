package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.config.MainConfig;

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

    public void reset() {
        prices.clear();
    }

    public void registerPrice(String key, double price) {
        prices.put(getKey(key), price);
    }

    public double getPrice(String key) {
        return prices.get(getKey(key));
    }

    public boolean isRegistered(String key) {
        return prices.containsKey(getKey(key));
    }

    private String getKey(String str) {
        if (MainConfig.get().getSellingHeads().CASE_INSENSITIVE) return str.toLowerCase();
        return str;
    }
}
