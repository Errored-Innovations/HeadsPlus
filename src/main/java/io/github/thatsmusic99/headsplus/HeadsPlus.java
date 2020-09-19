package io.github.thatsmusic99.headsplus;

import io.github.thatsmusic99.headsplus.api.*;
import io.github.thatsmusic99.headsplus.api.events.CommunicateEvent;
import io.github.thatsmusic99.headsplus.commands.*;
import io.github.thatsmusic99.headsplus.commands.Head;
import io.github.thatsmusic99.headsplus.commands.maincommand.*;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist.*;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist.*;
import io.github.thatsmusic99.headsplus.config.*;
import io.github.thatsmusic99.headsplus.config.challenges.HeadsPlusChallenges;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.crafting.RecipePerms;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.listeners.*;
import io.github.thatsmusic99.headsplus.listeners.tabcompleting.TabComplete;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.storage.Favourites;
import io.github.thatsmusic99.headsplus.storage.Pinned;
import io.github.thatsmusic99.headsplus.storage.PlayerScores;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.util.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusException;
import io.github.thatsmusic99.headsplus.util.events.IncorrectVersionException;
import io.github.thatsmusic99.headsplus.util.NewMySQLAPI;
import io.github.thatsmusic99.og.OreGenerator;
import io.github.thatsmusic99.specprotect.CoreClass;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

public class HeadsPlus extends JavaPlugin {

    // Private variables for the plugin
    private static HeadsPlus instance;
    private final PluginDescriptionFile pluginYml = getDescription();
    private final String author = pluginYml.getAuthors().toString();
    private final String version = pluginYml.getVersion();
    private Economy econ = null;
    private Permission perms;
    private static Object[] update = null;
    private Connection connection;
    private boolean con = false;
    // Config variables
    private HeadsPlusMessagesManager hpc;
    private HeadsPlusConfigHeads hpch;
    private HeadsPlusConfigCustomHeads hpchx;
    private HeadsPlusCrafting hpcr;
    private HeadsPlusChallenges hpchl;
    private HeadsPlusAPI hapi;
    private HeadsPlusLevels hpl;
    private HeadsPlusMainConfig config;
    private HeadsPlusConfigItems items;
    private HeadsPlusConfigSounds sounds;
    private HeadsPlusConfigTextMenu menus;
    // Other management stuff
    private final List<Challenge> challenges = new ArrayList<>();
    private final List<ChallengeSection> challengeSections = new ArrayList<>();
    private NMSManager nms;
    private NMSIndex nmsversion;
    private final LinkedHashMap<String, IHeadsPlusCommand> commands = new LinkedHashMap<>();
    private final HashMap<Integer, Level> levels = new HashMap<>();
    private final List<ConfigSettings> cs = new ArrayList<>();
    private Favourites favourites;
    private Pinned pinned;
    private PlayerScores scores;
    private boolean canUseWG = false;

