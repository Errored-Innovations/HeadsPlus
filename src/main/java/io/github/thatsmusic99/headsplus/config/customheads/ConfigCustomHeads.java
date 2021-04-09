package io.github.thatsmusic99.headsplus.config.customheads;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class ConfigCustomHeads extends CMFile {

    private final double cVersion = 3.4;
    public final Map<String, List<String>> sections = new HashMap<>();
    public final Map<String, ItemStack> headsCache = new HashMap<>();
    public final Set<String> allHeadsCache = new HashSet<>();
    public static ConfigCustomHeads instance;

    public ConfigCustomHeads() {
        super(HeadsPlus.getInstance(), "customheads");
        instance = this;
    }

    @Override
    public void loadTitle() {
        // Don't load any title
    }

    @Override
    public void loadDefaults() {
        addComment("This is where heads from custom textures can be made.\n" +
                "To use a custom head in another config such as mobs.yml, use HP#head_id, where head_id is the config section of the head.");
        addSection("Main Options");
        addDefault("update-heads", true, "Whether the plugin should add more heads included with updates.");
        addDefault("default-price", 10.00);
        addLenientSection("price-per-world");
        addDefault("price-per-world.example-one", 15.00);
        addDefault("autograb", false);
        addDefault("automatically-enable-grabbed-heads", true);
        addDefault("current-version", cVersion, "Please do not change this! This is used to track updates made.");
        boolean updateHeads = getBoolean("update-heads");
        double currentVersion = getDouble("current-version");
        if (updateHeads && currentVersion < cVersion) {
            set("current-version", cVersion);
            // Sections
            for (HeadsXSections h : HeadsXSections.values()) {
                if (isNew() || h.d > currentVersion) {
                    addDefault("sections." + h.let + ".display-name", h.dn);
                    addDefault("sections." + h.let + ".texture", h.tx);
                    addDefault("sections." + h.let + ".enabled", true);
                    addDefault("sections." + h.let + ".permission", "headsplus.section." + h.let);
                }
            }
            // Heads
            for (HeadsXEnums head : HeadsXEnums.values()) {
                if (isNew() || head.v > currentVersion) {
                    addDefault("heads." + head.name + ".displayname", head.dn);
                    addDefault("heads." + head.name + ".texture", head.tex);
                    addDefault("heads." + head.name + ".price", "default");
                    addDefault("heads." + head.name + ".section", head.sec);
                    addDefault("heads." + head.name + ".interact-name", head.interactName);
                }
            }
        }
    }

    @Override
    public void moveToNew() {
        moveTo("options.update-heads", "update-heads");
        moveTo("options.version", "current-version");
        moveTo("options.default-price", "default-price");
        moveTo("options.price-per-world", "price-per-world");
        // May as well add these.
        for (String key : getConfig().getConfigurationSection("sections").getKeys(false)) {
            addDefault("sections." + key + ".enabled", true);
            addDefault("sections." + key + ".permission", "headsplus.section." + key);
        }
    }

    @Override
    public void postSave() {
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
        }
    }

    public static ConfigCustomHeads get() {
        return instance;
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
                    getString("heads." + key + ".texture"),
                    getBoolean("heads." + key + ".encode"),
                    getString("heads." + key + ".displayname"));
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
            GameProfile profile = ProfileFetcher.getProfile(skull);
            if (profile == null) return null;
            String value = profile.getProperties().get("textures").iterator().next().getValue();
            JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(value.getBytes())));
            return new String(Base64.getEncoder().encode(((JSONObject)((JSONObject) json.get("textures")).get("SKIN")).get("url").toString().getBytes()));
        } catch (IllegalAccessException | SecurityException ex) {
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
                encodedData = Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", texture).getBytes());
            } else {
                encodedData = Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
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
                initiateSave();
                autosaveTask = -1;
            }, 5 * 60).getTaskId();
        }
    }

    public void flushSave() {
        if (autosaveTask != -1) {
            Bukkit.getScheduler().cancelTask(autosaveTask);
            initiateSave();
            autosaveTask = -1;
        }
    }


}
