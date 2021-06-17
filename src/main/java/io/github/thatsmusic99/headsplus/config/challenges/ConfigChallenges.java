package io.github.thatsmusic99.headsplus.config.challenges;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.FeatureConfig;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ConfigChallenges extends FeatureConfig {

    public ConfigChallenges() {
        super("challenges");
    }

    @Override
    public void loadDefaults() {
        addDefault("options.current-version", 1.3);
        addDefault("options.prepare-icons", true);
        addDefault("options.prepare-rewards", true);
        addDefault("options.update-challenges", true);

        addLenientSection("challenges");

        addExample("challenges.starter.name", "Starter");
        addExample("challenges.starter.header", "&8[&6&lStarter Challenge&8]");
        addExample("challenges.starter.description", Arrays.asList("&7Don't worry, just", "&7try this out ;)"));
        addExample("challenges.starter.type", "MISC");
        addExample("challenges.starter.min", 0);
        addExample("challenges.starter.reward", "default");
        addExample("challenges.starter.head-type", "");
        addExample("challenges.starter.section", "EASY");
        addExample("challenges.starter.difficulty", 1);
        addExample("challenges.starter.icon", "default");
        addExample("challenges.starter.completed-icon", "default-completed");

        addLenientSection("rewards");

        addExample("rewards.default.type", "ECO");
        addExample("rewards.default.base-value", 50);
        addExample("rewards.default.base-xp", 20);
        addExample("rewards.default.item-amount", 0);
        addExample("rewards.default.command-sender", "player");
        addExample("rewards.default.multiply-by-difficulty", true);

        addLenientSection("icons");

        addExample("icons.default.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 14).getType().name());
        addExample("icons.default.data-value", 14);
        addExample("icons.default-completed.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 13).getType().name());
        addExample("icons.default-completed.data-value", 13);

        int difficulty = 5;

        addLenientSection("sections");

        for (HeadsPlusChallengeDifficulty section : HeadsPlusChallengeDifficulty.values()) {
            addExample("sections." + section.name() + ".material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA,  section.color.ordinal()).getType().name());
            addExample("sections." + section.name() + ".material-data", section.color.ordinal());
            addExample("sections." + section.name() + ".display-name", section.displayName);
            addExample("sections." + section.name() + ".lore", new ArrayList<>());
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
                addExample("challenges." + t + "-" + section.min + "-hunting.name", e + "-" + s + " Hunting");
                addExample("challenges." + t + "-" + section.min + "-hunting.header", "&8[&c&l" + HeadsPlus.capitalize(e.toLowerCase()) + " Hunting " + s + "&8]");
                addExample("challenges." + t + "-" + section.min + "-hunting.description", Arrays.asList("&7Get " + (section.min * multiplier * tempDif) + " heads from", "&7killing " + t.toLowerCase().replaceAll("_", " ") + "(s)!"));
                addExample("challenges." + t + "-" + section.min + "-hunting.type", "LEADERBOARD");
                addExample("challenges." + t + "-" + section.min + "-hunting.min", section.min * multiplier * tempDif);
                addExample("challenges." + t + "-" + section.min + "-hunting.reward", "default");
                addExample("challenges." + t + "-" + section.min + "-hunting.head-type", t);
                addExample("challenges." + t + "-" + section.min + "-hunting.section", section.name());
                addExample("challenges." + t + "-" + section.min + "-hunting.difficulty", section.min);
                addExample("challenges." + t + "-" + section.min + "-hunting.icon", "default");
                addExample("challenges." + t + "-" + section.min + "-hunting.completed-icon", "default-completed");

                addExample("challenges." + t + "-" + section.min + "-crafting.name", e + "-" + s + " Crafting");
                addExample("challenges." + t + "-" + section.min + "-crafting.header", "&8[&a&l"  + HeadsPlus.capitalize(e.toLowerCase()) + " Crafting " + s + "&8]");
                addExample("challenges." + t + "-" + section.min + "-crafting.description", Arrays.asList("&7Get " + (section.min * multiplier * tempDif) + " heads from", "&7crafting " + t.toLowerCase().replaceAll("_", " ") + " heads!"));
                addExample("challenges." + t + "-" + section.min + "-crafting.type", "CRAFTING");
                addExample("challenges." + t + "-" + section.min + "-crafting.min", section.min * multiplier * tempDif);
                addExample("challenges." + t + "-" + section.min + "-crafting.reward", "default");
                addExample("challenges." + t + "-" + section.min + "-crafting.head-type", t);
                addExample("challenges." + t + "-" + section.min + "-crafting.section", section.name());
                addExample("challenges." + t + "-" + section.min + "-crafting.difficulty", section.min);
                addExample("challenges." + t + "-" + section.min + "-crafting.icon", "default");
                addExample("challenges." + t + "-" + section.min + "-crafting.completed-icon", "default-completed");

                addExample("challenges." + t + "-" + section.min + "-selling.name", e + "-" + s + " Selling");
                addExample("challenges." + t + "-" + section.min + "-selling.header", "&8[&e&l" + HeadsPlus.capitalize(e.toLowerCase()) + " Selling " + s + "&8]");
                addExample("challenges." + t + "-" + section.min + "-selling.description", Arrays.asList("&7Sell a total of", "&7" + (section.min * multiplier * tempDif) + " " + t.toLowerCase().replaceAll("_", " ") + " heads!"));
                addExample("challenges." + t + "-" + section.min + "-selling.type", "SELLHEAD");
                addExample("challenges." + t + "-" + section.min + "-selling.min", section.min * multiplier * tempDif);
                addExample("challenges." + t + "-" + section.min + "-selling.reward", "default");
                addExample("challenges." + t + "-" + section.min + "-selling.head-type", t);
                addExample("challenges." + t + "-" + section.min + "-selling.section", section.name());
                addExample("challenges." + t + "-" + section.min + "-selling.difficulty", section.min);
                addDefault("challenges." + t + "-" + section.min + "-selling.icon", "default");
                addDefault("challenges." + t + "-" + section.min + "-selling.completed-icon", "default-completed");
            }
            difficulty += 5;
        }
    }

    @Override
    public void moveToNew() {
        moveTo("challenges.options.current-version", "options.current-version");
        moveTo("challenges.options.prepare-icons", "options.prepare-icons");
        moveTo("challenges.options.prepare-rewards", "options.prepare-rewards");

    }

    @Override
    public void postSave() {
        HeadsPlus hp = HeadsPlus.getInstance();
        hp.getChallenges().clear();
        hp.getChallengeSections().clear();
        HashMap<String, Boolean> prepareOptions = new HashMap<>();
        prepareOptions.put("rewards", getBoolean("challenges.options.prepare-rewards"));
        prepareOptions.put("icons", getBoolean("challenges.options.prepare-icons"));
        LinkedHashMap<String, ChallengeSection> sections = new LinkedHashMap<>();
        for (String section : getConfig().getConfigurationSection("sections").getKeys(false)) {
            if (section.equalsIgnoreCase("current-version") || section.equalsIgnoreCase("options")) continue;
            try {
                sections.put(section, getSection(section));
            } catch (NullPointerException ex) {
                hp.getLogger().warning(ex.getMessage());
            }
        }
        HashMap<String, Reward> rewards = new HashMap<>();
        if (prepareOptions.get("rewards")) {
            for (String rewardName : getConfig().getConfigurationSection("rewards").getKeys(false)) {
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
            for (String iconName : getConfig().getConfigurationSection("icons").getKeys(false)) {
                try {
                    icons.put(iconName, getIcon(iconName));
                } catch (NullPointerException ex) {
                    hp.getLogger().warning(ex.getMessage());
                }
            }
        }
        for (String st : getConfig().getConfigurationSection("challenges").getKeys(false)) {
            try {
                if (st.equalsIgnoreCase("options")) continue;
                ConfigurationSection challenge = HPUtils.notNull(getConfig().getConfigurationSection("challenges." + st), "Challenge section " + st + " seems to be null!");
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
                String completeIconName = getString("challenges." + st + ".completed-icon");
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
        ConfigurationSection reward = HPUtils.notNull(getConfig().getConfigurationSection("rewards." + rewardName), "Reward section for " + rewardName + " seems to be null!");
        HPChallengeRewardTypes rewardType;
        try {
            rewardType = HPChallengeRewardTypes.valueOf(reward.getString("type").toUpperCase());
        } catch (Exception e) {
            rewardType = HPChallengeRewardTypes.NONE;
        }
        Object rewardVal = HPUtils.notNull(reward.get("base-value"), "Value for " + rewardName + "'s reward missing!");
        int items = reward.getInt("item-amount");
        int xp = getInteger("rewards." + rewardName + ".base-xp");
        String sender = getString("rewards." + rewardName + ".command-sender");
        String rewardString = getString("rewards." + rewardName + ".reward-string");
        boolean multiply = getBoolean("rewards." + rewardName + ".multiply-by-difficulty");

        return new Reward(rewardName, rewardType, rewardVal, items, sender, xp, multiply, rewardString);
    }

    private ItemStack getIcon(String iconName) {
        ItemStack icon = new ItemStack(HPUtils.notNull(Material.getMaterial(getString("icons." + iconName + ".material")), "Material for " + iconName + " does not exist!"), 1, (byte) getInteger("icons." + iconName + ".data-value"));
        String s = getString("icons." + iconName + ".skull-name");
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
        Material material = Material.valueOf(HPUtils.notNull(getString("sections." + section + ".material"), "Material for section " + section + " seems to not exist!").toUpperCase());
        int data = getInteger("sections." + section + ".material-data");
        String displayName = HPUtils.notNull(getString("sections." + section + ".display-name"), "Section " + section + " does not have a display name!");
        List<String> lore = HPUtils.notNull(getStringList("sections." + section + ".lore"), "Section " + section + " does not have a lore option! This can cause problems!");
        return new ChallengeSection(material, (byte) data, displayName, lore, section);
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().CHALLENGES;
    }
}
