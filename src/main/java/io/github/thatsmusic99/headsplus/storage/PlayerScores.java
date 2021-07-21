package io.github.thatsmusic99.headsplus.storage;

import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Deprecated
public class PlayerScores implements JSONFile {

    private JSONObject json = new JSONObject();

    @Override
    public String getName() {
        return "playerinfo";
    }

    @Override
    public void writeData(OfflinePlayer p, Object... values) {

    }

    @Override
    public JSONObject getJSON() {
        return json;
    }

    @Override
    public Object getData(Object key) {
        return null;
    }

    @Override
    public void setJSON(JSONObject jsonObject) {
        json = jsonObject;
    }

    public void completeChallenge(String uuid, Challenge c) {
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        List<String> challenges = getCompletedChallenges(uuid);
        challenges.add(c.getConfigName());
        jsonPlayer.put("completed-challenges", challenges);
        json.put(uuid, jsonPlayer);
    }

    public String getLocale(String uuid) {
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        return String.valueOf(jsonPlayer.get("locale"));
    }

    public void setLocale(String uuid, String locale, boolean auto) {
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        jsonPlayer.put("locale", locale + ":" + auto);
        json.put(uuid, jsonPlayer);
    }

    public void addXp(String uuid, int xp) {
        int exp = getXp(uuid);
        exp += xp;
        setXp(uuid, exp);
        HPUtils.addBossBar(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
    }

    public void deletePlayer(Player p) {
        UUID uuid = p.getUniqueId();
        json.remove(uuid.toString());
        HPPlayer.players.remove(uuid);
    }

    public void setXp(String uuid, int xp) {
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        jsonPlayer.put("xp", xp);
        json.put(uuid, jsonPlayer);
    }

    public int getXp(String uuid) {
        try {
            JSONObject jsonPlayer = getJsonPlayer(uuid);
            return getInt(jsonPlayer, "xp");
        } catch (NullPointerException ex) {
            setXp(uuid, 0);
            return getInt((JSONObject) json.get(uuid), "xp");
        }
    }

    public void setLevel(String uuid, String level) {
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        jsonPlayer.put("level", level);
        json.put(uuid, jsonPlayer);
    }

    public String getLevel(String uuid) {
        JSONObject jsonObject = (JSONObject) json.get(uuid);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        return (jsonObject.get("level") == null ? "" : (String) jsonObject.get("level"));
    }

    public void setCompletedChallenges(String uuid, List<String> ch) {
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        jsonPlayer.put("completed-challenges", ch);
        json.put(uuid, jsonPlayer);
    }

    public List<String> getCompletedChallenges(String uuid) {
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        return (jsonPlayer.get("completed-challenges") == null ? new ArrayList<>() : (List<String>) jsonPlayer.get("completed-challenges"));
    }

    public int getPlayerTotal(String uuid, String type, String db) {
        String dataType = getLeaderboardType(db);
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        JSONObject jsonData = getJsonData(jsonPlayer, dataType);
        int total = 0;
        if (jsonData.get(type) != null) {
            total = getInt(jsonData, type);
        }
        return total;
    }

    public void addPlayerTotal(String uuid, String type, String db, int amountToAdd) {
        String dataType = getLeaderboardType(db);
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        JSONObject jsonData = getJsonData(jsonPlayer, dataType);
        int typeTotal = 0;
        if (jsonData.get(type) != null) {
            typeTotal = getInt(jsonData, type);
        }
        typeTotal += amountToAdd;
        int total = 0;
        if (jsonData.get("total") != null) {
            total = getInt(jsonData, "total");
        }
        total += amountToAdd;
        jsonData.put("total", total);
        jsonData.put(type, typeTotal);
        jsonPlayer.put(dataType, jsonData);
        json.put(uuid, jsonPlayer);
    }

    public void setPlayerTotal(String uuid, String type, String db, int newTotal) {
        String dataType = getLeaderboardType(db);
        JSONObject jsonPlayer = getJsonPlayer(uuid);
        JSONObject jsonData = getJsonData(jsonPlayer, dataType);
        jsonData.put(type, newTotal);
        jsonPlayer.put(dataType, jsonData);
        json.put(uuid, jsonPlayer);
    }

    private int getInt(JSONObject jsonObject, String id) {
        try {
            return Math.toIntExact((long) jsonObject.get(id));
        } catch (ClassCastException a) {
            return (int) jsonObject.get(id);
        }
    }

    private JSONObject getJsonPlayer(String uuid) {
        JSONObject jsonPlayer = (JSONObject) json.get(uuid);
        if (jsonPlayer == null) {
            jsonPlayer = new JSONObject();
        }
        return jsonPlayer;
    }

    private JSONObject getJsonData(JSONObject jsonPlayer, String dataType) {
        JSONObject jsonData = (JSONObject) jsonPlayer.get(dataType);
        if (jsonData == null) {
            jsonData = new JSONObject();
        }
        return jsonData;
    }


    //TODO change this class to work with enums
    public enum LeaderboardType {
        HUNTING, SELLHEAD, CRAFTING
    }
    private String getLeaderboardType(String string) {
        switch (string) {
            case "headspluslb":
            case "hunting":
                return  "hunting";
            case "headsplussh":
            case "selling":
                return "sellhead";
            case "headspluscraft":
            case "crafting":
                return "crafting";
            default:
                return "";
        }
    }
}
