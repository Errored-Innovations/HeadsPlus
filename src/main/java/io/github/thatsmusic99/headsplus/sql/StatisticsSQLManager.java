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

    private static StatisticsSQLManager instance;

    public StatisticsSQLManager() {
        instance = this;
        createTable();
    }

    public static StatisticsSQLManager get() {
        return instance;
    }

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

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                                "WHERE user_id = ? AND collection_type = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, type.name());

                ResultSet set = statement.executeQuery();
                if (!set.next()) return -1;
                return set.getInt(1);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return -1;
        }, HeadsPlus.async);
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type, String head) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                                "WHERE user_id = ? AND collection_type = ? AND head = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, type.name());
                statement.setString(3, head);

                ResultSet set = statement.executeQuery();
                if (!set.next()) return -1;
                return set.getInt(1);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return -1;
        }, HeadsPlus.async);
    }

    public CompletableFuture<Integer> getStatMeta(UUID uuid, CollectionType type, String metadata) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                                "WHERE user_id = ? AND collection_type = ? AND metadata = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, type.name());
                statement.setString(3, metadata);

                ResultSet set = statement.executeQuery();
                if (!set.next()) return -1;
                return set.getInt(1);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return -1;
        }, HeadsPlus.async);
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type, String head, String metadata) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                                "WHERE user_id = ? AND collection_type = ? AND head = ? AND metadata = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, type.name());
                statement.setString(3, head);
                statement.setString(4, metadata);

                ResultSet set = statement.executeQuery();
                if (!set.next()) return -1;
                return set.getInt(1);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return -1;
        }, HeadsPlus.async);
    }

    public int getStatSync(UUID uuid, CollectionType type) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND collection_type = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            statement.setString(2, type.name());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players ORDER BY total DESC");

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

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal(CollectionType type, String head) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players" +
                                " WHERE collection_type = ? AND head = ? ORDER BY total DESC");

                statement.setString(1, type.name());
                statement.setString(2, head);

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

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotalMetadata(CollectionType type, String metadata) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players" +
                                " WHERE collection_type = ? AND metadata = ? ORDER BY total DESC");

                statement.setString(1, type.name());
                statement.setString(2, metadata);

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

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal(CollectionType type, String head, String metadata) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players" +
                                " WHERE collection_type = ? AND head = ? AND metadata = ? ORDER BY total DESC");

                statement.setString(1, type.name());
                statement.setString(2, head);
                statement.setString(3, metadata);

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

    public CompletableFuture<Void> addToTotal(UUID uuid, CollectionType type, String head, String metadata, int amount) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                // Check if the entry has been added
                PreparedStatement checkStatement = connection.prepareStatement("SELECT amount FROM headsplus_stats WHERE " +
                        "user_id = ? AND collection_type = ? AND head = ? AND metadata = ?");
                int id = PlayerSQLManager.get().getUserID(uuid);
                checkStatement.setInt(1, id);
                checkStatement.setString(2, type.name());
                checkStatement.setString(3, head);
                checkStatement.setString(4, metadata);

                ResultSet set = checkStatement.executeQuery();
                // Then use the statement appropriate
                PreparedStatement updateStatement;
                if (!set.next()) {
                    updateStatement = connection.prepareStatement("INSERT INTO headsplus_stats (user_id, collection_type, head, metadata, amount) VALUES (?, ?, ?, ?, ?)");
                    updateStatement.setInt(1, id);
                    updateStatement.setString(2, type.name());
                    updateStatement.setString(3, head);
                    updateStatement.setString(4, metadata);
                    updateStatement.setInt(5, amount);
                } else {
                    updateStatement = connection.prepareStatement("UPDATE headsplus_stats SET amount = amount + ? WHERE " +
                            "user_id = ? AND collection_type = ? AND head = ? AND metadata = ?");
                    updateStatement.setInt(1, amount);
                    updateStatement.setInt(2, id);
                    updateStatement.setString(3, type.name());
                    updateStatement.setString(4, head);
                    updateStatement.setString(5, metadata);
                }

                updateStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public enum CollectionType {
        HUNTING,
        CRAFTING;

        public static CollectionType getType(String str) {
            try {
                return CollectionType.valueOf(str);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    public static class LeaderboardEntry {
        private final String player;
        private final int sum;

        public LeaderboardEntry(String player, int sum) {
            this.player = player;
            this.sum = sum;
        }

        public String getPlayer() {
            return player;
        }

        public int getSum() {
            return sum;
        }
    }
}
