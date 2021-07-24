package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PinnedChallengeManager extends SQLManager {

    private static PinnedChallengeManager instance;

    public PinnedChallengeManager() {
        instance = this;
        createTable();
    }

    public static PinnedChallengeManager get() {
        return instance;
    }

    @Override
    public void createTable() {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS headsplus_pinned_challenges " +
                                "(user_id INT NOT NULL," +
                                "challenge VARCHAR(256) NOT NULL," +
                                "FOREIGN KEY (user_id) REFERENCES headsplus_players(id))"
                );

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    @Override
    public void transferOldData() {

    }

    public CompletableFuture<List<String>> getPinnedChallenges(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT challenge FROM headsplus_pinned_challenges WHERE headsplus_players.uuid = ? " +
                                "AND headsplus_players.id = user_id");
                statement.setString(1, uuid.toString());
                ResultSet set = statement.executeQuery();
                List<String> challenges = new ArrayList<>();
                while (set.next()) {
                    challenges.add(set.getString("challenge"));
                }
                return challenges;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return new ArrayList<>();
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> addChallenge(UUID uuid, String challenge) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO headsplus_pinned_challenges (user_id, challenge) VALUES (?, ?)");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, challenge);

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> removeChallenge(UUID uuid, String challenge) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM headsplus_pinned_challenges " +
                                "WHERE headsplus_players.id = ? AND challenge = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, challenge);

                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }
}
