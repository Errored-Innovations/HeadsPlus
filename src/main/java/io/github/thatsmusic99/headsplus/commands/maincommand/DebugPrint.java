package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.commands.SellHead;
import io.github.thatsmusic99.headsplus.config.ConfigCrafting;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.managers.DataManager;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@CommandInfo(
        commandname = "debug",
        permission = "headsplus.maincommand.debug",
        subcommand = "Debug",
        maincommand = true,
        usage = "/hp debug <dump|head|player|clearim|item|delete|save|transfer> <Player IGN>|<Database>"
)
@Deprecated // Also needs a cleanup
public class DebugPrint implements IHeadsPlusCommand {

    private static HeadsPlus hp;
    private static HeadsPlusMessagesManager hpc;

    public DebugPrint(HeadsPlus hp) {
        DebugPrint.hp = hp;
        hpc = HeadsPlusMessagesManager.get();
    }

    public static void createReport(Exception e, String name, boolean command, CommandSender sender) {
        try {
            Logger log = hp.getLogger();
            e.printStackTrace();
            if (command && sender != null) {
                hpc.sendMessage("commands.errors.cmd-fail", sender);
            }

            log.severe("HeadsPlus has failed to execute this task. An error report has been made in /plugins/HeadsPlus/debug");
            String s = DebugFileCreator.createReport(new HeadsPlusException(e));
            log.severe("Report name: " + s);
            log.severe("Please submit this report to the developer at one of the following links:");
            log.severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
            log.severe("https://discord.gg/nbT7wC2");
            log.severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-12-x.237088/");

        } catch (Exception ex) {
            HeadsPlus.get().getLogger().warning("An error has occurred! We tried creating a debug report, but that didn't work... stacktraces:");
            e.printStackTrace();
            ex.printStackTrace();
        }
    }

