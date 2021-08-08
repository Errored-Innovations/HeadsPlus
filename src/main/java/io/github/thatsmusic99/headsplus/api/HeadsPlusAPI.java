package io.github.thatsmusic99.headsplus.api;

import com.mojang.authlib.GameProfile;
import io.github.thatsmusic99.headsplus.managers.ChallengeManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

@Deprecated
public class HeadsPlusAPI {

    public static String getTexture(String owner) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(owner);
        GameProfile gm = new GameProfile(p.getUniqueId(), owner);
        return gm.getProperties().get("textures").iterator().next().getValue();
    }

    public static List<Challenge> getChallenges() {
        return ChallengeManager.get().getChallenges();
    }

}
