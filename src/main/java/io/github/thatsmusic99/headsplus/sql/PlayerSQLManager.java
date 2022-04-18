package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerSQLManager extends SQLManager {

    private static PlayerSQLManager instance;

    public PlayerSQLManager(Connection connection) throws SQLException {
        instance = this;
        createTable(connection);
        transferOldData(connection);
    }

    public static PlayerSQLManager get() {
        return instance;
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS headsplus_players " +
                        "(id INTEGER PRIMARY KEY " + getStupidAutoIncrementThing() + ", " +
                        "uuid VARCHAR(256) NOT NULL, " +
                        "username VARCHAR(32) NOT NULL, " +
                        "xp BIGINT NOT NULL," +
                        "level INT NOT NULL," +
                        "locale VARCHAR(16)," +
                        "last_joined BIGINT NOT NULL)"
        );

        statement.executeUpdate();
    }

    @Override
    public void transferOldData(Connection connection) throws SQLException {
        File storageFolder = new File(HeadsPlus.get().getDataFolder(), "storage");
        if (!storageFolder.exists()) return;
        File playerInfo = new File(storageFolder, "playerinfo.json");
        if (!playerInfo.exists()) return;
        try (FileReader reader = new FileReader(playerInfo)) {
            JSONObject core = (JSONObject) new JSONParser().parse(reader);

            PreparedStatement statement = connection.prepareStatement("INSERT INTO headsplus_players " +
                    "(uuid, username, xp, level, locale, last_joined) VALUES (?, ?, ?, ?, ?, ?)");

            for (Object uuidObj : core.keySet()) {
                if (uuidObj.equals("server-total")) continue;
                JSONObject playerObj = (JSONObject) core.get(uuidObj);
                long xp = 0;
                if (playerObj.containsKey("xp")) {
                    xp = (long) playerObj.get("xp");
                }
                String levelStr = (String) playerObj.get("level");
                int levelIndex = LevelsManager.get().getLevels().indexOf(levelStr);
                if (levelIndex == -1) levelIndex = 0;

                UUID uuid;
                try {
                     uuid = UUID.fromString((String) uuidObj);
                } catch (IllegalArgumentException ex) {
                    HeadsPlus.get().getLogger().severe("Failed to transfer data for " + uuidObj + " - invalid UUID");
                    continue;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                try {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, player.getName() == null ? "Unknown" : player.getName());
                    statement.setLong(3, xp);
                    statement.setInt(4, levelIndex);
                    statement.setString(5, getLocale(playerObj));
                    statement.setLong(6, player.getLastLogin());
                } catch (NoSuchMethodError ex) {
                    statement.setLong(6, -1);
                }

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ignored) {
        }
    }

    private String getLocale(JSONObject playerObj) {
        String rawLocale = (String) playerObj.get("locale");
        if (rawLocale == null) return null;
        String[] parts = rawLocale.split(":");
        if (parts.length == 1) return null;
        if (parts[1].equals("true")) return parts[0];
        return null;
    }

    private CompletableFuture<Void> updateUsername(UUID uuid, String newName) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE headsplus_players SET username = ? WHERE uuid = ?");
            statement.setString(1, newName);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            return null;
        }, true, "update username " + newName + " for " + uuid);
    }

    private CompletableFuture<Void> updateUUID(UUID newUuid, String name) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE headsplus_players SET uuid = ? WHERE username = ?");
            statement.setString(1, newUuid.toString());
            statement.setString(2, name);
            statement.executeUpdate();
            return null;
        }, true, "update uuid " + newUuid + " for " + name);
    }

    public CompletableFuture<Void> checkPlayer(UUID uuid, String name) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid, username FROM headsplus_players WHERE username = ? OR uuid = ?");
            statement.setString(1, name);
            statement.setString(2, uuid.toString());
            ResultSet results = statement.executeQuery();
            if (!results.next()) {
                connection.close();
                results.close();
                insertPlayer(uuid, name, 0, 0, System.currentTimeMillis());
                return null;
            }

            if (!results.getString("username").equals(name)) {
                connection.close();
                updateUsername(uuid, name).join();
            } else if (!results.getString("uuid").equals(uuid.toString())) {
                connection.close();
                updateUUID(uuid, name).join();
            }
            return null;
        }, true, "checking player " + uuid + ", " + name);
    }

    public CompletableFuture<HPPlayer> loadPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> new HPPlayer(uuid), HeadsPlus.async);
    }

    public CompletableFuture<HPPlayer> loadPlayer(String name) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM headsplus_players WHERE " +
                    "username = ?");
            statement.setString(1, name);

            ResultSet set = statement.executeQuery();
            if (!set.next()) return null;
            UUID uuid = UUID.fromString(set.getString("uuid"));
            return new HPPlayer(uuid);
        }, true, "load player " + name);
    }

    private void insertPlayer(UUID uuid, String name, long xp, int level, long timestamp) {
        createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO headsplus_players (uuid, username, xp, level, last_joined) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.setLong(3, xp);
            statement.setInt(4, level);
            statement.setLong(5, timestamp);

            statement.executeUpdate();
            return null;
        }, true, "insert player " + uuid + " with data " + name + ", " + xp + " XP, level" + level + " and timestamp "
                + timestamp);
    }

    public CompletableFuture<Void> setXP(UUID uuid, long xp) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET xp = ? WHERE " +
                    "uuid = ?");
            statement.setLong(1, xp);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
            return null;
        }, true, "set XP " + xp + " for " + uuid.toString());
    }

    public CompletableFuture<Long> getXP(UUID uuid, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT xp FROM headsplus_players WHERE uuid = " +
                    "?");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return (long) -1;
            return set.getLong("xp");
        }, async, "get XP for " + uuid.toString());
    }

    public CompletableFuture<Void> setLevel(UUID uuid, String level) {
        return createConnection(connection -> {
            int actualLevel = LevelsManager.get().getLevels().indexOf(level);
            PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET level = ? " +
                    "WHERE uuid = ?");
            statement.setInt(1, actualLevel);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
            return null;
        }, true, "set level " + level + " for " + uuid.toString());
    }

    public CompletableFuture<Void> setLevel(String username, String level) {
        return createConnection(connection -> {
            int actualLevel = LevelsManager.get().getLevels().indexOf(level);
            PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET level = ? " +
                    "WHERE username = ?");
            statement.setInt(1, actualLevel);
            statement.setString(2, username);

            statement.executeUpdate();
            return null;
        }, true, "set level " + level + " for " + username);
    }

    private CompletableFuture<Void> updateJoinTimestamp(UUID uuid, long timestamp) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET last_joined =" +
                    " ? WHERE uuid = ?");
            statement.setLong(1, timestamp);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
            return null;
        }, true, "update join timestamp to " + timestamp + " for " + uuid.toString());
    }

    public CompletableFuture<Integer> getLevel(UUID uuid, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT level FROM headsplus_players WHERE uuid" +
                    " = ?");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return 0;
            return set.getInt("level");
        }, async, "get level for " + uuid.toString());
    }

    public CompletableFuture<Integer> getLevel(String name, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT level FROM headsplus_players WHERE " +
                    "username" +
                    " = ?");
            statement.setString(1, name);

            ResultSet set = statement.executeQuery();
            if (!set.next()) return 0;
            return set.getInt("level");
        }, async, "get level for " + name);
    }

    public CompletableFuture<Void> setXP(String username, long xp) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET xp = ? WHERE " +
                    "username = ?");
            statement.setLong(1, xp);
            statement.setString(2, username);

            statement.executeUpdate();
            return null;
        }, true, "set XP " + xp + " for " + username);
    }

    public CompletableFuture<Void> addXP(String username, long xp) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET xp = xp + ? " +
                    "WHERE username = ?");
            statement.setLong(1, xp);
            statement.setString(2, username);

            statement.executeUpdate();
            return null;
        }, true, "add " + xp + " to " + username);
    }

    public CompletableFuture<Optional<String>> getLocale(UUID uuid) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT locale FROM headsplus_players WHERE" +
                    " uuid = ?");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return Optional.empty();
            return Optional.of(set.getString("locale"));
        }, true, "get locale for " + uuid.toString());
    }

    public CompletableFuture<Void> setLocale(String username, String locale) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET locale = ? " +
                    "WHERE username = ?");
            statement.setString(1, locale);
            statement.setString(2, username);

            statement.executeUpdate();
            return null;
        }, true, "set locale to " + locale + " for " + username);
    }

    public CompletableFuture<Long> getXP(String username, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT xp FROM headsplus_players WHERE " +
                    "username = ?");
            statement.setString(1, username);

            ResultSet set = statement.executeQuery();
            if (!set.next()) return (long) -1;
            return set.getLong("xp");
        }, async, "get XP for " + username);
    }

    protected int getUserID(UUID uuid) throws ExecutionException, InterruptedException {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM headsplus_players WHERE uuid = " +
                    "?");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt("id");
        }, false, "get user ID for " + uuid.toString()).get();
    }
}
