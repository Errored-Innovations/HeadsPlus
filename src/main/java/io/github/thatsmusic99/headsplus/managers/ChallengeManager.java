package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.config.challenges.ConfigChallenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

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
        breakUpTasks(0, challenges.getConfigSection("icons").getKeys(false), this::registerIcon, () ->
                // Then sections
                breakUpTasks(0, challenges.getConfigSection("sections").getKeys(false), this::registerSection, () ->
                // And then challenges last
                breakUpTasks(0, challenges.getConfigSection("challenges").getKeys(false), this::registerChallenge, null)));

    }

    private void registerIcon(String key) {
        try {
            HeadsPlus.debug("Attempting to register icon " + key + "...");
            ConfigSection section = ConfigChallenges.get().getConfigSection("icons." + key);
            if (section == null) return;
            ItemStack item;
            String material = Objects.requireNonNull(section.getString("material"), "Material for icon " + key + " is null!");
            if (material.startsWith("HP#")) {
                item = HeadManager.get().getHeadInfo(material).forceBuildHead();
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
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
            icons.put(key, item);
            HeadsPlus.debug("Registered icon " + key + ".");
        } catch (NullPointerException ex) {
            HeadsPlus.get().getLogger().warning("Null value received when registering challenge icon " + key + ": " + ex.getMessage());
        }
    }

    private void registerSection(String key) {
        try {
            HeadsPlus.debug("Attempting to register section " + key + "...");
            ConfigSection section = ConfigChallenges.get().getConfigSection("sections." + key);
            if (section == null) return;
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
            HeadsPlus.get().getLogger().warning("Null value received when registering challenge section " + key + ": " + ex.getMessage());
        }
    }

    private void registerChallenge(String key) {
        try {
            HeadsPlus.debug("Attempting to register challenge " + key + "...");
            // Get the challenge section
            ConfigSection section = ConfigChallenges.get().getConfigSection("challenges." + key);
            if (section == null) return;
            if (key.equals("options")) return;

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
            if (!challenge.canRegister()) return;

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
            HeadsPlus.get().getLogger().warning("Null value received when registering challenge " + key + ": " + ex.getMessage());
        }
    }

    private void breakUpTasks(int start, List<String> keys, Consumer<String> consumer, Runnable nextTask) {
        Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), () -> {
            boolean endReached = false;
            for (int i = start; i < start + 50; i++) {
                if (i >= keys.size()) {
                    endReached = true;
                    break;
                }
                consumer.accept(keys.get(i));
            }
            // Ends this task
            if (endReached) {
                if (nextTask == null) return;
                Bukkit.getScheduler().runTask(HeadsPlus.get(), nextTask);
            } else {
                Bukkit.getScheduler().runTask(HeadsPlus.get(),
                        () -> breakUpTasks(start + 50, keys, consumer, nextTask));
            }
        });
    }
}
