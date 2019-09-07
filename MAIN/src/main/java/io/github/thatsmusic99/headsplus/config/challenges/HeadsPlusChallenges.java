package io.github.thatsmusic99.headsplus.config.challenges;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadsPlusChallenges extends ConfigSettings {

    public HeadsPlusChallenges() {
        this.conName = "challenges";
        reloadC(false);
    }

    private boolean updated = false;

    @Override
    public void reloadC(boolean a) {
        if (configF == null || !configF.exists()) {
            configF = new File(HeadsPlus.getInstance().getDataFolder(), "challenges.yml");
        }
        config = YamlConfiguration.loadConfiguration(configF);
        if (configF.length() < 1000) {
            updated = true;
            load(false);
        }
        boolean b = getConfig().getBoolean("challenges.options.update-challenges");
        double v = getConfig().getDouble("challenges.options.current-version");
        if ((v < 1.3 || updated)) {
            getConfig().set("challenges.options.current-version", 1.3);
            updateChallenges();
        }
        config.options().copyDefaults(true);
        save();
        addChallenges();
        updated = false;
    }

    @Override
    public void load(boolean aaaan) {
        getConfig().options().header("HeadsPlus by Thatsmusic99");
        getConfig().addDefault("challenges.options.current-version", 1.3);
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
        HeadsPlus.getInstance().getChallenges().clear();
        List<ChallengeSection> sections = new ArrayList<>();
        for (String section : config.getConfigurationSection("sections").getKeys(false)) {
            if (section.equalsIgnoreCase("current-version") || section.equalsIgnoreCase("options")) continue;
            Material material = Material.valueOf(config.getString("sections." + section + ".material").toUpperCase());
            int data = config.getInt("sections." + section + ".material-data");
            String displayName = config.getString("sections." + section + ".display-name");
            List<String> lore = config.getStringList("sections." + section + ".lore");
            sections.add(new ChallengeSection(material, (byte) data, displayName, lore, section));
        }
        for (String st : config.getConfigurationSection("challenges").getKeys(false)) {
            String name = config.getString("challenges." + st + ".name");
            String header = config.getString("challenges." + st + ".header");
            List<String> desc = config.getStringList("challenges." + st + ".description");
            HeadsPlusChallengeTypes type;
            try {
                type = HeadsPlusChallengeTypes.valueOf(config.getString("challenges." + st + ".type").toUpperCase());
            } catch (Exception ex) {
                continue;
            }
            int min = config.getInt("challenges." + st + ".min");
            String headType = config.getString("challenges." + st + ".head-type");
            int difficulty = config.getInt("challenges." + st + ".difficulty");
            // Reward information
            String rewardName = config.getString("challenges." + st + ".reward");
            HPChallengeRewardTypes rewardType;
            try {
                rewardType = HPChallengeRewardTypes.valueOf(config.getString("rewards." + rewardName + ".type").toUpperCase());
            } catch (Exception e) {
                rewardType = HPChallengeRewardTypes.NONE;
            }
            Object rewardVal = config.get("rewards." + rewardName + ".base-value");
            int items = config.getInt("rewards." + rewardName + ".item-amount");
            int xp = config.getInt("rewards." + rewardName + ".base-xp");
            String sender = config.getString("rewards." + rewardName + ".command-sender");
            String rewardString = config.getString("rewards." + rewardName + ".reward-string");
            boolean multiply = config.getBoolean("rewards." + rewardName + ".multiply-by-difficulty");

            Reward reward1 = new Reward(rewardName, rewardType, rewardVal, items, sender, xp, multiply, rewardString);

            String iconName = config.getString("challenges." + st + ".icon");
            ItemStack icon;
            String completeIconName = config.getString("challenges." + st + ".completed-icon");
            ItemStack completedIcon;
            try {
                icon = new ItemStack(Material.getMaterial(config.getString("icons." + iconName + ".material")), 1, (byte) config.getInt("icons." + iconName + ".data-value"));
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
                completedIcon = new ItemStack(Material.getMaterial(config.getString("icons." + completeIconName + ".material")), 1, (byte) config.getInt("icons." + completeIconName+ ".data-value"));
                s = config.getString("icons." + completeIconName + ".skull-name");
                if (s != null && !s.isEmpty() ) {
                    if (s.startsWith("HP#")) {
                        completedIcon = HeadsPlus.getInstance().getHeadsXConfig().getSkull(s);
                    } else {
                        SkullMeta sm = (SkullMeta) completedIcon.getItemMeta();
                        sm = HeadsPlus.getInstance().getNMS().setSkullOwner(s, sm);
                        completedIcon.setItemMeta(sm);
                    }
                }
            } catch (Exception e) {
                continue;
            }

            Challenge c = new Challenge(st, name, header, desc, min, type, headType, reward1, difficulty, icon, completedIcon);
            HeadsPlus.getInstance().getChallenges().add(c);
            for (ChallengeSection section : sections) {
                if (section.getName().equalsIgnoreCase(config.getString("challenges." + st + ".section"))) {
                    section.addChallenge(c);
                }
            }
        }
        HeadsPlus.getInstance().getChallengeSections().addAll(sections);
    }

    private void updateChallenges() {
        if (getConfig().getBoolean("stop-hard-reset")) {
            // SECTIONS FIRST
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

                        config.addDefault("rewards." + challenge + ".type", reward);
                        config.addDefault("rewards." + challenge + ".base-value", rewardVal);
                        config.addDefault("rewards." + challenge + ".item-amount", items);
                        config.addDefault("rewards." + challenge + ".xp", xp);
                        config.addDefault("rewards." + challenge + ".command-sender", sender != null ? sender : "player");
                        config.addDefault("rewards." + challenge + ".reward-string", rewardString);
                        config.addDefault("rewards." + challenge + ".multiply-by-difficulty", true);

                        config.set("challenges." + section.name() + "." + challenge, null);
                    }
                } catch (NullPointerException ignored) {
                    // Some sections have no challenges
                }

            }
            config.addDefault("icons.default.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 14).getType().name());
            config.addDefault("icons.default.data-value", 14);
            config.addDefault("icons.default-completed.material", HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 13).getType().name());
            config.addDefault("icons.default-completed.data-value", 13);
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
                for (EntityType t : HeadsPlus.getInstance().getDeathEvents().ableEntities) {
                    String s = numberToRomanNumeral(section.min);
                    String e = t.name().replaceAll("_", " ");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.name", e + "-" + s + " Hunting");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.header", "&8[&c&l" + e + "-" + s + " Hunting&8]");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.description", Arrays.asList("&7Get " + (section.min * 5 * difficulty) + " heads from", "&7killing " + t.name().toLowerCase().replaceAll("_", " ") + "(s)!"));
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.type", "LEADERBOARD");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.min", section.min * 5 * difficulty);
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.reward", "default");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.head-type", t.name());
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.section", section.name());
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.difficulty", section.min);
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.icon", "default");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-hunting.completed-icon", "default-completed");

                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.name", e + "-" + s + " Crafting");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.header", "&8[&c&l" + e + "-" + s + " Crafting&8]");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.description", Arrays.asList("&7Get " + (section.min * 5 * difficulty) + " heads from", "&7crafting " + t.name().toLowerCase().replaceAll("_", " ") + " heads!"));
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.type", "CRAFTING");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.min", section.min * 5 * difficulty);
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.reward", "default");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.head-type", t.name());
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.section", section.name());
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.difficulty", section.min);
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.icon", "default");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-crafting.completed-icon", "default-completed");

                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.name", e + "-" + s + " Selling");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.header", "&8[&c&l" + e + "-" + s + " Selling&8]");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.description", Arrays.asList("&7Sell a total of", "&7" + (section.min * 5 * difficulty) + " " + t.name().toLowerCase().replaceAll("_", " ") + " heads!"));
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.type", "SELLHEAD");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.min", section.min * 5 * difficulty);
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.reward", "default");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.head-type", t.name());
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.section", section.name());
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.difficulty", section.min);
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.icon", "default");
                    config.addDefault("challenges." + t.name() + "-" + section.min + "-selling.completed-icon", "default-completed");
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
            switch (no.charAt(i)) {
                case '0':
                    break;
                case '1':
                case '2':
                case '3':
                    amount = Integer.parseInt(String.valueOf(no.charAt(i)));
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
                    amount = Integer.parseInt(String.valueOf(no.charAt(i))) % 5;
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
}
