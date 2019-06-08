package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Deprecated
public class HeadsPlusLeaderboards extends ConfigSettings {

    private final DeathEvents de = HeadsPlus.getInstance().getDeathEvents();

    public HeadsPlusLeaderboards() {
        this.conName = "leaderboards";
        enable(false);
    }

    @Override
    public void reloadC(boolean aaaa) {
        boolean n = false;
        if (configF == null) {
            configF = new File(HeadsPlus.getInstance().getDataFolder(), "leaderboards.yml");
            n = true;
        }
        config = YamlConfiguration.loadConfiguration(configF);
        if (n) {
            loadLeaderboards();
        }
        save();

    }

    private void loadLeaderboards() {
        try {
            getConfig().options().header("HeadsPlus by Thatsmusic99 - Config wiki: https://github.com/Thatsmusic99/HeadsPlus/wiki/Configuration");
            getConfig().addDefault("server-total.total", 0);
            for (EntityType e : de.ableEntities) {
                getConfig().addDefault("server-total." + e.name(), 0);
            }
            try {
                if (getConfig().getInt("server-total") != 0) {
                    getConfig().set("server-total.total", getConfig().getInt("server-total"));
                }
            } catch (Exception ignored) {
            }
            getConfig().options().copyDefaults(true);
            save();
        } catch (Exception e) {
            if (HeadsPlus.getInstance().getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    private void addPlayer(Player p, String section) throws SQLException {
        if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
            Connection c = HeadsPlus.getInstance().getConnection();
            Statement s;
            s = c.createStatement();

            StringBuilder sb2 = new StringBuilder();
            sb2.append("INSERT INTO `headspluslb` (uuid, total");
            for (EntityType e : de.ableEntities) {
                sb2.append(", ").append(e.name());
            }
            sb2.append(") VALUES('").append(p.getUniqueId().toString()).append("', '0'");
            for (EntityType ignored : de.ableEntities) {
                sb2.append(", 0");
            }
            sb2.append(")");
            s.executeUpdate(sb2.toString());
        } else {
            getConfig().addDefault("player-data." + p.getUniqueId().toString() + ".total", 0);
            getConfig().addDefault("player-data." + p.getUniqueId().toString() + "." + section, 0);
            int s = getConfig().getInt("server-total");
            s++;
            getConfig().set("server-total", s);
            getConfig().options().copyDefaults(true);
            save();
        }
    }

    @Deprecated
    public void addNewPlayerValue(Player p, String section) throws SQLException {
        if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
            Connection c = HeadsPlus.getInstance().getConnection();
            Statement s;
            ResultSet rs;
            s = c.createStatement();
            try {
                rs = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='" + p.getUniqueId().toString() + "'");
                Integer.parseInt(rs.getString(section));
            } catch (SQLException ex) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("INSERT INTO `headspluslb` (uuid, total");
                for (EntityType e : de.ableEntities) {
                    sb2.append(", ").append(e.name());
                }
                sb2.append(") VALUES('").append(p.getUniqueId().toString()).append("', '0'");
                for (EntityType ignored : de.ableEntities) {
                    sb2.append(", '0'");
                }
                sb2.append(");");

                s.executeUpdate(sb2.toString());

                rs = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='" + p.getUniqueId().toString() + "'");

                rs.next();

                int val = Integer.parseInt(rs.getString(section));

                val++;
                s.executeUpdate("UPDATE `headspluslb` SET `" + section + "`='" + val + "' WHERE `uuid`='" + p.getUniqueId().toString() + "'");
                int val2;
                ResultSet rs3 = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='" + p.getUniqueId().toString() + "'");
                rs3.next();
                val2 = Integer.parseInt(rs3.getString("total"));

                val2++;
                s.executeUpdate("UPDATE `headspluslb` SET total='" + val2 + "' WHERE `uuid`='" + p.getUniqueId().toString() + "'");

                ResultSet rs4 = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='server-total'");

                rs4.next();

                int val3 = Integer.parseInt(rs4.getString(section));
                val3++;
                s.executeUpdate("UPDATE `headspluslb` SET `" + section + "`='" + val3 + "' WHERE `uuid`='server-total'");

                ResultSet rs2;
                rs2 = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='server-total'");

                rs2.next();

                val2 = Integer.parseInt(rs2.getString("total"));

                val2++;
                s.executeUpdate("UPDATE `headspluslb` SET total='" + val2 + "' WHERE `uuid`='server-total'");

            }
        } else {
            getConfig().addDefault("player-data." + p.getUniqueId().toString() + "." + section, 0);
            int s = getConfig().getInt("server-total");
            s++;
            getConfig().set("server-total", s);
            getConfig().options().copyDefaults(true);
            save();
        }
    }

