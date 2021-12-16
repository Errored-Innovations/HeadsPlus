package io.github.thatsmusic99.headsplus.sql;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FavouriteHeadsSQLManager extends SQLManager {

    private static FavouriteHeadsSQLManager instance;

    public FavouriteHeadsSQLManager() {
        instance = this;
        createTable();
        transferOldData();
    }

    public static FavouriteHeadsSQLManager get() {
        return instance;
    }

    @Override
    public void createTable() {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS headsplus_fav_heads " +
                            "(user_id INT NOT NULL," +
                            "head VARCHAR(256) NOT NULL," +
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
        File favHeadsFile = new File(storageFolder, "favourites.json");
        if (!favHeadsFile.exists()) return;
        try (FileInputStream file = new FileInputStream(favHeadsFile)) {
            // Parse the JSON
            JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(file));
            for (Object obj : json.keySet()) {
                String uuid = (String) obj;
                JSONArray heads = (JSONArray) json.get(uuid);
                heads.forEach(head -> addHead(UUID.fromString(uuid), (String) head));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        favHeadsFile.delete();
    }

    public CompletableFuture<Void> addHead(UUID uuid, String head) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO headsplus_fav_heads (user_id, head) VALUES (?, ?)");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, head);

                executeUpdate(statement);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public CompletableFuture<Void> removeHead(UUID uuid, String head) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = implementConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM headsplus_fav_heads WHERE user_id = ? AND head = ?");
                statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
                statement.setString(2, head);

                executeUpdate(statement);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, HeadsPlus.async);
    }

    public List<String> getFavouriteHeads(UUID uuid) {
        try (Connection connection = implementConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT head FROM headsplus_fav_heads WHERE user_id = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            ResultSet set = executeQuery(statement);
            List<String> heads = new ArrayList<>();
            while (set.next()) {
                heads.add(set.getString("head"));
            }
            return heads;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }
}
