package io.github.thatsmusic99.headsplus.util.events;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Set;

public abstract class HeadsPlusListener<T> implements Listener {

    private final HashMap<String, String> data = new HashMap<>();
    private final HashMap<String, String[]> possibleValues = new HashMap<>();
    protected HeadsPlus hp = HeadsPlus.get();

    public abstract void onEvent(T event);

    public <D> D addData(String variableName, D data) {
        this.data.put(variableName, String.valueOf(data));
        return data;
    }

    public void addPossibleData(String key, String... vals) {
        possibleValues.put(key, vals);
    }

    public String[] getPossibleData(String key) {
        return possibleValues.get(key);
    }

    public String getData(String variableName) {
        return data.get(variableName);
    }

    public Set<String> getKeySet() {
        return data.keySet();
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public HashMap<String, String[]> getPossibleValues() {
        return possibleValues;
    }

    public boolean shouldEnable() { return true; }

    public abstract void init();
}
