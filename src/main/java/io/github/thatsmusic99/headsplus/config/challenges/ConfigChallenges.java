package io.github.thatsmusic99.headsplus.config.challenges;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.FeatureConfig;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigChallenges extends FeatureConfig {

    private static ConfigChallenges instance;

    public ConfigChallenges() {
        super("challenges.yml");
        instance = this;
    }

    @Override
    public void loadDefaults() {
        addDefault("options.current-version", 1.3);
        addDefault("options.prepare-icons", true);
        addDefault("options.prepare-rewards", true);
        addDefault("options.update-challenges", true);

        makeSectionLenient("rewards");
        addComment("rewards", "Rewards are able to be provided to a player when completing a challenge.\n" +
                "You are able to easily add them using the /hp settings rewards command if you are unsure on how to configure this.");
        addExample("rewards.default.type", "ECO", "The type of reward to use.\n" +
                "Valid types include ECO, ADD_GROUP, REMOVE_GROUP, GIVE_ITEM AND RUN_COMMAND.\n" +
                "GIVE_ITEM requires an additional config section called \"item\" with properties \"material\" and \"amount\".");
        addExample("rewards.default.base-value", 50);
        addExample("rewards.default.base-xp", 20);
        addExample("rewards.default.multiply-by-difficulty", true);

        makeSectionLenient("icons");

        addExample("icons.default.material", "RED_TERRACOTTA");
        addExample("icons.default-completed.material", "LIME_TERRACOTTA");

        int difficulty = 5;

        makeSectionLenient("sections");

        makeSectionLenient("challenges");

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

        for (HeadsPlusChallengeDifficulty section : HeadsPlusChallengeDifficulty.values()) {
            addExample("sections." + section.name() + ".material", section.material);
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
                addExample("challenges." + t + "-" + section.min + "-hunting.header", "&8[&c&l" + HeadsPlus.capitalize(e) + " Hunting " + s + "&8]");
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
                addExample("challenges." + t + "-" + section.min + "-crafting.header", "&8[&a&l"  + HeadsPlus.capitalize(e) + " Crafting " + s + "&8]");
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
                addExample("challenges." + t + "-" + section.min + "-selling.header", "&8[&e&l" + HeadsPlus.capitalize(e) + " Selling " + s + "&8]");
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

    public static ConfigChallenges get() {
        return instance;
    }

    @Override
    public void moveToNew() {
        moveTo("challenges.options.current-version", "options.current-version");
        moveTo("challenges.options.prepare-icons", "options.prepare-icons");
        moveTo("challenges.options.prepare-rewards", "options.prepare-rewards");

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
                    numeral.insert(0, fullNumber);
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

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().CHALLENGES;
    }
}
