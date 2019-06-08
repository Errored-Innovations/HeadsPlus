package io.github.thatsmusic99.headsplus.config.challenges;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum HeadsPlusChallengeEnums {

    STARTER("starter", "Starter", "&8[&6&lStarter Challenge&8]", Arrays.asList("&7Don't worry, just", "&7try this out ;)"), 0, HeadsPlusChallengeTypes.MISC, HPChallengeRewardTypes.ECO, 50, 0, HeadsPlusChallengeDifficulty.EASY, "", 20, 0.0),
    FIRST_HEADS_SOLD("first_heads_sold", "First Heads Sold", "&8[&a&lFirst Heads sold&8]", Arrays.asList("&7Sell your first", "&7ever 5 heads!"), 5, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.ECO, 100, 0, HeadsPlusChallengeDifficulty.EASY, "total", 50, 0.0),
    BEGINNING_CRAFTER("beginning_crafter", "Beginning Crafter", "&8[&e&lBeginning Crafter&8]", Arrays.asList("&7Craft your first", "&73 heads!"), 3, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 100, 0, HeadsPlusChallengeDifficulty.EASY, "total", 50, 0.0),
    HUNTING_STARTER("hunting_starter", "Hunting Starter", "&8[&c&lHunting Starter&8]", Arrays.asList("&7Kill mobs until you", "&7get 5 heads!"), 5, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 100, 0, HeadsPlusChallengeDifficulty.EASY, "total", 50, 0.0),
    NEW_PLAYER_HUNTER("new_player_hunter", "New Player Hunter", "&8[&c&lNew Player Hunter&8]", Arrays.asList("&7Kill a player and", "&7get their head! :o"), 1, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 150, 0, HeadsPlusChallengeDifficulty.EASY, "player", 200, 0.0),
    MOB_HUNTER("mob_hunter", "Mob Hunter", "&8[&c&lMob Hunter&8]", Arrays.asList("&7Get at least 5 heads", "&7From killing zombies!"), 5, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 200, 0, HeadsPlusChallengeDifficulty.EASY, "zombie", 150, 0.0),
    PROFIT("profit", "Profit", "&8[&a&lProfit!&8]", Collections.singletonList("&7Sell a total of 15 heads!"), 15, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.ECO, 200, 0, HeadsPlusChallengeDifficulty.EASY, "total", 150, 0.0),
    WB_WORKS("wb_works", "Workbench Works", "&8[&e&lThe Workbench works!&8]", Arrays.asList("&7Craft a total", "&7of 15 heads!"), 15, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 200, 0, HeadsPlusChallengeDifficulty.EASY, "total", 150, 0.0),
    BROKEN_BONES("broken_bones", "Broken Bones", "&8[&c&lBroken Bones&8]", Arrays.asList("&7Get at least 10", "&7heads from skeletons!"), 10, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.GIVE_ITEM, Material.REDSTONE, 5, HeadsPlusChallengeDifficulty.EASY_MEDIUM, "skeleton", 300, 0.0),
    PVP_FAN("pvp_fan", "PVP Fan", "&8[&c&lPVP Fan&8]", Arrays.asList("&7Get 10 player heads", "&7from killing players!"), 10, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 200, 0, HeadsPlusChallengeDifficulty.EASY_MEDIUM, "player", 350, 0.0),
    CRAFTING_HOBBYIST("crafting_hobbyist", "Crafting Hobbyist", "&8[&e&lCrafting Hobbyist&8]", Arrays.asList("&7Craft a total", "&7of 30 heads!"), 30, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 200, 0, HeadsPlusChallengeDifficulty.EASY_MEDIUM, "total", 250, 0.0),
    JAB_BLOODTHIRSTY("jab_bloodthirsty", "Just a bit bloodthirsty", "&8[&c&lJust a bit bloodthirsty...&8]", Arrays.asList("&7Get a total of", "&730 heads!"), 30, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 350, 0, HeadsPlusChallengeDifficulty.EASY_MEDIUM, "total", 400, 0.0),
    TOWN_MARKET_OWNER("town_market_owner", "Town Market Owner", "&8[&a&lTown Market Owner&8]", Arrays.asList("&7Sell a total of", "&725 heads!"), 25, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.GIVE_ITEM, Material.IRON_INGOT, 2, HeadsPlusChallengeDifficulty.EASY_MEDIUM, "total", 350, 0.0),
    DR_FRANKENSTEIN("dr_frankenstein", "Dr. Frankenstein", "&8[&e&lDr. Frankenstein&8]", Arrays.asList("&7Craft a total", "&7of 30 zombie heads!"), 30, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 400, 0, HeadsPlusChallengeDifficulty.MEDIUM, "zombie", 400, 0.0),
    BUTCHER("butcher", "Butcher", "&8[&c&lButcher&8]", Arrays.asList("&7Get a total of", "&725 pig heads!"), 25, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.GIVE_ITEM, HeadsPlus.getInstance().getNMS().getNewItems(MaterialTranslator.ChangedMaterials.GRILLED_PORK), 10, HeadsPlusChallengeDifficulty.MEDIUM, "pig", 450, 0.0),
    EVIL_LAUGH("evil_laugh", "\"Nyehehe...\"", "&8[&c&lNyehehe...&8]", Arrays.asList("&7Get a total", "&7of 60 heads!"), 60, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.GIVE_ITEM, Material.DIAMOND, 2, HeadsPlusChallengeDifficulty.MEDIUM, "total", 500, 0.0),
    START_BUSINESS("start_business", "Starting a Business", "&8[&a&lStarting a Business&8]", Arrays.asList("&7Sell a total", "&7of 40 heads!"), 40, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.ECO, 350, 0, HeadsPlusChallengeDifficulty.MEDIUM, "total", 400, 0.0),
    DONT_NEED("dont_need", "I don't need this", "&8[&a&lI don't need this!&8]", Arrays.asList("&7Sell 20 creeper", "&7heads, they may", "&7explode if you don't..."), 20, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.ECO, 300, 0, HeadsPlusChallengeDifficulty.MEDIUM, "creeper", 400, 0.0),
    STRONGER_STEEL("stronger_steel", "Stronger than Steel", "&8[&c&lStronger than Steel&8]", Arrays.asList("&7Get 15 iron", "&7golem heads!"), 15, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.GIVE_ITEM, Material.EMERALD, 3, HeadsPlusChallengeDifficulty.MEDIUM, "irongolem", 450, 0.0),
    NO_END("no_end", "There's no End to this!", "&8[&c&lThere's no End to this!&8]", Arrays.asList("&7Kill and get at", "&7least 25 heads", "&7from endermen!"), 25, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 400, 0, HeadsPlusChallengeDifficulty.MEDIUM, "enderman", 400, 0.0),
    OFF_HEAD("off_head", "Off with their head!", "&8[&c&lOff with their head!&8]", Arrays.asList("&7Get a total of", "&735 player heads!"), 35, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 500, 0, HeadsPlusChallengeDifficulty.MEDIUM_HARD, "player", 450, 0.0),
    WALMART_BOSS("walmart_boss", "Walmart Boss", "&8[&a&lWalmart Boss&8]", Arrays.asList("&7Sell up to", "&760 heads!"), 60, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.ECO, 400, 0, HeadsPlusChallengeDifficulty.MEDIUM_HARD, "total", 400, 0.0),
    TRUE_CRAFTSMAN("true_craftsman", "True Craftsman", "&8[&e&lTrue Craftsman&8]", Arrays.asList("&7Craft up to", "&750 heads!"), 50, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 500, 0, HeadsPlusChallengeDifficulty.MEDIUM_HARD, "total", 550, 0.0),
    SECRETLY_ANTVENOM("secretly_antvenom", "Secretly Antvenom", "&8[&c&lSecretly Antvenom&8]", Collections.singletonList("&7Get 40 cow heads!"), 40, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 550, 0, HeadsPlusChallengeDifficulty.MEDIUM_HARD, "cow", 550, 0.0),
    ON_FIRE("on_fire", "Forever on Fire", "&8[&c&lForever on Fire&8]", Arrays.asList("&7Get a total of", "&740 blaze heads!"), 40, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.GIVE_ITEM, Material.BLAZE_POWDER, 10, HeadsPlusChallengeDifficulty.MEDIUM_HARD, "blaze", 600, 0.0),
    SLIMY_WORK("slimy_work", "Slimy Work", "&8[&e&lSlimy Work&8]", Arrays.asList("&7Craft a total of", "&735 slime heads!"), 35, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 500, 0, HeadsPlusChallengeDifficulty.MEDIUM_HARD, "slime", 550, 0.0),
    MOST_WANTED("most_wanted", "Most Wanted", "&8[&c&lMost Wanted&8]", Arrays.asList("&7Get up to", "&775 heads!"), 75, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 750, 0, HeadsPlusChallengeDifficulty.MEDIUM_HARD, "total", 650, 0.0),
    THE_REAPER("the_reaper", "The Reaper", "&8[&c&lThe Reaper&8]", Arrays.asList("&7Get a total of", "&7100 heads!"), 100, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 900, 0, HeadsPlusChallengeDifficulty.HARD, "total", 850, 0.0),
    AMAZON_PRIME("amazon_prime", "Amazon Prime", "&8[&a&lAmazon Prime&8]", Arrays.asList("&7Sell a total", "&7of 90 heads!"), 90, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.GIVE_ITEM, Material.GOLD_INGOT, 20, HeadsPlusChallengeDifficulty.HARD, "total", 800, 0.0),
    MORE_TEAR("more_tear", "More Than Tears", "&8[&c&lMore Than Tears&8]", Arrays.asList("&7Get a total of", "&730 ghast heads!"), 30, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 850, 0, HeadsPlusChallengeDifficulty.HARD, "ghast", 850, 0.0),
    NEW_OWNER("new_owner", "New Server Owner", "&8[&c&lNew Server Owner&8]", Arrays.asList("&7After getting 70", "&7player heads, surely", "&7there's no owner..."), 70, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.GIVE_ITEM, Material.DIAMOND, 10, HeadsPlusChallengeDifficulty.HARD, "player", 900, 0.0),
    WALKING_DEAD("the_walking_dead", "The Walking Dead", "&8[&c&lThe Walking Dead&8]", Arrays.asList("&7Get a total of", "&760 zombie heads,", "&7dear God..."), 60, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 750, 0, HeadsPlusChallengeDifficulty.HARD, "zombie", 800, 0.0),
    GENOCIDE("genocide", "Genocide", "&8&k[&c&lGENOCIDE&8&k]", Arrays.asList("&7Takes time to", "&7get 200 heads..."), 200, HeadsPlusChallengeTypes.LEADERBOARD, HPChallengeRewardTypes.ECO, 1000, 0, HeadsPlusChallengeDifficulty.HARD, "total", 1500, 0.0),
    AMBIDEXTROUS("ambidextrous", "Ambidextrous", "&8&k[&e&lAMBIDEXTROUS&8&k]", Arrays.asList("&7How many heads", "&7can you craft?", "&7150?"), 150, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 1000, 0, HeadsPlusChallengeDifficulty.HARD, "total", 1500, 0.0),
    MILLIONAIRE("millionaire", "Millionaire", "&8&k[&a&lMILLIONAIRE&8&k]", Arrays.asList("&7Bet you can't", "&7sell 250 heads..."), 250, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.ECO, 1000, 0, HeadsPlusChallengeDifficulty.HARD, "total", 1500, 0.0),
    BACK_AGAIN("back_again", "Back again", "&8[&9&lBack again...&8]", Arrays.asList("&7Welcome back,", "&7help me get", "&7400 heads..."), 400, HeadsPlusChallengeTypes.LEADERBOARD,  HPChallengeRewardTypes.ECO, 1500, 0, HeadsPlusChallengeDifficulty.TEDIOUS, "total", 2000, 1.2),
    RESTARTING_THE_BUSINESS("restarting_business", "Restarting the business", "&8[&9&lRestarting the business&8]", Collections.singletonList("&7Sell 450 heads."), 450, HeadsPlusChallengeTypes.SELLHEAD, HPChallengeRewardTypes.ECO, 1750, 0, HeadsPlusChallengeDifficulty.TEDIOUS, "total", 2000, 1.2),
    CRAFTING_RETURN("crafting_return", "Crafting return", "&8[&9&lReturning back to crafting&8]", Collections.singletonList("&7Craft 450 heads."), 450, HeadsPlusChallengeTypes.CRAFTING, HPChallengeRewardTypes.ECO, 1750, 0, HeadsPlusChallengeDifficulty.TEDIOUS, "total", 2000, 1.2);

    public String n;
    public String dName;
    public String h;
    public List<String> d;
    public int m;
    public HeadsPlusChallengeTypes p;
    public HPChallengeRewardTypes r;
    public HeadsPlusChallengeDifficulty cd;
    public Object o;
    public int a;
    public String t;
    public int exp;
    public double v;

    HeadsPlusChallengeEnums(String name, String n, String header, List<String> desc, int min, HeadsPlusChallengeTypes hpct, HPChallengeRewardTypes hpcrt, Object o, int amount, HeadsPlusChallengeDifficulty difficulty, String type, int exp, double v) {
        this.n = name;
        this.h = header;
        this.d = desc;
        this.m = min;
        this.p = hpct;
        this.r = hpcrt;
        this.o = o;
        this.a = amount;
        this.cd = difficulty;
        this.t = type;
        this.exp = exp;
        this.dName = n;
        this.v = v;
    }

}
