package io.github.thatsmusic99.headsplus.util;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusLeaderboards;
import io.github.thatsmusic99.headsplus.config.challenges.HeadsPlusChallenges;
import io.github.thatsmusic99.headsplus.storage.PlayerScores;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.*;


public class MySQLAPI {

    private final HeadsPlusLeaderboards hpl;
    private final HeadsPlusChallenges hpc;
    private final HeadsPlus hp;

    public MySQLAPI() {
        hp = HeadsPlus.getInstance();
        hpl = hp.getLeaderboardsConfig();
        hpc = hp.getChallengeConfig();
        if (hpc.getConfig().get("player-data") instanceof ConfigurationSection) {
            hp.getLogger().info("Old storage detected! Transfering data (this will be saved when the server stops)...");
            transferScoresToJSON();
        }
    }

    private void addNewPlayerValue(OfflinePlayer p, String section, String database, int shAmount) throws SQLException {
        final String uuid = p.getUniqueId().toString();
        if (hp.isConnectedToMySQLDatabase()) {
            Connection c = hp.getConnection();
            Statement s;
            ResultSet rs;
            s = c.createStatement();
            try {
                PreparedStatement st = c.prepareStatement("SELECT " + section + " FROM `" + database + "` WHERE `uuid`=?");
                st.setString(1, uuid);
                rs = st.executeQuery();
                rs.next();
                rs.getInt(section); // I don't care if it's ignored
            } catch (SQLException | NumberFormatException ex) {
                if (ex instanceof MySQLSyntaxErrorException) {
                    s.executeUpdate("ALTER TABLE `" + database + "` ADD COLUMN `" + section + "` VARCHAR(45)");
                }

                // Correct MySQL version of inserting new value based on existence of old row:
                //String sb2 = "INSERT INTO `" + database + "` (uuid)" +
                //        " SELECT '" + p.getUniqueId().toString() + "' FROM DUAL"
                //        " WHERE NOT EXISTS (SELECT uuid FROM `" + database + "` WHERE uuid=`" + p.getUniqueId().toString() + "`);";
                // But we don't need to do this, really - this creates duplicate rows (id is the pk, not uuid);
                ResultSet rs_uuid = s.executeQuery("SELECT uuid FROM `" + database + "` WHERE uuid='" + uuid + "';");
                if(!rs_uuid.next()) {
                    // no row - add one!
                    s.executeUpdate("INSERT INTO `" + database + "` (uuid) VALUE ('" + uuid + "');");
                }
                rs_uuid.close();

                PreparedStatement st = c.prepareStatement("SELECT "  + section + ", total FROM `" + database + "` WHERE `uuid`=?");
                st.setString(1, uuid);
                rs = st.executeQuery();
                int val = 0;
                int val2 = 0;
                if (rs.next()) {
                    val = rs.getInt(section);
                    val2 = rs.getInt("total");
                }
                val += shAmount;
                val2 += shAmount;
                s.executeUpdate("UPDATE `" + database + "` SET `" + section + "`='" + val + "', `total`='" + val2 + "' WHERE `uuid`='" + uuid + "'");
                ResultSet rs4 = s.executeQuery("SELECT " + section + ", total FROM `" + database + "` WHERE `uuid`='server-total'");
                int val3 = 0;
                int val4 = 0;
                if (rs4.next()) {
                    val3 = rs4.getInt(section);
                    val4 = rs4.getInt("total");
                }
                val3 += shAmount;
                val4 += shAmount;
                s.executeUpdate("UPDATE `" + database + "` SET `" + section + "`='" + val3 + "', `total`='" + val4 + "' WHERE `uuid`='server-total'");

            }
        } else {
            PlayerScores scores = hp.getScores();
            if (database.equalsIgnoreCase("headspluslb")) {
                scores.setPlayerTotal(uuid, section, database, 0);
                scores.addPlayerTotal("server-total", section.toUpperCase(), database, 1);
            } else if (database.equalsIgnoreCase("headsplussh")) {
                scores.setPlayerTotal(uuid, section, database, 0);
                scores.addPlayerTotal("server-total", section.toUpperCase(), database, shAmount);
            } else {
                try {
                    scores.setPlayerTotal(uuid, section, database, 0);
                    scores.addPlayerTotal("server-total", section.toUpperCase(), database, shAmount);
                } catch (IllegalArgumentException ignored) { // Idk why the hell this happens????

                }

            }

        }
    }

