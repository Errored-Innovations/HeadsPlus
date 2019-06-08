package io.github.thatsmusic99.headsplus.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.headsx.HeadsPlusConfigHeadsX;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class HeadsPlusAPI {

    // S
    private HeadsPlus hp = HeadsPlus.getInstance();
    private final HeadsPlusConfigHeadsX hpcHeadsX = hp.getHeadsXConfig();
    private final HeadsPlusConfigHeads hpcHeads = hp.getHeadsConfig();

    public ItemStack getHead(String option) {
        return hpcHeadsX.getSkull(option);
    }

    public boolean isSellable(ItemStack is) {
        if (is.getType() == hp.getNMS().getSkullMaterial(1).getType()) {
            return hp.getNBTManager().isSellable(is);
        }
        return false;
    }

    public ItemStack createSkull(String texture, String displayname) {
        NMSManager nms = hp.getNMS();
        ItemStack s = nms.getSkullMaterial(1);
        SkullMeta sm = (SkullMeta) s.getItemMeta();
        GameProfile gm = new GameProfile(UUID.fromString("7091cdbc-ebdc-4eac-a6b2-25dd8acd3a0e"), "HPXHead");
        gm.getProperties().put("textures", new Property("texture", texture));

        Field profileField = null;
        try {
            profileField = sm.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            if (hp.getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
            }
        }
        profileField.setAccessible(true);
        try {
            profileField.set(sm, gm);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            if (hp.getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
            }
        }
        sm.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
        s.setItemMeta(sm);
        return s;
    }

    public String getTexture(String owner) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(owner);
        GameProfile gm = new GameProfile(p.getUniqueId(), owner);
        return gm.getProperties().get("textures").iterator().next().getValue();
    }

    public SkullMeta setSkullMeta(SkullMeta m, String t) {
        GameProfile gm = new GameProfile(UUID.fromString("7091cdbc-ebdc-4eac-a6b2-25dd8acd3a0e"), "HPXHead");
        gm.getProperties().put("textures", new Property("texture", t));

        Field profileField = null;
        try {
            profileField = m.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            if (hp.getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
            }
        }
        profileField.setAccessible(true);
        try {
            profileField.set(m, gm);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            if (hp.getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
            }
        }
        return m;
    }

    public String getSkullType(ItemStack is) {
        return hp.getNBTManager().getType(is);
    }

    @Deprecated
    public int getPlayerInLeaderboards(OfflinePlayer p, String section) throws SQLException {
        return hp.getLeaderboardsConfig().getScores(section).get(p);
    }

    @Deprecated
    public LinkedHashMap<OfflinePlayer, Integer> getScores(String section) throws SQLException {
        return hp.getLeaderboardsConfig().getScores(section);
    }

    public int getPlayerInLeaderboards(OfflinePlayer p, String section, String database) throws SQLException {
        try {
            return hp.getMySQLAPI().getScores(section, database).get(p);
        } catch (NullPointerException ex) {
            return -1;
        }
    }

    public LinkedHashMap<OfflinePlayer, Integer> getScores(String section, String database) throws SQLException {
        return hp.getMySQLAPI().getScores(section, database);
    }

    public List<Challenge> getChallenges() {
        return hp.getChallenges();
    }

    public Challenge getChallenge(String challengeName) {
        for (Challenge c : getChallenges()) {
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', c.getChallengeHeader())).equals(challengeName)) {
                return c;
            }
        }
        return null;
    }

    public Challenge getChallengeByConfigName(String name) {
        for (Challenge c : getChallenges()) {
            if (c.getConfigName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public EntityType strToEntityType(String str) {
        switch (str) {
            case "cavespider":
                str = "CAVE_SPIDER";
                break;
            case "irongolem":
                str = "IRON_GOLEM";
                break;
            case "mushroomcow":
                str = "MUSHROOM_COW";
                break;
            case "enderdragon":
                str = "ENDER_DRAGON";
                break;
            case "elderguardian":
                str = "ELDER_GUARDIAN";
                break;
            case "magmacube":
                str = "MAGMA_CUBE";
                break;
            case "pigzombie":
                str = "PIG_ZOMBIE";
                break;
            case "polarbear":
                str = "POLAR_BEAR";
                break;
            case "skeletonhorse":
                str = "SKELETON_HORSE";
                break;
            case "traderllama":
                str = "TRADER_LLAMA";
                break;
            case "tropicalfish":
                str = "TROPICAL_FISH";
                break;
            case "wanderingtrader":
                str = "WANDERING_TRADER";
                break;
            case "witherskeleton":
                str = "WITHER_SKELETON";
                break;
            case "zombiehorse":
                str = "ZOMBIE_HORSE";
                break;
            case "zombievillager":
                str = "ZOMBIE_VILLAGER";
                break;
            default:
                str = str.toUpperCase();
                break;
        }
        return EntityType.valueOf(str);
    }

}
