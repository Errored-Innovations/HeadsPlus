package io.github.thatsmusic99.headsplus.config;

import org.bukkit.configuration.ConfigurationSection;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeadsPlusMainConfig extends ConfigSettings {

    Perks perks = new Perks();
    SelectorList whitelist_worlds = new SelectorList();
    SelectorList blacklist_worlds = new SelectorList();
    SelectorList whitelist_heads = new SelectorList();
    SelectorList blacklist_heads = new SelectorList();

    public HeadsPlusMainConfig() {
        this.conName = "config";
        enable(false);
    }

    @Override
    protected void load(boolean nullp) {

        if (config.get("blacklistOn") instanceof Boolean) {
            configF.delete();
            reloadC(false);
        }
        config.options().header("HeadsPlus by Thatsmusic99 - Config wiki: https://github.com/Thatsmusic99/HeadsPlus/wiki/Configuration");
        config.addDefault("blacklist.default.enabled", true);
        config.addDefault("blacklist.world.enabled", true);
        config.addDefault("whitelist.default.enabled", false);
        config.addDefault("whitelist.world.enabled", false);
        config.addDefault("blacklist.default.list", new ArrayList<>());
        config.addDefault("blacklist.world.list", new ArrayList<>());
        config.addDefault("whitelist.default.list", new ArrayList<>());
        config.addDefault("whitelist.world.list", new ArrayList<>());
        config.addDefault("mysql.host", "localhost");
        config.addDefault("mysql.port", "3306");
        config.addDefault("mysql.database", "db");
        config.addDefault("mysql.username", "username");
        config.addDefault("mysql.password", "password");
        config.addDefault("mysql.enabled", false);
        config.addDefault("theme-colours.1", "DARK_BLUE");
        config.addDefault("theme-colours.2", "GOLD");
        config.addDefault("theme-colours.3", "GRAY");
        config.addDefault("theme-colours.4", "DARK_AQUA");
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
        config.addDefault("plugin.perks.pvp.percentage-lost", 0.05);
        config.addDefault("plugin.perks.pvp.percentage-balance-for-head", 0.05);
        // config.addDefault("plugin.perks.houses.enabled", true);
        config.addDefault("plugin.mechanics.theme", "classic");
        config.addDefault("plugin.mechanics.plugin-theme-dont-change", "classic");
        config.addDefault("plugin.mechanics.update.check", true);
        config.addDefault("plugin.mechanics.update.notify", true);
        config.addDefault("plugin.mechanics.allow-looting-enchantment", true);
        config.addDefault("plugin.mechanics.looting.ignored-entities", new ArrayList<>());
        config.addDefault("plugin.mechanics.looting.use-old-system", false);
        config.addDefault("plugin.mechanics.stop-placement-of-sellable-heads", false);
        config.addDefault("plugin.mechanics.sellhead-gui", true);
        config.addDefault("plugin.mechanics.debug.create-debug-files", true);
        config.addDefault("plugin.mechanics.debug.print-stacktraces-in-console", true);
        config.addDefault("plugin.mechanics.debug.console.enabled", false);
        config.addDefault("plugin.mechanics.debug.console.level", 1);
        config.addDefault("plugin.mechanics.anvil-menu-search", false);
        config.addDefault("plugin.mechanics.mythicmobs.no-hp-drops", true);
        config.addDefault("plugin.mechanics.round-balance-to-2-d-p", true);
        config.addDefault("plugin.mechanics.boss-bar.enabled", true);
        config.addDefault("plugin.mechanics.boss-bar.color", "RED");
        config.addDefault("plugin.mechanics.boss-bar.title", "&c&lXP to next HP level");
        config.addDefault("plugin.mechanics.boss-bar.lifetime", 5);
        config.addDefault("plugin.mechanics.broadcasts.level-up", true);
        config.addDefault("plugin.mechanics.broadcasts.challenge-complete", true);
        //    config.addDefault("plugin.mechanics.ignored-players-head-drops", new ArrayList<>());
        config.set("mysql.passworld", null); // I still love this
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
        perks.pvp_percentabe_lost = p.getDouble("pvp.percentage-lost");
        perks.pvp_balance_for_head = p.getDouble("pvp.percentage-balance-for-head");
    }

    public ConfigurationSection getMechanics() {
        return config.getConfigurationSection("plugin.mechanics");
    }

    public Perks getPerks() {
        return perks;
    }

    public SelectorList getBlacklist() {
        return blacklist_worlds;
    }

    public SelectorList getWhitelist() {
        return whitelist_worlds;
    }

    public SelectorList getHeadsBlacklist() {
        return blacklist_heads;
    }

    public SelectorList getHeadsWhitelist() {
        return whitelist_heads;
    }

    public ConfigurationSection getMySQL() {
        return config.getConfigurationSection("mysql");
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

    public static class Perks {

        public final List<String> drops_entities_requiring_killer = new ArrayList<>();
        public final List<String> drops_ignore_players = new ArrayList<>();
        public boolean drops_needs_killer;
        public boolean sell_heads, drop_heads, craft_heads, disable_crafting, heads_selector, challenges, leaderboards, levels, player_death_messages, smite_on_head, mask_powerups;
        public final List<String> death_messages = new ArrayList<>();
        public boolean pvp_player_balance_competition;
        public double pvp_percentabe_lost, pvp_balance_for_head;
    }

    public static class SelectorList {

        public boolean enabled;
        public final List<String> list = new ArrayList<>();
    }
}
