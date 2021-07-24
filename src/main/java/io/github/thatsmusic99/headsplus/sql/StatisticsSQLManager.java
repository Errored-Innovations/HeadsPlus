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

public class StatisticsSQLManager extends SQLManager {

    @Override
    public void createTable() {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS headsplus_stats " +
                                "(user_id INT NOT NULL," +
                                "collection_type VARCHAR(32) NOT NULL," +
                                "head VARCHAR(256) NOT NULL," +
                                "metadata VARCHAR(256) NOT NULL," +
                                "count INT NOT NULL)"
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

    public CompletableFuture<LeaderboardEntry> getStat(UUID uuid, CollectionType type) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                                "WHERE user_id = ? AND collection_type = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, type.name());

                ResultSet set = statement.executeQuery();
                if (!set.next()) return new LeaderboardEntry(uuid.toString(), -1);
                return new LeaderboardEntry(set.getString("username"), set.getInt(1));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return new LeaderboardEntry(uuid.toString(), -1);
        }, HeadsPlus.async);
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal(CollectionType type) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players" +
                                " WHERE collection_type = ? ORDER BY total DESC");

                statement.setString(1, type.name());

                ResultSet set = statement.executeQuery();
                List<LeaderboardEntry> leaderboard = new ArrayList<>();
                while (set.next()) {
                    leaderboard.add(new LeaderboardEntry(set.getString("username"), set.getInt("total")));
                }
                return leaderboard;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return new ArrayList<>();
        }, HeadsPlus.async);
    }

    public enum CollectionType {
        HUNTING,
        CRAFTING
    }

    public class LeaderboardEntry {
        private String player;
        private int sum;

        public LeaderboardEntry(String player, int sum) {
            this.player = player;
            this.sum = sum;
        }
    }
}
