package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.util.Counter;
import org.jetbrains.annotations.NotNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class StatisticsSQLManager extends SQLManager {

    private static StatisticsSQLManager instance;

    public StatisticsSQLManager(Connection connection) throws SQLException, ExecutionException, InterruptedException {
        instance = this;
        createTable(connection);
        transferOldData(connection);
    }

    public static StatisticsSQLManager get() {
        return instance;
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS headsplus_stats " +
                        "(user_id INTEGER NOT NULL," +
                        "collection_type VARCHAR(32) NOT NULL," +
                        "head VARCHAR(256) NOT NULL," +
                        "metadata VARCHAR(256) NOT NULL," +
                        "count INT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES headsplus_players(id))"
        );

        statement.executeUpdate();
    }

    @Override
    public void transferOldData(Connection connection) throws SQLException, ExecutionException, InterruptedException {
        File storageFolder = new File(HeadsPlus.get().getDataFolder(), "storage");
        if (storageFolder.exists()) {

            File playerInfo = new File(storageFolder, "playerinfo.json");
            if (playerInfo.exists()) {
                try (FileReader reader = new FileReader(playerInfo)) {
                    JSONObject core = (JSONObject) new JSONParser().parse(reader);

                    PreparedStatement statement = connection.prepareStatement("INSERT INTO headsplus_stats (user_id, collection_type," +
                            " head, metadata, count) VALUES (?, ?, ?, ?, ?)");

                    for (Object uuidObj : core.keySet()) {
                        if (uuidObj.equals("server-total")) continue;
                        JSONObject playerObj = (JSONObject) core.get(uuidObj);
                        UUID uuid;
                        try {
                            uuid = UUID.fromString((String) uuidObj);
                        } catch (IllegalArgumentException ex) {
                            HeadsPlus.get().getLogger().severe("Failed to transfer stats data for " + uuidObj + " - invalid UUID");
                            continue;
                        }

                        JSONObject huntingObj = (JSONObject) playerObj.get("hunting");
                        if (huntingObj != null) {
                            for (Object mobObj : huntingObj.keySet()) {
                                if (mobObj.equals("total")) continue;
                                ConfigSection defaultSection = ConfigMobs.get().getConfigSection(mobObj + ".default");
                                String head = "";
                                if (defaultSection != null && !defaultSection.getKeys(false).isEmpty()) {
                                    head = defaultSection.getKeys(false).get(0);
                                }
                                int total = Integer.parseInt(String.valueOf(huntingObj.get(mobObj)));

                                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid, connection));
                                statement.setString(2, "HUNTING");
                                statement.setString(3, head);
                                statement.setString(4, "entity=" + mobObj);
                                statement.setInt(5, total);

                                statement.addBatch();
                            }
                        }

                        JSONObject craftingObj = (JSONObject) playerObj.get("crafting");
                        if (craftingObj != null) {
                            for (Object mobObj : craftingObj.keySet()) {
                                if (mobObj.equals("total")) continue;
                                ConfigSection defaultSection = ConfigMobs.get().getConfigSection(mobObj + ".default");
                                String head = "";
                                if (defaultSection != null && !defaultSection.getKeys(false).isEmpty()) {
                                    head = defaultSection.getKeys(false).get(0);
                                }
                                int total = Integer.parseInt(String.valueOf(craftingObj.get(mobObj)));

                                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid, connection));
                                statement.setString(2, "CRAFTING");
                                statement.setString(3, head);
                                statement.setString(4, "mob=" + mobObj);
                                statement.setInt(5, total);

                                statement.addBatch();
                            }
                        }
                    }

                    statement.executeBatch();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException ignored) {
                }
                playerInfo.renameTo(new File(storageFolder, "playerinfo-backup.json"));
            }
        }

        // Figure out if there's been a fault with the database where entries have been duplicated,
        // Rename it to a backup and update it that way
        PreparedStatement expectedCountQuery = connection.prepareStatement(
                "SELECT user_id FROM headsplus_stats GROUP BY user_id, metadata, collection_type, head");
        ResultSet result = expectedCountQuery.executeQuery();
        int expectedCount = getRowCount(result);
        result.close();

        PreparedStatement actualCountQuery = connection.prepareStatement("SELECT user_id, collection_type, head, " +
                "metadata, count FROM headsplus_stats");
        ResultSet actualResult = actualCountQuery.executeQuery();
        int actualCount = getRowCount(actualResult);
        actualResult.close();

        HeadsPlus.get().getLogger().info("Expected count: " + expectedCount + ", actual: " + actualCount);

        // If the results are botched
        if (expectedCount != actualCount) {

            HeadsPlus.get().getLogger().warning("Database fault found in headsplus_stats - we'll attempt to fix it now.");
            HeadsPlus.get().getLogger().warning("If things don't look right after the update, old data will be stored in headsplus_stats_old, and you can inform the developer.");

            // First, change the name of the table and re-create the actual one
            PreparedStatement updateTableName = connection.prepareStatement("ALTER TABLE headsplus_stats RENAME TO headsplus_stats_old");
            updateTableName.executeUpdate();
            createTable(connection);

            Counter<StatsEntry> statCounter = new Counter<>();

            HeadsPlus.get().getLogger().warning("Starting transfer...");
            long startingTime = System.currentTimeMillis();

            PreparedStatement dataQuery = connection.prepareStatement("SELECT user_id, collection_type, head, " +
                    "metadata, count FROM headsplus_stats_old");
            ResultSet dataResult = dataQuery.executeQuery();

            while (dataResult.next()) {
                int userId = dataResult.getInt("user_id");
                CollectionType type = CollectionType.getType(dataResult.getString("collection_type"));
                String head = dataResult.getString("head");
                String metadata = dataResult.getString("metadata");
                int count = dataResult.getInt("count");

                StatsEntry entry = new StatsEntry(userId, type, head, metadata);

                // If the entry has not been counted yet, then it's the first time we're witnessing this value
                // The first time
                if (!statCounter.containsKey(entry)) {
                    statCounter.add(entry, count);
                } else {

                    // If it has been counted though, then only increment by 1
                    // We can't use the actual value because if an updated dev build has been used to fix it, that value
                    // is also updated.
                    statCounter.increment(entry);
                }
            }

            HeadsPlus.get().getLogger().warning("Saving values... (" + statCounter.keySet().size() + " entries) - please do not restart the server, this might take a while!");
            dataResult.close();

            // Perform a batch update to update everything efficiently
            PreparedStatement correctValues = connection.prepareStatement("INSERT INTO headsplus_stats VALUES (?, ?, ?, ?, ?)");


            connection.setAutoCommit(false); // This makes each thing go individually, lmao
            int count = 0;
            for (StatsEntry entry : statCounter.keySet()) {

                correctValues.setInt(1, entry.userId);
                correctValues.setString(2, entry.type.name());
                correctValues.setString(3, entry.head);
                correctValues.setString(4, entry.metadata);
                correctValues.setInt(5, statCounter.get(entry));
                correctValues.addBatch();
                count++;

                if (count % 100 == 0) {
                    correctValues.executeBatch();
                    correctValues.clearBatch();
                }
            }

            correctValues.executeBatch();
            connection.commit();

            long time = System.currentTimeMillis() - startingTime;
            HeadsPlus.get().getLogger().warning("Done (" + time + "ms)! Sorry for the stupid coding error B)");
        }
    }

    private int getRowCount(ResultSet set) throws SQLException {
        int i = 0;
        while (set.next()) {
            i++;
        }
        return i;
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid, connection));
            statement.setString(2, type.name());

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt(1);
        }, async, "get statistic for " + uuid + " in " + type.name());
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type, String head, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ? AND head = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid, connection));
            statement.setString(2, type.name());
            statement.setString(3, head);

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt(1);
        }, async, "get stat " + type.name() + " for head " + head + " and user " + uuid.toString());
    }

    public CompletableFuture<Integer> getStatMeta(UUID uuid, CollectionType type, String metadata, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ? AND metadata LIKE ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid, connection));
            statement.setString(2, type.name());
            statement.setString(3, "%" + metadata + "%");

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt(1);
        }, async, "get stat " + type.name() + " with metadata " + metadata + " for user " + uuid);
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type, String head, String metadata, boolean async) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ? AND head = ? AND metadata LIKE ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid, connection));
            statement.setString(2, type.name());
            statement.setString(3, head);
            statement.setString(4, "%" + metadata + "%");

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt(1);
        }, async, "get stat " + type.name() + " with metadata " + metadata + " for head " + head + " and user " + uuid);
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal() {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players " +
                            "WHERE headsplus_stats.user_id = headsplus_players.id " +
                            "GROUP BY headsplus_stats.user_id ORDER BY total DESC");

            ResultSet set = statement.executeQuery();
            List<LeaderboardEntry> leaderboard = new ArrayList<>();
            while (set.next()) {
                leaderboard.add(new LeaderboardEntry(set.getString("username"), set.getInt("total")));
            }
            return leaderboard;
        }, true, "get leaderboard total");
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal(
            CollectionType type,
            boolean async
    ) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players " +
                            "WHERE headsplus_stats.user_id = headsplus_players.id AND collection_type = ?" +
                            "AND headsplus_stats.user_id = headsplus_players.id " +
                            "GROUP BY headsplus_stats.user_id ORDER BY total DESC");

            statement.setString(1, type.name());

            ResultSet set = statement.executeQuery();
            List<LeaderboardEntry> leaderboard = new ArrayList<>();
            while (set.next()) {
                leaderboard.add(new LeaderboardEntry(set.getString("username"), set.getInt("total")));
            }
            return leaderboard;
        }, async, "get leaderboard total for " + type.name());
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal(
            @NotNull CollectionType type,
            @NotNull String head,
            boolean async
    ) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players" +
                            " WHERE collection_type = ? AND head = ? " +
                            "AND headsplus_stats.user_id = headsplus_players.id " +
                            "GROUP BY headsplus_stats.user_id ORDER BY total DESC");

            statement.setString(1, type.name());
            statement.setString(2, head);

            ResultSet set = statement.executeQuery();
            List<LeaderboardEntry> leaderboard = new ArrayList<>();
            while (set.next()) {
                leaderboard.add(new LeaderboardEntry(set.getString("username"), set.getInt("total")));
            }
            return leaderboard;
        }, async, "get leaderboard total for " + type.name() + " and head " + head);
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotalMetadata(
            @NotNull CollectionType type,
            @NotNull String metadata,
            boolean async
    ) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players " +
                            "WHERE collection_type = ? AND metadata LIKE ? " +
                            "AND headsplus_stats.user_id = headsplus_players.id " +
                            "GROUP BY headsplus_stats.user_id ORDER BY total DESC");

            statement.setString(1, type.name());
            statement.setString(2, "%" + metadata + "%");

            ResultSet set = statement.executeQuery();
            List<LeaderboardEntry> leaderboard = new ArrayList<>();
            while (set.next()) {
                leaderboard.add(new LeaderboardEntry(set.getString("username"), set.getInt("total")));
            }
            return leaderboard;
        }, async, "get leaderboard total for " + type.name() + " and metadata " + metadata);
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal(
            @NotNull CollectionType type,
            @NotNull String head,
            @NotNull String metadata,
            boolean async
    ) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players " +
                            "WHERE collection_type = ? AND head = ? AND metadata LIKE ? " +
                            "AND headsplus_stats.user_id = headsplus_players.id " +
                            "GROUP BY headsplus_stats.user_id ORDER BY total DESC");

            statement.setString(1, type.name());
            statement.setString(2, head);
            statement.setString(3, "%" + metadata + "%");

            ResultSet set = statement.executeQuery();
            List<LeaderboardEntry> leaderboard = new ArrayList<>();
            while (set.next()) {
                leaderboard.add(new LeaderboardEntry(set.getString("username"), set.getInt("total")));
            }
            return leaderboard;
        }, async, "get leaderboard total for " + type.name() + ", head " + head + " and metadata " + metadata);
    }

    private void addToTotal(Connection connection, int id, CollectionType type, String head, String metadata, int amount) throws SQLException {

        boolean exists;
        try (PreparedStatement checkStatement = connection.prepareStatement("SELECT count FROM headsplus_stats WHERE " +
                "user_id = ? AND collection_type = ? AND head = ? AND metadata = ?")) {
            checkStatement.setInt(1, id);
            checkStatement.setString(2, type.name());
            checkStatement.setString(3, head);
            checkStatement.setString(4, metadata);

            try (final ResultSet set = checkStatement.executeQuery()) {
                exists = set.next();
            }
        }

        // Then use the statement appropriate
        PreparedStatement updateStatement;
        if (exists) {
            updateStatement = connection.prepareStatement("UPDATE headsplus_stats SET count = count + ? WHERE " +
                    "user_id = ? AND collection_type = ? AND head = ? AND metadata = ?");
            updateStatement.setInt(1, amount);
            updateStatement.setInt(2, id);
            updateStatement.setString(3, type.name());
            updateStatement.setString(4, head);
            updateStatement.setString(5, metadata);
        } else {
            updateStatement = connection.prepareStatement("INSERT INTO headsplus_stats (user_id, collection_type," +
                    " head, metadata, count) VALUES (?, ?, ?, ?, ?)");
            updateStatement.setInt(1, id);
            updateStatement.setString(2, type.name());
            updateStatement.setString(3, head);
            updateStatement.setString(4, metadata);
            updateStatement.setInt(5, amount);
        }
        updateStatement.executeUpdate();
    }

    public void addToTotal(UUID uuid, CollectionType type, String head, String metadata, int amount, boolean async) {

        createConnection(connection -> {
            final int id = PlayerSQLManager.get().getUserID(uuid, connection);

            addToTotal(connection, id, type, head, metadata, amount);
            return null;
        }, async, "add to statistics total " + type.name() + ", metadata " + metadata + ", head " + head + " and user " + uuid);
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

    private class StatsEntry {

        private final int userId;
        private final CollectionType type;
        private final String head;
        private final String metadata;

        public StatsEntry(int userId, CollectionType type, String head, String metadata) {
            this.userId = userId;
            this.type = type;
            this.head = head;
            this.metadata = metadata;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof StatsEntry)) return false;
            StatsEntry that = (StatsEntry) object;
            return userId == that.userId && Objects.equals(head, that.head) && Objects.equals(metadata, that.metadata) && type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, type, head, metadata);
        }
    }
}
