package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;
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

public class ChallengeSQLManager extends SQLManager {

    private static ChallengeSQLManager instance;

    public ChallengeSQLManager() {
        instance = this;
        createTable();
        transferOldData();
    }

    public static ChallengeSQLManager get() {
        return instance;
    }

    @Override
    public void createTable() {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS headsplus_challenges " +
                                "(user_id INT NOT NULL," +
                                "challenge VARCHAR(256) NOT NULL," +
                                "count INT NOT NULL," +
                                "last_completion_time BIGINT NOT NULL," +
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
        File storageFolder = new File(HeadsPlus.get().getDataFolder(), "storage");
        if (!storageFolder.exists()) return;
        File playerInfo = new File(storageFolder, "playerinfo.json");
        if (!playerInfo.exists()) return;
        try (FileReader reader = new FileReader(playerInfo)) {
            JSONObject core = (JSONObject) new JSONParser().parse(reader);
            for (Object uuidObj : core.keySet()) {
                JSONObject playerObj = (JSONObject) core.get(uuidObj);
                UUID uuid = UUID.fromString((String) uuidObj);

                List<String> completedChallenges = (List<String>) playerObj.get("completed-challenges");
                if (completedChallenges == null) continue;
                completedChallenges.forEach(challenge -> completeChallengeSync(uuid, challenge));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ignored) {}
    }

    public CompletableFuture<Integer> getTotalChallengesComplete(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getTotalChallengesCompleteSync(uuid), HeadsPlus.async);
    }

    public CompletableFuture<Void> completeChallenge(UUID uuid, String challenge) {
        return CompletableFuture.runAsync(() -> completeChallengeSync(uuid, challenge), HeadsPlus.async);
    }

    public int getTotalChallengesCompleteSync(UUID uuid) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(count) FROM headsplus_challenges WHERE user_id = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));

            ResultSet set = statement.executeQuery();
            if (!set.next()) return -1;
            return set.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    private void completeChallengeSync(UUID uuid, String challenge) {
        try (Connection connection = implementConnection()) {
            PreparedStatement checkStatement = connection.prepareStatement("SELECT count FROM headsplus_challenges WHERE user_id = ? AND challenge = ?");
            checkStatement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            checkStatement.setString(2, challenge);

            ResultSet set = checkStatement.executeQuery();
            PreparedStatement updateStatement;
            if (!set.next()) {
                updateStatement = connection.prepareStatement("INSERT INTO headsplus_challenges (last_completion_time, user_id, challenge, count) VALUES (?, ?, ?, 1)");
            } else {
                updateStatement = connection.prepareStatement("UPDATE headsplus_challenges SET count = count + 1, last_completion_time = ? WHERE user_id = ? AND challenge = ?");
            }
            updateStatement.setLong(1, System.currentTimeMillis());
            updateStatement.setInt(2, PlayerSQLManager.get().getUserID(uuid));
            updateStatement.setString(3, challenge);

            updateStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public List<String> getCompleteChallenges(UUID uuid) {
        List<String> challenges = new ArrayList<>();
        try (Connection connection = implementConnection()) {
            PreparedStatement checkStatement = connection.prepareStatement("SELECT challenge FROM headsplus_challenges WHERE user_id = ?");
            checkStatement.setInt(1, PlayerSQLManager.get().getUserID(uuid));

            ResultSet set = checkStatement.executeQuery();
            while (set.next()) {
                challenges.add(set.getString(1));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return challenges;
    }
}