    public void addOntoValue(OfflinePlayer p, String section, String database, int shAmount) throws SQLException {
        String uuid = p.getUniqueId().toString();
        section = section.toUpperCase();
        if (hp.isConnectedToMySQLDatabase()) {
            try {

                Connection c = hp.getConnection();
                Statement s = c.createStatement();
                // Update player
                ResultSet rs;
                rs = s.executeQuery("SELECT " + section + ", total FROM `" + database + "` WHERE uuid='" + uuid + "'");
                int val;
                int val2;
                if (rs.next()) {
                    val = rs.getInt(section);
                    val2 = rs.getInt("total");
                } else {
                    addNewPlayerValue(p, section, database, shAmount);
                    return;
                }
                val += shAmount;
                val2 += shAmount;
                s.executeUpdate("UPDATE `" + database + "` SET `" + section + "`='" + val + "', `total`='" + val2 + "' WHERE `uuid`='" + uuid + "'");
                // Update server total
                ResultSet rs2;
                rs2 = s.executeQuery("SELECT " + section + ", total FROM `" + database + "` WHERE `uuid`='server-total'");
                int val3 = 0;
                int val4 = 0;
                if (rs2.next()) {
                    val3 = rs2.getInt(section);
                    val4 = rs2.getInt("total");
                }
                val3 += shAmount;
                val4 += shAmount;
                s.executeUpdate("UPDATE `" + database + "` SET `" + section + "`='" + val3 + "', `total`='" + val4 + "' WHERE `uuid`='server-total'");
            } catch (SQLException e) {
                addNewPlayerValue(p, section, database, shAmount);
            }

        } else {
            PlayerScores scores = hp.getScores();
            if (database.equalsIgnoreCase("headspluslb")) {
                try {
                    scores.addPlayerTotal(uuid, section.toUpperCase(), database, 1);
                    scores.addPlayerTotal("server-total", section.toUpperCase(), database, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    addNewPlayerValue(p, section, database, shAmount);
                }
            } else {
                if (database.equalsIgnoreCase("headsplussh")) {
                    try {
                        scores.addPlayerTotal(uuid, section.toUpperCase(), database, shAmount);
                        scores.addPlayerTotal("server-total", section.toUpperCase(), database, shAmount);
                    } catch (Exception e) {
                        addNewPlayerValue(p, section, database, shAmount);
                    }
                } else {
                    try {
                        scores.addPlayerTotal(uuid, section.toUpperCase(), database, shAmount);
                        scores.addPlayerTotal("server-total", section.toUpperCase(), database, shAmount);
                    } catch (Exception e) {
                        e.printStackTrace();
                        addNewPlayerValue(p, section, database, shAmount);
                    }
                }
            }
        }
    }

    public LinkedHashMap<OfflinePlayer, Integer> getScores(String section, String database) throws SQLException {
        return getScores(section, database, false);
    }
    public LinkedHashMap<OfflinePlayer, Integer> getScores(String section, String database, boolean transfer) throws SQLException {
        if (hp.isConnectedToMySQLDatabase() && !transfer) {
            LinkedHashMap<OfflinePlayer, Integer> hs = new LinkedHashMap<>();
            Connection c = hp.getConnection();
            Statement s = c.createStatement();
            switch (database) {
                case "hunting":
                    database = "headspluslb";
                    break;
                case "selling":
                    database = "headsplussh";
                    break;
                case "crafting":
                    database = "headspluscraft";
                    break;
            }
            ResultSet rs = s.executeQuery("SELECT uuid, " + section + " FROM `" + database + "` ORDER BY id");
            while (rs.next()) {

                boolean player = false;
                UUID uuid = null;
                OfflinePlayer name;
                try {
                    uuid = UUID.fromString(rs.getString("uuid"));
                    player = true;
                } catch (Exception ex) {
                    //
                }
                if (player) {
                    name = Bukkit.getOfflinePlayer(uuid);
                    hs.put(name, rs.getInt(section));
                }
            }
            hs = sortHashMapByValues(hs);
            return hs;
        } else {
            PlayerScores scores = hp.getScores();
            LinkedHashMap<OfflinePlayer, Integer> hs = new LinkedHashMap<>();
            for (Object cs : scores.getJSON().keySet()) {
                if (String.valueOf(cs).equalsIgnoreCase("server-total")) continue;
                OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(cs)));
                int i = hp.getScores().getPlayerTotal(String.valueOf(cs), (section.equalsIgnoreCase("total") ? section : section.toUpperCase()), database);
                hs.put(p, i);
            }
            hs = sortHashMapByValues(hs);
            return hs;

        }

    }

    private LinkedHashMap<OfflinePlayer, Integer> sortHashMapByValues(HashMap<OfflinePlayer, Integer> passedMap) {
        List<OfflinePlayer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.reverse(mapValues);

        LinkedHashMap<OfflinePlayer, Integer> sortedMap =
                new LinkedHashMap<>();

        for (int val : mapValues) {
            Iterator<OfflinePlayer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                OfflinePlayer key = keyIt.next();
                Integer comp1 = passedMap.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    private void transferScoresToJSON() {
        PlayerScores scores = hp.getScores();
        for (String uuid : hpc.getConfig().getConfigurationSection("player-data").getKeys(false)) {
            if (!hp.isConnectedToMySQLDatabase()) {
                for (String database : hpc.getConfig().getConfigurationSection("player-data." + uuid).getKeys(false)) {
                    if (database.equalsIgnoreCase("sellhead") || database.equalsIgnoreCase("crafting")) {
                        for (String section : hpc.getConfig().getConfigurationSection("player-data." + uuid + "." + database).getKeys(false)) {

                            scores.setPlayerTotal(uuid, section, database,
                                    hpc.getConfig().getInt("player-data." + uuid + "." + database + "." + section));


                        }
                    }
                }

            }
            scores.setCompletedChallenges(uuid, hpc.getConfig().getStringList("player-data." + uuid + ".completed-challenges"));
            scores.setXp(uuid, hpc.getConfig().getInt("player-data." + uuid + ".profile.xp"));
            scores.setLevel(uuid, hpc.getConfig().getString("player-data." + uuid + ".profile.level"));
        }
        if (!hp.isConnectedToMySQLDatabase()) {
            try {
                for (String uuid : hpl.getConfig().getConfigurationSection("player-data").getKeys(false)) {
                    for (String section : hpl.getConfig().getConfigurationSection("player-data." + uuid).getKeys(false)) {
                        scores.setPlayerTotal(uuid, section, "headspluslb",
                                hpl.getConfig().getInt("player-data." + uuid + "." + section));
                    }
                }
                for (String section : hpl.getConfig().getConfigurationSection("server-total").getKeys(false)) {
                    scores.setPlayerTotal("server-total", section, "headspluslb",
                            hpl.getConfig().getInt("server-total." + section));
                }
                for (String database : hpc.getConfig().getConfigurationSection("server-total").getKeys(false)) {
                    for (String section : hpc.getConfig().getConfigurationSection("server-total." +  database).getKeys(false)) {
                        scores.setPlayerTotal("server-total", section, database,
                                hpc.getConfig().getInt("server-total." + database + section));
                    }
                }
            } catch (NullPointerException ex) {
                hp.getLogger().warning("leaderboards.yml wasn't found - has it already been deleted..?");
            }

        }

        try {
            hpl.getConfig().set("player-data", null);
            hpl.getConfig().set("server-total", null);
            hpl.selfDestruct();
        } catch (NullPointerException ignored) {

        }

        hpc.getConfig().set("player-data", null);
        hpc.getConfig().set("server-total", null);
        hpc.getConfig().options().copyDefaults(true);
        hpc.save();
    }
}
