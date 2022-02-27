package io.github.thatsmusic99.headsplus;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

class UpdateChecker {

    private final static String updateURL = "https://api.spiget.org/v2/resources/40265/updates/latest";
    private final static String versionURL = "https://api.spiget.org/v2/resources/40265/versions/latest";

    static Object[] getUpdate() {
        try {
            JSONObject latestVersionObj = getURLResults(versionURL);
            if (latestVersionObj == null) return null;
            String newVersion = (String) latestVersionObj.get("name");
            // we are a little stupid
            if (newVersion.equals(HeadsPlus.get().getDescription().getVersion())) return null;
            long latestTimestamp = (long) latestVersionObj.get("releaseDate") * 1000;
            if (latestTimestamp <= getInternalTimestamp()) return null;
            JSONObject updateDesc = getURLResults(updateURL);
            String updateName = (String) updateDesc.get("title");
            return new Object[]{newVersion, updateName};
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException ex) {
            HeadsPlus.get().getLogger().severe("Failed to get plugin update information, is Spiget down?");
            return null;
        }
    }

    private static JSONObject getURLResults(String urlStr) throws IOException, ParseException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", "HeadsPlusPA");
        InputStream inputStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        return (JSONObject) new JSONParser().parse(reader);
    }

    private static long getInternalTimestamp() throws IOException, java.text.ParseException {
        InputStream updateStream = HeadsPlus.class.getResourceAsStream("/update.properties");
        Properties updateProperties = new Properties();
        updateProperties.load(updateStream);
        String timestamp = (String) updateProperties.get("update-timestamp");
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timestamp);
        return date.getTime();
    }
}
