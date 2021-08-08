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
        addDefault("thresholds.common", 100);
        addDefault("thresholds.uncommon", 20);
        addDefault("thresholds.rare", 5);
        addDefault("looting-ignored", new ArrayList<>(),
                "Mobs that will get ignored by the looting enchantment so their head drop count remains unchanged.");
        addDefault("disable-for-mythic-mobs", true,
                "Whether the plugin should ignore MythicMobs mobs or not.");

        addSection("Player Head Drops");
        addDefault("default-player-drop-chance", 100, "The default chance that a player head will drop when killed.");
        addDefault("default-player-head-price", 10.0, "The default price of a player head when it is dropped.");
        addDefault("ignored-players", new ArrayList<>());
        addDefault("enable-player-head-death-messages", false);
        addDefault("player-head-death-messages",
                Lists.newArrayList("&c{player} &7was killed by &c{killer} &7and had their head removed!",
                        "&c{killer} &7finished the job and removed the worst part of &c{player}&7: The head.",
                        "&7The server owner screamed at &c{player} &7\"OFF WITH HIS HEAD!\". &c{killer} &7finished the job."));
        addDefault("adjust-price-according-to-balance", false);
        addDefault("use-killer-balance", false);
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
        addDefault("broadcast-challenge-complete", true);

        addSection("Levels");
        addDefault("add-boss-bars", true, "Whether or not boss bars should be displayed to show the progress of a player's level.");
        addDefault("boss-bar-color", "RED", "The colour of the bossbar.\n" +
                "See https://papermc.io/javadocs/paper/1.17/org/bukkit/boss/BarColor.html for a list of possible colours.");
        addDefault("boss-bar-title", "&c&lXP to next HP level", "The title of the bossbar.");
        addDefault("boss-bar-lifetime", 5, "The number of seconds the bossbar should last before disappearing.");
        addDefault("broadcast-level-up", true, "Whether or not ");
        addDefault("multiple-level-ups", false);

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
        addDefault("suppress-gui-warnings", true);
        addDefault("allow-negative-xp", false);
        addDefault("suppress-messages-during-search", false);
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
         moveTo("plugin.mechanics.leaderboards.cache-boards", "cache-leaderboards");
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
>>>>>>> configuration-rewrite:src/main/java/io/github/thatsmusic99/headsplus/config/MainConfig.java
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

        ConfigurationSection l = config.getConfigurationSection("blacklist.world");
        blacklist_worlds.list.addAll(l.getStringList("list"));
        blacklist_worlds.enabled = l.getBoolean("enabled");

        l = config.getConfigurationSection("whitelist.world");
        whitelist_worlds.list.addAll(l.getStringList("list"));
        whitelist_worlds.enabled = l.getBoolean("enabled");

        l = config.getConfigurationSection("blacklist.default");
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
<<<<<<< HEAD:src/main/java/io/github/thatsmusic99/headsplus/config/HeadsPlusMainConfig.java
    }

    public ConfigurationSection getMechanics() {
        return config.getConfigurationSection("plugin.mechanics");
=======
    } */

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
