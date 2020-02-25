package io.github.thatsmusic99.headsplus.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public interface JSONFile {

    String getName();

    default void create() throws IOException {

        File f = new File(HeadsPlus.getInstance().getDataFolder() + File.separator + "storage" + File.separator + getName() + ".json");
        if (!f.exists()) {
            new File(HeadsPlus.getInstance().getDataFolder() + File.separator + "storage").mkdirs();
            f.createNewFile();
            JSONObject o = new JSONObject();
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String s = gson.toJson(o);
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(f));
            try {
                fw.write(s.replace("\u0026", "&"));
            } finally {
                fw.flush();
                fw.close();
            }
        }
    }

    default void read() throws IOException {
        File f = new File(HeadsPlus.getInstance().getDataFolder() + File.separator + "storage" + File.separator + getName() + ".json");
        try {
            setJSON((JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(f))));
        }  catch (ParseException e) {
            setJSON(new JSONObject());
        }

    }

    void writeData(OfflinePlayer p, Object... values);

    default void save() throws IOException {
        File f = new File(HeadsPlus.getInstance().getDataFolder() + File.separator + "storage");
        if (!f.exists()) {
            f.mkdirs();
        }
        File jsonfile = new File(f + File.separator, getName() + ".json");
        if (jsonfile.exists()) {
            PrintWriter writer = new PrintWriter(jsonfile);
            writer.print("");
            writer.close();
        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        String s = gson.toJson(getJSON());
        OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(jsonfile));
        try {
            fw.write(s.replace("\u0026", "&"));
        } finally {
            fw.flush();
            fw.close();
        }
    }

    JSONObject getJSON();

    Object getData(Object key);

    void setJSON(JSONObject s);
}
