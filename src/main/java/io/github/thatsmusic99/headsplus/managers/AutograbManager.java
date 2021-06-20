package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AutograbManager {

    // texture lookups need to be protected from spam
    private static HashMap<String, Long> lookups = new HashMap<>();

    public static String grabUUID(String username, int tries, CommandSender callback) {
        String uuid = null;
        BufferedReader reader;
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if(sb.length() == 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
            String json = sb.toString();
            JSONObject resp = (JSONObject) JSONValue.parse(json);
            if (resp == null || resp.isEmpty()) {
                HeadsPlus.get().getLogger().warning("Failed to grab data for user " + username + " - invalid username.");
                if(callback != null) {
                    callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + username + "!");
                }
                return null;
            } else if (resp.containsKey("error")) {
                // Retry
                if(tries > 0) {
                    grabUUID(username, tries - 1, callback);
                } else if(callback != null) {
                    callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + username + "!");
                }
                return null;
            } else {
                uuid = String.valueOf(resp.get("id")); // Trying to parse this as a UUID will cause an IllegalArgumentException
            }
        } catch (IOException e) {
            DebugPrint.createReport(e, "Retreiving UUID (addhead)", true, callback);
        }
        return uuid;
    }


    public static boolean grabProfile(String id, CommandSender callback, boolean forceAdd) {
        Long last = lookups.get(id);
        long now = System.currentTimeMillis();
        if(last != null && last > now - 180000) {
            if(callback != null) {
                callback.sendMessage(ChatColor.RED + "/addhead spam protection - try again in a few minutes");
            }
            return false;
        } else {
            lookups.put(id, now);
        }
        grabProfile(id, 3, callback, forceAdd, forceAdd ? 5 : 20 * 20);
        return true;
    }

    public static void grabProfile(String id) {
        grabProfile(id, null, false);
    }

    protected static void grabProfile(String id, int tries, CommandSender callback, boolean forceAdd, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(HeadsPlus.get(), () -> {
            BufferedReader reader = null;
            try {
                if (id == null) return;
                URL uRL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id.replace("-", ""));

                reader = new BufferedReader(new InputStreamReader(uRL.openConnection().getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if(sb.length() == 0) {
                        sb.append("\n");
                    }
                    sb.append(line);
                }
                String json = sb.toString();

                JSONObject resp = (JSONObject) JSONValue.parse(json);
                if(resp == null || resp.isEmpty()) {
                    HeadsPlus.get().getLogger().warning("Failed to grab data for user " + id + " - invalid id");
                    if(callback != null) {
                        callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + Bukkit.getOfflinePlayer(id).getName());
                    }
                    return;
                } else if(resp.containsKey("error")) {
                    // retry
                    if(tries > 0) {
                        grabProfile(id, tries - 1, callback, forceAdd, 30 * 20);
                    } else if(callback != null) {
                        callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + Bukkit.getOfflinePlayer(id).getName());
                    }
                    return;
                }

                Object o = resp.get("properties");
                if(o instanceof List) {
                    for(Object o2 : (List) o) {
                        if(o2 instanceof Map) {
                            Map m = (Map) o2;
                            if("textures".equals(m.get("name")) && m.containsKey("value")) {
                                String encoded = m.get("value").toString();
                                String decoded = new String(Base64.getDecoder().decode(encoded));
                                JSONObject resp2 = (JSONObject) JSONValue.parse(decoded);
                                if((o2 = resp2.get("textures")) instanceof Map
                                        && (o2 = ((Map) o2).get("SKIN")) instanceof Map
                                        && ((Map) o2).containsKey("url")) {
                                    String texUrl = ((Map) o2).get("url").toString();
                                    int last = texUrl.lastIndexOf('/');
                                    if(last != -1) {
                                        texUrl = texUrl.substring(last + 1);
                                        String name = resp.get("name").toString();
                                        if(!ConfigCustomHeads.get().allHeadsCache.contains(texUrl)) {
                                            ConfigCustomHeads.get().addHead(texUrl, true,
                                                    HeadsPlus.get().getConfig().getString("plugin.autograb.title").replace("{player}", name),
                                                    HeadsPlus.get().getConfig().getString("plugin.autograb.section"),
                                                    HeadsPlus.get().getConfig().getString("plugin.autograb.price"),
                                                    forceAdd || HeadsPlus.get().getConfig().getBoolean("plugin.autograb.add-as-enabled"));
                                            if(callback != null) {
                                                HeadsPlusMessagesManager.get().sendMessage("commands.addhead.head-added", callback, "{player}", name);
                                            }
                                        } else if (forceAdd && ConfigCustomHeads.get().enableHead(texUrl)){
                                            if(callback != null) {
                                                HeadsPlusMessagesManager.get().sendMessage("commands.addhead.head-added", callback, "{player}", name);
                                            }
                                        } else if(callback != null) {
                                            HeadsPlusMessagesManager.get().sendMessage("commands.addhead.head-already-added", callback, "{player}", name);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                if(ex instanceof IOException && ex.getMessage().contains("Server returned HTTP response code: 429 for URL")) {
                    grabProfile(id, tries - 1, callback, forceAdd, 30 * 20);
                } else {
                    DebugPrint.createReport(ex, "Retreiving profile (addhead)", true, callback);
                }
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }, delay);
    }

    public static void grabTexture(OfflinePlayer player, boolean force, CommandSender sender) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final String[] playerInfo = new String[1];
                try {
                    // TODO - replacement
                    playerInfo[0] = ProfileFetcher.getProfile(player).getProperties().get("textures").iterator().next().getValue();
                    addTexture(playerInfo[0], force, sender, player);
                } catch (NoSuchElementException exception) {

                }
            }
        }.runTask(HeadsPlus.get());
    }

    private static void addTexture(String info, boolean force, CommandSender sender, OfflinePlayer player) {
        try {
            JSONObject playerJson = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(info.getBytes())));
            JSONObject textureJson = (JSONObject) playerJson.get("textures");
            if (textureJson.isEmpty()) return;
            JSONObject skinJSON = ((JSONObject)textureJson.get("SKIN"));
            String texture = String.valueOf(skinJSON.get("url"));
            ConfigurationSection section = HeadsPlus.get().getConfig().getConfigurationSection("plugin.autograb");
            // If the head never existed
            if(!ConfigCustomHeads.get().allHeadsCache.contains(texture)) {
                ConfigCustomHeads.get().addHead(texture, true,
                        section.getString("title").replace("{player}", player.getName()),
                        section.getString("section"),
                        section.getString("price"),
                        force || section.getBoolean("add-as-enabled"));

            } else if (force && ConfigCustomHeads.get().enableHead(texture)){
                // Keep going.
            } else if(sender != null) {
                HeadsPlusMessagesManager.get().sendMessage("commands.addhead.head-already-added", sender, "{player}", player.getName());
                return;
            }
            if(sender != null) {
                HeadsPlusMessagesManager.get().sendMessage("commands.addhead.head-added", sender, "{player}", player.getName());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