    @Deprecated
    public void addOntoValue(Player p, String section) throws SQLException {
        if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
            try {
                Connection c = HeadsPlus.getInstance().getConnection();
                Statement s = c.createStatement();
                ResultSet rs;
                rs = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='" + p.getUniqueId().toString() + "'");
                rs.next();
                int val = Integer.parseInt(rs.getString(section));
                val++;
                s.executeUpdate("UPDATE `headspluslb` SET `" + section + "`='" + val + "' WHERE `uuid`='" + p.getUniqueId().toString() + "'");
                ResultSet rs3 = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='" + p.getUniqueId().toString() + "'");
                rs3.next();
                int val2 = Integer.parseInt(rs3.getString("total"));
                val2++;
                s.executeUpdate("UPDATE `headspluslb` SET `total`='" + val2 + "' WHERE `uuid`='" + p.getUniqueId().toString() + "'");
                ResultSet rs2;
                rs2 = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='server-total'");
                rs2.next();
                int val3 = Integer.parseInt(rs2.getString(section));
                val3++;
                s.executeUpdate("UPDATE `headspluslb` SET `" + section + "`='" + val3 + "' WHERE `uuid`='server-total'");
                ResultSet rs4 = s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='server-total'");
                rs4.next();
                val2 = Integer.parseInt(rs4.getString("total"));
                val2++;
                s.executeUpdate("UPDATE `headspluslb` SET `total`='" + val2 + "' WHERE `uuid`='server-total'");
            } catch (SQLException e) {
                addNewPlayerValue(p, section);
            }

        } else {
            try {
                int i = getConfig().getInt("player-data." + p.getUniqueId().toString() + "." + section);
                i++;
                getConfig().set("player-data." + p.getUniqueId().toString() + "." + section, i);
                int is = getConfig().getInt("player-data." + p.getUniqueId().toString() + ".total");
                is++;
                getConfig().set("player-data." + p.getUniqueId().toString() + ".total", is);
                int s = getConfig().getInt("server-total");
                s++;
                getConfig().set("server-total", s);
                getConfig().options().copyDefaults(true);
                save();
            } catch (Exception e) {
                try {
                    addNewPlayerValue(p, section);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

    @Deprecated
    public LinkedHashMap<OfflinePlayer, Integer> getScores(String section) throws SQLException {
        if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
            LinkedHashMap<OfflinePlayer, Integer> hs = new LinkedHashMap<>();
            Connection c = HeadsPlus.getInstance().getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM `headspluslb` ORDER BY id");
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
                    Statement st = c.createStatement();
                    ResultSet rs2 = st.executeQuery("SELECT * FROM `headspluslb` WHERE `uuid`='" + name.getUniqueId().toString() + "'");
                    rs2.next();
                    hs.put(name, Integer.valueOf(rs2.getString(section)));
                }
            }
            hs = sortHashMapByValues(hs);
            return hs;
        } else {
            LinkedHashMap<OfflinePlayer, Integer> hs = new LinkedHashMap<>();
            for (String cs : getConfig().getConfigurationSection("player-data").getKeys(false)) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(cs));
                int i = getConfig().getInt("player-data." + p.getUniqueId().toString() + "." + section);
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
    @Deprecated
    public boolean addPlayerOnFileIfNotFound(Player p, String section) throws SQLException {
        if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
            Connection c = HeadsPlus.getInstance().getConnection();
            Statement s;
            s = c.createStatement();
            try {
                s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='" + p.getUniqueId() + "'");
                return true;
            } catch (SQLException ex) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("INSERT INTO `headspluslb` (uuid, total");
                for (EntityType e : de.ableEntities) {
                    sb2.append(", ").append(e.name());
                }
                sb2.append(") VALUES('").append(p.getUniqueId().toString()).append("', '0'");
                for (EntityType ignored : de.ableEntities) {
                    sb2.append(", 0");
                }
                sb2.append(")");
                s.executeUpdate(sb2.toString());
                addOntoValue(p, section);
                return false;
            }

        } else {
            try {
                if (getConfig().getInt("player-data." + p.getUniqueId().toString() + ".total") != 0) {
                    return true;
                } else {
                    addPlayer(p, section);
                    return false;
                }

            } catch (Exception ex) {
                addPlayer(p, section);
                return false;
            }
        }
    }

    @Deprecated
    public boolean addSectionOnFileIfNotFound(Player p, String section) throws SQLException {
        if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
            Connection c = HeadsPlus.getInstance().getConnection();
            Statement s;
            s = c.createStatement();
            try {

                s.executeQuery("SELECT * FROM `headspluslb` WHERE uuid='" + p.getUniqueId() + "'");
                return true;
            } catch (SQLException ex) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("INSERT INTO `headspluslb` (uuid, total");
                for (EntityType e : de.ableEntities) {
                    sb2.append(", ").append(e.name());
                }
                sb2.append(") VALUES('").append(p.getUniqueId().toString()).append("', '0'");
                for (EntityType ignored : de.ableEntities) {
                    sb2.append(", 0");
                }
                sb2.append(")");
                s.executeUpdate(sb2.toString());
                addOntoValue(p, section);
                return false;
            }
        } else {
            try {
                if (getConfig().getInt("player-data." + p.getUniqueId().toString() + "." + section) != 0) {
                    getConfig().getInt("player-data." + p.getUniqueId().toString() + "." + section);
                    return true;
                } else {
                    addNewPlayerValue(p, section);
                    return false;
                }
            } catch (Exception ex) {
                addNewPlayerValue(p, section);
                return false;
            }
        }
    }

    public void selfDestruct() {
        configF.delete();
    }
}
