package io.github.thatsmusic99.headsplus.config.customheads;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigHeads;
import io.github.thatsmusic99.headsplus.config.HPConfig;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXEnums;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXSections;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

@Deprecated
public class ConfigCustomHeads extends HPConfig {

    private final double cVersion = 3.4;
    public final Map<String, List<String>> sections = new HashMap<>();
    public final Map<String, ItemStack> headsCache = new HashMap<>();
    public final Set<String> allHeadsCache = new HashSet<>();
    public static ConfigCustomHeads instance;

    public ConfigCustomHeads() {
        super("customheads.yml");
        instance = this;
    }

    @Override
    public void loadDefaults() {
        addComment("This is where heads from custom textures can be made.\n" +
                "To use a custom head in another config such as mobs.yml, use HP#head_id, where head_id is the config section of the head.");
        addSection("Main Options");
        addDefault("update-heads", true, "Whether the plugin should add more heads included with updates.");
        addDefault("default-price", 10.00);
        makeSectionLenient("price-per-world");
        addDefault("price-per-world.example-one", 15.00);
        addDefault("autograb", false, "Autograb is a feature that grabs ");
        addDefault("automatically-enable-grabbed-heads", true);
        addDefault("current-version", cVersion, "Please do not change this! This is used to track updates made.");

        makeSectionLenient("sections");
        makeSectionLenient("heads");

        boolean updateHeads = getBoolean("update-heads", true);
        double currentVersion = getDouble("current-version");
        if (isNew()) {
            currentVersion = 0.0;
        }
        if (updateHeads && currentVersion < cVersion) {
            set("current-version", cVersion);
            // Sections
            for (HeadsXSections h : HeadsXSections.values()) {
                if (isNew() || h.version > currentVersion) {
                    addDefault("sections." + h.id + ".display-name", h.displayName);
                    addDefault("sections." + h.id + ".texture", h.texture);
                    addDefault("sections." + h.id + ".enabled", true);
                    addDefault("sections." + h.id + ".permission", "headsplus.section." + h.id);
                }
            }
            // Heads
            for (HeadsXEnums head : HeadsXEnums.values()) {
                if (isNew() || head.version > currentVersion) {
                    addDefault("heads." + head.name + ".displayname", head.displayName);
                    addDefault("heads." + head.name + ".texture", head.texture);
                    addDefault("heads." + head.name + ".price", "default");
                    addDefault("heads." + head.name + ".section", head.section);
                    addDefault("heads." + head.name + ".interact-name", head.interactName);
                }
            }
        }
    }

    @Override
    public void moveToNew() {
        moveTo("options.update-heads", "update-heads", ConfigHeads.get());
        moveTo("options.version", "current-version", ConfigHeads.get());
        moveTo("options.default-price", "default-price");
        moveTo("options.price-per-world", "price-per-world");
        // May as well add these.
        for (String key : getConfigSection("sections").getKeys(false)) {
            addDefault("sections." + key + ".enabled", true);
            addDefault("sections." + key + ".permission", "headsplus.section." + key);
        }
    }

    @Override
    public void postSave() {
        sections.clear();
        for (String cat : getConfigSection("sections").getKeys(false)) {
            sections.put(cat, new ArrayList<>());
        }
        ConfigSection heads = getConfigSection("heads");
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
            HeadsPlus.get().getLogger().log(Level.SEVERE, "Failed to init skull database", ex);
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
            HeadsPlus.get().getLogger().severe("An empty ID was found when fetching a head! Please check your customheads.yml configuration or send it to the developer.");
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
        ItemStack i = new ItemStack(Material.PLAYER_HEAD);
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
            return getString("heads." + st[1] + ".texture");
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
        ConfigSection heads = getConfigSection("heads");
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
        ConfigSection heads = getConfigSection("heads");
        String key;
        while(heads.contains(key = section + "_" + i)) ++i;
        ConfigSection head = heads.createConfigSection(key);
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
        allHeadsCache.add(getString("heads." + id + ".texture"));
        sections.get(section).add(id);
    }

    int autosaveTask = -1;

    void delaySave() {
        if (autosaveTask == -1 && HeadsPlus.get().isEnabled()) {
            autosaveTask = Bukkit.getScheduler().runTaskLaterAsynchronously(HeadsPlus.get(), ()->{
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                autosaveTask = -1;
            }, 5 * 60).getTaskId();
        }
    }

    public void flushSave() {
        if (autosaveTask != -1) {
            Bukkit.getScheduler().cancelTask(autosaveTask);
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            autosaveTask = -1;
        }
    }


}
