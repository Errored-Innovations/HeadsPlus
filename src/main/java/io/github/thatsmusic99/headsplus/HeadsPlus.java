package io.github.thatsmusic99.headsplus;

import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.api.HPExpansion;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.api.events.*;
import io.github.thatsmusic99.headsplus.commands.*;
import io.github.thatsmusic99.headsplus.commands.maincommand.*;
import io.github.thatsmusic99.headsplus.config.*;
import io.github.thatsmusic99.headsplus.config.challenges.ConfigChallenges;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.listeners.*;
import io.github.thatsmusic99.headsplus.listeners.tabcompleting.TabCompleteSellhead;
import io.github.thatsmusic99.headsplus.managers.*;
import io.github.thatsmusic99.headsplus.storage.Favourites;
import io.github.thatsmusic99.headsplus.storage.Pinned;
import io.github.thatsmusic99.headsplus.storage.PlayerScores;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.NewMySQLAPI;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusException;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executor;

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
    // Other management stuff
    private final List<Challenge> challenges = new ArrayList<>();
    private final List<ChallengeSection> challengeSections = new ArrayList<>();
    private final LinkedHashMap<String, IHeadsPlusCommand> commands = new LinkedHashMap<>();
    private final List<HeadsPlusListener<?>> listeners = new ArrayList<>();
    private final HashMap<Integer, Level> levels = new HashMap<>();
    private List<HPConfig> configFiles = new ArrayList<>();
    private Favourites favourites;
    private Pinned pinned;
    private PlayerScores scores;
    private boolean canUseWG = false;
    private boolean fullyEnabled = false;
    private boolean vaultEnabled = false;

    public static final Executor async = task -> Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), task);
    public static final Executor sync = task -> Bukkit.getScheduler().runTask(HeadsPlus.get(), task);

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
            if (!checkVersion()) return;
            // Set up Vault connection
            vaultEnabled = setupEconomy();
            // Set up early managers
            initiateEarlyManagers();
            // Create locale files
            createLocales();

            // Build plugin instances
            createInstances();
            new HeadsPlusMessagesManager();
            io.github.thatsmusic99.headsplus.inventories.InventoryManager.initiateInvsAndIcons();

            if (!isEnabled()) return;

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
            PlayerJoinListener.reloaded = false;
            // Sets up Metrics
            Metrics metrics = new Metrics(this, 1285);
            metrics.addCustomChart(new Metrics.SimplePie("languages", () -> MainConfig.get().getString("locale")));
             // if (getConfiguration().getMechanics().getBoolean("update.check")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new CraftingManager();
                        update = UpdateChecker.getUpdate();
                        if (update != null) {
                            getServer().getConsoleSender().sendMessage(HeadsPlusMessagesManager.get().getString("update.current-version").replaceAll("\\{version}", getDescription().getVersion())
                                    + "\n" + HeadsPlusMessagesManager.get().getString("update.new-version").replaceAll("\\{version}", String.valueOf(update[0]))
                                    + "\n" + HeadsPlusMessagesManager.get().getString("update.description").replaceAll("\\{description}", String.valueOf(update[1])));
                            getLogger().info("Download link: https://www.spigotmc.org/resources/headsplus-1-8-x-1-12-x.40265/");
                        } else {
                            getLogger().info(HeadsPlusMessagesManager.get().getString("update.plugin-up-to-date"));
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

           // }
            getServer().getConsoleSender().sendMessage(HeadsPlusMessagesManager.get().getString("startup.plugin-enabled"));
           // if (getConfiguration().getPerks().ascii) {
                getServer().getConsoleSender().sendMessage("                                                               §f\n" +
                        "§c    __  __               __     §9____  __                   §e_____§r\n" +
                        "§c   / / / /__  ____ _____/ /____§9/ __ \\/ /_  _______  §e _   _/__  /§r\n" +
                        "§c  / /_/ / _ \\/ __ `/ __  / ___§9/ /_/ / / / / / ___/  §e| | / / / / §r\n" +
                        "§4 / __  /  __/ /_/ / /_/ /__  §1/ ____/ / /_/ /__  /   §6| |/ / / /  §r\n" +
                        "§4/_/ /_/\\___/\\__,_/\\__,_/____§1/_/   /_/\\__,_/____/    §6|___/ /_/  §r\n" +
                        "                                                                \n" +
                        ChatColor.GREEN + "HeadsPlus " + getDescription().getVersion() + " has been enabled successfully!" + "\n");
           // }
            fullyEnabled = true;

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
        if (!fullyEnabled) return;
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
        getLogger().info(HeadsPlusMessagesManager.get().getString("startup.plugin-disabled"));
    }

    public static HeadsPlus get() {
        return instance;

    }

    public boolean setupEconomy() {
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
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + MainConfig.get().getMySQL().MYSQL_HOST + ":" +
                                MainConfig.get().getMySQL().MYSQL_PORT + "/" +
                                MainConfig.get().getMySQL().MYSQL_DATABASE + "?useSSL=false&autoReconnect=true",
                        MainConfig.get().getMySQL().MYSQL_USERNAME,
                        MainConfig.get().getMySQL().MYSQL_PASSWORD);
                NewMySQLAPI.createTable();
                con = true;
            } catch (SQLException ex) {
                getLogger().warning("MySQL could not be enabled due to a problem connecting. Details to follow... (Error code: 3)");
                getLogger().warning(ex.getMessage() + " (MySQL Error code: " + ex.getErrorCode() + ")");
                getLogger().warning(ex.getCause().getMessage());
            }
        }
    }

    private void initiateEarlyManagers() {
        EntityDataManager.createEntityList();
        new HeadManager();
        new PersistenceManager();
        new SellableHeadsManager();
        new PaperUtil();
    }

    private void registerEvents() {
        listeners.add(new HeadInteractListener());
        listeners.add(new EntityDeathListener());
        listeners.add(new EntitySpawnListener());
        listeners.add(new BlockPlaceListener());
        listeners.add(new PlayerDeathListener());
        listeners.add(new ItemCheckListener());
        listeners.add(new MaskListener());
        listeners.add(new PlayerCraftListener());
        listeners.add(new PlayerJoinListener());
        listeners.add(new BlockPlaceListener());
        listeners.add(new PlayerPickBlockListener());
        listeners.add(new PlayerMessageDeathListener());
        listeners.add(new SoundListener<>("on-sell-head", SellHeadEvent.class));
        listeners.add(new SoundListener<>("on-buy-head", HeadPurchaseEvent.class));
        listeners.add(new SoundListener<>("on-change-section", SectionChangeEvent.class));
        listeners.add(new SoundListener<>("on-entity-head-drop", EntityHeadDropEvent.class));
        listeners.add(new SoundListener<>("on-player-head-drop", "getDeadPlayer", PlayerHeadDropEvent.class));
        listeners.add(new SoundListener<>("on-level-up", LevelUpEvent.class));
        listeners.add(new SoundListener<>("on-craft-head", HeadCraftEvent.class));
        initiateEvents();
    }

    public void initiateEvents() {
        HandlerList.unregisterAll(this);
        new LeaderboardListeners();
        for (HeadsPlusListener<?> listener : listeners) {
            if (!listener.shouldEnable()) continue;
            listener.init();
        }
    }

    private void registerCommands() {
        getCommand("headsplus").setExecutor(new HeadsPlusCommand());
        getCommand("hp").setTabCompleter(new HeadsPlusCommand());
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
        configFiles = new ArrayList<>();
        MainConfig config;
        configFiles.add(config = new MainConfig());
        config.load();
        configFiles.add(new ConfigAnimations());
        configFiles.add(new ConfigChallenges());
        configFiles.add(new ConfigCustomHeads());
        configFiles.add(new ConfigCrafting());
        configFiles.add(new ConfigHeads());
        configFiles.add(new ConfigHeadsSelector());
        configFiles.add(new ConfigInteractions());
        configFiles.add(new ConfigInventories());
        configFiles.add(new ConfigLevels());
        configFiles.add(new ConfigMasks());
        configFiles.add(new ConfigMobs());
        configFiles.add(new ConfigSounds());
        configFiles.add(new ConfigTextMenus());

        if (!getDescription().getAuthors().get(0).equals("Thatsmusic99") && !getDescription().getName().equals("HeadsPlus")) {
            getLogger().severe("The plugin has been tampered with! The real download can be found here: https://www.spigotmc.org/resources/headsplus-1-8-x-1-15-x.40265/");
            getLogger().severe("Only reupload the plugin on other sites with my permission, please! (Error code: 4)");
            setEnabled(false);
            return;
        }

        for (HPConfig file : configFiles) {
            try {
                if (file instanceof MainConfig) continue;
                if (file instanceof FeatureConfig) {
                    if (!((FeatureConfig) file).shouldLoad()) continue;
                }
                file.load();
            } catch (Exception ex) {
                getLogger().severe("Failed to load config " + file.getClass().getSimpleName() + "!");
                ex.printStackTrace();
            }

        }

        try {
            setupJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (MainConfig.get().getMySQL().ENABLE_MYSQL) {
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
        EntityDataManager.init();
        new MaskManager();
    }

    public void restartMessagesManager() {
        createLocales();
        new HeadsPlusMessagesManager();
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
        List<String> locales = new ArrayList<>(Arrays.asList("de_de", "en_us", "es_es", "fr_fr", "hu_hu", "lol_us", "nl_nl", "pl_pl", "ro_ro", "ru_ru", "zh_cn", "zh_tw"));
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

    private boolean checkVersion() {
        String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        int number = Integer.parseInt(bukkitVersion.split("_")[1]);
        if (number < 15) {
            getLogger().severe("!!! YOU ARE USING HEADSPLUS ON AN OLD UNSUPPORTED VERSION. !!!");
            getLogger().severe("The plugin only supports 1.15.2 to 1.17.");
            getLogger().severe("Please update your server if you wish to continue using the plugin.");
            getLogger().severe("To prevent any damage, the plugin is now disabling...");
            setEnabled(false);
            return false;
        } else if (number > 17) {
            getLogger().severe("!!! YOU ARE USING HEADSPLUS ON A NEW UNSUPPORTED VERSION. !!!");
            getLogger().severe("The plugin only supports 1.15.2 to 1.17.");
            getLogger().severe("Considering this is a new version though, there's a chance the plugin still works.");
            getLogger().severe("The plugin will remain enabled, but update it as soon as possible.");
            getLogger().severe("If this is the latest version and you find problems/bugs, please report them.");
            getLogger().severe("Any new entities with special properties will be implemented in a newer plugin version.");
            getLogger().severe("And lastly, how DARE you update faster than I can, pesky lass");
        }
        return true;
    }

    private void registerSubCommands() {
        commands.put("help", new HelpMenu());
        commands.put("info", new Info());
        commands.put("reload", new ReloadCommand());
        commands.put("profile", new ProfileCommand());
        commands.put("hpc", new ChallengeCommand());
        commands.put("addhead", new AddHead());
        commands.put("head", new Head());
        commands.put("heads", new Heads());
        commands.put("hplb", new LeaderboardsCommand());
        commands.put("myhead", new MyHead());
        commands.put("sellhead", new SellHead(this));
        commands.put("debug", new DebugPrint(this));
        commands.put("conjure", new Conjure());
        commands.put("complete", new Complete());
        commands.put("tests", new TestsCommand());
        commands.put("xp", new XPCommand());
        commands.put("locale", new LocaleCommand());
        commands.put("restore", new RestoreCommand());
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

    public boolean isConnectedToMySQLDatabase() {
        return con;
    }

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public Connection getConnection() {
        return connection;
    }

    public HashMap<Integer, Level> getLevels() {
        return levels;
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

    public String getAuthor() {
        return author;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public List<ChallengeSection> getChallengeSections() {
        return challengeSections;
    }

    public List<HPConfig> getConfigs() {
        return configFiles;
    }

    public static Object[] getUpdate() {
        return update;
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
            HashMap<String, String> pluginCommunications = new HashMap<>();
            pluginCommunications.put("AdvancedTeleport", "wait, what");
            pluginCommunications.put("CHRONOS", "GET THE HELL OUT OF MY ROOM I'M PLAYING MINECRAFT");
            pluginCommunications.put("SimplePets", "red looks kinda sus");
            pluginCommunications.put("AdvancedOreGenerator", "bro i'm dead");
            pluginCommunications.put("ConfigurationMaster", "You almost forgot me... :(");
            getLogger().info("Avengers, assemble!");

            for (String plugin : pluginCommunications.keySet()) {
                Plugin actualPlugin = Bukkit.getPluginManager().getPlugin(plugin);
                if (actualPlugin != null) {
                    actualPlugin.getLogger().info(pluginCommunications.get(plugin));
                }
            }
        } catch (NoClassDefFoundError ignored) {

        }
    }

    public static void debug(String message) {
        if (MainConfig.get().getMiscellaneous() == null) return;
        if (MainConfig.get().getMiscellaneous().DEBUG) {
            get().getLogger().info(message);
        }
    }

    public static String capitalize(String str) {
        if (str != null && str.length() != 0) {
            int strLen = str.length();
            StringBuilder buffer = new StringBuilder(strLen);
            boolean capitalizeNext = true;

            for(int i = 0; i < strLen; ++i) {
                char ch = str.charAt(i);
                if (Character.isWhitespace(ch)) {
                    buffer.append(ch);
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer.append(Character.toTitleCase(ch));
                    capitalizeNext = false;
                } else {
                    buffer.append(Character.toLowerCase(ch));
                }
            }
            return buffer.toString();
        } else {
            return str;
        }
    }

    private void checkDates() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month == Calendar.MAY && day == 6) {
            getLogger().info("Happy anniversary, Holly and Nie! <3");
        } else if (month == Calendar.SEPTEMBER && day == 21) {
            getLogger().info("Happy Birthday, Nie!");
        } else if (month == Calendar.SEPTEMBER && day == 23) {
            getLogger().info("Happy Birthday, Holly!");
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
