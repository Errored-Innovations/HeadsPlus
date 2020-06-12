package io.github.thatsmusic99.headsplus.config.customheads;

import com.google.common.io.Files;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class HeadsPlusConfigCustomHeads extends ConfigSettings {

    public boolean s = false;
    private final double cVersion = 3.1;
    public final Map<String, List<String>> sections = new HashMap<>();
    public final Map<String, ItemStack> headsCache = new HashMap<>();
    public final Set<String> allHeadsCache = new HashSet<>();
    public final HeadsPlusMessagesManager hpc;

    public HeadsPlusConfigCustomHeads() {
        this.conName = "customheads";
        headsxEnable();
        hpc = HeadsPlus.getInstance().getMessagesConfig();
    }

    private void loadHeadsX() {
        getConfig().options().header("HeadsPlus by Thatsmusic99 "
                + "\n WARNING: This is an advanced section of the plugin. If you do not know what you a doing with it, please do not use it due to risk of crashing your own and other's games. "
                + "\n For more information visit the GitHub wiki for HeadsX.yml: https://github.com/Thatsmusic99/HeadsPlus/wiki/customheads.yml");
        getConfig().addDefault("options.update-heads", true);
        getConfig().addDefault("options.version", cVersion);
        getConfig().addDefault("options.default-price", 10.00);
        getConfig().addDefault("options.price-per-world.example-one", 15.00);
        for (HeadsXSections h : HeadsXSections.values()) {
            getConfig().addDefault("sections." + h.let + ".display-name", h.dn);
            getConfig().addDefault("sections." + h.let + ".texture", h.tx);
        }
        for (HeadsXEnums e : HeadsXEnums.values()) {
            // getConfig().addDefault("heads." + e.name + ".database", true);
            getConfig().addDefault("heads." + e.name + ".encode", true);
            getConfig().addDefault("heads." + e.name + ".displayname", e.dn);
            getConfig().addDefault("heads." + e.name + ".texture", e.tex);
            getConfig().addDefault("heads." + e.name + ".price", "default");
            getConfig().addDefault("heads." + e.name + ".section", e.sec);
        }

        getConfig().options().copyDefaults(true);
        save();
        initCategories();
    }

    @Override
    public String getDefaultPath() {
        return "options.default-price";
    }

    @Override
    public void reloadC() {
        if (configF == null) {
            File oldFile = new File(HeadsPlus.getInstance().getDataFolder(), "headsx.yml");
            File newFile = new File(HeadsPlus.getInstance().getDataFolder(), "customheads.yml");
            if (oldFile.exists()) {
                try {
                    Files.copy(oldFile, newFile);
                    oldFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            configF = newFile;
        }
        performFileChecks();
        getConfig().addDefault("options.update-heads", true);
        getConfig().addDefault("options.version", cVersion);
        getConfig().addDefault("options.default-price", 10.00);
        getConfig().addDefault("options.price-per-world.example-one", 15.00);
        if (configF.length() <= 500) {
            loadHeadsX();
        }
        boolean b = getConfig().getBoolean("options.update-heads");
        if (getConfig().getDouble("options.version") < cVersion && b) {
            for (String str : getConfig().getConfigurationSection("heads").getKeys(false)) {
                getConfig().addDefault("heads." + str + ".price", "default");
            }
            getConfig().set("options.version", cVersion);
            for (String str : getConfig().getConfigurationSection("heads").getKeys(false)) {
                for (HeadsXEnums e : HeadsXEnums.values()) {
                    if (e.name.equalsIgnoreCase(str)) {
                        getConfig().addDefault("heads." + e.name + ".section", e.sec);
                    }
                }
            }
            for (HeadsXSections h : HeadsXSections.values()) {
                if (h.d >= cVersion) {
                    getConfig().addDefault("sections." + h.let + ".display-name", h.dn);
                    getConfig().addDefault("sections." + h.let + ".texture", h.tx);
                }
            }
            for (HeadsXEnums e : HeadsXEnums.values()) {
                if (e.v >= cVersion) {
                    //getConfig().addDefault("heads." + e.name + ".database", true); // isn't actually a required field
                    getConfig().addDefault("heads." + e.name + ".encode", true);
                    getConfig().addDefault("heads." + e.name + ".displayname", e.dn);
                    getConfig().addDefault("heads." + e.name + ".texture", e.tex);
                    getConfig().addDefault("heads." + e.name + ".price", "default");
                    getConfig().addDefault("heads." + e.name + ".section", e.sec);
                }
            }
        }
        getConfig().options().copyDefaults(true);
        save();
        initCategories();
        s = false;
    }

    private void headsxEnable() {
        reloadC();
       // if (s) {
        //      loadHeadsX();
        //  }
        s = false;
    }

    private void initCategories() {
        sections.clear();
        for (String cat : getConfig().getConfigurationSection("sections").getKeys(false)) {
            sections.put(cat, new ArrayList<>());
        }
        ConfigurationSection heads = getConfig().getConfigurationSection("heads");
        try {
            for (String head : heads.getKeys(false)) {
                allHeadsCache.add(heads.getString(head + ".texture"));
                if (heads.getBoolean(head + ".database", true)) {
                    final String sec = heads.getString(head + ".section");
                    List<String> list = sections.get(sec);
                    if (list != null) {
                        list.add(head);
                        headsCache.put(head, getSkull(head));
                    }
                }
            }
        } catch (RuntimeException ex) {
            HeadsPlus.getInstance().getLogger().log(Level.SEVERE, "Failed to init skull database", ex);
            sections.clear();
            return;
        }
        if (getConfig().getBoolean("options.advent-calendar")) {
            sections.put("advent-calendar", new ArrayList<>());
        }
    }

    public boolean isHPXSkull(String str) {
        return str.startsWith("HP#");
    }

    @Nullable
    public ItemStack getSkull(String s) {
        try {
            final String key = s.contains("#") ? s.split("#")[1] : s;
            ItemStack is = headsCache.get(s);
            // todo? allow loading texture directly from parameter if matches base64 pattern?
            return is != null ? is.clone() : getSkullFromTexture(
                    getConfig().getString("heads." + key + ".texture"),
                    getConfig().getBoolean("heads." + key + ".encode"),
                    getConfig().getString("heads." + key + ".displayname"));
        } catch (ArrayIndexOutOfBoundsException ex) {
            HeadsPlus.getInstance().getLogger().severe("An empty ID was found when fetching a head! Please check your customheads.yml configuration or send it to the developer.");
            return null;
        }
    }

    public double getPrice(String id) {
        return getDouble("heads." + id + ".price");
    }

    public String getTexture(ItemStack skull) {
        try {
            Field profileField;
            profileField = skull.getItemMeta().getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile profile = (GameProfile) profileField.get(skull.getItemMeta());
            String value = profile.getProperties().get("textures").iterator().next().getValue();
            JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(value.getBytes())));
            return new String(Base64.getEncoder().encode(((JSONObject)((JSONObject) json.get("textures")).get("SKIN")).get("url").toString().getBytes()));
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
            throw new RuntimeException("Reflection error while getting head texture", ex);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStack getSkullFromTexture(String texture, boolean encoded, String displayName) {
        NMSManager nms = HeadsPlus.getInstance().getNMS();
        ItemStack i = nms.getSkullMaterial(1);
        SkullMeta sm = (SkullMeta) i.getItemMeta();
        GameProfile gm;
        if (encoded) {
            gm = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
            byte[] encodedData;
            if (texture.startsWith("http")) {
                encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture).getBytes());
            } else {
                encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            }
            gm.getProperties().put("textures", new Property("textures", new String(encodedData)));
        } else {
            gm = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
            gm.getProperties().put("textures", new Property("textures", texture.replaceAll("=", "")));
        }

        try {
            Field profileField;
            profileField = sm.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(sm, gm);
            if (displayName != null) {
                sm.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            }
            i.setItemMeta(sm);
            return i;
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
            throw new RuntimeException("Reflection error while setting head texture", ex);
        }
    }

    public String getTextures(String s) {
        String[] st = s.split("#");
        try {
            return getConfig().getString("heads." + st[1] + ".texture");
        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Startup (customheads.yml)", false, null);
            return "";
        }
    }

    public ItemStack setTexture(String tex, ItemStack is) throws IllegalAccessException, NoSuchFieldException {
        SkullMeta sm = (SkullMeta) is.getItemMeta();
        GameProfile gm = new GameProfile(UUID.nameUUIDFromBytes(tex.getBytes()), "HPXHead");
        gm.getProperties().put("textures", new Property("textures", tex.replaceAll("=", "")));

        Field profileField;
        profileField = sm.getClass().getDeclaredField("profile");

        profileField.setAccessible(true);
        profileField.set(sm, gm);
        is.setItemMeta(sm);
        return is;
    }

    public void grabTexture(OfflinePlayer player, boolean force, CommandSender sender) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final String[] playerInfo = new String[1];
                try {
                    playerInfo[0] = NBTManager.getProfile(player).getProperties().get("textures").iterator().next().getValue();
                    addTexture(playerInfo[0], force, sender, player);
                } catch (NoSuchElementException exception) {

                }
            }
        }.runTask(HeadsPlus.getInstance());

    }

    private void addTexture(String info, boolean force, CommandSender sender, OfflinePlayer player) {
        try {
            JSONObject playerJson = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(info.getBytes())));
            JSONObject skinJSON = ((JSONObject)((JSONObject) playerJson.get("textures")).get("SKIN"));
            String texture = String.valueOf(skinJSON.get("url"));
            ConfigurationSection section = HeadsPlus.getInstance().getConfig().getConfigurationSection("plugin.autograb");
            // If the head never existed
            if(!allHeadsCache.contains(texture)) {
                addHead(texture, true,
                        section.getString("title").replace("{player}", player.getName()),
                        section.getString("section"),
                        section.getString("price"),
                        force || section.getBoolean("add-as-enabled"));

            } else if (force && enableHead(texture)){
                // Keep going.
            } else if(sender != null) {
                hpc.sendMessage("commands.addhead.head-already-added", sender, "{player}", player.getName());
                return;
            }
            if(sender != null) {
                hpc.sendMessage("commands.addhead.head-added", sender, "{player}", player.getName());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void grabProfile(String id) {
        grabProfile(id, null, false);
    }

    // texture lookups need to be protected from spam
    HashMap<String, Long> lookups = new HashMap<>();

    public boolean grabProfile(String id, CommandSender callback, boolean forceAdd) {
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

    public String grabUUID(String username, int tries, CommandSender callback) {
        String uuid = null;
        BufferedReader reader = null;
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
            if(resp == null || resp.isEmpty()) {
                HeadsPlus.getInstance().getLogger().warning("Failed to grab data for user " + username + " - invalid username.");
                if(callback != null) {
                    callback.sendMessage(ChatColor.RED + "Error: Failed to grab data for user " + username + "!");
                }
                return null;
            } else if(resp.containsKey("error")) {
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

    protected void grabProfile(String id, int tries, CommandSender callback, boolean forceAdd, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(HeadsPlus.getInstance(), () -> {
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
                    HeadsPlus.getInstance().getLogger().warning("Failed to grab data for user " + id + " - invalid id");
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
                                       if(!allHeadsCache.contains(texUrl)) {
                                           addHead(texUrl, true,
                                                   HeadsPlus.getInstance().getConfig().getString("plugin.autograb.title").replace("{player}", name),
                                                   HeadsPlus.getInstance().getConfig().getString("plugin.autograb.section"), 
                                                   HeadsPlus.getInstance().getConfig().getString("plugin.autograb.price"), 
                                                   forceAdd || HeadsPlus.getInstance().getConfig().getBoolean("plugin.autograb.add-as-enabled"));
                                            if(callback != null) {
                                                hpc.sendMessage("commands.addhead.head-added", callback, "{player}", name);
                                            }
                                       } else if (forceAdd && enableHead(texUrl)){
                                           if(callback != null) {
                                               hpc.sendMessage("commands.addhead.head-added", callback, "{player}", name);
                                            }
                                       } else if(callback != null) {
                                           hpc.sendMessage("commands.addhead.head-already-added", callback, "{player}", name);
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

    /**
     * Enable player head texture, if not enabled already
     * @param texture
     * @return true if the texture exists and was not previously enabled.
     */
    public boolean enableHead(String texture) {
        ConfigurationSection heads = getConfig().getConfigurationSection("heads");
        for(String k : heads.getKeys(false)) {
            if(texture.equals(heads.getString(k + ".texture"))) {
                if(!heads.getBoolean(k + ".database", true)) {
                    heads.set(k + ".database", true);
                    List<String> list = sections.get(heads.getString(k + ".section"));
                    if (list != null) {
                        list.add(k);
                        headsCache.put(k, getSkull(k));
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public void addHead(String texture, boolean encode, String displayname, String section, String price, boolean enable) {
        // find a name that's open
        List l = sections.get(section);
        int i = l == null ? 0 : l.size() + 1;
        ConfigurationSection heads = getConfig().getConfigurationSection("heads");
        String key;
        while(heads.contains(key = section + "_" + i)) ++i;
        ConfigurationSection head = heads.createSection(key);
        if(!enable) {
            head.set("database", enable);
        }
        head.set("encode", encode);
        head.set("price", price);
        head.set("section", section);
        head.set("texture", texture);
        head.set("displayname", displayname);
        if(enable) {
            List<String> list = sections.get(section);
            if (list != null) {
                list.add(key);
                headsCache.put(key, getSkull(key));
            }
        }
		allHeadsCache.add(texture);
        delaySave();
    }

    public void addHeadToCache(String id, String section) {
        headsCache.put(id, getSkull(id));
        allHeadsCache.add(getConfig().getString("heads." + id + ".texture"));
        sections.get(section).add(id);
    }

    int autosaveTask = -1;

    void delaySave() {
        if (autosaveTask == -1 && HeadsPlus.getInstance().isEnabled()) {
            autosaveTask = Bukkit.getScheduler().runTaskLaterAsynchronously(HeadsPlus.getInstance(), ()->{
                save();
                autosaveTask = -1;
            }, 5 * 60).getTaskId();
        }
    }

    public void flushSave() {
        if (autosaveTask != -1) {
            Bukkit.getScheduler().cancelTask(autosaveTask);
            save();
            autosaveTask = -1;
        }
    }
}
