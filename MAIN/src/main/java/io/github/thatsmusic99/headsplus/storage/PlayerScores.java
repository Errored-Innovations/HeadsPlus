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
    public void setJSON(JSONObject s) {
        json = s;
    }

    public void completeChallenge(String uuid, Challenge c) {
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        List<String> challenges = getCompletedChallenges(uuid);
        challenges.add(c.getConfigName());
        o1.put("completed-challenges", challenges);
        json.put(uuid, o1);
    }

    public void addXp(String uuid, int xp) {
        int exp = getXp(uuid);
        exp += xp;
        setXp(uuid, exp);
        HPUtils.addBossBar(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
    }

    public void deletePlayer(Player p) {
        json.remove(p.getUniqueId().toString());
        HPPlayer.players.remove(HPPlayer.getHPPlayer(p));
    }

    public void setXp(String uuid, int xp) {
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        o1.put("xp", xp);
        json.put(uuid, o1);
    }

    public int getXp(String uuid) {
        try {
            JSONObject o1 = (JSONObject) json.get(uuid);
            if (o1 == null) {
                o1 = new JSONObject();
            }
            return getInt(o1, "xp");
        } catch (NullPointerException ex) {
            setXp(uuid, 0);
            return getInt((JSONObject) json.get(uuid), "xp");
        }

    }

    public void setLevel(String uuid, String level) {
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        o1.put("level", level);
        json.put(uuid, o1);
    }

    public String getLevel(String uuid) {
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        return (o1.get("level") == null ? "" : (String) o1.get("level"));
    }

    public void setCompletedChallenges(String uuid, List<String> ch) {
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        o1.put("completed-challenges", ch);
        json.put(uuid, o1);
    }

    public List<String> getCompletedChallenges(String uuid) {
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        return (o1.get("completed-challenges") == null ? new ArrayList<>() : (List<String>) o1.get("completed-challenges"));
    }

    public int getPlayerTotal(String uuid, String type, String db) {
        String s = "";
        switch (db) {
            case "headspluslb":
            case "hunting":
                s = "hunting";
                break;
            case "headsplussh":
            case "selling":
                s = "sellhead";
                break;
            case "headspluscraft":
            case "crafting":
                s = "crafting";
                break;
        }
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        JSONObject o2 = (JSONObject) o1.get(s);
        if (o2 == null) {
            o2 = new JSONObject();
        }
        Object o = o2.get(type);
        int i = 0;
        if (o != null) {
            i = getInt(o2, type);
        }
        return i;
    }

    public void addPlayerTotal(String uuid, String type, String db, int amount) {
        String s;
        switch (db) {
            case "headspluslb":
                s = "hunting";
                break;
            case "headsplussh":
                s = "sellhead";
                break;
            case "headspluscraft":
                s = "crafting";
                break;
            default:
                s = db;
                break;
        }
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        JSONObject o2 = (JSONObject) o1.get(s);
        if (o2 == null) {
            o2 = new JSONObject();
        }
        Object o = o2.get(type);
        int i = 0;
        if (o != null) {
            i = getInt(o2, type);
        }

        i += amount;
        Object o3 = o2.get("total");
        int j = 0;
        if (o3 != null) {
            j = getInt(o2, "total");
        }
        j += amount;
        o2.put("total", j);
        o2.put(type, i);
        o1.put(s, o2);
        json.put(uuid, o1);
    }

    public void setPlayerTotal(String uuid, String type, String db, int no) {
        String s;
        switch (db) {
            case "headspluslb":
                s = "hunting";
                break;
            case "headsplussh":
                s = "sellhead";
                break;
            case "headspluscraft":
                s = "crafting";
                break;
            default:
                s = db;
                break;
        }
        JSONObject o1 = (JSONObject) json.get(uuid);
        if (o1 == null) {
            o1 = new JSONObject();
        }
        JSONObject o2 = (JSONObject) o1.get(s);
        if (o2 == null) {
            o2 = new JSONObject();
        }
        o2.put(type, no);
        o1.put(s, o2);
        json.put(uuid, o1);
    }

    private int getInt(JSONObject o, String s) {
        try {
            return Math.toIntExact((long) o.get(s));
        } catch (ClassCastException a) {
            return (int) o.get(s);
        }
    }
}
