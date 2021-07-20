package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.config.challenges.ConfigChallenges;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ChallengeManager {

    private final HashMap<String, ItemStack> icons = new HashMap<>();
    private final HashMap<String, Challenge> challenges = new HashMap<>();
    private final HashMap<String, ChallengeSection> sections = new LinkedHashMap<>();
    private static ChallengeManager instance;

    public ChallengeManager() {
        instance = this;
        init();
    }

    public void reload() {
        challenges.clear();
        sections.clear();
        icons.clear();
        init();
    }

    public static ChallengeManager get() {
        return instance;
    }

    public List<Challenge> getChallenges() {
        return new ArrayList<>(challenges.values());
    }

    public List<String> getChallengeNames() {
        return new ArrayList<>(challenges.keySet());
    }

    public Challenge getChallengeByName(String name) {
        return challenges.get(name);
    }

    public ChallengeSection getSectionByName(String name) {
        return sections.get(name);
    }

    public List<ChallengeSection> getChallengeSections() {
        return new ArrayList<>(sections.values());
    }

    public void init() {
        ConfigChallenges challenges = ConfigChallenges.get();
        if (challenges.get("sections") == null) return;
        if (challenges.get("challenges") == null) return;
        // Set up icons first
        for (String key : challenges.getConfigSection("icons").getKeys(false)) {
            try {
                HeadsPlus.debug("Attempting to register icon " + key + "...");
                ConfigSection section = challenges.getConfigSection("icons." + key);
                if (section == null) continue;
                ItemStack item;
                String material = Objects.requireNonNull(section.getString("material"), "Material for icon " + key + " is null!");
                if (material.startsWith("HP#")) {
                    item = HeadManager.get().getHeadInfo(material).buildHead().join();
                } else {
                    Material actualMaterial = Objects.requireNonNull(Material.getMaterial(material.toUpperCase()), "Material " + material.toUpperCase() + " does not exist!");
                    item = new ItemStack(actualMaterial);
                }
                String displayName = section.getString("display-name");
                if (displayName != null) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                        item.setItemMeta(meta);
                    }
                }
                // TODO lore
                icons.put(key, item);
                HeadsPlus.debug("Registered icon " + key + ".");
            } catch (NullPointerException ex) {

            }
        }
        // Set up sections
        for (String key : challenges.getConfigSection("sections").getKeys(false)) {
            try {
                HeadsPlus.debug("Attempting to register section " + key + "...");
                ConfigSection section = challenges.getConfigSection("sections." + key);
                if (section == null) continue;
                String materialStr = Objects.requireNonNull(section.getString("material"),
                        "Material for " + key + " was not found!");
                Material material = Objects.requireNonNull(Material.getMaterial(materialStr.toUpperCase()),
                        "Material " + materialStr.toUpperCase() + " for " + key + " is not an existing material!");
                String displayName = Objects.requireNonNull(section.getString("display-name"),
                        "Display name for " + key + " was not found!");
                List<String> lore = section.getStringList("lore");
                this.sections.put(key, new ChallengeSection(material, displayName, lore, key));
                HeadsPlus.debug("Registered section " + key + ".");
            } catch (NullPointerException ex) {
                HeadsPlus.get().getLogger().warning(ex.getMessage());
            }
        }
        //
        for (String key : challenges.getConfigSection("challenges").getKeys(false)) {
            try {
                HeadsPlus.debug("Attempting to register challenge " + key + "...");
                // Get the challenge section
                ConfigSection section = challenges.getConfigSection("challenges." + key);
                if (section == null) continue;

                // Check that the main challenge icon exists
                // TODO - shove repeated code into a function?
                String iconId = Objects.requireNonNull(section.getString("icon"),
                        "Icon for " + key + " not found!");
                if (!icons.containsKey(iconId))
                    throw new NullPointerException("Icon " + iconId + " for " + key + " does not exist!");

                // Check that the completed challenge icon exists
                String completeIconId = Objects.requireNonNull(section.getString("completed-icon"),
                        "Completed icon for " + key + " not found!");
                if (!icons.containsKey(completeIconId))
                    throw new NullPointerException("Completed icon " + iconId + " for " + key + " does not exist!");

                // Load the challenge
                Challenge challenge = Challenge.fromConfigSection(key, section, icons.get(iconId), icons.get(completeIconId));

                // Add the challenge to the section
                String sectionStr = Objects.requireNonNull(section.getString("section"),
                        "Section for " + key + " was not found!");
                if (!sections.containsKey(sectionStr))
                    throw new NullPointerException("Section " + sectionStr + " is not registered (challenge: " + key + "!)");
                sections.get(sectionStr).addChallenge(challenge);

                // Register the challenge fully
                this.challenges.put(key, challenge);
                HeadsPlus.debug("Registered challenge " + key + ".");
            } catch (NullPointerException ex) {
                HeadsPlus.get().getLogger().warning(ex.getMessage());
            }
        }
    }
}
