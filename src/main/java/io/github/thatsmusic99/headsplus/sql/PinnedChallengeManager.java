package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        transferOldData();
    }

    public static PinnedChallengeManager get() {
        return instance;
    }

    @Override
    public void createTable() {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = prepareStatement(connection,
                    "CREATE TABLE IF NOT EXISTS headsplus_pinned_challenges " +
                            "(user_id INT NOT NULL," +
                            "challenge VARCHAR(256) NOT NULL," +
                            "FOREIGN KEY (user_id) REFERENCES headsplus_players(id))"
            );
            executeUpdate(statement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void transferOldData() {
        // Checks to see if the storage folder exists
        File storageFolder = new File(HeadsPlus.get().getDataFolder(), "storage");
        if (!storageFolder.exists() || !storageFolder.isDirectory()) return;
        // Checks to see if the actual file exists
        File pinnedChallenges = new File(storageFolder, "pinned-challenges.json");
        if (!pinnedChallenges.exists()) return;
        try (FileInputStream file = new FileInputStream(pinnedChallenges)) {
            // Parse the JSON
            JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(file));
            for (Object obj : json.keySet()) {
                String uuid = (String) obj;
                JSONArray challenges = (JSONArray) json.get(uuid);
                challenges.forEach(challenge -> addChallenge(UUID.fromString(uuid), (String) challenge));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        pinnedChallenges.delete();
    }

    public List<String> getPinnedChallenges(UUID uuid) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = prepareStatement(connection,
                    "SELECT challenge FROM headsplus_pinned_challenges WHERE user_id = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            ResultSet set = executeQuery(statement);
            List<String> challenges = new ArrayList<>();
            while (set.next()) {
                challenges.add(set.getString("challenge"));
            }
            return challenges;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }

    public CompletableFuture<Void> addChallenge(UUID uuid, String challenge) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = prepareStatement(connection,
                        "INSERT INTO headsplus_pinned_challenges (user_id, challenge) VALUES (?, ?)");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, challenge);

                executeUpdate(statement);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> removeChallenge(UUID uuid, String challenge) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = prepareStatement(connection,
                        "DELETE FROM headsplus_pinned_challenges " +
                                "WHERE headsplus_players.id = ? AND challenge = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, challenge);

                executeUpdate(statement);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }
}