    public DebugPrint() {

    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlusMessagesManager.get().getString("descriptions.hp.debug", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            if (args.length < 2) {
                hpc.sendMessage("commands.errors.invalid-args", sender);
                return false;
            }
            String subcommand = args[1].toLowerCase();
            // Subcommands are fairly brief, so there isn't necessarily any point in creating separate classes
            if (sender.hasPermission("headsplus.maincommand.debug." + subcommand)) {
                String report;
                switch (subcommand) {
                    case "dump":
                        report = DebugFileCreator.createReport(null);
                        sender.sendMessage(ChatColor.GREEN + "Report name: " + report);
                        break;
                    case "player":
                        if (args.length > 2) {
                            HPPlayer pl = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[2]));
                            if (pl != null) {
                                report = new DebugFileCreator().createPlayerReport(pl);
                                sender.sendMessage(ChatColor.GREEN + "Report name: " + report);
                            } else {
                                hpc.sendMessage("commands.profile.no-data",  sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.invalid-args", sender);
                        }
                        break;
                    case "head":
                        if (sender instanceof Player) {
                            ItemStack is = ((Player) sender).getInventory().getItemInMainHand();
                            String s = new DebugFileCreator().createHeadReport(is);
                            sender.sendMessage(ChatColor.GREEN + "Report name: " + s);
                        }
                        break;
                    case "clearim":
                        InventoryManager.storedInventories.clear();
                        sender.sendMessage(ChatColor.GREEN + "Inventory cache cleared.");
                        break;
                    case "item":
                        if (sender instanceof Player) {
                            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                            String s = new DebugFileCreator().createItemReport(item);
                            sender.sendMessage(ChatColor.GREEN + "Report name: " + s);
                        } else {
                            hpc.sendMessage("commands.errors.not-a-player", sender);
                        }
                        break;
                    case "delete":
                        if (args.length > 2) {
                            HPPlayer pl = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[2]));
                            if (pl != null) {
                                hp.getScores().deletePlayer(Bukkit.getOfflinePlayer(args[2]).getPlayer());
                                sender.sendMessage(ChatColor.GREEN + "Player data for " + args[2] + " cleared.");
                            } else {
                                hpc.sendMessage("commands.profile.no-data", sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.invalid-args", sender);
                        }
                        break;
                    case "save":
                        try {
                            hp.getFavourites().save();
                        } catch (IOException e) {
                            DebugPrint.createReport(e, "Debug (saving favourites)", false, sender);
                        }
                        try {
                            hp.getScores().save();
                        } catch (IOException e) {
                            DebugPrint.createReport(e, "Debug (saving scores)", false, sender);
                        }
                        sender.sendMessage(ChatColor.GREEN + "Data has been saved.");
                        break;
                    case "transfer":
                        if (args.length > 2) {
                            if (hp.isConnectedToMySQLDatabase()) {
                                if (args[2].equalsIgnoreCase("database")) {
                                    sender.sendMessage(ChatColor.GREEN + "Starting transition to database...");
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                for (String section : EntityDataManager.ableEntities) {

                                                    LinkedHashMap<OfflinePlayer, Integer> hashmap = DataManager.getScores(section, "headspluslb", true);
                                                    for (OfflinePlayer player : hashmap.keySet()) {
                                                        DataManager.addToTotal(player, section, "headspluslb", hashmap.get(player));
                                                    }
                                                    hashmap = DataManager.getScores(section, "headsplussh", true);
                                                    for (OfflinePlayer player : hashmap.keySet()) {
                                                        DataManager.addToTotal(player, section, "headsplussh", hashmap.get(player));
                                                    }
                                                    hashmap = DataManager.getScores(section, "headspluscraft", true);
                                                    for (OfflinePlayer player : hashmap.keySet()) {
                                                        DataManager.addToTotal(player, section, "headspluscraft", hashmap.get(player));
                                                    }
                                                }
                                                LinkedHashMap<OfflinePlayer, Integer> hashmap = DataManager.getScores("PLAYER", "headspluslb", true);
                                                for (OfflinePlayer player : hashmap.keySet()) {
                                                    DataManager.addToTotal(player, "PLAYER", "headspluslb", hashmap.get(player));
                                                }
                                                hashmap = DataManager.getScores("PLAYER", "headsplussh", true);
                                                for (OfflinePlayer player : hashmap.keySet()) {
                                                    DataManager.addToTotal(player, "PLAYER", "headsplussh", hashmap.get(player));
                                                }
                                                hashmap = DataManager.getScores("PLAYER", "headspluscraft", true);
                                                for (OfflinePlayer player : hashmap.keySet()) {
                                                    DataManager.addToTotal(player, "PLAYER", "headspluscraft", hashmap.get(player));
                                                }
                                                sender.sendMessage(ChatColor.GREEN + "Transition successful.");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                sender.sendMessage(ChatColor.RED + "Transition failed! More information in console error.");
                                            }
                                        }
                                    }.runTaskAsynchronously(HeadsPlus.get());
                                } else {
                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Please ensure you have MySQL enabled.");
                            }
                        }
                    case "fix":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (args.length > 2) {
                                if (SellHead.getRegisteredIDs().contains(args[2])) {
                                    int slot = player.getInventory().getHeldItemSlot();
                                    // lmao who needs getItemInMainHand
                                    ItemStack item = player.getInventory().getItem(slot);
                                    PersistenceManager.get().setSellable(item, true);
                                    PersistenceManager.get().setSellType(item, args[2]);
                                    double price;
                                    double headsPrice = ConfigMobs.get().getPrice(args[2].toLowerCase().replaceAll("_", ""));
                                    if (headsPrice != 0.0) {
                                        price = headsPrice;
                                    } else {
                                        price = ConfigCrafting.get().getPrice(args[2]);
                                    }
                                    PersistenceManager.get().setSellPrice(item, price);
                                    player.getInventory().setItem(slot, item);
                                } else {
                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                }
                            } else {
                                hpc.sendMessage("commands.errors.invalid-args", sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.not-a-player", sender);
                        }
                        break;
                    case "verbose":
                        DebugVerbose.fire(sender, args);
                }
            }
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (sender.hasPermission("headsplus.commands.debug")) {
            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], Arrays.asList("dump", "head", "player", "clearim", "item", "delete", "save", "transfer", "fix", "verbose"), results);
            } else if (args.length > 2) {
                switch (args[1].toLowerCase()) {
                    case "fix":
                        StringUtil.copyPartialMatches(args[2], SellHead.getRegisteredIDs(), results);
                        break;
                    case "verbose":
                        results = DebugVerbose.onTabComplete(sender, args);
                        break;
                    default:
                        StringUtil.copyPartialMatches(args[2], IHeadsPlusCommand.getPlayers(sender), results);
                        break;
                }
            }
        }

        return results;
    }
}
