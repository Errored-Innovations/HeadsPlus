package io.github.thatsmusic99.headsplus.storage;

import org.bukkit.OfflinePlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class Favourites implements JSONFile {

    private JSONObject json = new JSONObject();

    @Override
    public String getName() {
        return "favourites";
    }

    @Override
    public void writeData(OfflinePlayer p, Object... values) {
        JSONArray a = (JSONArray) json.get(p.getUniqueId().toString());
        if (a == null) {
            a = new JSONArray();
        }
        a.addAll(Arrays.asList(values));
        json.put(p.getUniqueId().toString(), a);
    }

    @Override
    public JSONObject getJSON() {
        return json;
    }

    @Override
    public Object getData(Object key) {
        return json.get(key);
    }

    @Override
    public void setJSON(JSONObject s) {
        json = s;
    }

    public void removeHead(OfflinePlayer p, String s) {
        JSONArray a = (JSONArray) json.get(p.getUniqueId().toString());
        a.remove(s);
        json.put(p.getUniqueId().toString(), a);
    }


}
