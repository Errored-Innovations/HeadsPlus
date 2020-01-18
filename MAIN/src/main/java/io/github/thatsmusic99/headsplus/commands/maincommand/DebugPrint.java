package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
    public static void createReport(Exception e, String name, boolean command, CommandSender sender) {
        Logger log = HeadsPlus.getInstance().getLogger();
        ConfigurationSection cs = HeadsPlus.getInstance().getConfiguration().getMechanics();
        if (cs.getBoolean("debug.print-stacktraces-in-console")) {
            e.printStackTrace();
        }
        if (command && sender != null) {
            sender.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.cmd-fail", sender));
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
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.debug", sender);
    }

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        Player p = sender instanceof Player ? (Player) sender : null;
        if (args.length > 1) {
            if(args[1].equalsIgnoreCase("dump") || args[1].equalsIgnoreCase("clearim") || args[1].equalsIgnoreCase("save")) {
                return "";
            } else if (args[1].equalsIgnoreCase("head")) {
                if (sender instanceof Player) {
                    NMSManager nms = HeadsPlus.getInstance().getNMS();
                    if (nms.getItemInHand(p) != null) {
                        List<Material> skulls = new ArrayList<>(Arrays.asList(nms.getSkull(0).getType(),
                                nms.getSkull(1).getType(),
                                nms.getSkull(2).getType(),
                                nms.getSkull(3).getType(),
                                nms.getSkull(4).getType(),
                                nms.getSkull(5).getType()));
                        if (skulls.contains(nms.getItemInHand(p).getType())) {
                            return "";
                        } else {
                            return HeadsPlus.getInstance().getMessagesConfig().getString("commands.sellhead.false-item", p);
                        }
                    } else {
                        return HeadsPlus.getInstance().getMessagesConfig().getString("commands.sellhead.false-item", p);
                    }
                } else {
                    return "[HeadsPlus] You have to be a player to run this command!";
                }
            } else if (args[1].equalsIgnoreCase("player")) {
                if (args.length > 2) {
                    HPPlayer pl = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[2]));
                    if (pl != null) {
                        return "";
                    } else {
                        return HeadsPlus.getInstance().getMessagesConfig().getString("commands.profile.no-data", p);
                    }
                } else {
                    return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-args", p);
                }
            } else if (args[1].equalsIgnoreCase("item")) {
                if (sender instanceof Player) {
                    NMSManager nms = HeadsPlus.getInstance().getNMS();
                    if (nms.getItemInHand((Player) sender) != null) {
                        return "";
                    } else {
                        return HeadsPlus.getInstance().getMessagesConfig().getString("commands.sellhead.false-head", p);
                    }
                } else {
                    return "[HeadsPlus] You have to be a player to run this command!";
                }
            } else if (args[1].equalsIgnoreCase("delete")) {
                if (args.length > 2) {
                    HPPlayer pl = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[2]));
                    if (pl != null) {
                        return "";
                    } else {
                        return HeadsPlus.getInstance().getMessagesConfig().getString("commands.profile.no-data", p);
                    }
                } else {
                    return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-args", p);
                }
            } else if (args[1].equalsIgnoreCase("transfer")) {
                if (args.length > 2) {
                    if (args[2].equalsIgnoreCase("database")) {
                        return "";
                //    } else if (args[2].equalsIgnoreCase("files")) {
                //        h.put(true, "");
                    } else {
                        return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-args", p);
                    }
                } else {
                    return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-args", p);
                }
            } else {
                return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-args", p);
            }
        } else {
            return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-args", p);
        }
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
                    DebugPrint.createReport(e, "Debug (saving favourites)", false, sender);
                }
                try {
                    HeadsPlus.getInstance().getScores().save();
                } catch (IOException e) {
                    DebugPrint.createReport(e, "Debug (saving scores)", false, sender);
                }
                sender.sendMessage(ChatColor.GREEN + "Data has been saved.");
            } else if (args[1].equalsIgnoreCase("transfer")) {
                if (HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
                    if (args[2].equalsIgnoreCase("database")) {
                        sender.sendMessage(ChatColor.GREEN + "Starting transition to database...");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    for (String section : HeadsPlus.getInstance().getDeathEvents().ableEntities) {

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
