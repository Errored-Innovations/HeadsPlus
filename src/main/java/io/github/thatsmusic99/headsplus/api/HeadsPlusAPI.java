package io.github.thatsmusic99.headsplus.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.github.thatsmusic99.headsplus.managers.DataManager;
import io.github.thatsmusic99.headsplus.util.LeaderboardsCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class HeadsPlusAPI {

    // S
    private final HeadsPlus hp = HeadsPlus.getInstance();

    public ItemStack getHead(String option) {
        return hp.getHeadsXConfig().getSkull(option);
    }

    public boolean isSellable(ItemStack is) {
        if (is.getType() == Material.PLAYER_HEAD) {
            return NBTManager.isSellable(is);
        }
        return false;
    }

    public ItemStack createSkull(String texture, String displayname) {
        ItemStack s = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) s.getItemMeta();
        GameProfile gm = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
        gm.getProperties().put("textures", new Property("texture", texture));
        ProfileFetcher.setProfile(sm, gm);
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
        return ProfileFetcher.setProfile(m, gm);
    }

    public String getSkullType(ItemStack is) {
        return NBTManager.getType(is);
    }

    public int getPlayerInLeaderboards(OfflinePlayer p, String section, String database) {
        try {
            return DataManager.getPlayerScore(p, database, section);
        } catch (NullPointerException ex) {
            return -1;
        }
    }

    public LinkedHashMap<OfflinePlayer, Integer> getScores(String section, String database) {
        return LeaderboardsCache.getType(section, database, false, true);
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
}
