package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import org.bukkit.configuration.ConfigurationSection;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainConfig extends CMFile {

    Perks perks = new Perks();
    SelectorList whitelist_worlds = new SelectorList();
    SelectorList blacklist_worlds = new SelectorList();
    SelectorList whitelist_heads = new SelectorList();
    SelectorList blacklist_heads = new SelectorList();

    private MainFeatures mainFeatures;
    private MySQL mySQL;

    private static MainConfig instance;

    public MainConfig() {
        super(HeadsPlus.getInstance(), "config");
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
        addDefault("enable-crafting", true, "Whether or not players can craft heads.\n" +
                "Whilst this option is set to true,");
        addDefault("heads-selector", true, "Whether to allow people to use /heads or not.\n" +
                "The permission for this is heasplus.heads.");
        addDefault("challenges", true, "Whether players should be able to complete challenges or not.");
        addDefault("leaderboards", true);
        addDefault("levels", true);
        addDefault("masks", true);
        addDefault("interactions", true);

        addSection("MySQL");
        addDefault("enable-mysql", false);
        addDefault("mysql-host", "127.0.0.1");
        addDefault("mysql-port", 3306);
        addDefault("mysql-database", "database");
        addDefault("mysql-username", "username");
        addDefault("mysql-password", "password");

        addSection("Mob Drops");
        addComment("Configure this further in the mobs.yml config file.");
        addDefault("blocked-spawn-causes", new ArrayList<>(Collections.singleton("SPAWNER_EGG")),
                "Spawn causes that stop heads dropping from a given mob.\n" +
                        "In this example, mobs spawned using spawner eggs will not drop heads at all.");
        addDefault("ignored-players", new ArrayList<>());
        addDefault("needs-killer", false);
        addDefault("entities-needing-killer", new ArrayList<>(Collections.singleton("player")));
        addDefault("enable-looting", true);
        addDefault("thresholds.common", 100);
        addDefault("thresholds.uncommon", 20);
        addDefault("thresholds.rare", 5);
        addDefault("looting-ignored", new ArrayList<>());
        addDefault("disable-for-mythic-mobs", true);
        addDefault("enable-player-head-death-messages", false);
        addDefault("player-head-death-messages",
                new ArrayList<>(Arrays.asList("&c{player} &7was killed by &c{killer} &7and had their head removed!",
                        "&c{killer} &7finished the job and removed the worst part of &c{player}&7: The head.",
                        "&7The server owner screamed at &c{player} &7\"OFF WITH HIS HEAD!\". &c{killer} &7finished the job.")));

        addSection("Selling Heads");
        addDefault("stop-placement-of-sellable-heads", false);
        addDefault("use-sellhead-gui", true);
        addDefault("case-sensitive-names", true);

        addSection("Masks");
        addDefault("check-interval", 60);
        addDefault("reset-after-x-intervals", 20);
        addDefault("effect-length", 12000);

        addSection("Restrictions");

        addSection("Updates");
        addDefault("check-for-updates", true);
        addDefault("notify-admins-about-updates", true);

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
    }

    public static MainConfig get() {
        return instance;
    }

    public ConfigurationSection getMechanics() {
        return getConfig().getConfigurationSection("plugin.mechanics");
    }

    public Perks getPerks() {
        return perks;
    }

    public SelectorList getWorldBlacklist() {
        return blacklist_worlds;
    }

    public SelectorList getWorldWhitelist() {
        return whitelist_worlds;
    }

    public SelectorList getHeadsBlacklist() {
        return blacklist_heads;
    }

    public SelectorList getHeadsWhitelist() {
        return whitelist_heads;
    }

    public ConfigurationSection getMySQL() {
        return getConfig().getConfigurationSection("mysql");
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
                ENABLE_CRAFTING = getBoolean("enable-crafting"),
                HEADS_SELECTOR = getBoolean("heads-selector"),
                CHALLENGES = getBoolean("challenges"),
                LEADERBOARDS = getBoolean("leaderboards"),
                LEVELS = getBoolean("levels"),
                MASKS = getBoolean("masks"),
                INTERACTIONS = getBoolean("interactions");
    }

    public class MySQL {
        public boolean ENABLE_MYSQL = getBoolean("enable-mysql");
        public String MYSQL_HOST = getString("mysql-host"),
                MYSQL_DATABASE = getString("mysql-database"),
                MYSQL_USERNAME = getString("mysql-username"),
                MYSQL_PASSWORD = getString("mysql-password");
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
