package io.github.thatsmusic99.headsplus.config;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainConfig extends HPConfig {

    private MainFeatures mainFeatures;
    private MySQL mySQL;
    private MobDrops mobDrops;
    private PlayerDrops playerDrops;
    private SellingHeads sellingHeads;
    private Masks masks;
    private Autograbber autograbber;
    private Challenges challenges;
    private Levels levels;
    private Leaderboards leaderboards;
    private Localisation localisation;
    private HeadsSelector headsSelector;
    private Updates updates;
    private Miscellaneous miscellaneous;
    private List<String> defaults;

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
        addDefault("challenges", true, "Whether players should be able to complete challenges or not.\n" +
                "The command to access challenges is /hpc, with the permission headsplus.challenges.");

        addDefault("leaderboards", true, "Whether or not people can view leaderboards for the plugin that list up the players with the most heads collected" +
                  " from mob drops and crafting.\nThe command to access this is /hplb, with the permission headsplus.leaderboards"); // TODO maybe lol
        addDefault("levels", true, "Whether or not HeadsPlus Levels should be enabled or not.\n" +
                  "HeadsPlus Levels are cosmetic levels which players can reach through gaining XP. XP can be gained by getting/crafting heads and completing challenges.\n" +
                  "To configure this further, use the levels.yml file.");
        addDefault("masks", true, "Whether or not masks should be enabled or not.\nMasks are heads which can be worn and apply a special effect upon the person who wears it." +
                  "\nHowever, it does require extensive configuration, which is best done using /hp settings.");
        addDefault("interactions", true, "Whether or not the plugin should respond to interactions with a given head." +
                   "\nThis can be configured further using interactions.yml.");
        addDefault("block-pickup", true, "Whether or not creative mode players are able to retreive heads using the pick block button.");

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
        addDefault("default-drop-chance", 5, "The default drop chance of mob heads.\n" +
                "Per-head drop chance can be configured in the mobs.yml file.");
        addDefault("default-head-price", 10.0, "The default price that mob heads can be sold at.\n" +
                "Per-head price can be configured in the mobs.yml file.");
        addDefault("default-xp-gained", 10, "The default amount of XP (plugin) gained when receiving a dropped head.\n" +
                "Per-head XP gained can be configured in the mobs.yml file.");
        addDefault("blocked-spawn-causes", Lists.newArrayList("SPAWNER_EGG"),
                "Spawn causes that stop heads dropping from a given mob.\n" +
                        "In this example, mobs spawned using spawner eggs will not drop heads at all.");
        addDefault("needs-killer", false, "Whether or not mob drops need a player killing them or not.");
        addDefault("entities-needing-killer", Lists.newArrayList("player"),
                "If the above option is disabled, list here the entities that DO need a killer to drop a head.\n" +
                        "This is to prevent potential exploits with sellable heads.");
        addDefault("enable-looting", true,
                "Whether or not the looting enchantment should have an effect on how many heads are dropped.");
        addDefault("thresholds.common", 100,
                "The maximum chance needed for the looting enchantment to increase the head drop number.\n" +
                        "For example, heads with a 50% chance of dropping will drop extra heads depending on the looting level.\n" +
                        "Every 100% added guarantees a new head, but the extra 50% - the remaining head - is decided by random chance.\n" +
                        "Equation used: chance += level * 100");
        addDefault("thresholds.uncommon", 20,
                "The minimum chance needed for the looting enchantment to bump up the chance of the head dropping.\n" +
                        "In this example, a head with a 10% chance is changed to 50% with level 1 looting, 66% with level 2 and 75%  with level 3.\n" +
                        "Equation used: chance = level / (level + 1)");
        addDefault("thresholds.rare", 5, "The minimum chance for the looting enchantment to softly bump the chance of a head dropping.\n" +
                "Equation used: chance *= level when chance is below 1, chance += level when chance is above or equal to 1");
        addDefault("looting-ignored", new ArrayList<>(),
                "Mobs that will get ignored by the looting enchantment so their head drop count remains unchanged.");
        addDefault("disable-for-mythic-mobs", true,
                "Whether the plugin should ignore MythicMobs mobs or not.");

        addSection("Player Head Drops");
        addDefault("default-player-drop-chance", 100, "The default chance that a player head will drop when killed.");
        addDefault("default-player-head-price", 10.0, "The default price of a player head when it is dropped.");
        addDefault("ignored-players", new ArrayList<>(), "A list of players that shouldn't drop heads at all.\n" +
                "Any entries made here should all be in lowercase.");
        addDefault("enable-player-head-death-messages", false,
                "Whether or not messages should be broadcasted when a player loses their head upon death.");
        addDefault("player-head-death-messages",
                Lists.newArrayList("&c{player} &7was killed by &c{killer} &7and had their head removed!",
                        "&c{killer} &7finished the job and removed the worst part of &c{player}&7: The head.",
                        "&7The server owner screamed at &c{player} &7\"OFF WITH HIS HEAD!\". &c{killer} &7finished the job."),
                "The list of death messages you can set and add.");
        addDefault("adjust-price-according-to-balance", false,
                "Whether or not to adjust the price of the head according to either the victim's or killer's balance (see below).");
        addDefault("use-killer-balance", false, "Whether to use the killer's balance or victim's balance when the option above is enabled.\n" +
                "If set to true, the killer's balance is used.\n" +
                "If set to false, the victim's balance is used.");
        addDefault("percentage-taken-off-victim", 5,
                "The percentage of the victim's/killer's balance that is taken off the actual victim.\n" +
                        "This is a value out of 100, so in the default option, 5% of the balance is taken.");
        addDefault("percentage-of-balance-as-price", 5,
                "The percentage of the victim's/killer's balance that is used as the actual price of the head.\n" +
                        "Must be a value out of 100.");

        addSection("Selling Heads");
        addDefault("stop-placement-of-sellable-heads", false, "Whether or not players should be able to place heads that can be sold.\n" +
                "Heads lose their metadata when placed on the ground, so this purely serves as an option to prevent that.");
        addDefault("use-sellhead-gui", true, "Whether or not the sellhead GUI is opened when a player does /sellhead.");
        addDefault("case-sensitive-names", false, "Whether or not names in /sellhead should be case sensitive.");

        addSection("Masks");
        addDefault("check-interval", 60, "How often in ticks the plugin checks to make sure it is still on the player's head.\n" +
                "Default is 3 seconds.");
        addDefault("reset-after-x-intervals", 20, "How many check intervals it takes for a potion mask to reset.\n" +
                "The default here makes the mask reset every minute.");
        addDefault("effect-length", 12000, "How long in ticks the effects on the potion mask lasts.\n" +
                "The effect is removed after the mask is taken off.");

        addSection("Restrictions");
        addDefault("whitelist-worlds", false, "Whether or not the list below should be treated as a whitelist.\n" +
                "If this is disabled, all worlds in the lists below will not have the respected event occur in them.\n" +
                "If this is enabled, on the other hand, any worlds not in the lists will not have the respected event occur in them.");

        addDefault("mob-drops-list", new ArrayList<>(),
                "Worlds in which heads cannot drop from mobs.\n" +
                "If whitelist-worlds is enabled, worlds specified below *will* drop mob heads.");
        addDefault("player-drops-list", new ArrayList<>(), "Worlds in which heads cannot drop from players.");
        addDefault("crafting-list", new ArrayList<>(),
                "Worlds in which players cannot craft heads. The heads will still appear, but the player cannot pick them up.");
        addDefault("masks-list", new ArrayList<>(), "Worlds in which masks cannot be effective.");
        addDefault("xp-gain", new ArrayList<>(), "Worlds in which players cannot gain XP.");
        addDefault("stats-collection", new ArrayList<>(), "Worlds in which players cannot have their HP statistics increased.");
        addDefault("blocked-heads", new ArrayList<>(), "Heads which players cannot access using /head.\n" +
                "For example, if a player tries to do /head Thatsmusic99 but the name is included in this list, they cannot access the head.\n" +
                "All additions made to this list must be in lowercase only!");

        addSection("Autograbber");
        addDefault("enable-autograb", false, "Enables the autograbber feature.\n" +
                "This grabs the texture of a player's head when they join and add it to the local head storage cache for HeadsPlus.");
        addDefault("add-grabbed-heads-to-selector", true, "Whether or not player heads fetched through the autograbber should be put in the /heads selector.");
        addDefault("autograb-section", "players", "The section in the /heads selector that grabbed heads get placed in if the option above is enabled.");
        addDefault("autograb-display-name", "&8[&6{player}&8]", "The display name given to the head when it is autograbbed.");
        addDefault("autograb-price", "default", "The default price set for autograbbed heads. Use \"default\" to use the default price used for all heads.");

        addSection("Challenges");
        addDefault("broadcast-challenge-complete", true, "Whether or not challenge completion should be broadcasted.");

        addSection("Levels");
        addDefault("add-boss-bars", true, "Whether or not boss bars should be displayed to show the progress of a player's level.");
        addDefault("boss-bar-color", "RED", "The colour of the bossbar.\n" +
                "See https://papermc.io/javadocs/paper/1.17/org/bukkit/boss/BarColor.html for a list of possible colours.");
        addDefault("boss-bar-title", "&c&lXP to next HP level", "The title of the bossbar.");
        addDefault("boss-bar-lifetime", 5, "The number of seconds the bossbar should last before disappearing.");
        addDefault("broadcast-level-up", true, "Whether or not a broadcast should be made when a player levels up.\n" +
                "The message for this can be changed in your localisation file (in the locale folder).");
        addDefault("multiple-level-ups", false, "Whether or not multiple level-ups should occur at once.\n" +
                "If disabled for example, a player levelling up from A to C will only get the broadcast for level C, but all rewards in between.\n" +
                "If enabled, there will be broadcasts for levelling up to B and C despite the player's new level being level C.");

        addSection("Statistics");
        addDefault("cache-duration", 300, "How long in seconds statistics are cached for.\n" +
                "Statistics are cached if you use the PlaceholderAPI expansion for HeadsPlus.");

        addSection("Updates");
        addDefault("check-for-updates", true,
                "Whether or not the plugin should check for new updates.");
        addDefault("notify-admins-about-updates", true,
                "Whether or not the plugin should notify admins when a plugin update is available.");

        addSection("Localisation");
        addDefault("locale", "en_us", "The localisation used globally within the plugin.\n" +
                "See the locale folder for viable options. To select a language, write its file name - without the .yml - in here.");
        addDefault("smart-locale", false, "Whether or not to enable smart locale.\n" +
                "This makes HeadsPlus automatically translate itself into a different language for each user depending on their chosen language.\n" +
                "However, ");
        addDefault("use-tellraw", true, "Whether or not /tellraw should be used to send messages.\n" +
                "Will be replaced in a future version. May cause problems with floodgate players.");

        addSection("Heads Selector");
        addDefault("default-selector-head-price", 10.0,
                "The default price the heads in the heads selector can be sold at.");
        addComment("per-world-prices", "Defines the price of heads by default in a given world.");
        makeSectionLenient("per-world-prices");
        addExample("per-world-prices.cool-world", 15.0);

        addSection("Permissions");
        addDefault("default-permissions", Lists.newArrayList("headsplus.craft.*",
                "headsplus.challenges",
                "headsplus.drops.*",
				"headsplus.drops.player.*",
				"headsplus.head",
				"headsplus.heads",
				"headsplus.leaderboards",
				"headsplus.maincommand", 
				"headsplus.maincommand.info",
				"headsplus.maincommand.profile",
				"headsplus.myhead",
				"headsplus.sellhead",
				"headsplus.sellhead.gui"), 
			"The list of permissions users should have by default.\n" +
			"It is not recommended to rely on this alone, but use this with a permissions plugin like LuckPerms.\n" +
			"If you want to set up permissions purely from scratch, turn this into an empty list:\n" +
			"default-permissions:[]");

        addSection("Miscellaneous");
        addDefault("debug", false, "Enables the debugging verbose in the console.");
        addDefault("smite-player", false, "This April Fool's feature genuinely got me a complaint.\n" +
                "Basically, it strikes the player with lightning whenever a head is dropped. That is it.\n" +
                "Someone genuinely complained about it.");
        addDefault("suppress-gui-warnings", true, "Whether or not GUI warnings from HeadsPlus should be suppressed.");
        addDefault("allow-negative-xp", false, "Whether or not the plugin should allow negative XP.");
        addDefault("suppress-messages-during-search", false, "Whether or not you are able to receive messages when searching for a head.");
        addDefault("price-decimal-format", "#,###.##",
                "The format in which prices should appear in messages, heads, wherever.\n" +
                        "By default, this adds a comma for every 1000$ and rounds to two decimal points.\n" +
                        "Please do not swap the notation for this as Java doesn't like that, see the option below.");
    }

     @Override
     public void moveToNew() {
         // Main Features
         moveTo("plugin.perks.sell-heads", "sell-heads");
         boolean b = getBoolean("plugin.perks.drop-heads", getBoolean("player-drops"));
         moveTo("plugin.perks.drop-heads", "mob-drops");
         set("player-drops", b);
         moveTo("plugin.perks.craft-heads", "enable-crafting");
         moveTo("plugin.perks.heads-selector", "heads-selector");
         moveTo("plugin.perks.challenges", "challenges");
         moveTo("plugin.perks.leaderboards", "leaderboards");
         moveTo("plugin.perks.levels", "levels");
         moveTo("plugin.perks.mask-powerups", "masks");
         moveTo("plugin.perks.interact.click-head", "interactions");
         moveTo("plugin.perks.interact.middle-click-head", "block-pickup");

         // MySQL Options
         moveTo("mysql.enabled", "enable-mysql");
         moveTo("mysql.host", "mysql-host");
         moveTo("mysql.port", "mysql-port");
         moveTo("mysql.database", "mysql-database");
         moveTo("mysql.username", "mysql-username");
         moveTo("mysql.password", "mysql-password");

         // Mob Drops Options
         moveTo("plugin.mechanics.blocked-spawn-causes", "blocked-spawn-causes");
         moveTo("plugin.perks.drops.needs-killer", "needs-killer");
         moveTo("plugin.perks.drops.entities-requiring-killer", "entities-needing-killer");
         moveTo("plugin.mechanics.allow-looting-enchantment", "enable-looting");
         moveTo("plugin.mechanics.looting.thresholds.common", "thresholds.common");
         moveTo("plugin.mechanics.looting.thresholds.uncommon", "thresholds.uncommon");
         moveTo("plugin.mechanics.looting.thresholds.rare", "thresholds.rare");
         moveTo("plugin.mechanics.looting.ignored-entities", "looting-ignored");
         moveTo("plugin.mechanics.mythicmobs.no-hp-drops", "disable-for-mythic-mobs");

         // Player Drops Options
         moveTo("plugin.perks.drops.ignore-players", "ignored-players");
         moveTo("plugin.perks.player-death-messages", "enable-player-head-death-messages");
         moveTo("plugin.perks.death-messages", "player-head-death-messages");
         moveTo("plugin.perks.pvp.player-balance-competition", "adjust-price-according-to-balance");
         moveTo("plugin.perks.pvp.use-killer-balance", "use-killer-balance");
         moveTo("plugin.perks.pvp.percentage-lost", "percentage-taken-off-victim");
         moveTo("plugin.perks.pvp.percentage-balance-for-head", "percentage-of-balance-as-price");

         // Sellhead Options
         moveTo("plugin.mechanics.stop-placement-of-sellable-heads", "stop-placement-of-sellable-heads");
         moveTo("plugin.mechanics.sellhead-gui", "use-sellhead-gui");
         moveTo("plugin.mechanics.sellhead-ids-case-sensitive", "case-sensitive-names");

         // Mask Options
         moveTo("plugin.mechanics.masks.check-interval", "check-interval");
         moveTo("plugin.mechanics.masks.reset-after-x-intervals", "reset-after-x-intervals");
         moveTo("plugin.mechanics.masks.effect-length", "effect-length");

         // Autograb Options
         moveTo("plugin.autograb.enabled", "enable-autograb");
         moveTo("plugin.autograb.add-as-enabled", "add-grabbed-heads-to-selector");
         moveTo("plugin.autograb.section", "autograb-section");
         moveTo("plugin.autograb.title", "autograb-display-name");
         moveTo("plugin.autograb.price", "autograb-price");

         // Rererestreeeeeeeeeeeeeeecshuns maybe?
         boolean whitelist = false;
         if (getBoolean("whitelist.default.enabled")) {
             moveTo("whitelist.default.list", "blocked-heads");
             set("whitelist-worlds", whitelist = true);
         } else if (getBoolean("blacklist.default.enabled")) {
             moveTo("blacklist.default.list", "blocked-heads");
         }

         List<String> worlds = new ArrayList<>();
         if (whitelist && getBoolean("whitelist.worlds.enabled")) {
             worlds = getStringList("whitelist.worlds.list");
         } else if (!whitelist && getBoolean("blacklist.worlds.enabled")) {
             worlds = getStringList("blacklist.worlds.list");
         }
         if (!worlds.isEmpty()) {
             set("mobs-drops-list", worlds);
             set("player-drops-list", worlds);
             set("crafting-list", worlds);
         }

         // Challenge Options
         moveTo("plugin.mechanics.broadcasts.challenge-complete", "broadcast-challenge-complete");

         // Level Options
         moveTo("plugin.mechanics.boss-bar.enabled", "add-boss-bars");
         moveTo("plugin.mechanics.boss-bar.color", "boss-bar-color");
         moveTo("plugin.mechanics.boss-bar.title", "boss-bar-title");
         moveTo("plugin.mechanics.boss-bar.lifetime", "boss-bar-lifetime");
         moveTo("plugin.mechanics.broadcasts.level-up", "broadcast-level-up");

         // Leaderboard Options
         moveTo("plugin.mechanics.leaderboards.cache-lifetime-seconds", "cache-duration");

         // Update Options
         moveTo("plugin.mechanics.update.check", "check-for-updates");
         moveTo("plugin.mechanics.update.notify", "notify-admins-about-updates");

         // Localisation Options
         moveTo("plugin.mechanics.use-tellraw", "use-tellraw");

         // Misc Options
         moveTo("plugin.perks.smite-player-if-they-get-a-head", "smite-player");
         moveTo("plugin.mechanics.suppress-gui-warnings", "suppress-gui-warnings");
         moveTo("plugin.perks.xp.allow-negative", "allow-negative-xp");
         moveTo("plugin.mechanics.suppress-messages-during-search", "suppress-messages-during-search");

         // temp. patches 
         set("plugin", null);
         set("whitelist", null);
         set("blacklist", null);
     }

    @Override
    public void postSave() {
        mainFeatures = new MainFeatures();
        mySQL = new MySQL();
        mobDrops = new MobDrops();
        playerDrops = new PlayerDrops();
        sellingHeads = new SellingHeads();
        masks = new Masks();
        autograbber = new Autograbber();
        challenges = new Challenges();
        levels = new Levels();
        leaderboards = new Leaderboards();
        localisation = new Localisation();
        headsSelector = new HeadsSelector();
        updates = new Updates();
        miscellaneous = new Miscellaneous();

        // Default permissions handling
        List<String> permissions = getStringList("default-permissions-list");
        // If there's no defaults already set up, just create a new list
        // Otherwise, if this is a reload, reset the permissions set
        if (defaults == null) {
            defaults = new ArrayList<>();
        } else {
            for (String defaultPerm : defaults) {
                Permission permObj = Bukkit.getPluginManager().getPermission(defaultPerm);
                if (permObj == null) continue;
                permObj.setDefault(PermissionDefault.OP);
            }
        }
        // Then set up the actual permissions
        for (String perm : permissions) {
            if (!perm.startsWith("headsplus")) continue;
            Permission permission = Bukkit.getPluginManager().getPermission(perm);
            if (permission == null) {
                permission = new Permission(perm);
                Bukkit.getPluginManager().addPermission(permission);
            }
            permission.setDefault(PermissionDefault.TRUE);
            defaults.add(perm);
        }
    }

    public static MainConfig get() {
        return instance;
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

    public Autograbber getAutograbber() {
        return autograbber;
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

    public HeadsSelector getHeadsSelector() {
        return headsSelector;
    }

    public Updates getUpdates() {
        return updates;
    }

    public Miscellaneous getMiscellaneous() {
        return miscellaneous;
    }

    public String fixBalanceStr(double balance) {
        DecimalFormat format = new DecimalFormat(getString("price-decimal-format", "#,###.##"));
        format.setRoundingMode(RoundingMode.CEILING);
        return format.format(balance);
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
        public double DEFAULT_DROP_CHANCE = getDouble("default-drop-chance"),
                DEFAULT_PRICE = getDouble("default-head-price");
        public long DEFAULT_XP_GAINED = getLong("default-xp-gained");

    }

    public class PlayerDrops {
        public List<String> PLAYER_HEAD_DEATH_MESSAGES = getStringList("player-head-death-messages"),
                IGNORED_PLAYERS = getStringList("ignored-players");
        public boolean ENABLE_PLAYER_DEATH_MESSAGES = getBoolean("enable-player-head-death-messages"),
                ADJUST_PRICE_ACCORDING_TO_PRICE = getBoolean("adjust-price-according-to-balance"),
                USE_VICTIM_BALANCE = getBoolean("use-victim-balance");
        public double PERCENTAGE_TAKEN_OFF_VICTIM = getDouble("percentage-taken-off-victim"),
                PERCENTAGE_OF_BALANCE_AS_PRICE = getDouble("percentage-of-balance-as-price"),
                DEFAULT_DROP_CHANCE = getDouble("default-player-drop-chance"),
                DEFAULT_PRICE = getDouble("default-player-head-price");
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

    public class Autograbber {
        public boolean ENABLE_AUTOGRABBER = getBoolean("enable-autograb"),
                ADD_GRABBED_HEADS = getBoolean("add-grabbed-heads-to-selector");
        public String SECTION = getString("autograb-section"),
                DISPLAY_NAME = getString("autograb-display-name");
        public double PRICE = getDouble("autograb-price", -1);
    }

    public class Challenges {
        public boolean BROADCAST_CHALLENGE_COMPLETE = getBoolean("broadcast-challenge-complete");
    }

    public class Levels {
        public boolean ENABLE_BOSS_BARS = getBoolean("add-boss-bars"),
                BROADCAST_LEVEL_UP = getBoolean("broadcast-level-up"),
                MULTIPLE_LEVEL_UPS = getBoolean("multiple-level-ups");
        public String BOSS_BAR_COLOR = getString("boss-bar-color"),
                BOSS_BAR_TITLE = getString("boss-bar-title");
        public int BOSS_BAR_LIFETIME = getInteger("boss-bar-lifetime");
    }

    public class Leaderboards {
        public int CACHE_DURATION = getInteger("cache-duration");
    }

    public class Localisation {
        public String LOCALE = getString("locale");
        public boolean SMART_LOCALE = getBoolean("smart-locale"),
                USE_TELLRAW = getBoolean("use-tellraw");
    }

    public class HeadsSelector {
        public double DEFAULT_PRICE = getDouble("default-selector-head-price");
        public ConfigSection PER_WORLD_PRICES = getConfigSection("per-world-prices");
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
}
