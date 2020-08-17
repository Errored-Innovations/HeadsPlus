package io.github.thatsmusic99.headsplus.util.events;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Set;

public abstract class HeadsPlusListener<T> implements Listener {

    private final HashMap<String, String> data;
    private final HashMap<String, String[]> possibleValues;
    protected HeadsPlus hp;

    public HeadsPlusListener() {
        data = new HashMap<>();
        possibleValues = new HashMap<>();
        hp = HeadsPlus.getInstance();
    }

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
}
