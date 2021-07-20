package io.github.thatsmusic99.headsplus.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ChallengeSection {

    private final List<Challenge> challenges = new ArrayList<>();
    private final Material material;
    private final String displayName;
    private final String name;
    private final List<String> lore;

    public ChallengeSection(Material mat, String displayName, List<String> lore, String name) {
        this.material = mat;
        this.displayName = displayName;
        this.name = name;
        this.lore = new ArrayList<>();
        for (String str : lore) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', str));
        }
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }

    public void addChallenge(Challenge c) {
        challenges.add(c);
    }
}
