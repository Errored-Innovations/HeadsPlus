package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class NewMySQLAPI {

    private static Connection connection = HeadsPlus.getInstance().getConnection();

    public static void createTable() {

    }

    public static void addToTotal(String database, int addition, String section, OfflinePlayer player) {
        try {
            if (!doesPlayerExist(database, player)) addPlayer(database, player);
            ResultSet set = query(OperationType.SELECT, "total, " + section, database, "uuid", player.getUniqueId().toString());
            int total = set.getInt("total");
            int totalSec = set.getInt(section);
            update(OperationType.UPDATE, database, section, String.valueOf(totalSec + addition), String.valueOf(total + addition), "uuid", player.getUniqueId().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean doesPlayerExist(String database, OfflinePlayer player) {
        try {
            return query(OperationType.SELECT, "COUNT(1)", database, "uuid", player.getUniqueId().toString()).next();
        } catch (SQLException e) {
            return false;
        }
    }

    private static void addPlayer(String database, OfflinePlayer player) {
        try {
            update(OperationType.INSERT_INTO, "", database, "uuid", player.getUniqueId().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void update(OperationType type, String special, String... args) throws SQLException {
        prepareStatement(type, special, args).executeUpdate();
    }

    private static ResultSet query(OperationType type, String special, String... args) throws SQLException {
        return prepareStatement(type, special, args).executeQuery();
    }

    private static PreparedStatement prepareStatement(OperationType type, String special, String... args) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(type.syntax, special));
        for (int i = 0; i < args.length; i++) {
            statement.setString(i + 1, args[i]);
        }
        return statement;
    }

    private enum OperationType {
        SELECT("SELECT %s FROM ? WHERE ?=?"),
        INSERT_TABLE("INSERT TABLE ? ADD COLUMN ? VARCHAR(45)"),
        INSERT_INTO("INSERT INTO ? (?) VALUE (?)"),
        UPDATE("UPDATE ? SET ?=?, total=? WHERE ?=?"),
        CREATE("CREATE TABLE IF NOT EXISTS "),
        ALTER("ALTER TABLE ? ADD COLUMN ? VARCHAR(45)");

        String syntax;

        OperationType(String syntax) {
            this.syntax = syntax;
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
}
