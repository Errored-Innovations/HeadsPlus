package io.github.thatsmusic99.headsplus;

import io.github.thatsmusic99.headsplus.api.events.*;
import io.github.thatsmusic99.headsplus.commands.*;
import io.github.thatsmusic99.headsplus.commands.maincommand.*;
import io.github.thatsmusic99.headsplus.config.*;
import io.github.thatsmusic99.headsplus.config.challenges.ConfigChallenges;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.listeners.*;
import io.github.thatsmusic99.headsplus.listeners.persistence.BreakListener;
import io.github.thatsmusic99.headsplus.listeners.persistence.PlaceListener;
import io.github.thatsmusic99.headsplus.managers.*;
import io.github.thatsmusic99.headsplus.placeholders.CacheManager;
import io.github.thatsmusic99.headsplus.placeholders.HPExpansion;
import io.github.thatsmusic99.headsplus.sql.*;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusException;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
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
    // Other management stuff
    private final LinkedHashMap<String, IHeadsPlusCommand> commands = new LinkedHashMap<>();
    private final List<HeadsPlusListener<?>> listeners = new ArrayList<>();
    private List<HPConfig> configFiles = new ArrayList<>();
    private boolean canUseWG = false;
    private boolean fullyEnabled = false;
    private boolean vaultEnabled = false;

    public static final Executor async = task -> Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), task);
    public static final Executor sync = task -> Bukkit.getScheduler().runTask(HeadsPlus.get(), task);

    @Override
    public void onLoad() {
        instance = this;
        Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null || getServer().getPluginManager().getPlugin("WorldEdit") == null) return;
        if (!wg.getDescription().getVersion().startsWith("7")) return;
        canUseWG = true;
        try {
            new FlagHandler();
        } catch (IllegalStateException ex) {
            HeadsPlus.get().getLogger().severe("Failed to register WorldGuard flags, are you reloading the server, you masochist??");
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
            if (vaultEnabled) setupPermissions();
            // Set up early managers
            initiateEarlyManagers();
            // Create locale files
            createLocales();

            // Build plugin instances
            createInstances();
            io.github.thatsmusic99.headsplus.inventories.InventoryManager.initiateInvsAndIcons();

            if (!isEnabled()) return;
            new MessagesManager();
            // Registers plugin events
            registerEvents();

            // Registers commands
            registerCommands();

            // Registers subcommands
            registerSubCommands();

            // Hooks PlaceholderAPI
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new HPExpansion(this).register();
                new CacheManager();
                getLogger().info("We've registered our PAPI placeholders!");
            }

            // Initiates later managers
            initiateAsyncManagers();

            // Sets up Metrics
            Metrics metrics = new Metrics(this, 1285);
            metrics.addCustomChart(new Metrics.SimplePie("languages", () -> MainConfig.get().getString("locale")));
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                update = UpdateChecker.getUpdate();
                if (update != null) {
                    getServer().getConsoleSender().sendMessage(MessagesManager.get().getString("update" +
                            ".current-version").replaceAll("\\{version}", getDescription().getVersion())
                            + "\n" + MessagesManager.get().getString("update.new-version").replaceAll("\\{version}",
                            String.valueOf(update[0]))
                            + "\n" + MessagesManager.get().getString("update.description").replaceAll("\\{description" +
                            "}", String.valueOf(update[1])));
                    getLogger().info("Download link: https://www.spigotmc.org/resources/headsplus-1-8-x-1-12-x.40265/");
                } else {
                    getLogger().info(MessagesManager.get().getString("update.plugin-up-to-date"));
                }
                checkDates();
            });
            Bukkit.getScheduler().runTaskLater(this, this::checkForMutuals, 20);

            getServer().getConsoleSender().sendMessage(MessagesManager.get().getString("startup.plugin-enabled"));
            for (String str : Arrays.asList(
                    "§c    __  __               __     §9____  __                   §e_____§r",
                    "§c   / / / /__  ____ _____/ /____§9/ __ \\/ /_  _______  §e _   _/__  /§r",
                    "§c  / /_/ / _ \\/ __ `/ __  / ___§9/ /_/ / / / / / ___/  §e| | / / / / §r",
                    "§4 / __  /  __/ /_/ / /_/ /__  §1/ ____/ / /_/ /__  /   §6| |/ / / /  §r",
                    "§4/_/ /_/\\___/\\__,_/\\__,_/____§1/_/   /_/\\__,_/____/    §6|___/ /_/  §r",
                    "                                                                ",
                    ChatColor.GREEN + "HeadsPlus " + getDescription().getVersion() + " has been enabled successfully!", "")) {
                getServer().getConsoleSender().sendMessage(str);
            }
            fullyEnabled = true;

        } catch (Exception e) {
            try {
                DebugPrint.createReport(e, "Startup", false, null);
            } catch (Exception ex) {
                getLogger().severe("HeadsPlus has failed to start up correctly and can not read the config. An error " +
                        "report has been made in /plugins/HeadsPlus/debug");
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
        for (UUID p : InventoryManager.storedInventories.keySet()) {
            Player player = Bukkit.getPlayer(p);
            if (player == null) continue;
            final InventoryManager im = InventoryManager.getManager(player);
            if (im.getInventory() == null) continue;
            player.closeInventory();
        }
        getLogger().info(MessagesManager.get().getString("startup.plugin-disabled"));
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
        return true;
    }

    private void initiateEarlyManagers() {
        EntityDataManager.createEntityList();
        new HeadManager();
        new PersistenceManager();
        new SellableHeadsManager();
        new PaperUtil();
    }

    private void initiateAsyncManagers() {
        SQLManager.setupSQL();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            new RewardsManager();
            new ChallengeManager();
            new LevelsManager();
            new CraftingManager();
        });
    }

    private void registerEvents() {
        listeners.add(new HeadInteractListener());
        listeners.add(new EntityDeathListener());
        listeners.add(new EntitySpawnListener());
        listeners.add(new BlockPlaceListener());
        listeners.add(new PlayerDeathListener());
        listeners.add(new ItemCheckListener());
        listeners.add(new PlayerQuitListener());
        listeners.add(new MaskListener());
        listeners.add(new PlayerCraftListener());
        listeners.add(new PlayerJoinListener());
        listeners.add(new PlayerLocaleListener());
        listeners.add(new BlockPlaceListener());
        listeners.add(new PlayerPickBlockListener());
        listeners.add(new PlayerMessageDeathListener());
        listeners.add(new PlaceListener());
        listeners.add(new BreakListener());
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

        for (InventoryManager manager : InventoryManager.storedInventories.values()) {
            getServer().getPluginManager().registerEvents(manager.getInventory(), this);
        }

        for (HeadsPlusListener<?> listener : listeners) {
            if (!listener.shouldEnable()) continue;
            listener.init();
        }
    }

    private void registerCommands() {
        registerCommand("headsplus", new HeadsPlusCommand(), "hp");
        registerCommand("head", new Head());
        registerCommand("myhead", new MyHead());
        registerCommand("heads", new Heads());
        registerCommand("hplb", new LeaderboardsCommand());
        registerCommand("sellhead", new SellHead());
        registerCommand("hpc", new ChallengeCommand());
        registerCommand("addhead", new AddHead());
    }

    private void registerCommand(String command, CommandExecutor executor, String... aliases) {
        PluginCommand pluginCommand = getCommand(command);
        if (pluginCommand == null) return;
        pluginCommand.setExecutor(executor);
        if (executor instanceof TabExecutor)
            pluginCommand.setTabCompleter((TabExecutor) executor);
        if (aliases == null || aliases.length == 0) return;
        for (String alias : aliases) {
            registerCommand(alias, executor);
        }
    }

    private void createInstances() {
        configFiles = new ArrayList<>();
        MainConfig config = addConfig(MainConfig.class, "config.yml");
        if (config != null) config.load();
        addConfig(ConfigChallenges.class, "challenges.yml");
        if (new File(getDataFolder(), "customheads.yml").exists()) addConfig(ConfigCustomHeads.class, "customheads.yml");
        addConfig(ConfigCrafting.class, "crafting.yml");
        addConfig(ConfigHeads.class, "heads.yml");
        addConfig(ConfigMasks.class, "masks.yml");
        addConfig(ConfigHeadsSelector.class, "heads-selector.yml");
        addConfig(ConfigInteractions.class, "interactions.yml");
        addConfig(ConfigInventories.class, "inventories.yml");
        addConfig(ConfigLevels.class, "levels.yml");
        addConfig(ConfigMobs.class, "mobs.yml");
        addConfig(ConfigSounds.class, "sounds.yml");
        addConfig(ConfigTextMenus.class, "textmenus.yml");

        if (!getDescription().getAuthors().get(0).equals("Thatsmusic99") && !getDescription().getName().equals(
                "HeadsPlus")) {
            getLogger().severe("The plugin has been tampered with! The real download can be found here: https://www" +
                    ".spigotmc.org/resources/headsplus-1-8-x-1-15-x.40265/");
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

        EntityDataManager.init();
    }

    private <T extends HPConfig> T addConfig(Class<? extends T> clazz, String name) {
        T file = null;
        try {
            file = clazz.getConstructor().newInstance();
        } catch (InvocationTargetException ex) {
            HeadsPlus.get().getLogger().severe("A fatal error occurred loading the config file " + name + ":");
            HeadsPlus.get().getLogger().severe(ex.getCause().getMessage());
            File badFile = new File(getDataFolder(), name);
            String partialName = name.substring(0, name.indexOf('.'));

            File newFile = new File(HeadsPlus.get().getDataFolder(), partialName + "-errored-" + System.currentTimeMillis() + ".yml");

            try {
                Files.move(badFile.toPath(), newFile.toPath());
            } catch (IOException e) {
                HeadsPlus.get().getLogger().severe("Uh oh, looks like we weren't able to rename the file:");
                HeadsPlus.get().getLogger().severe(ex.getMessage());
            }

            try {
                file = clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                HeadsPlus.get().getLogger().severe("Aaaaand it happened again...");
                e.printStackTrace();
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            HeadsPlus.get().getLogger().severe("A fatal error occurred creating an instance of the config file " + name + ":");
            e.printStackTrace();
        }

        if (file == null) return null;

        configFiles.add(file);
        return file;
    }

    public void restartMessagesManager() {
        createLocales();
        new MessagesManager();
    }

    private void setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return;
        }
        perms = rsp.getProvider();
    }

    private void createLocales() {
        List<String> locales = new ArrayList<>(Arrays.asList("de_de", "en_us", "es_es", "fr_fr", "hu_hu", "lol_us",
                "nl_nl", "pl_pl", "ro_ro", "ru_ru", "zh_cn", "zh_tw"));
        File dir = new File(getDataFolder(), "locale");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                getLogger().warning("Failed to make the locale directory! Please check your file permissions.");
                return;
            }
        }
        for (String locale : locales) {
            File conf = new File(dir + File.separator + locale + ".yml");
            if (conf.exists()) continue;
            InputStream is = getResource(locale + ".yml");
            if (is == null) {
                getLogger().warning("Locale resource file " + locale + ".yml was not found, please report this to the" +
                        " developer!");
                continue;
            }
            try {
                Files.copy(is, new File(getDataFolder() + File.separator + "locale" + File.separator,
                        locale + ".yml").toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkVersion() {
        String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        int number = Integer.parseInt(bukkitVersion.split("_")[1]);
        if (number < 15) {
            getLogger().severe("!!! YOU ARE USING HEADSPLUS ON AN OLD UNSUPPORTED VERSION. !!!");
            getLogger().severe("The plugin only supports 1.15.2 to 1.19.2.");
            getLogger().severe("Please update your server if you wish to continue using the plugin.");
            getLogger().severe("To prevent any damage, the plugin is now disabling...");
            setEnabled(false);
            return false;
        } else if (number > 19) {
            getLogger().severe("!!! YOU ARE USING HEADSPLUS ON A NEW UNSUPPORTED VERSION. !!!");
            getLogger().severe("The plugin only supports 1.15.2 to 1.19.2.");
            getLogger().severe("Considering this is a new version though, there's a chance the plugin still works.");
            getLogger().severe("The plugin will remain enabled, but update it as soon as possible.");
            getLogger().severe("If this is the latest version and you find problems/bugs, please report them.");
            getLogger().severe("Any new entities with special properties will be implemented in a newer plugin " +
                    "version.");
            getLogger().severe("And lastly, how DARE you update faster than I can, pesky lass");
        }
        // death to the dodgy forks and hybrids
        for (DangerousServer server : Arrays.asList(
                new DangerousServer("Yatopia", "dev.tr7wz.yatopia.events.GameProfileLookupEvent",
                        "!!! YOU ARE USING YATOPIA. !!!",
                        "This is considered an unstable server type that mindlessly implements patches with no full " +
                                "testing.",
                        "It is even abandoned now and not recommended for use whatsoever.",
                        "If you are worried about performance, please look into Paper or Airplane.",
                        "To prevent potential breakage in the plugin due to the server type, HeadsPlus will now " +
                                "disable."),
                new DangerousServer("SugarcaneMC", "org.sugarcane.sugarcane.events.GameProfileLookupEvent",
                        "!!! YOU ARE USING SUGARCANE. !!!",
                        "Sugarcane is a fork that is following Yatopia's steps in making itself unstable through " +
                                "implementing patches not written themselves.",
                        "If you are worried about performance, please look into Paper, Tuinity or Airplane.",
                        "To prevent potential breakage in the plugin due to the server type, HeadsPlus will now " +
                                "disable."),
                new DangerousServer("Mohist", "com.mohistmc.Mohist",
                        "!!! YOU ARE USING MOHIST. !!!",
                        "HeadsPlus is not made to work with Forge-Bukkit hybrid server types.",
                        "Generally, Mohist is not recommended for use either see why here: https://essentialsx" +
                                ".net/do-not-use-mohist.html",
                        "To prevent possible problems arising from this, HeadsPlus will now disable."))) {
            try {
                Class.forName(server.clazz);
                for (String message : server.message) getLogger().severe(message);
                setEnabled(false);
                return false;
            } catch (ClassNotFoundException ignored) {
            }
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
        commands.put("sellhead", new SellHead());
        commands.put("debug", new DebugPrint());
        commands.put("conjure", new Conjure());
        commands.put("complete", new Complete());
        commands.put("tests", new TestsCommand());
        commands.put("xp", new XPCommand());
        commands.put("locale", new LocaleCommand());
        commands.put("restore", new RestoreCommand());
    }

    public String getVersion() {
        return version;
    }

    public boolean isVaultEnabled() {
        return vaultEnabled;
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

    public List<HPConfig> getConfigs() {
        return configFiles;
    }

    public static Object[] getUpdate() {
        return update;
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
        if (str == null || str.length() == 0) return str;
        int strLen = str.length();
        StringBuilder buffer = new StringBuilder(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; ++i) {
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

    private static class DangerousServer {

        private final String name;
        private final String[] message;
        private final String clazz;

        public DangerousServer(String name, String clazz, String... message) {
            this.name = name;
            this.message = message;
            this.clazz = clazz;
        }

    }
}
