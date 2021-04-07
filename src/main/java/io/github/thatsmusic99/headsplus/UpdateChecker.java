package io.github.thatsmusic99.headsplus;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

class UpdateChecker {

    private final static String versionURL = "https://api.spiget.org/v2/resources/40265/versions?size=1000";
    private final static String descriptionURL = "https://api.spiget.org/v2/resources/40265/updates?size=1000";

    static Object[] getUpdate() {
        HeadsPlus hp = HeadsPlus.getInstance();
        try {
            JSONArray versionsArray = getURLResults(versionURL);
            if (versionsArray != null) {
                int size = Objects.requireNonNull(versionsArray).size();
                String lastVersion = ((JSONObject) versionsArray.get(size - 1)).get("name").toString();
                String currentVersion = hp.getDescription().getVersion();
                if (!lastVersion.equals(currentVersion)) {
                    JSONArray updatesArray = getURLResults(descriptionURL);
                    if (updatesArray != null) {
                        int updateSize = updatesArray.size();
                        String updateName = ((JSONObject) updatesArray.get(updateSize - 1)).get("title").toString();
                        return new Object[]{lastVersion, updateName};
                    }
                }
            }

        } catch (ParseException | IOException e) {
            if (hp.getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private static JSONArray getURLResults(String urlStr) throws IOException, ParseException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", "HeadsPlusPluginAgent");
        try {
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            return (JSONArray) new JSONParser().parse(reader);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
}
