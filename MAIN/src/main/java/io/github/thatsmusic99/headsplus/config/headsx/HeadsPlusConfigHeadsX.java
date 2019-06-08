package io.github.thatsmusic99.headsplus.config.headsx;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

public class HeadsPlusConfigHeadsX extends ConfigSettings {

    public boolean s = false;
    private final double cVersion = 2.4;
    public final Map<String, List<String>> sections = new HashMap<>();
    public final Map<String, ItemStack> headsCache = new HashMap<>();

    public HeadsPlusConfigHeadsX() {
        this.conName = "headsx";
        headsxEnable();
    }

    private void loadHeadsX() {
        getConfig().options().header("HeadsPlus by Thatsmusic99 "
                + "\n WARNING: This is an advanced section of the plugin. If you do not know what you a doing with it, please do not use it due to risk of crashing your own and other's games. "
                + "\n For more information visit the GitHub wiki for HeadsX.yml: https://github.com/Thatsmusic99/HeadsPlus/wiki/headsx.yml");

        for (HeadsXSections h : HeadsXSections.values()) {
            getConfig().addDefault("sections." + h.let + ".display-name", h.dn);
            getConfig().addDefault("sections." + h.let + ".texture", h.tx);
        }
        for (HeadsXEnums e : HeadsXEnums.values()) {
            // getConfig().addDefault("heads." + e.name + ".database", true);
            getConfig().addDefault("heads." + e.name + ".encode", false);
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
    public void reloadC(boolean a) {
        if (configF == null) {
            configF = new File(HeadsPlus.getInstance().getDataFolder(), "headsx.yml");
        }
        config = YamlConfiguration.loadConfiguration(configF);
        getConfig().addDefault("options.update-heads", true);
        getConfig().addDefault("options.version", cVersion);
        getConfig().addDefault("options.default-price", 10.00);
        getConfig().addDefault("options.price-per-world.example-one", 15.00);
        getConfig().options().copyDefaults(true);
     //   getConfig().addDefault("options.advent-calendar", true);
        //    getConfig().addDefault("options.advent-texture", "HP#snowman");
        //    getConfig().addDefault("options.advent-display-name", "&4[&a&lHeadsPlus &c&lAdvent Calendar!&2]");
        //    getConfig().addDefault("options.christmas-hype", 0);
        //    if (getConfig().getBoolean("options.advent-calendar")) {
        //        for (AdventCManager acm : AdventCManager.values()) {
        //            getConfig().addDefault("advent-18." + acm.name(), new ArrayList<>());
        //        }
        //    }
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
                if (h.d == cVersion) {
                    getConfig().addDefault("sections." + h.let + ".display-name", h.dn);
                    getConfig().addDefault("sections." + h.let + ".texture", h.tx);
                }
            }
            for (HeadsXEnums e : HeadsXEnums.values()) {
                if (e.v == cVersion) {
                    //getConfig().addDefault("heads." + e.name + ".database", true); // isn't actually a required field
                    getConfig().addDefault("heads." + e.name + ".encode", false);
                    getConfig().addDefault("heads." + e.name + ".displayname", e.dn);
                    getConfig().addDefault("heads." + e.name + ".texture", e.tex);
                    getConfig().addDefault("heads." + e.name + ".price", "default");
                    getConfig().addDefault("heads." + e.name + ".section", e.sec);
                }
            }

            getConfig().options().copyDefaults(true);
        }
        save();
        initCategories();
        s = false;
    }

    private void headsxEnable() {
        reloadC(false);
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

    public boolean isAdvent() {
        return getConfig().getBoolean("options.advent-calendar", false) && (new GregorianCalendar()).get(GregorianCalendar.MONTH) == GregorianCalendar.DECEMBER;
    }

    public boolean isHPXSkull(String str) {
        return str.startsWith("HP#");
    }

    public ItemStack getSkull(String s) {
        final String key = s.contains("#") ? s.split("#")[1] : s;
        ItemStack is = headsCache.get(s);
        // todo? allow loading texture directly from parameter if matches base64 pattern?
        return is != null ? is.clone() : getSkullFromTexture(
                getConfig().getString("heads." + key + ".texture"),
                getConfig().getBoolean("heads." + key + ".encode"),
                getConfig().getString("heads." + key + ".displayname"));
    }

    public ItemStack getSkullFromTexture(String texture, boolean encoded, String displayName) {
        NMSManager nms = HeadsPlus.getInstance().getNMS();
        ItemStack i = nms.getSkullMaterial(1);
        SkullMeta sm = (SkullMeta) i.getItemMeta();
        GameProfile gm;
        if (encoded) {
            gm = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            gm.getProperties().put("textures", new Property("textures", new String(encodedData)));
        } else {
            gm = new GameProfile(UUID.randomUUID(), "HPXHead");
            gm.getProperties().put("textures", new Property("texture", texture.replaceAll("=", "")));
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
            new DebugPrint(ex, "Startup (headsx.yml)", false, null);
            return "";
        }
    }

    public ItemStack setTexture(String tex, ItemStack is) throws IllegalAccessException, NoSuchFieldException {
        SkullMeta sm = (SkullMeta) is.getItemMeta();
        GameProfile gm = new GameProfile(UUID.randomUUID(), "HPXHead");
        gm.getProperties().put("textures", new Property("texture", tex.replaceAll("=", "")));

        Field profileField;
        profileField = sm.getClass().getDeclaredField("profile");

        profileField.setAccessible(true);
        profileField.set(sm, gm);
        is.setItemMeta(sm);
        return is;
    }

    public void addChristmasHype() {
        int hype = getConfig().getInt("options.christmas-hype");
        ++hype;
        getConfig().set("options.christmas-hype", hype);
        save();
    }
}
