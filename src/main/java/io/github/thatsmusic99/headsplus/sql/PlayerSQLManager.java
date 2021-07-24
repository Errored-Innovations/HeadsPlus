package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerSQLManager extends SQLManager {

    private static PlayerSQLManager instance;

    public PlayerSQLManager() {
        createTable();
        instance = this;
    }

    public static PlayerSQLManager get() {
        return instance;
    }

    @Override
    public void createTable() {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS headsplus_players " +
                        "(id INTEGER PRIMARY KEY " + getStupidAutoIncrementThing() + ", "+
                        "uuid VARCHAR(256) NOT NULL, " +
                        "name VARCHAR(256) NOT NULL, " +
                        "xp BIGINT NOT NULL," +
                        "level INT NOT NULL," +
                        "last_joined BIGINT NOT NULL)"
                );

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    @Override
    public void transferOldData() {
        // TODO
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
                        "SELECT uuid, name FROM headsplus_players WHERE username = ? OR uuid = ?");
                statement.setString(1, name);
                statement.setString(2, uuid.toString());
                ResultSet results = statement.executeQuery();
                if (!results.next()) {
                    insertPlayer(uuid, name, 0, 0, System.currentTimeMillis());
                    return;
                }

                if (!results.getString("username").equals(name)) {
                    updateUsername(uuid, name);
                } else if (!results.getString("uuid").equals(uuid.toString())) {
                    updateUUID(uuid, name);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    private CompletableFuture<Void> insertPlayer(UUID uuid, String name, long xp, int level, long timestamp) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO headsplus_players (uuid, name, xp, level, last_joined) VALUES (?, ?, ?, ?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, name);
                statement.setLong(3, xp);
                statement.setInt(4, level);
                statement.setLong(5, timestamp);

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    protected int getUserID(UUID uuid) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM headsplus_players WHERE uuid = ?");
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
