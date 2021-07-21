package io.github.thatsmusic99.headsplus;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Deprecated // should probably replace regardless lmao
class UpdateChecker {
    // TODO - implement AT's update checking system

    private final static String updateURL = "https://api.spiget.org/v2/resources/40265/updates/latest";
    private final static String versionURL = "https://api.spiget.org/v2/resources/40265/versions/latest";

    static Object[] getUpdate() {
        HeadsPlus hp = HeadsPlus.get();
        try {
            JSONObject results = getURLResults(versionURL);
            if (results == null) return null;
            String version = (String) results.get("name");
            if (version.equals(hp.getDescription().getVersion())) return null;
            JSONObject updateInfo = getURLResults(updateURL);
            if (updateInfo == null) return null;
            String title = (String) updateInfo.get("title");
            if (title == null) return null;
            return new Object[]{version, title};
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject getURLResults(String urlStr) throws IOException, ParseException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", "HeadsPlusPluginAgent");
        try {
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            return (JSONObject) new JSONParser().parse(reader);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
}
