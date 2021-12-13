package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatisticsSQLManager extends SQLManager {

    private static StatisticsSQLManager instance;

    public StatisticsSQLManager() {
        instance = this;
        createTable();
        transferOldData();
    }

    public static StatisticsSQLManager get() {
        return instance;
    }

    @Override
    public void createTable() {
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
                UUID uuid = UUID.fromString((String) uuidObj);

                JSONObject huntingObj = (JSONObject) playerObj.get("hunting");
                if (huntingObj != null) {
                    for (Object mobObj : huntingObj.keySet()) {
                        if (mobObj.equals("total")) continue;
                        HeadsPlus.debug((String) mobObj);
                        ConfigSection defaultSection = ConfigMobs.get().getConfigSection(mobObj + ".default");
                        String head = "";
                        if (defaultSection != null && defaultSection.getKeys(false).size() != 0) {
                            head = defaultSection.getKeys(false).get(0);
                        }
                        int total = Integer.parseInt(String.valueOf(huntingObj.get(mobObj)));
                        addToTotalSync(uuid, CollectionType.HUNTING, head, "mob=" + mobObj, total);
                    }
                }

                JSONObject craftingObj = (JSONObject) playerObj.get("crafting");
                if (craftingObj != null) {
                    for (Object mobObj : craftingObj.keySet()) {
                        if (mobObj.equals("total")) continue;
                        ConfigSection defaultSection = ConfigMobs.get().getConfigSection(mobObj + ".default");
                        String head = "";
                        if (defaultSection != null && defaultSection.getKeys(false).size() != 0) {
                            head = defaultSection.getKeys(false).get(0);
                        }
                        int total = Integer.parseInt(String.valueOf(craftingObj.get(mobObj)));
                        addToTotalSync(uuid, CollectionType.CRAFTING, head, "mob=" + mobObj, total);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ignored) {
        }
        playerInfo.renameTo(new File(storageFolder, "playerinfo-backup.json"));
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type) {
        return CompletableFuture.supplyAsync(() -> getStatSync(uuid, type), HeadsPlus.async);
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type, String head) {
        return CompletableFuture.supplyAsync(() -> getStatSync(uuid, type, head), HeadsPlus.async);
    }

    public CompletableFuture<Integer> getStatMeta(UUID uuid, CollectionType type, String metadata) {
        return CompletableFuture.supplyAsync(() -> getStatMetaSync(uuid, type, metadata), HeadsPlus.async);
    }

    public CompletableFuture<Integer> getStat(UUID uuid, CollectionType type, String head, String metadata) {
        return CompletableFuture.supplyAsync(() -> getStatSync(uuid, type, head, metadata), HeadsPlus.async);
    }

    public int getStatSync(UUID uuid, CollectionType type) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ?");
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

    public int getStatSync(UUID uuid, CollectionType type, String head) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ? AND head = ?");
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
    }

    public int getStatMetaSync(UUID uuid, CollectionType type, String metadata) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ? AND metadata LIKE ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            statement.setString(2, type.name());
            statement.setString(3, "%" + metadata + "%");

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public int getStatSync(UUID uuid, CollectionType type, String head, String metadata) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(count), username FROM headsplus_stats, headsplus_players " +
                            "WHERE user_id = ? AND id = user_id AND collection_type = ? AND head = ? AND metadata LIKE ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            statement.setString(2, type.name());
            statement.setString(3, head);
            statement.setString(4, "%" + metadata + "%");

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
                        "SELECT SUM(count) as total, username FROM headsplus_stats, headsplus_players " +
                                "WHERE headsplus_stats.user_id = headsplus_players.id " +
                                "GROUP BY headsplus_stats.user_id ORDER BY total DESC");

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
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return new ArrayList<>();
        }, HeadsPlus.async);
    }

    public CompletableFuture<List<LeaderboardEntry>> getLeaderboardTotal(CollectionType type, String head,
                                                                         String metadata) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = implementConnection()) {
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
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return new ArrayList<>();
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> addToTotal(UUID uuid, CollectionType type, String head, String metadata,
                                              int amount) {
        return CompletableFuture.runAsync(() -> addToTotalSync(uuid, type, head, metadata, amount), HeadsPlus.async);
    }

    private void addToTotalSync(UUID uuid, CollectionType type, String head, String metadata, int amount) {
        try (Connection connection = implementConnection()) {
            // Check if the entry has been added
            PreparedStatement checkStatement = connection.prepareStatement("SELECT count FROM headsplus_stats WHERE " +
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
                updateStatement = connection.prepareStatement("INSERT INTO headsplus_stats (user_id, collection_type," +
                        " head, metadata, count) VALUES (?, ?, ?, ?, ?)");
                updateStatement.setInt(1, id);
                updateStatement.setString(2, type.name());
                updateStatement.setString(3, head);
                updateStatement.setString(4, metadata);
                updateStatement.setInt(5, amount);
            } else {
                updateStatement = connection.prepareStatement("UPDATE headsplus_stats SET count = count + ? WHERE " +
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
