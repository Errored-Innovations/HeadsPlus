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

public class PlayerSQLManager extends SQLManager {

    private static PlayerSQLManager instance;

    public PlayerSQLManager() {
        createTable();
        transferOldData();
        instance = this;
    }

    public static PlayerSQLManager get() {
        return instance;
    }

    @Override
    public void createTable() {
        try (Connection connection = implementConnection()) {
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
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void transferOldData() {
        File storageFolder = new File(HeadsPlus.get().getDataFolder(), "storage");
        if (!storageFolder.exists()) return;
        File playerInfo = new File(storageFolder, "playerinfo.json");
        if (!playerInfo.exists()) return;
        try (FileReader reader = new FileReader(playerInfo)) {
            JSONObject core = (JSONObject) new JSONParser().parse(reader);
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

                UUID uuid = UUID.fromString((String) uuidObj);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                try {
                    insertPlayer(uuid, player.getName() == null ? "Unknown" : player.getName(), xp, levelIndex,
                            player.getLastLogin());
                } catch (NoSuchMethodError ex) {
                    insertPlayer(uuid, player.getName() == null ? "Unknown" : player.getName(), xp, levelIndex, -1);
                }

                String rawLocale = (String) playerObj.get("locale");
                if (rawLocale == null) continue;
                String[] parts = rawLocale.split(":");
                if (parts.length == 1) continue;
                if (parts[1].equals("true")) setLocale(player.getName(), parts[0]).join();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ignored) {
        }
    }

    private CompletableFuture<Void> updateUsername(UUID uuid, String newName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE headsplus_players SET username = ? WHERE uuid = ?");
                statement.setString(1, newName);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    private CompletableFuture<Void> updateUUID(UUID newUuid, String name) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE headsplus_players SET uuid = ? WHERE username = ?");
                statement.setString(1, newUuid.toString());
                statement.setString(2, name);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> checkPlayer(UUID uuid, String name) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT uuid, username FROM headsplus_players WHERE username = ? OR uuid = ?");
                statement.setString(1, name);
                statement.setString(2, uuid.toString());
                ResultSet results = statement.executeQuery();
                if (!results.next()) {
                    connection.close();
                    insertPlayer(uuid, name, 0, 0, System.currentTimeMillis());
                    return;
                }

                if (!results.getString("username").equals(name)) {
                    connection.close();
                    updateUsername(uuid, name).join();
                } else if (!results.getString("uuid").equals(uuid.toString())) {
                    connection.close();
                    updateUUID(uuid, name).join();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            updateJoinTimestamp(uuid, System.currentTimeMillis()).join();
        }, HeadsPlus.async);
    }

    public CompletableFuture<HPPlayer> loadPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> new HPPlayer(uuid), HeadsPlus.async);
    }

    public CompletableFuture<HPPlayer> loadPlayer(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM headsplus_players WHERE " +
                        "username = ?");
                statement.setString(1, name);

                ResultSet set = statement.executeQuery();
                if (!set.next()) return null;
                UUID uuid = UUID.fromString(set.getString("uuid"));
                return new HPPlayer(uuid);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        }, HeadsPlus.async);
    }

    private void insertPlayer(UUID uuid, String name, long xp, int level, long timestamp) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO headsplus_players (uuid, username, xp, level, last_joined) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.setLong(3, xp);
            statement.setInt(4, level);
            statement.setLong(5, timestamp);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public CompletableFuture<Void> setXP(UUID uuid, long xp) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET xp = ? WHERE " +
                        "uuid = ?");
                statement.setLong(1, xp);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Long> getXP(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getXPSync(uuid), HeadsPlus.async);
    }

    public CompletableFuture<Void> setLevel(UUID uuid, String level) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                int actualLevel = LevelsManager.get().getLevels().indexOf(level);
                PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET level = ? " +
                        "WHERE uuid = ?");
                statement.setInt(1, actualLevel);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Integer> getLevel(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getLevelSync(uuid), HeadsPlus.async);
    }

    private CompletableFuture<Void> updateJoinTimestamp(UUID uuid, long timestamp) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET last_joined =" +
                        " ? WHERE uuid = ?");
                statement.setLong(1, timestamp);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Integer> getLevel(String username) {
        return CompletableFuture.supplyAsync(() -> getLevelSync(username), HeadsPlus.async);
    }

    public int getLevelSync(String username) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT level FROM headsplus_players WHERE " +
                    "username = ?");
            statement.setString(1, username);

            ResultSet set = statement.executeQuery();
            if (!set.next()) return 0;
            return set.getInt("level");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public int getLevelSync(UUID uuid) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT level FROM headsplus_players WHERE uuid" +
                    " = ?");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return 0;
            return set.getInt("level");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public CompletableFuture<Long> getXP(String username) {
        return CompletableFuture.supplyAsync(() -> getXPSync(username), HeadsPlus.async);
    }

    public CompletableFuture<Void> setXP(String username, long xp) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET xp = ? WHERE " +
                        "username = ?");
                statement.setLong(1, xp);
                statement.setString(2, username);

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> addXP(String username, long xp) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET xp = xp + ? " +
                        "WHERE username = ?");
                statement.setLong(1, xp);
                statement.setString(2, username);

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Optional<String>> getLocale(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT locale FROM headsplus_players WHERE" +
                        " uuid = ?");
                statement.setString(1, uuid.toString());

                ResultSet set = statement.executeQuery();
                if (!set.next()) return Optional.empty();
                return Optional.of(set.getString("locale"));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return Optional.empty();
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> setLocale(String username, String locale) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE headsplus_players SET locale = ? " +
                        "WHERE username = ?");
                statement.setString(1, locale);
                statement.setString(2, username);

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public long getXPSync(String username) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT xp FROM headsplus_players WHERE " +
                    "username = ?");
            statement.setString(1, username);

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getLong("xp");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public long getXPSync(UUID uuid) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT xp FROM headsplus_players WHERE uuid = " +
                    "?");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getLong("xp");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    protected int getUserID(UUID uuid) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM headsplus_players WHERE uuid = " +
                    "?");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt("id");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }
}
