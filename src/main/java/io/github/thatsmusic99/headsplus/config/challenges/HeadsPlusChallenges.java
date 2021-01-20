package io.github.thatsmusic99.headsplus.config.challenges;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.util.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class HeadsPlusChallenges extends ConfigSettings {

    public HeadsPlusChallenges() {
        this.conName = "challenges";
        reloadC();
    }

    private boolean updated = false;

    @Override
    public void reloadC() {
        performFileChecks();
        if (configF.length() < 1) {
            updated = true;
            load();
        }
        double v = config.getDouble("challenges.options.current-version");
        if ((v < 1.3 || updated)) {
            config.set("challenges.options.current-version", 1.3);
            updateChallenges();
        }
        config.addDefault("challenges.options.prepare-sections", true);
        config.addDefault("challenges.options.prepare-icons", true);
        config.addDefault("challenges.options.prepare-rewards", true);
        config.options().copyDefaults(true);
        save();
        addChallenges();
        updated = false;
    }

    @Override
    public void load() {
        getConfig().options().header("HeadsPlus by Thatsmusic99");
        getConfig().addDefault("challenges.options.current-version", 1.3);
        getConfig().addDefault("challenges.options.prepare-icons", true);
        getConfig().addDefault("challenges.options.prepare-rewards", true);
        getConfig().set("challenges.options.prepare-sections", null);
        double v = getConfig().getDouble("challenges.options.current-version");
        if (v < 1.3) {
            updated = true;
            getConfig().set("challenges.options.current-version", 1.3);
            updateChallenges();
        }
        getConfig().addDefault("challenges.options.update-challenges", true);
        getConfig().options().copyDefaults(true);
        save();
    }

    private void addChallenges() {
        HeadsPlus hp = HeadsPlus.getInstance();
        hp.getChallenges().clear();
        hp.getChallengeSections().clear();
        HashMap<String, Boolean> prepareOptions = new HashMap<>();
        prepareOptions.put("rewards", config.getBoolean("challenges.options.prepare-rewards"));
        prepareOptions.put("icons", config.getBoolean("challenges.options.prepare-icons"));
        LinkedHashMap<String, ChallengeSection> sections = new LinkedHashMap<>();
        for (String section : config.getConfigurationSection("sections").getKeys(false)) {
            if (section.equalsIgnoreCase("current-version") || section.equalsIgnoreCase("options")) continue;
            try {
                sections.put(section, getSection(section));
            } catch (NullPointerException ex) {
                hp.getLogger().warning(ex.getMessage());
            }
        }
        HashMap<String, Reward> rewards = new HashMap<>();
        if (prepareOptions.get("rewards")) {
            for (String rewardName : config.getConfigurationSection("rewards").getKeys(false)) {
                try {
                    Reward reward = getReward(rewardName);
                    rewards.put(rewardName, reward);
                } catch (NullPointerException ex) {
                    hp.getLogger().warning("Error when creating reward: " + ex.getMessage());
                }
            }
        }
        HashMap<String, ItemStack> icons = new HashMap<>();
        if (prepareOptions.get("icons")) {
            for (String iconName : config.getConfigurationSection("icons").getKeys(false)) {
                try {
                    icons.put(iconName, getIcon(iconName));
                } catch (NullPointerException ex) {
                    hp.getLogger().warning(ex.getMessage());
                }
            }
        }
        for (String st : config.getConfigurationSection("challenges").getKeys(false)) {
            try {
                if (st.equalsIgnoreCase("options")) continue;
                ConfigurationSection challenge = HPUtils.notNull(config.getConfigurationSection("challenges." + st), "Challenge section " + st + " seems to be null!");
                String name = HPUtils.notNull(challenge.getString("name"), "Challenge name for " + st + " not found!");
                String header = HPUtils.notNull(challenge.getString("header"), "Challenge header for " + st + " not found!");
                List<String> desc = HPUtils.notNull(challenge.getStringList("description"), "Challenge description for " + st + " not found!");
                HeadsPlusChallengeTypes type;
                try {
                    type = HeadsPlusChallengeTypes.valueOf(challenge.getString("type").toUpperCase());
                } catch (Exception ex) {
                    continue;
                }
                int min = challenge.getInt("min");
                String headType = HPUtils.notNull(challenge.getString("head-type"), "Head type for " + st + " not found!");
                int difficulty = challenge.getInt("difficulty");
                Reward reward;
                if (prepareOptions.get("rewards")) {
                    reward = rewards.get(challenge.getString("reward")).clone();
                } else {
                    reward = getReward(challenge.getString("reward"));
                }
                Validate.notNull(reward, "Reward for " + st + " is invalid!");
                String iconName = challenge.getString("icon");
                ItemStack icon;
                String completeIconName = config.getString("challenges." + st + ".completed-icon");
                ItemStack completedIcon;
                if (prepareOptions.get("icons")) {
                    icon = icons.get(iconName);
                    completedIcon = icons.get(completeIconName);
                } else {
                    icon = getIcon(iconName);
                    completedIcon = getIcon(completeIconName);
                }
                Validate.notNull(icon, "Icon for " + st + " not found!");
                Validate.notNull(completedIcon, "Completed icon for " + st + " not found!");
                Challenge c = new Challenge(st, name, header, desc, min, type, headType, reward, difficulty, icon, completedIcon);
                hp.getChallenges().add(c);
                sections.get(challenge.getString("section")).addChallenge(c);
            } catch (NullPointerException ex) {
                hp.getLogger().warning(ex.getMessage());
            }

        }

        hp.getChallengeSections().addAll(sections.values());
    }

    private void updateChallenges() {
        if (getConfig().getBoolean("stop-hard-reset")) {
            // Add the default icons first
            config.addDefault("icons.default.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 14).getType().name());
            config.addDefault("icons.default.data-value", 14);
            config.addDefault("icons.default-completed.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 13).getType().name());
            config.addDefault("icons.default-completed.data-value", 13);
            // Then sections will follow
            for (HeadsPlusChallengeDifficulty section : HeadsPlusChallengeDifficulty.values()) {
                config.addDefault("sections." + section.name() + ".material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA,  section.color.ordinal()).getType().name());
                config.addDefault("sections." + section.name() + ".material-data", section.color.ordinal());
                config.addDefault("sections." + section.name() + ".display-name", section.displayName);
                config.addDefault("sections." + section.name() + ".lore", new ArrayList<>());
                // And now the challenges inside it
                try {
                    for (String challenge : config.getConfigurationSection("challenges." + section.name()).getKeys(false)) {
                        // Get the values
                        String name = config.getString("challenges." + section.name() + "." + challenge + ".name");
                        String header = config.getString("challenges." + section.name() + "." + challenge + ".header");
                        List<String> desc = config.getStringList("challenges." + section.name() + "." + challenge + ".description");
                        String type = config.getString("challenges." + section.name() + "." + challenge + ".type");
                        int min = config.getInt("challenges." + section.name() + "." + challenge + ".min");
                        String reward  = config.getString("challenges." + section.name() + "." + challenge + ".reward-type");
                        Object rewardVal = config.get("challenges." + section.name() + "." + challenge + ".reward-value");
                        int items = config.getInt("challenges." + section.name() + "." + challenge + ".item-amount");
                        String headType = config.getString("challenges." + section.name() + "." + challenge + ".head-type");
                        int xp = config.getInt("challenges." + section.name() + "." + challenge + ".xp");
                        String sender = config.getString("challenges." + section.name() + "." + challenge + ".command-sender");
                        String rewardString = config.getString("challenges." + section.name() + "." + challenge + ".reward-string");

                        config.addDefault("rewards." + challenge + ".type", reward);
                        config.addDefault("rewards." + challenge + ".base-value", rewardVal);
                        config.addDefault("rewards." + challenge + ".item-amount", items);
                        config.addDefault("rewards." + challenge + ".xp", xp);
                        config.addDefault("rewards." + challenge + ".command-sender", sender != null ? sender : "player");
                        config.addDefault("rewards." + challenge + ".reward-string", rewardString);
                        config.addDefault("rewards." + challenge + ".multiply-by-difficulty", true);

                        // Add section information
                        config.addDefault("challenges." + challenge + ".name", name);
                        config.addDefault("challenges." + challenge + ".header", header);
                        config.addDefault("challenges." + challenge + ".description", desc);
                        config.addDefault("challenges." + challenge + ".type", type);
                        config.addDefault("challenges." + challenge + ".min", min);
                        config.addDefault("challenges." + challenge + ".reward", challenge);
                        config.addDefault("challenges." + challenge + ".head-type", headType);
                        config.addDefault("challenges." + challenge + ".section", section.name());
                        config.addDefault("challenges." + challenge + ".difficulty", 1);
                        config.addDefault("challenges." + challenge + ".icon", "default");
                        config.addDefault("challenges." + challenge + ".completed-icon", "default-completed");

                        config.set("challenges." + section.name() + "." + challenge, null);
                    }
                } catch (NullPointerException ignored) {
                    // Some sections have no challenges
                }

            }

        } else {
            config.set("challenges", null);
            config.addDefault("challenges.options.current-version", 1.3);
            config.addDefault("challenges.starter.name", "Starter");
            config.addDefault("challenges.starter.header", "&8[&6&lStarter Challenge&8]");
            config.addDefault("challenges.starter.description", Arrays.asList("&7Don't worry, just", "&7try this out ;)"));
            config.addDefault("challenges.starter.type", "MISC");
            config.addDefault("challenges.starter.min", 0);
            config.addDefault("challenges.starter.reward", "default");
            config.addDefault("challenges.starter.head-type", "");
            config.addDefault("challenges.starter.section", "EASY");
            config.addDefault("challenges.starter.difficulty", 1);
            config.addDefault("challenges.starter.icon", "default");
            config.addDefault("challenges.starter.completed-icon", "default-completed");

            config.addDefault("rewards.default.type", "ECO");
            config.addDefault("rewards.default.base-value", 50);
            config.addDefault("rewards.default.base-xp", 20);
            config.addDefault("rewards.default.item-amount", 0);
            config.addDefault("rewards.default.command-sender", "player");
            config.addDefault("rewards.default.multiply-by-difficulty", true);

            config.addDefault("icons.default.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 14).getType().name());
            config.addDefault("icons.default.data-value", 14);
            config.addDefault("icons.default-completed.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 13).getType().name());
            config.addDefault("icons.default-completed.data-value", 13);

            int difficulty = 5;
            for (HeadsPlusChallengeDifficulty section : HeadsPlusChallengeDifficulty.values()) {
                config.addDefault("sections." + section.name() + ".material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA,  section.color.ordinal()).getType().name());
                config.addDefault("sections." + section.name() + ".material-data", section.color.ordinal());
                config.addDefault("sections." + section.name() + ".display-name", section.displayName);
                config.addDefault("sections." + section.name() + ".lore", new ArrayList<>());
                for (String t : EntityDataManager.ableEntities) {
                    int multiplier = 5;
                    int tempDif = difficulty;
                    switch (t) {
                        case "ELDER_GUARDIAN":
                        case "ENDER_DRAGON":
                        case "WITHER":
                            multiplier = 1;
                            tempDif = difficulty / 5;
                    }
                    String s = numberToRomanNumeral(section.min);
                    String e = t.replaceAll("_", " ");
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.name", e + "-" + s + " Hunting");
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.header", "&8[&c&l" + HeadsPlus.capitalize(e.toLowerCase()) + " Hunting " + s + "&8]");
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.description", Arrays.asList("&7Get " + (section.min * multiplier * tempDif) + " heads from", "&7killing " + t.toLowerCase().replaceAll("_", " ") + "(s)!"));
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.type", "LEADERBOARD");
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.min", section.min * multiplier * tempDif);
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.reward", "default");
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.head-type", t);
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.section", section.name());
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.difficulty", section.min);
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.icon", "default");
                    config.addDefault("challenges." + t + "-" + section.min + "-hunting.completed-icon", "default-completed");

                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.name", e + "-" + s + " Crafting");
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.header", "&8[&a&l"  + HeadsPlus.capitalize(e.toLowerCase()) + " Crafting " + s + "&8]");
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.description", Arrays.asList("&7Get " + (section.min * multiplier * tempDif) + " heads from", "&7crafting " + t.toLowerCase().replaceAll("_", " ") + " heads!"));
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.type", "CRAFTING");
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.min", section.min * multiplier * tempDif);
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.reward", "default");
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.head-type", t);
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.section", section.name());
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.difficulty", section.min);
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.icon", "default");
                    config.addDefault("challenges." + t + "-" + section.min + "-crafting.completed-icon", "default-completed");

                    config.addDefault("challenges." + t + "-" + section.min + "-selling.name", e + "-" + s + " Selling");
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.header", "&8[&e&l" + HeadsPlus.capitalize(e.toLowerCase()) + " Selling " + s + "&8]");
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.description", Arrays.asList("&7Sell a total of", "&7" + (section.min * multiplier * tempDif) + " " + t.toLowerCase().replaceAll("_", " ") + " heads!"));
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.type", "SELLHEAD");
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.min", section.min * multiplier * tempDif);
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.reward", "default");
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.head-type", t);
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.section", section.name());
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.difficulty", section.min);
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.icon", "default");
                    config.addDefault("challenges." + t + "-" + section.min + "-selling.completed-icon", "default-completed");
                }
                difficulty += 5;
            }

        }

        config.options().copyDefaults(true);
        save();
    }

    private String numberToRomanNumeral(int in) {
        String no = String.valueOf(in);
        int length = no.length();
        StringBuilder numeral = new StringBuilder();
        int pos = 0;
        for (int i = length - 1; i > -1; i--) {
            int amount;
            int num = Integer.parseInt(String.valueOf(no.charAt(i)));
            switch (no.charAt(i)) {
                case '0':
                    break;
                case '1':
                case '2':
                case '3':
                    amount = num;
                    if (pos == 0) {
                        for (int j = 0; j < amount; j++) {
                            numeral.insert(0, "I");
                        }
                    } else if (pos == 1) {
                        for (int j = 0; j < amount; j++) {
                            numeral.insert(0, "X");
                        }
                    } else {
                        for (int j = 0; j < amount; j++) {
                            numeral.insert(0, "C");
                        }
                    }
                    break;
                case '4':
                    if (pos == 0) {
                        numeral.insert(0, "IV");
                    } else if (pos == 1) {
                        numeral.insert(0, "XL");
                    } else {
                        numeral.insert(0, "CD");
                    }
                    break;
                case '5':
                case '6':
                case '7':
                case '8':
                    amount = num % 5;
                    StringBuilder fullNumber;
                    if (pos == 0) {
                        fullNumber = new StringBuilder("V");
                        for (int j = 0; j < amount; j++) {
                            fullNumber.append("I");
                        }
                    } else if (pos == 1) {
                        fullNumber = new StringBuilder("L");
                        for (int j = 0; j < amount; j++) {
                            fullNumber.append("X");
                        }
                    } else {
                        fullNumber = new StringBuilder("D");
                        for (int j = 0; j < amount; j++) {
                            fullNumber.append("C");
                        }
                    }
                    numeral.insert(0, fullNumber.toString());
                    break;
                case '9':
                    if (pos == 0) {
                        numeral.insert(0, "IX");
                    } else if (pos == 1) {
                        numeral.insert(0, "XC");
                    } else {
                        numeral.insert(0, "CI");
                    }
                    break;

            }
            pos++;
        }
        return numeral.toString();
    }

    private Reward getReward(String rewardName) {
        ConfigurationSection reward = HPUtils.notNull(config.getConfigurationSection("rewards." + rewardName), "Reward section for " + rewardName + " seems to be null!");
        HPChallengeRewardTypes rewardType;
        try {
            rewardType = HPChallengeRewardTypes.valueOf(reward.getString("type").toUpperCase());
        } catch (Exception e) {
            rewardType = HPChallengeRewardTypes.NONE;
        }
        Object rewardVal = HPUtils.notNull(reward.get("base-value"), "Value for " + rewardName + "'s reward missing!");
        int items = reward.getInt("item-amount");
        int xp = config.getInt("rewards." + rewardName + ".base-xp");
        String sender = config.getString("rewards." + rewardName + ".command-sender");
        String rewardString = config.getString("rewards." + rewardName + ".reward-string");
        boolean multiply = config.getBoolean("rewards." + rewardName + ".multiply-by-difficulty");

        return new Reward(rewardName, rewardType, rewardVal, items, sender, xp, multiply, rewardString);
    }

    private ItemStack getIcon(String iconName) {
        ItemStack icon = new ItemStack(HPUtils.notNull(Material.getMaterial(config.getString("icons." + iconName + ".material")), "Material for " + iconName + " does not exist!"), 1, (byte) config.getInt("icons." + iconName + ".data-value"));
        String s = config.getString("icons." + iconName + ".skull-name");
        if (s != null && !s.isEmpty() ) {
            if (s.startsWith("HP#")) {
                icon = HeadsPlus.getInstance().getHeadsXConfig().getSkull(s);
            } else {
                SkullMeta sm = (SkullMeta) icon.getItemMeta();
                sm = HeadsPlus.getInstance().getNMS().setSkullOwner(s, sm);
                icon.setItemMeta(sm);
            }
        }
        return icon;
    }

    private ChallengeSection getSection(String section) {
        Material material = Material.valueOf(HPUtils.notNull(config.getString("sections." + section + ".material"), "Material for section " + section + " seems to not exist!").toUpperCase());
        int data = config.getInt("sections." + section + ".material-data");
        String displayName = HPUtils.notNull(config.getString("sections." + section + ".display-name"), "Section " + section + " does not have a display name!");
        List<String> lore = HPUtils.notNull(config.getStringList("sections." + section + ".lore"), "Section " + section + " does not have a lore option! This can cause problems!");
        return new ChallengeSection(material, (byte) data, displayName, lore, section);
    }
}
