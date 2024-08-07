package io.github.thatsmusic99.headsplus.managers;

import com.google.gson.Gson;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigHeads;
import io.github.thatsmusic99.headsplus.config.ConfigHeadsSelector;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Used to manage the texture autograbber.
 */
public class AutograbManager {

    // texture lookups need to be protected from spam
    private static final HashMap<String, Long> lookups = new HashMap<>();
    private static final Gson gson = new Gson();
    private static final HashMap<String, String> uuidCache = new HashMap<>();

    public static @Nullable String grabUUID(String username, int tries, CommandSender callback) {

        // If the UUID is already cached, return it
        if (uuidCache.containsKey(username)) return uuidCache.get(username);

        String uuid = null;
        BufferedReader reader;
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(),
                    StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (sb.length() == 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
            String json = sb.toString();
            JSONObject resp = (JSONObject) JSONValue.parse(json);
            if (resp == null || resp.isEmpty()) {
                HeadsPlus.get().getLogger().warning("Failed to grab data for user " + username + " - invalid username" +
                        ".");
                if (callback != null) {
                    callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + username + "!");
                }
                return null;
            } else if (resp.containsKey("error")) {
                // Retry
                if (tries > 0) {
                    return grabUUID(username, tries - 1, callback);
                } else if (callback != null) {
                    callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + username + "!");
                }
                return null;
            } else {
                uuid = String.valueOf(resp.get("id")); // Trying to parse this as a UUID will cause an
                // IllegalArgumentException
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {

            // If it's not a ratelimit then debug it
            if (!e.getMessage().contains("Server returned HTTP response code: 429 for URL")) {
                DebugPrint.createReport(e, "Retreiving UUID (addhead)", true, callback);
            }
        }

        // If the UUID isn't null, cache it
        if (uuid != null) uuidCache.put(username, uuid);
        return uuid;
    }


    public static boolean grabProfile(@Nullable String id, CommandSender callback, boolean forceAdd) {
        Long last = lookups.get(id);
        long now = System.currentTimeMillis();
        if (last != null && last > now - 180000) {
            if (callback != null) {
                callback.sendMessage(ChatColor.RED + "/addhead spam protection - try again in a few minutes");
            }
            return false;
        } else {
            lookups.put(id, now);
        }
        grabProfile(id, 3, callback, forceAdd, forceAdd ? 5 : 20 * 20);
        return true;
    }

    public static void grabProfileUUID(@NotNull String name) {
        Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), () -> {
            String uuid = grabUUID(name, 3, null);
            grabProfile(uuid);
        });
    }

    public static void grabProfile(@Nullable String id) {
        grabProfile(id, null, false);
    }

    protected static void grabProfile(@Nullable String id, int tries, CommandSender callback, boolean forceAdd, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(HeadsPlus.get(), () -> {
            BufferedReader reader = null;
            try {
                if (id == null) return;
                URL uRL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id.replace("-", ""));

                reader = new BufferedReader(new InputStreamReader(uRL.openConnection().getInputStream(),
                        StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (sb.length() == 0) {
                        sb.append("\n");
                    }
                    sb.append(line);
                }
                String json = sb.toString();

                JSONObject resp = (JSONObject) JSONValue.parse(json);
                if (resp == null || resp.isEmpty()) {
                    HeadsPlus.get().getLogger().warning("Failed to grab data for user " + id + " - invalid id");
                    if (callback != null) {
                        callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + Bukkit.getOfflinePlayer(id).getName());
                    }
                    return;
                } else if (resp.containsKey("error")) {
                    // retry
                    if (tries > 0) {
                        grabProfile(id, tries - 1, callback, forceAdd, 30 * 20);
                    } else if (callback != null) {
                        callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + Bukkit.getOfflinePlayer(id).getName());
                    }
                    return;
                }

                String name = (String) resp.get("name");

                Object o = resp.get("properties");
                if (!(o instanceof List)) return;
                for (Object mapObj : (List) o) {
                    if (!(mapObj instanceof Map)) continue;
                    Map map = (Map) mapObj;
                    if (!"textures".equals(map.get("name")) || !map.containsKey("value")) continue;
                    String encoded = map.get("value").toString();
                    addTexture(encoded, forceAdd, callback, name);
                    return;
                }
            } catch (FileNotFoundException ignored) {
            } catch (Exception ex) {
                if (ex instanceof IOException && ex.getMessage().contains("Server returned HTTP response code: 429 " +
                        "for URL")) {
                    grabProfile(id, tries - 1, callback, forceAdd, 30 * 20);
                } else {
                    DebugPrint.createReport(ex, "Retreiving profile (addhead)", true, callback);
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }, delay);
    }

    public static void grabTexture(OfflinePlayer player, boolean force, CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), () -> {
            try {
                addTexture(HeadsPlus.get().getProfileHandler().getTexture(player), force, sender, player.getName() == null ? "<No Name>" : player.getName());
            } catch (NoSuchElementException ignored) {
            }
        });
    }

    private static void addTexture(String texture, boolean force, CommandSender sender, String name) {
        try {
            if (name == null) name = texture;
            String sectionStr = MainConfig.get().getAutograbber().SECTION;
            if (!ConfigHeadsSelector.get().getSections().containsKey(sectionStr)) {
                HeadsPlus.get().getLogger().warning("Section " + sectionStr + " does not exist!");
                return;
            }
            ConfigHeadsSelector.SectionInfo section = ConfigHeadsSelector.get().getSection(sectionStr);
            String title = MainConfig.get().getAutograbber().DISPLAY_NAME.replace("{player}", name);

            // If the head doesn't exist, add it - for REAL, this time!
            String id;
            HeadManager.HeadInfo headInfo;
            if (HeadManager.get().contains(name)) {

                // If it's an automated addition then let's not try to force it in
                if (sender == null) return;
                name += "_" + section.getHeads().size();
            }
            if (texture.startsWith("http")) {
                texture = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", texture);
                texture = new String(Base64.getEncoder().encode(texture.getBytes(StandardCharsets.UTF_8)));
            }
            if (!HeadManager.get().getAddedTextures().contains(texture)) {
                headInfo = new HeadManager.HeadInfo();
                id = name;
                headInfo.withTexture(texture);
            } else if (force) {
                id = HeadManager.get().getId(texture);
                headInfo = HeadManager.get().getHeadInfo(id);

            } else if (sender != null) {
                MessagesManager.get().sendMessage("commands.addhead.head-already-added", sender, "{player}", name);
                return;
            } else {
                return;
            }

            headInfo.withDisplayName(title);
            HeadManager.get().registerHead(id, headInfo);

            ConfigHeads.get().forceExample("heads." + id + ".display-name", title);
            ConfigHeads.get().forceExample("heads." + id + ".texture", texture);
            ConfigHeads.get().save();

            if (MainConfig.get().getAutograbber().ADD_GRABBED_HEADS) {
                ConfigHeadsSelector.BuyableHeadInfo buyableHead = new ConfigHeadsSelector.BuyableHeadInfo(headInfo, id);
                buyableHead.withPrice(MainConfig.get().getAutograbber().PRICE);
                section.addHead(id, buyableHead);
                // Add to the actual config
                ConfigHeads.get().forceExample("heads." + id + ".section", sectionStr);
                if (buyableHead.getPrice() != -1) {
                    ConfigHeads.get().forceExample("heads." + id + ".price", buyableHead.getPrice());
                }
                ConfigHeads.get().save();
            }
            if (sender != null) {
                MessagesManager.get().sendMessage("commands.addhead.head-added", sender, "{player}", name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
