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
import java.util.concurrent.ExecutionException;

public class FavouriteHeadsSQLManager extends SQLManager {

    private static FavouriteHeadsSQLManager instance;

    public FavouriteHeadsSQLManager(Connection connection) throws SQLException {
        instance = this;
        createTable(connection);
        transferOldData(connection);
    }

    public static FavouriteHeadsSQLManager get() {
        return instance;
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS headsplus_fav_heads " +
                        "(user_id INT NOT NULL," +
                        "head VARCHAR(256) NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES headsplus_players(id))"
        );

        statement.executeUpdate();
    }

    @Override
    public void transferOldData(Connection connection) {
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
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO headsplus_fav_heads (user_id, head) VALUES (?, ?)");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            statement.setString(2, head);

            statement.executeUpdate();
            return null;
        }, true, "add favourite head " + head + " for " + uuid);
    }

    public CompletableFuture<Void> removeHead(UUID uuid, String head) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM headsplus_fav_heads WHERE user_id = ? AND head = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            statement.setString(2, head);

            statement.executeUpdate();
            return null;
        }, true, "remove favourite head " + head + " for " + uuid);
    }

    public CompletableFuture<List<String>> getFavouriteHeads(UUID uuid) {
        return createConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT head FROM headsplus_fav_heads WHERE user_id = ?");
            statement.setInt(1, PlayerSQLManager.get().getUserID(uuid));
            ResultSet set = statement.executeQuery();
            List<String> heads = new ArrayList<>();
            while (set.next()) {
                heads.add(set.getString("head"));
            }
            return heads;
        }, false, "get favourite heads for " + uuid);
    }
}
