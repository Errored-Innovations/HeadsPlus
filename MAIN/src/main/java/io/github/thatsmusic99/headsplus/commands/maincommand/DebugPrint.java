package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import io.github.thatsmusic99.headsplus.util.MySQLAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
public class DebugPrint implements IHeadsPlusCommand {

    // R
    public DebugPrint(Exception e, String name, boolean command, CommandSender sender) {
        Logger log = HeadsPlus.getInstance().getLogger();
        ConfigurationSection cs = HeadsPlus.getInstance().getConfiguration().getMechanics();
        if (cs.getBoolean("debug.print-stacktraces-in-console")) {
            e.printStackTrace();
        }
        if (command) {
            sender.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("cmd-fail"));
        }

        if (cs.getBoolean("debug.create-debug-files")) {
            log.severe("HeadsPlus has failed to execute this task. An error report has been made in /plugins/HeadsPlus/debug");
            try {
                String s = new DebugFileCreator().createReport(e, name);
                log.severe("Report name: " + s);
                log.severe("Please submit this report to the developer at one of the following links:");
                log.severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
                log.severe("https://discord.gg/nbT7wC2");
                log.severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-12-x.237088/");
            } catch (IOException e1) {
                if (cs.getBoolean("debug.print-stacktraces-in-console")) {
                    e1.printStackTrace();
                }
            }
        }

    }
    public DebugPrint() {

    }

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descDebug();
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 1) {
            if(args[1].equalsIgnoreCase("dump") || args[1].equalsIgnoreCase("clearim") || args[1].equalsIgnoreCase("save")) {
                h.put(true, "");
            } else if (args[1].equalsIgnoreCase("head")) {
                if (sender instanceof Player) {
                    NMSManager nms = HeadsPlus.getInstance().getNMS();
                    if (nms.getItemInHand((Player) sender) != null) {
                        List<Material> skulls = new ArrayList<>(Arrays.asList(nms.getSkull(0).getType(),
                                nms.getSkull(1).getType(),
                                nms.getSkull(2).getType(),
                                nms.getSkull(3).getType(),
                                nms.getSkull(4).getType(),
                                nms.getSkull(5).getType()));
                        if (skulls.contains(nms.getItemInHand((Player) sender).getType())) {
                            h.put(true, "");
                        } else {
                            h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("false-item"));
                        }
                    } else {
                        h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("false-item"));
                    }
                } else {
                    h.put(false, "[HeadsPlus] You have to be a player to run this command!");
                }
            } else if (args[1].equalsIgnoreCase("player")) {
                if (args.length > 2) {
                    HPPlayer pl = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[2]));
                    if (pl != null) {
                        h.put(true, "");
                    } else {
                        h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("no-data"));
                    }
                } else {
                    h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("invalid-args"));
                }
            } else if (args[1].equalsIgnoreCase("item")) {
                if (sender instanceof Player) {
                    NMSManager nms = HeadsPlus.getInstance().getNMS();
                    if (nms.getItemInHand((Player) sender) != null) {
                        h.put(true, "");
                    } else {
                        h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("false-head"));
                    }
                } else {
                    h.put(false, "[HeadsPlus] You have to be a player to run this command!");
                }
            } else if (args[1].equalsIgnoreCase("delete")) {
                if (args.length > 2) {
                    HPPlayer pl = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[2]));
                    if (pl != null) {
                        h.put(true, "");
                    } else {
                        h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("no-data"));
                    }
                }
            } else if (args[1].equalsIgnoreCase("transfer")) {
                if (args.length > 2) {
                    if (args[2].equalsIgnoreCase("database")) {
                        h.put(true, "");
                //    } else if (args[2].equalsIgnoreCase("files")) {
                //        h.put(true, "");
                    } else {
                        h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("invalid-args"));
                    }
                } else {
                    h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("invalid-args"));
                }
            } else {
                h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("invalid-args"));
            }
        } else {
            h.put(false, HeadsPlus.getInstance().getMessagesConfig().getString("invalid-args"));
        }
        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            if (args[1].equalsIgnoreCase("dump")) {
                String s = new DebugFileCreator().createReport(null, "Debug command");
                sender.sendMessage(ChatColor.GREEN + "Report name: " + s);
            } else if (args[1].equalsIgnoreCase("head")) {
                if (sender instanceof Player) {
                    ItemStack is = HeadsPlus.getInstance().getNMS().getItemInHand((Player) sender);
                    String s = new DebugFileCreator().createHeadReport(is);
                    sender.sendMessage(ChatColor.GREEN + "Report name: " + s);
                }

            } else if (args[1].equalsIgnoreCase("player")) {
                OfflinePlayer pl = Bukkit.getOfflinePlayer(args[2]);
                String s = new DebugFileCreator().createPlayerReport(HPPlayer.getHPPlayer(pl));
                sender.sendMessage(ChatColor.GREEN + "Report name: " + s);
            } else if (args[1].equalsIgnoreCase("clearim")) {
                InventoryManager.pls.clear();
                sender.sendMessage(ChatColor.GREEN + "Inventory cache cleared.");
            } else if (args[1].equalsIgnoreCase("item")) {
                if (sender instanceof Player) {
                    String s = new DebugFileCreator().createItemReport(HeadsPlus.getInstance().getNMS().getItemInHand((Player) sender));
                    sender.sendMessage(ChatColor.GREEN + "Report name: " + s);
                }

            } else if (args[1].equalsIgnoreCase("delete")) {
                HeadsPlus.getInstance().getScores().deletePlayer(Bukkit.getOfflinePlayer(args[2]).getPlayer());
                sender.sendMessage(ChatColor.GREEN + "Player data for " + args[2] + " cleared.");
            } else if (args[1].equalsIgnoreCase("save")) {
                try {
                    HeadsPlus.getInstance().getFavourites().save();
                } catch (IOException e) {
                    new DebugPrint(e, "Debug (saving favourites)", false, sender);
                }
                try {
                    HeadsPlus.getInstance().getScores().save();
                } catch (IOException e) {
                    new DebugPrint(e, "Debug (saving scores)", false, sender);
                }
                sender.sendMessage(ChatColor.GREEN + "Data has been saved.");
            } else if (args[1].equalsIgnoreCase("transfer")) {
                if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
                    if (args[2].equalsIgnoreCase("database")) {
                        sender.sendMessage(ChatColor.GREEN + "Starting transition to database...");
                        MySQLAPI mysql = HeadsPlus.getInstance().getMySQLAPI();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    for (EntityType section : HeadsPlus.getInstance().getDeathEvents().ableEntities) {

                                        LinkedHashMap<OfflinePlayer, Integer> hashmap = mysql.getScores(section.name(), "headspluslb", true);
                                        for (OfflinePlayer player : hashmap.keySet()) {
                                            mysql.addOntoValue(player, section.name(), "headspluslb", hashmap.get(player));
                                        }
                                        hashmap = mysql.getScores(section.name(), "headsplussh", true);
                                        for (OfflinePlayer player : hashmap.keySet()) {
                                            mysql.addOntoValue(player, section.name(), "headsplussh", hashmap.get(player));
                                        }
                                        hashmap = mysql.getScores(section.name(), "headspluscraft", true);
                                        for (OfflinePlayer player : hashmap.keySet()) {
                                            mysql.addOntoValue(player, section.name(), "headspluscraft", hashmap.get(player));
                                        }
                                    }
                                    LinkedHashMap<OfflinePlayer, Integer> hashmap = mysql.getScores("PLAYER", "headspluslb", true);
                                    for (OfflinePlayer player : hashmap.keySet()) {
                                        mysql.addOntoValue(player, "PLAYER", "headspluslb", hashmap.get(player));
                                    }
                                    hashmap = mysql.getScores("PLAYER", "headsplussh", true);
                                    for (OfflinePlayer player : hashmap.keySet()) {
                                        mysql.addOntoValue(player, "PLAYER", "headsplussh", hashmap.get(player));
                                    }
                                    hashmap = mysql.getScores("PLAYER", "headspluscraft", true);
                                    for (OfflinePlayer player : hashmap.keySet()) {
                                        mysql.addOntoValue(player, "PLAYER", "headspluscraft", hashmap.get(player));
                                    }
                                    sender.sendMessage(ChatColor.GREEN + "Transition successful.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sender.sendMessage(ChatColor.RED + "Transition failed! More information in console error.");
                                }
                            }
                        }.runTaskAsynchronously(HeadsPlus.getInstance());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Please ensure you have MySQL enabled.");
                }
            }
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            if (HeadsPlus.getInstance().getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
