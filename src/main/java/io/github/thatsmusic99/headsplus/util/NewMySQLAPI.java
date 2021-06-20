package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.DataManager;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.UUID;

@Deprecated
public class NewMySQLAPI {

    private static final Connection connection = HeadsPlus.get().getConnection();

    public static void createTable() {
        for (String str : Arrays.asList("headspluslb", "headsplussh", "headspluscraft")) {
            try {
                StringBuilder arg = new StringBuilder();
                arg.append(str).append("(`id` INT NOT NULL AUTO_INCREMENT, `uuid` BLOB, `total` INT, ");
                for (String entity : EntityDataManager.ableEntities) {
                    arg.append(entity).append(" INT, ");

                }
                arg.append("PLAYER INT, PRIMARY KEY (`id`))");
                update(OperationType.CREATE, arg.toString());
                if (!query(OperationType.SELECT_COUNT, str, "uuid", "server-total").next()) {
                    update(OperationType.INSERT_INTO, str, "uuid", "server-total");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void addToTotal(String database, int addition, String section, String uuid) {
        try {
            if (!doesPlayerExist(database, uuid)) addPlayer(database, uuid);
            ResultSet set = query(OperationType.SELECT, section, database, "uuid", uuid);
            set.next();
            int total = set.getInt("total");
            int totalSec = set.getInt(section);
            update(OperationType.UPDATE, database, section, String.valueOf(totalSec + addition), String.valueOf(total + addition), "uuid", uuid);
        } catch (SQLSyntaxErrorException e) {
            try {
                update(OperationType.ALTER, database, section);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LinkedHashMap<OfflinePlayer, Integer> getScores(String section, String database) {
        try {
            String mdatabase = "";
            switch (database) {
                case "hunting":
                    mdatabase = "headspluslb";
                    break;
                case "selling":
                    mdatabase = "headsplussh";
                    break;
                case "crafting":
                    mdatabase = "headspluscraft";
                    break;
                default:
                    mdatabase = database;
                    break;
            }
            LinkedHashMap<OfflinePlayer, Integer> hs = new LinkedHashMap<>();
            ResultSet rs = query(OperationType.SELECT_ORDER,  "uuid", section, mdatabase);
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    hs.put(player, rs.getInt(section));
                } catch (Exception ignored) {
                }
            }
            if (hs.isEmpty()) return hs;
            hs = DataManager.sortHashMapByValues(hs);
            LeaderboardsCache.init(database + "_" + section, hs);
            return hs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getScore(OfflinePlayer player, String section, String database) {
        try {
            String mdatabase = "";
            switch (database) {
                case "hunting":
                    mdatabase = "headspluslb";
                    break;
                case "selling":
                    mdatabase = "headsplussh";
                    break;
                case "crafting":
                    mdatabase = "headspluscraft";
                    break;
                default:
                    mdatabase = database;
                    break;
            }
            if (mdatabase.isEmpty()) return -1;
            ResultSet rs = query(OperationType.SELECT,  section, mdatabase, "uuid", player.getUniqueId().toString());
            if (rs.next()) {
                return rs.getInt(section);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static boolean doesPlayerExist(String database, String uuid) {
        try {
            return query(OperationType.SELECT_COUNT, database, "uuid", uuid).next();
        } catch (SQLException e) {
            return false;
        }
    }

    private static void addPlayer(String database, String uuid) {
        try {
            update(OperationType.INSERT_INTO, database, "uuid", uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void update(OperationType type, String... args) throws SQLException {
        prepareStatement(type, args).executeUpdate();
    }

    private static ResultSet query(OperationType type, String... args) throws SQLException {
        return prepareStatement(type, args).executeQuery();
    }

    private static PreparedStatement prepareStatement(OperationType type, String... args) throws SQLException {
        return connection.prepareStatement(String.format(type.syntax, args));
    }

    private enum OperationType {
        SELECT("SELECT %1$s, total FROM %2$s WHERE %3$s='%4$s'"),
        INSERT_TABLE("INSERT TABLE %1$s ADD COLUMN `%2$s` VARCHAR(45)"),
        INSERT_INTO("INSERT INTO %1$s (`%2$s`) VALUES ('%3$s')"),
        UPDATE("UPDATE %1$s SET %2$s='%3$s', total=%4$s WHERE %5$s='%6$s'"),
        CREATE("CREATE TABLE IF NOT EXISTS %1$s"),
        ALTER("ALTER TABLE %1$s ADD COLUMN %2$s VARCHAR(45)"),
        SELECT_ORDER("SELECT %1$s, %2$s FROM %3$s ORDER BY `id`"),
        SELECT_COUNT("SELECT 1 FROM %1$s WHERE `%2$s`='%3$s'");

        String syntax;

        OperationType(String syntax) {
            this.syntax = syntax;
        }


    }
}
