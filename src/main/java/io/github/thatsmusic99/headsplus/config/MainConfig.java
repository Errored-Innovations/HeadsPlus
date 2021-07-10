package io.github.thatsmusic99.headsplus.config;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import org.bukkit.configuration.ConfigurationSection;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainConfig extends HPConfig {

    Perks perks = new Perks();
    SelectorList whitelist_worlds = new SelectorList();
    SelectorList blacklist_worlds = new SelectorList();
    SelectorList whitelist_heads = new SelectorList();
    SelectorList blacklist_heads = new SelectorList();

    private MainFeatures mainFeatures;
    private MySQL mySQL;
    private MobDrops mobDrops;
    private PlayerDrops playerDrops;
    private SellingHeads sellingHeads;
    private Masks masks;
    private Challenges challenges;
    private Levels levels;
    private Leaderboards leaderboards;
    private Localisation localisation;
    private Updates updates;
    private Miscellaneous miscellaneous;

    private static MainConfig instance;

    public MainConfig() {
        super("config.yml");
        instance = this;
    }

    @Override
    public void loadDefaults() {
        addSection("Main Features");

        addDefault("sell-heads", true, "Whether or not players are able to sell heads.\n" +
                "This requires the Vault and an economy plugin - such as Essentials - to be enabled!\n" +
                "To troubleshoot this, please make sure that Economy in /vault-info isn't null. If it is, you don't have an economy plugin.\n" +
                "If it isn't null and HeadsPlus isn't picking it up, please contact the developer.");
        addDefault("mob-drops", true, "Whether or not mobs drop their heads when they die.\n" +
                "To see the full settings for this, please look at the mobs.yml config file.");
        addDefault("player-drops", true, "Whether or not players drop their heads when they die.\n" +
                "To see the full settings for this too, please look at the mobs.yml config file.");
        addDefault("enable-crafting", false, "Whether or not players can craft heads.\n" +
                "Whilst this option is set to true,");
        addDefault("heads-selector", true, "Whether to allow people to use /heads or not.\n" +
                "The permission for this is heasplus.heads.");
        addDefault("challenges", true, "Whether players should be able to complete challenges or not.");

        addDefault("leaderboards", true);
        addDefault("levels", true);
        addDefault("masks", true);
        addDefault("interactions", true);
        addDefault("block-pickup", true);

        addSection("MySQL");
        addDefault("enable-mysql", false,
                "Whether or not the plugin should connect using MySQL.\n" +
                        "When this option is set to false, it just uses the default SQLite storage.");
        addDefault("mysql-host", "127.0.0.1", "The host of the MySQL database to be used.");
        addDefault("mysql-port", 3306, "The port of the MySQL database. It's often 3306 by default.");
        addDefault("mysql-database", "database", "The name of the database/schema to connect to.");
        addDefault("mysql-username", "username", "The username used to access the database.");
        addDefault("mysql-password", "password", "The password for the user specified above.");

        addSection("Mob Drops");
        addComment("Configure this further in the mobs.yml config file.");
        addDefault("blocked-spawn-causes", Lists.newArrayList("SPAWNER_EGG"),
                "Spawn causes that stop heads dropping from a given mob.\n" +
                        "In this example, mobs spawned using spawner eggs will not drop heads at all.");
        addDefault("needs-killer", false, "Whether or not mob drops need a player killing them or not.");
        addDefault("entities-needing-killer", Lists.newArrayList("player"),
                "If the above option is disabled, list here the entities that DO need a killer to drop a head.\n" +
                        "This is to prevent potential exploits with sellable heads.");
        addDefault("enable-looting", true,
                "Whether or not the looting enchantment should have an effect on how many heads are dropped.");
        addDefault("thresholds.common", 100);
        addDefault("thresholds.uncommon", 20);
        addDefault("thresholds.rare", 5);
        addDefault("looting-ignored", new ArrayList<>(),
                "Mobs that will get ignored by the looting enchantment so their head drop count remains unchanged.");
        addDefault("disable-for-mythic-mobs", true,
                "Whether the plugin should ignore MythicMobs mobs or not.");

        addSection("Player Head Drops");
        addComment("Note - this is also further configured in the mobs.yml file.");
        addDefault("ignored-players", new ArrayList<>());
        addDefault("enable-player-head-death-messages", false);
        addDefault("player-head-death-messages",
                Lists.newArrayList("&c{player} &7was killed by &c{killer} &7and had their head removed!",
                        "&c{killer} &7finished the job and removed the worst part of &c{player}&7: The head.",
                        "&7The server owner screamed at &c{player} &7\"OFF WITH HIS HEAD!\". &c{killer} &7finished the job."));
        addDefault("adjust-price-according-to-balance", false);
        addDefault("use-victim-balance", true);
        addDefault("percentage-taken-off-victim", 5,
                "The percentage of the victim's/killer's balance that is taken off the actual victim.\n" +
                        "This is a value out of 100, so in the default option, 5% of the balance is taken.");
        addDefault("percentage-of-balance-as-price", 5);

        addSection("Selling Heads");
        addDefault("stop-placement-of-sellable-heads", false);
        addDefault("use-sellhead-gui", true);
        addDefault("case-sensitive-names", true);

        addSection("Masks");
        addDefault("check-interval", 60);
        addDefault("reset-after-x-intervals", 20);
        addDefault("effect-length", 12000);

        addSection("Restrictions");
        addDefault("whitelist-worlds", false, "Whether or not the list below should be treated as a whitelist.\n" +
                "If this is disabled, all worlds in the lists below will not have the respected event occur in them.\n" +
                "If this is enabled, on the other hand, any worlds not in the lists will not have the respected event occur in them.");

        addDefault("mob-drops-list", new ArrayList<>(),
                "Worlds in which heads cannot drop from mobs...\n" +
                "... Or worlds in which heads can only drop in if whitelist-worlds is enabled.");
        addDefault("crafting-list", new ArrayList<>());
        addDefault("masks-list", new ArrayList<>());
        addDefault("levels", new ArrayList<>());
        addDefault("blocked-heads", new ArrayList<>());

        addSection("Challenges");
        addDefault("broadcast-challenge-complete", true);

        addSection("Levels");
        addDefault("add-boss-bars", true);
        addDefault("boss-bar-color", "RED");
        addDefault("boss-bar-title", "&c&lXP to next HP level");
        addDefault("boss-bar-lifetime", 5);
        addDefault("broadcast-level-up", true);

        addSection("Leaderboards");
        addDefault("cache-leaderboards", true);
        addDefault("cache-duration", 300);

        addSection("Updates");
        addDefault("check-for-updates", true,
                "Whether or not the plugin should check for new updates.");
        addDefault("notify-admins-about-updates", true);

        addSection("Localisation");
        addDefault("locale", "en_us");
        addDefault("smart-locale", false);
        addDefault("use-tellraw", true);

        addSection("Miscellaneous");
        addDefault("debug", false, "Enables the debugging verbose in the console.");
        addDefault("smite-player", false, "This April Fool's feature genuinely got me a complaint.\n" +
                "Basically, it strikes the player with lightning whenever a head is dropped. That is it.\n" +
                "Someone genuinely complained about it.");
        addDefault("suppress-gui-warnings", true);
        addDefault("allow-negative-xp", false);
        addDefault("suppress-messages-during-search", false);
        addDefault("price-decimal-format", "#,###.##",
                "The format in which prices should appear in messages, heads, wherever.\n" +
                        "By default, this adds a comma for every 1000$ and rounds to two decimal points.\n" +
                        "Please do not swap the notation for this as Java doesn't like that, see the option below.");
        // TODO: depends on locale rather than config option
        addDefault("swap-decimal-notation", false,
                "If you decimal notation is different to the Western standard (i.e. uses , instead of . for decimal points) and want this in the plugin, turn on this option.");

    }

     @Override
     public void moveToNew() {
        moveTo("plugin.autograb.enabled", "autograb", ConfigCustomHeads.get());
        moveTo("plugin.autograb.add-as-enabled", "automatically-enable-grabbed-heads", ConfigCustomHeads.get());

     }
    /*
    protected void loadS() {

        getConfig().options().header("HeadsPlus by Thatsmusic99 - Config wiki: https://github.com/Thatsmusic99/HeadsPlus/wiki/Configuring-config.yml");
        getConfig().addDefault("locale", "en_us");
        getConfig().addDefault("smart-locale", false);
        getConfig().addDefault("blacklist.default.enabled", true);
        getConfig().addDefault("blacklist.world.enabled", true);
        getConfig().addDefault("whitelist.default.enabled", false);
        getConfig().addDefault("whitelist.world.enabled", false);
        getConfig().addDefault("blacklist.default.list", new ArrayList<>());
        getConfig().addDefault("blacklist.world.list", new ArrayList<>());
        getConfig().addDefault("whitelist.default.list", new ArrayList<>());
        getConfig().addDefault("whitelist.world.list", new ArrayList<>());
        getConfig().addDefault("mysql.host", "localhost");
        getConfig().addDefault("mysql.port", "3306");
        getConfig().addDefault("mysql.database", "db");
        getConfig().addDefault("mysql.username", "username");
        getConfig().addDefault("mysql.password", "password");
        getConfig().addDefault("mysql.enabled", false);
        getConfig().addDefault("theme-colours.1", "DARK_BLUE");
        getConfig().addDefault("theme-colours.2", "GOLD");
        getConfig().addDefault("theme-colours.3", "GRAY");
        getConfig().addDefault("theme-colours.4", "DARK_AQUA");
        getConfig().addDefault("plugin.larger-menus", false);
        getConfig().addDefault("plugin.autograb.enabled", false);
        getConfig().addDefault("plugin.autograb.add-as-enabled", true);
        getConfig().addDefault("plugin.autograb.section", "players");
        getConfig().addDefault("plugin.autograb.title", "&8[&6{player}&8]");
        getConfig().addDefault("plugin.autograb.price", "default");
        getConfig().addDefault("plugin.perks.interact.middle-click-head", true);
        config.addDefault("plugin.perks.interact.click-head", true);
        config.addDefault("plugin.perks.xp.allow-negative", false);
        config.addDefault("plugin.perks.ascii-art", true);
        config.addDefault("plugin.perks.sell-heads", true);
        config.addDefault("plugin.perks.drop-heads", true);
        config.addDefault("plugin.perks.drops.ignore-players", new ArrayList<>());
        config.addDefault("plugin.perks.drops.needs-killer", false);
        config.addDefault("plugin.perks.drops.entities-requiring-killer", new ArrayList<>(Collections.singleton("player")));
        config.addDefault("plugin.perks.craft-heads", false);
        config.addDefault("plugin.perks.disable-crafting", false);
        config.addDefault("plugin.perks.heads-selector", true);
        config.addDefault("plugin.perks.challenges", true);
        config.addDefault("plugin.perks.leaderboards", true);
        config.addDefault("plugin.perks.levels", true);
        config.addDefault("plugin.perks.player-death-messages", false);
        config.addDefault("plugin.perks.death-messages",
                new ArrayList<>(Arrays.asList("&b{player} &3was killed by &b{killer} &3and had their head removed!",
                                "&b{killer} &3finished the job and removed the worst part of &b{player}&3: The head.",
                                "&3The server owner screamed at &b{player} &3\"OFF WITH HIS HEAD!\"&3. &b{killer} &3finished the job.")));
        config.addDefault("plugin.perks.smite-player-if-they-get-a-head", false);
        config.addDefault("plugin.perks.mask-powerups", true);
        config.addDefault("plugin.perks.pvp.player-balance-competition", false);
        config.addDefault("plugin.perks.pvp.use-killer-balance", false);
        config.addDefault("plugin.perks.pvp.percentage-lost", 5);
        config.addDefault("plugin.perks.pvp.percentage-balance-for-head", 5);
        config.addDefault("plugin.mechanics.theme", "classic");
        config.addDefault("plugin.mechanics.plugin-theme-dont-change", "classic");
        config.addDefault("plugin.mechanics.update.check", true);
        config.addDefault("plugin.mechanics.update.notify", true);
        config.addDefault("plugin.mechanics.allow-looting-enchantment", true);
        config.addDefault("plugin.mechanics.looting.ignored-entities", new ArrayList<>());
        config.addDefault("plugin.mechanics.looting.use-old-system", false);
        config.addDefault("plugin.mechanics.looting.thresholds.common", 100);
        config.addDefault("plugin.mechanics.looting.thresholds.uncommon", 20);
        config.addDefault("plugin.mechanics.looting.thresholds.rare", 5);
        config.addDefault("plugin.mechanics.stop-placement-of-sellable-heads", false);
        config.addDefault("plugin.mechanics.sellhead-gui", true);
        config.addDefault("plugin.mechanics.debug.create-debug-files", true);
        config.addDefault("plugin.mechanics.debug.print-stacktraces-in-console", true);
        config.addDefault("plugin.mechanics.anvil-menu-search", false);
        config.addDefault("plugin.mechanics.suppress-messages-during-search", false);
        config.addDefault("plugin.mechanics.mythicmobs.no-hp-drops", true);
        config.addDefault("plugin.mechanics.round-balance-to-2-d-p", true);
        config.addDefault("plugin.mechanics.boss-bar.enabled", true);
        config.addDefault("plugin.mechanics.boss-bar.color", "RED");
        config.addDefault("plugin.mechanics.boss-bar.title", "&c&lXP to next HP level");
        config.addDefault("plugin.mechanics.boss-bar.lifetime", 5);
        config.addDefault("plugin.mechanics.broadcasts.level-up", true);
        config.addDefault("plugin.mechanics.broadcasts.challenge-complete", true);
        config.addDefault("plugin.mechanics.leaderboards.cache-boards", true);
        config.addDefault("plugin.mechanics.leaderboards.cache-lifetime-seconds", 300);
        config.addDefault("plugin.mechanics.xp.crafting", 10);
        config.addDefault("plugin.mechanics.xp.head-drops", 10);
        config.addDefault("plugin.mechanics.xp.selling", 10);
        config.addDefault("plugin.mechanics.suppress-gui-warnings", true);
        config.addDefault("plugin.mechanics.blocked-spawn-causes", new ArrayList<>(Collections.singleton("SPAWNER_EGG")));
        config.addDefault("plugin.mechanics.use-tellraw", false);
        config.addDefault("plugin.mechanics.masks.check-interval", 60);
        config.addDefault("plugin.mechanics.masks.reset-after-x-intervals", 20);
        config.addDefault("plugin.mechanics.masks.effect-length", 12000);
        config.addDefault("plugin.mechanics.sellhead-ids-case-sensitive", true);
        config.options().copyDefaults(true);
        save();

        // Whitelist / Blacklist
        whitelist_worlds.list.clear();
        blacklist_worlds.list.clear();
        whitelist_heads.list.clear();
        blacklist_heads.list.clear();

        ConfigurationSection l = getConfig().getConfigurationSection("blacklist.world");
        blacklist_worlds.list.addAll(l.getStringList("list"));
        blacklist_worlds.enabled = l.getBoolean("enabled");

        l = getConfig().getConfigurationSection("whitelist.world");
        whitelist_worlds.list.addAll(l.getStringList("list"));
        whitelist_worlds.enabled = l.getBoolean("enabled");

        l = getConfig().getConfigurationSection("blacklist.default");
        blacklist_heads.list.addAll(l.getStringList("list"));
        blacklist_heads.enabled = l.getBoolean("enabled");

        l = config.getConfigurationSection("whitelist.default");
        whitelist_heads.list.addAll(l.getStringList("list"));
        whitelist_heads.enabled = l.getBoolean("enabled");

        // Perks
        perks.drops_entities_requiring_killer.clear();
        perks.drops_ignore_players.clear();
        perks.death_messages.clear();

        ConfigurationSection p = config.getConfigurationSection("plugin.perks");
        perks.drops_entities_requiring_killer.addAll(p.getStringList("drops.entities-requiring-killer"));
        perks.drops_ignore_players.addAll(p.getStringList("drops.ignore-players"));
        perks.drops_needs_killer = p.getBoolean("drops.needs-killer");

        perks.death_messages.addAll(p.getStringList("death-messages"));
        perks.sell_heads = p.getBoolean("sell-heads");
        perks.drop_heads = p.getBoolean("drop-heads");
        perks.craft_heads = p.getBoolean("craft-heads");
        perks.disable_crafting = p.getBoolean("disable-crafting");
        perks.heads_selector = p.getBoolean("heads-selector");
        perks.challenges = p.getBoolean("challenges");
        perks.leaderboards = p.getBoolean("leaderboards");
        perks.levels = p.getBoolean("levels");
        perks.player_death_messages = p.getBoolean("player-death-messages");
        perks.smite_on_head = p.getBoolean("smite-player-if-they-get-a-head");
        perks.mask_powerups = p.getBoolean("mask-powerups");

        perks.pvp_player_balance_competition = p.getBoolean("pvp.player-balance-competition");
        perks.pvp_percentage_lost = p.getDouble("pvp.percentage-lost");
        perks.pvp_balance_for_head = p.getDouble("pvp.percentage-balance-for-head");
        perks.use_killer_balance = p.getBoolean("pvp.use-killer-balance");
        perks.ascii = p.getBoolean("ascii-art");
        perks.middle_click_in = p.getBoolean("interact.middle-click-head");
        perks.click_in = p.getBoolean("interact.click-head");
        perks.negative_xp = p.getBoolean("xp.allow-negative");
    } */

    @Override
    public void postSave() {
        mainFeatures = new MainFeatures();
        mySQL = new MySQL();
        mobDrops = new MobDrops();
        playerDrops = new PlayerDrops();
        sellingHeads = new SellingHeads();
        masks = new Masks();
        challenges = new Challenges();
        levels = new Levels();
        leaderboards = new Leaderboards();
        localisation = new Localisation();
        updates = new Updates();
        miscellaneous = new Miscellaneous();
    }

    public static MainConfig get() {
        return instance;
    }

    @Deprecated
    public ConfigSection getMechanics() {
        return getConfigSection("plugin.mechanics");
    }

    @Deprecated
    public Perks getPerks() {
        return perks;
    }

    @Deprecated
    public SelectorList getWorldBlacklist() {
        return blacklist_worlds;
    }

    @Deprecated
    public SelectorList getWorldWhitelist() {
        return whitelist_worlds;
    }

    @Deprecated
    public SelectorList getHeadsBlacklist() {
        return blacklist_heads;
    }

    @Deprecated
    public SelectorList getHeadsWhitelist() {
        return whitelist_heads;
    }

    public MainFeatures getMainFeatures() {
        return mainFeatures;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public SellingHeads getSellingHeads() {
        return sellingHeads;
    }

    public MobDrops getMobDrops() {
        return mobDrops;
    }

    public PlayerDrops getPlayerDrops() {
        return playerDrops;
    }

    public Masks getMasks() {
        return masks;
    }

    public Challenges getChallenges() {
        return challenges;
    }

    public Levels getLevels() {
        return levels;
    }

    public Leaderboards getLeaderboards() {
        return leaderboards;
    }

    public Localisation getLocalisation() {
        return localisation;
    }

    public Updates getUpdates() {
        return updates;
    }

    public Miscellaneous getMiscellaneous() {
        return miscellaneous;
    }

    public String fixBalanceStr(double balance) {
        if (getMechanics().getBoolean("round-balance-to-2-d-p")) {
            DecimalFormat format = new DecimalFormat("#.##");
            format.setRoundingMode(RoundingMode.CEILING);
            return format.format(balance);
        } else {
            return String.valueOf(balance);
        }

    }


    public class MainFeatures {
        public boolean SELL_HEADS = getBoolean("sell-heads"),
                MOB_DROPS = getBoolean("mob-drops"),
                PLAYER_DROPS = getBoolean("player-drops"),
                ENABLE_CRAFTING = getBoolean("enable-crafting"),
                HEADS_SELECTOR = getBoolean("heads-selector"),
                CHALLENGES = getBoolean("challenges"),
                LEADERBOARDS = getBoolean("leaderboards"),
                LEVELS = getBoolean("levels"),
                MASKS = getBoolean("masks"),
                INTERACTIONS = getBoolean("interactions"),
                BLOCK_PICKUP = getBoolean("block-pickup");
    }

    public class MySQL {
        public boolean ENABLE_MYSQL = getBoolean("enable-mysql");
        public String MYSQL_HOST = getString("mysql-host"),
                MYSQL_DATABASE = getString("mysql-database"),
                MYSQL_USERNAME = getString("mysql-username"),
                MYSQL_PASSWORD = getString("mysql-password");
        public int MYSQL_PORT = getInteger("mysql-port");
    }

    public class MobDrops {
        public List<String> BLOCKED_SPAWN_CAUSES = getStringList("blocked-spawn-causes"),
                ENTITIES_NEEDING_KILLER = getStringList("entities-needing-killer"),
                LOOTING_IGNORED = getStringList("looting-ignored");
        public boolean NEEDS_KILLER = getBoolean("needs-killer"),
                ENABLE_LOOTING = getBoolean("enable-looting"),
                DISABLE_FOR_MYTHIC_MOBS = getBoolean("disable-for-mythic-mobs");

    }

    public class PlayerDrops {
        public List<String> PLAYER_HEAD_DEATH_MESSAGES = getStringList("player-head-death-messages"),
                IGNORED_PLAYERS = getStringList("ignored-players");
        public boolean ENABLE_PLAYER_DEATH_MESSAGES = getBoolean("enable-player-head-death-messages"),
                ADJUST_PRICE_ACCORDING_TO_PRICE = getBoolean("adjust-price-according-to-balance"),
                USE_VICTIM_BALANCE = getBoolean("use-victim-balance");
        public double PERCENTAGE_TAKEN_OFF_VICTIM = getDouble("percentage-taken-off-victim"),
                PERCENTAGE_OF_BALANCE_AS_PRICE = getDouble("percentage-of-balance-as-price");
    }

    public class SellingHeads {
        public boolean STOP_PLACEMENT = getBoolean("stop-placement-of-sellable-heads"),
                USE_GUI = getBoolean("use-sellhead-gui"),
                CASE_INSENSITIVE = getBoolean("case-sensitive-names");
    }

    public class Masks {
        public int CHECK_INTERVAL = getInteger("check-interval"),
                RESET_INTERVAL = getInteger("reset-after-x-intervals"),
                EFFECT_LENGTH = getInteger("effect-length");
    }

    public class Challenges {
        public boolean BROADCAST_CHALLENGE_COMPLETE = getBoolean("broadcast-challenge-complete");
    }

    public class Levels {
        public boolean ENABLE_BOSS_BARS = getBoolean("add-boss-bars"),
                BROADCAST_LEVEL_UP = getBoolean("broadcast-level-up");
        public String BOSS_BAR_COLOR = getString("boss-bar-color"),
                BOSS_BAR_TITLE = getString("boss-bar-title");
        public int BOSS_BAR_LIFETIME = getInteger("boss-bar-lifetime");
    }

    public class Leaderboards {
        public boolean CACHE_LEADERBOARDS = getBoolean("cache-leaderboards");
        public int CACHE_DURATION = getInteger("cache-duration");
    }

    public class Localisation {
        public String LOCALE = getString("locale");
        public boolean SMART_LOCALE = getBoolean("smart-locale"),
                USE_TELLRAW = getBoolean("use-tellraw");
    }

    public class Updates {
        public boolean CHECK_FOR_UPDATES = getBoolean("check-for-updates"),
                NOTIFY_ADMINS = getBoolean("notify-admins-about-updates");

    }

    public class Miscellaneous {
        public boolean DEBUG = getBoolean("debug"),
                ALLOW_NEGATIVE_XP = getBoolean("allow-negative-xp"),
                SMITE_PLAYER = getBoolean("smite-player"),
                SUPPRESS_GUI_WARNINGS = getBoolean("suppress-gui-warnings"),
                SUPPRESS_MESSAGES_DURING_SEARCH = getBoolean("suppress-messages-during-search");
    }

    public static class Perks {

        public final List<String> drops_entities_requiring_killer = new ArrayList<>();
        public final List<String> drops_ignore_players = new ArrayList<>();
        public boolean drops_needs_killer, ascii, middle_click_in, click_in;
        public boolean sell_heads, drop_heads, craft_heads, disable_crafting, heads_selector, challenges, leaderboards, levels, player_death_messages, smite_on_head, mask_powerups;
        public final List<String> death_messages = new ArrayList<>();
        public boolean pvp_player_balance_competition, negative_xp, use_killer_balance;
        public double pvp_percentage_lost, pvp_balance_for_head;
    }

    public static class SelectorList {

        public boolean enabled;
        public final List<String> list = new ArrayList<>();
    }
}
