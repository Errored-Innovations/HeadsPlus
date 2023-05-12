package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SellableHeadsManager {

    private final HashMap<String, Double> prices = new HashMap<>();
    private final HashMap<SellingType, List<String>> types = new HashMap<>();
    private static SellableHeadsManager instance;

    public SellableHeadsManager() {
        instance = this;
    }

    public static SellableHeadsManager get() {
        return instance;
    }

    public void reset() {
        prices.clear();
        types.clear();
    }

    public void registerPrice(String key, SellingType type, double price) {
        HeadsPlus.debug("Registering " + key + " price of " + price + "!");
        prices.put(getKey(key), price);
        if (!types.containsKey(type)) types.put(type, new ArrayList<>());
        types.get(type).add(getKey(key));
    }

    public List<String> getKeys(SellingType type) {
        return types.get(type);
    }

    public Set<String> getKeys() {
        return prices.keySet();
    }

    public double getPrice(String key) {
        if (!isRegistered(key) && key.startsWith("mobs_PLAYER")) key = "mobs_PLAYER";
        return prices.get(getKey(key));
    }

    public boolean isRegistered(String key) {
        return prices.containsKey(getKey(key));
    }

    private String getKey(String str) {
        if (MainConfig.get().getSellingHeads().CASE_INSENSITIVE) return str.toLowerCase();
        return str;
    }

    public enum SellingType {
        HUNTING,
        CRAFTING
    }
}