    @Override
    public void onLoad() {
        instance = this;
        Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg != null && getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            if (wg.getDescription().getVersion().startsWith("7")) {
                canUseWG = true;
                new FlagHandler();
            }
        }
    }

    @Override
    public void onEnable() {
        try {
            instance = this;
            // Set up the NMS
            setupNMS();

            // Create locale files
            createLocales();


            // Build plugin instances
            createInstances();
            io.github.thatsmusic99.headsplus.inventories.InventoryManager.initiateInvsAndIcons();


            if (!isEnabled()) return;
            // Checks theme, believe it or not!
            checkTheme();

            // Handles recipes
            if (!getConfiguration().getPerks().disable_crafting) {
                new RecipePerms();
            }
            // If sellable heads are enabled and yet there isn't Vault
            if (!(econ()) && (getConfiguration().getPerks().sell_heads)) {
                getServer().getConsoleSender().sendMessage(hpc.getString("startup.no-vault"));
            }

            // If Vault exists
            if (econ()) {
                setupPermissions();
            }

            // Registers plugin events
            registerEvents();

            // Registers commands
            registerCommands();

            // Registers subcommands
            registerSubCommands();

            // Hooks PlaceholderAPI
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new HPExpansion(this).register();
                getLogger().info("We've registered our PAPI placeholders!");
            }
            HPPlayerJoinEvent.reloaded = false;
            // Sets up Metrics
            Metrics metrics = new Metrics(this, 1285);
            metrics.addCustomChart(new Metrics.SimplePie("languages", () -> getConfiguration().getConfig().getString("locale")));
            metrics.addCustomChart(new Metrics.SimplePie("theme", () -> capitalize(getConfiguration().getMechanics().getString("plugin-theme-dont-change").toLowerCase())));
            if (getConfiguration().getMechanics().getBoolean("update.check")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        update = UpdateChecker.getUpdate();
                        if (update != null) {
                            getServer().getConsoleSender().sendMessage(hpc.getString("update.current-version").replaceAll("\\{version}", getDescription().getVersion())
                                    + "\n" + hpc.getString("update.new-version").replaceAll("\\{version}", String.valueOf(update[0]))
                                    + "\n" + hpc.getString("update.description").replaceAll("\\{description}", String.valueOf(update[1])));
                            getLogger().info("Download link: https://www.spigotmc.org/resources/headsplus-1-8-x-1-12-x.40265/");
                        } else {
                            getLogger().info(hpc.getString("update.plugin-up-to-date"));
                        }
                        checkDates();

                    }
                }.runTaskAsynchronously(this);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        checkForMutuals();
                    }
                }.runTaskLater(this, 20);

            }
            getServer().getConsoleSender().sendMessage(hpc.getString("startup.plugin-enabled"));
            if (getConfiguration().getPerks().ascii) {
                getServer().getConsoleSender().sendMessage("\n" + ChatColor.DARK_BLUE + "-------------------------------------------------------------------------\n" +
                        "§c$$\\   $$\\                           $$\\           §9$$$$$$$\\  $$\\\n" +
                        "§c$$ |  $$ |                          $$ |          §9$$  __$$\\ $$ |\n" +
                        "§c$$ |  $$ | $$$$$$\\   $$$$$$\\   $$$$$$$ | $$$$$$$\\ §9$$ |  $$ |$$ |$$\\   $$\\  $$$$$$$\\\n" +
                        "§4$$$$$$$$ |$$  __$$\\  \\____$$\\ $$  __$$ |$$  _____|§1$$$$$$$  |$$ |$$ |  $$ |$$  _____|\n" +
                        "§4$$  __$$ |$$$$$$$$ | $$$$$$$ |$$ /  $$ |\\$$$$$$\\  §1$$  ____/ $$ |$$ |  $$ |\\$$$$$$\\\n" +
                        "§4$$ |  $$ |$$   ____|$$  __$$ |$$ |  $$ | \\____$$\\ §1$$ |      $$ |$$ |  $$ | \\____$$\\\n" +
                        "§4$$ |  $$ |\\$$$$$$$\\ \\$$$$$$$ |\\$$$$$$$ |$$$$$$$  |§1$$ |      $$ |\\$$$$$$  |$$$$$$$  |\n" +
                        "§4\\__|  \\__| \\_______| \\_______| \\_______|\\_______/ §1\\__|      \\__| \\______/ \\_______/\n" +
                        ChatColor.DARK_BLUE + "-------------------------------------------------------------------------\n" +
                        ChatColor.GREEN + "HeadsPlus " + getDescription().getVersion() + " has been enabled successfully!" + "\n" +
                        ChatColor.DARK_BLUE + "-------------------------------------------------------------------------\n");
            }

        } catch (Exception e) {
            try {
                DebugPrint.createReport(e, "Startup", false, null);
            } catch (Exception ex) {
                getLogger().severe("HeadsPlus has failed to start up correctly and can not read the config. An error report has been made in /plugins/HeadsPlus/debug");
                getLogger().info("First stacktrace: ");
                e.printStackTrace();
                getLogger().info("Second stacktrace: ");
                ex.printStackTrace();
                String s = DebugFileCreator.createReport(new HeadsPlusException(e));
                getLogger().severe("Report name: " + s);
                getLogger().severe("Please submit this report to the developer at one of the following links:");
                getLogger().severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
                getLogger().severe("https://discord.gg/nbT7wC2");
                getLogger().severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-12-x.237088/");
            }
        }
    }

    @Override
    public void onDisable() {
		// close any open interfaces
		for(UUID p : InventoryManager.storedInventories.keySet()) {
		    Player player = Bukkit.getPlayer(p);
		    if (player != null) {
                final InventoryManager im = InventoryManager.getManager(player);
                if(im.getInventory() != null) {
                    player.closeInventory();
                }
            }

		}
        try {
            favourites.save();
        } catch (IOException e) {
            DebugPrint.createReport(e, "Disabling (saving favourites)", false, null);
        }  catch (NullPointerException ignored) {

        }
        try {
            scores.save();
        } catch (IOException e) {
            DebugPrint.createReport(e, "Disabling (saving scores)", false, null);
        } catch (NullPointerException ignored) {

        }

        try {
            pinned.save();
        } catch (IOException e) {
            DebugPrint.createReport(e, "Disabling (saving pinned challenges)", false, null);
        } catch (NullPointerException ignored) {

        }
        getLogger().info(hpc.getString("startup.plugin-disabled"));
    }

    public static HeadsPlus getInstance() {
        return instance;

    }

    public boolean econ() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Sellhead disabled due to Vault itself not being found. (Error code: 1)");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("Sellhead disabled due to no economy plugin being found. (Error code: 2)");
            return false;
        }
        econ = rsp.getProvider();

        return econ != null;
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            ConfigurationSection mysql = getConfiguration().getMySQL();
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + mysql.getString("host") + ":" + mysql.getString("port") + "/" + mysql.getString("database") + "?useSSL=false&autoReconnect=true", mysql.getString("username"), mysql.getString("password"));
                NewMySQLAPI.createTable();
                con = true;
            } catch (SQLException ex) {
                getLogger().warning("MySQL could not be enabled due to a problem connecting. Details to follow... (Error code: 3)");
                getLogger().warning(ex.getMessage() + " (MySQL Error code: " + ex.getErrorCode() + ")");
                getLogger().warning(ex.getCause().getMessage());
            }
        }
    }

    private void checkTheme() {
        HeadsPlusMainConfig fc = getInstance().getConfiguration();
        if (!fc.getMechanics().getString("theme").equalsIgnoreCase(fc.getMechanics().getString("plugin-theme-dont-change"))) {
            try {
                MenuThemes mt = MenuThemes.valueOf(fc.getMechanics().getString("theme").toUpperCase());
                fc.getConfig().set("theme-colours.1", mt.c1);
                fc.getConfig().set("theme-colours.2", mt.c2);
                fc.getConfig().set("theme-colours.3", mt.c3);
                fc.getConfig().set("theme-colours.4", mt.c4);
                fc.getMechanics().set("plugin-theme-dont-change", mt.name());
                fc.getConfig().options().copyDefaults(true);
                fc.save();
            } catch (Exception ex) {
                getLogger().warning("Faulty theme was put in! No theme changes will be made.");
            }
        }
    }

    private void registerEvents() {
        new HPHeadInteractEvent();
        new HPEntityDeathEvent();
        new HPEntitySpawnEvent();
        new HPBlockPlaceEvent();
        new HPPlayerDeathEvent();
        new HPMaskEvents();
        new HPPlayerJoinEvent();
        new HPBlockPlaceEvent();
        new PlayerPickBlockEvent();
        new LeaderboardEvents();
        new HPPlayerMessageDeathEvent();
        getServer().getPluginManager().registerEvents(new SoundEvent(), this);
    }

    private void registerCommands() {
        getCommand("headsplus").setExecutor(new HeadsPlusCommand());
        getCommand("hp").setTabCompleter(new TabComplete());
        getCommand("head").setExecutor(new Head());
        getCommand("head").setTabCompleter(new Head());
        getCommand("myhead").setExecutor(new MyHead());
        getCommand("heads").setExecutor(new Heads());
        getCommand("hplb").setExecutor(new LeaderboardsCommand());
        getCommand("hplb").setTabCompleter(new LeaderboardsCommand());
        getCommand("sellhead").setExecutor(new SellHead(this));
        getCommand("sellhead").setTabCompleter(new TabCompleteSellhead());
        getCommand("hpc").setExecutor(new ChallengeCommand());
        getCommand("addhead").setExecutor(new AddHead());
    }

    private void createInstances() {

        config = new HeadsPlusMainConfig();
        cs.add(config);
        hpc = new HeadsPlusMessagesManager();
        hapi = new HeadsPlusAPI();
        hpch = new HeadsPlusConfigHeads();
        cs.add(hpch);
        hpchx = new HeadsPlusConfigCustomHeads();
        cs.add(hpchx);
        hpcr = new HeadsPlusCrafting();
        cs.add(hpcr);
        hpchl = new HeadsPlusChallenges();
        cs.add(hpchl);
        if (!getDescription().getAuthors().get(0).equals("Thatsmusic99") && !getDescription().getName().equals("HeadsPlus")) {
            getLogger().severe("The plugin has been tampered with! The real download can be found here: https://www.spigotmc.org/resources/headsplus-1-8-x-1-15-x.40265/");
            getLogger().severe("Only reupload the plugin on other sites with my permission, please! (Error code: 4)");
            setEnabled(false);
            return;
        }
        try {
            setupJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getConfiguration().getMySQL().getBoolean("enabled")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        openConnection();
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(this);

        }
        hpl = new HeadsPlusLevels();
        cs.add(hpl);
        items = new HeadsPlusConfigItems();
        cs.add(items);
        sounds = new HeadsPlusConfigSounds();
        cs.add(sounds);
        menus = new HeadsPlusConfigTextMenu();
        cs.add(menus);
        EntityDataManager.init();
    }

    public void restartMessagesManager() {
        createLocales();
        hpc = new HeadsPlusMessagesManager();
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private void setupJSON() throws IOException {
        favourites = new Favourites();
        favourites.create();
        favourites.read();
        scores = new PlayerScores();
        scores.create();
        scores.read();
        pinned = new Pinned();
        pinned.create();
        pinned.read();
    }

    private void createLocales() {
        List<String> locales = new ArrayList<>(Arrays.asList("de_de", "en_us", "es_es", "fr_fr", "hu_hu", "lol_us", "nl_nl", "pl_pl", "ro_ro", "ru_ru", "zh_cn"));
        File dir = new File(getDataFolder() + File.separator + "locale");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (String locale : locales) {
            File conf = new File(dir + File.separator + locale + ".yml");
            if (!conf.exists()) {
                InputStream is = getResource(locale + ".yml");
                try {
                    Files.copy(is, new File(getDataFolder() + File.separator + "locale" + File.separator,locale + ".yml").toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setupNMS() {
        String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        NMSManager nmsMan = null;
        try {
            nmsMan = (NMSManager) Class.forName("io.github.thatsmusic99.headsplus.nms." + bukkitVersion + ".NMSUtil").newInstance();
            if (!nmsMan.getNMSVersion().equals(bukkitVersion)) {
                throw new IncorrectVersionException("Incorrect version of HeadsPlus being used! You are using version " + bukkitVersion + ", this is meant for " + nmsMan.getNMSVersion());
            }
            nmsversion = NMSIndex.valueOf(bukkitVersion);
            nms = nmsMan;
        } catch (ClassNotFoundException | IncorrectVersionException e) {
            getLogger().severe("ERROR: Incorrect version of HeadsPlus being used! You are using version " + bukkitVersion);
            getLogger().severe("If this is not known of, let the developer know in one of these places:");
            getLogger().severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
            getLogger().severe("https://discord.gg/nbT7wC2");
            getLogger().severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-13-x.237088/");
            getLogger().severe("To prevent any further damage, the plugin is being disabled... (Error code: 5)");
            setEnabled(false);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void registerSubCommands() {
        commands.put("blacklistadd", new BlacklistAdd(this));
        commands.put("blacklistdel", new BlacklistDelete(this));
        commands.put("blacklistl", new BlacklistList(this));
        commands.put("blacklist", new BlacklistToggle(this));
        commands.put("blacklistwadd", new BlacklistwAdd(this));
        commands.put("blacklistwdel", new BlacklistwDelete(this));
        commands.put("blacklistwl", new BlacklistwList(this));
        commands.put("blacklistw", new BlacklistwToggle(this));
        commands.put("help", new HelpMenu());
        commands.put("info", new Info());
        commands.put("reload", new MCReload());
        commands.put("profile", new ProfileCommand());
        commands.put("whitelistadd", new WhitelistAdd(this));
        commands.put("whitelistdel", new WhitelistDel(this));
        commands.put("whitelistl", new WhitelistList(this));
        commands.put("whitelist", new WhitelistToggle(this));
        commands.put("whitelistwadd", new WhitelistwAdd(this));
        commands.put("whitelistwdel", new WhitelistwDelete(this));
        commands.put("whitelistwl", new WhitelistwList(this));
        commands.put("whitelistw", new WhitelistwToggle(this));
        commands.put("hpc", new ChallengeCommand());
        commands.put("addhead", new AddHead());
        commands.put("head", new Head());
        commands.put("heads", new Heads());
        commands.put("hplb", new LeaderboardsCommand());
        commands.put("myhead", new MyHead());
        commands.put("sellhead", new SellHead(this));
        commands.put("debug", new DebugPrint(this));
        commands.put("headinfo", new HeadInfoCommand());
        commands.put("conjure", new Conjure());
        commands.put("complete", new Complete());
        commands.put("tests", new TestsCommand());
        commands.put("xp", new XPCommand());
        commands.put("locale", new LocaleCommand());
    }

    // GETTERS


    public Favourites getFavourites() {
        return favourites;
    }

    public PlayerScores getScores() {
        return scores;
    }

    public String getVersion() {
        return version;
    }

    public boolean isUsingHeadDatabase() {
        return getConfiguration().getPerks().heads_selector;
    }

    public boolean hasChallengesEnabled() {
        return getConfiguration().getPerks().challenges;
    }

    public boolean isConnectedToMySQLDatabase() {
        return con;
    }

    public boolean isDeathMessagesEnabled() {
        return getConfiguration().getPerks().player_death_messages;
    }

    public boolean isDropsEnabled() {
        return getConfiguration().getPerks().drop_heads;
    }

    public boolean canSellHeads() {
        return (econ()) && (getConfiguration().getPerks().sell_heads);
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isStoppingPlaceableHeads() {
        return getConfiguration().getMechanics().getBoolean("stop-placement-of-sellable-heads");
    }

    public HashMap<Integer, Level> getLevels() {
        return levels;
    }

    public boolean isUsingLeaderboards() {
        return getConfiguration().getPerks().leaderboards;
    }

    public HeadsPlusConfigTextMenu getMenus() {
        return menus;
    }

    public Economy getEconomy() {
        return econ;
    }

    public LinkedHashMap<String, IHeadsPlusCommand> getCommands() {
        return commands;
    }

    public Permission getPermissions() {
        return perms;
    }

    public HeadsPlusAPI getAPI() {
        return hapi;
    }

    public String getAuthor() {
        return author;
    }

    public HeadsPlusChallenges getChallengeConfig() {
        return hpchl;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public List<ChallengeSection> getChallengeSections() {
        return challengeSections;
    }

    public NMSManager getNMS() {
        return nms;
    }

    public HeadsPlusConfigHeads getHeadsConfig() {
        return hpch;
    }

    public HeadsPlusMessagesManager getMessagesConfig() {
        return hpc;
    }

    public HeadsPlusConfigCustomHeads getHeadsXConfig() {
        return hpchx;
    }

    public HeadsPlusLevels getLevelsConfig() {
        return hpl;
    }

    public HeadsPlusCrafting getCraftingConfig() {
        return hpcr;
    }

    public List<ConfigSettings> getConfigs() {
        return cs;
    }

    public boolean usingLevels() {
        return getConfiguration().getPerks().levels;
    }

    public static Object[] getUpdate() {
        return update;
    }

    public HeadsPlusMainConfig getConfiguration() {
        return config;
    }

    public HeadsPlusConfigItems getItems() {
        return items;
    }

    public HeadsPlusConfigSounds getSounds() {
        return sounds;
    }

    public NMSIndex getNMSVersion() {
        return nmsversion;
    }

    public ChatColor getThemeColour(int i) {
        return ChatColor.valueOf(getConfiguration().getConfig().getString("theme-colours." + i));
    }

    public Challenge getChallengeByName(String name) {
        for (Challenge c : getChallenges()) {
            if (c.getConfigName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public ChallengeSection getSectionByName(String name) {
        for (ChallengeSection s : getChallengeSections()) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    public Pinned getPinned() {
        return pinned;
    }

    public void checkForMutuals() {
        try {
            if (Bukkit.getServer().getPluginManager().getPlugin("AdvancedOreGenerator") instanceof OreGenerator) {
                getLogger().info("What're we gonna do, AOG?");
                Bukkit.getPluginManager().callEvent(new CommunicateEvent("AOG"));
            }
            if (Bukkit.getPluginManager().getPlugin("SpectateProtection") instanceof CoreClass) {
                getLogger().info("'Ello SpectateProtection! What's up??");
                Bukkit.getPluginManager().callEvent(new CommunicateEvent("SpectateProtection"));
            }
            if (Bukkit.getPluginManager().getPlugin("AdvancedTeleport") instanceof io.github.at.main.CoreClass) {
                getLogger().info("HEY! ADVANCEDTELEPORT! THINK FAST!");
                io.github.at.main.CoreClass.getInstance().getLogger().info("Huh?");
                getLogger().info("Actually... don't worry about it, it's all good.");
            }
        } catch (NoClassDefFoundError ignored) {

        }



    }

    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = delimiters == null ? -1 : delimiters.length;
        if (str != null && str.length() != 0 && delimLen != 0) {
            int strLen = str.length();
            StringBuilder buffer = new StringBuilder(strLen);
            boolean capitalizeNext = true;

            for(int i = 0; i < strLen; ++i) {
                char ch = str.charAt(i);
                if (isDelimiter(ch, delimiters)) {
                    buffer.append(ch);
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer.append(Character.toTitleCase(ch));
                    capitalizeNext = false;
                } else {
                    buffer.append(ch);
                }
            }

            return buffer.toString();
        } else {
            return str;
        }
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        } else {
            int i = 0;

            for(int isize = delimiters.length; i < isize; ++i) {
                if (ch == delimiters[i]) {
                    return true;
                }
            }

            return false;
        }
    }

    private void checkDates() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month == Calendar.MAY && day == 6) {
            getLogger().info("Happy anniversary, TM and Nie! <3");
        } else if (month == Calendar.SEPTEMBER && day == 21) {
            getLogger().info("Happy Birthday, Nie!");
        } else if (month == Calendar.SEPTEMBER && day == 23) {
            getLogger().info("Happy Birthday, TM!");
        } else if (month == Calendar.DECEMBER && (day == 25 || day == 24)) {
            getLogger().info("Merry Christmas!");
        } else if (month == Calendar.APRIL && day == 30) {
            getLogger().info("Happy Birthday to me!");
        }
    }

    public boolean canUseWG() {
        return canUseWG;
    }
}
