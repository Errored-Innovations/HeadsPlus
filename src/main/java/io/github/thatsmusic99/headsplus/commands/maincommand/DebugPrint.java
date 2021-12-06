package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigCrafting;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.managers.MaskManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.managers.SellableHeadsManager;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@CommandInfo(
        commandname = "debug",
        permission = "headsplus.maincommand.debug",
        maincommand = true,
        usage = "/hp debug <dump|head|player|clearim|item|delete|save|transfer> <Player IGN>|<Database>",
        descriptionPath = "descriptions.hp.debug"
)
@Deprecated // Also needs a cleanup
public class DebugPrint implements IHeadsPlusCommand {

    private static final MessagesManager hpc = MessagesManager.get();

    public static void createReport(Exception e, String name, boolean command, CommandSender sender) {
        try {
            Logger log = HeadsPlus.get().getLogger();
            e.printStackTrace();
            if (command && sender != null) {
                hpc.sendMessage("commands.errors.cmd-fail", sender);
            }

            log.severe("HeadsPlus has failed to execute this task. An error report has been made in " +
                    "/plugins/HeadsPlus/debug - task: " + name);
            String s = DebugFileCreator.createReport(new HeadsPlusException(e));
            log.severe("Report name: " + s);
            log.severe("Please submit this report to the developer at one of the following links:");
            log.severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
            log.severe("https://discord.gg/nbT7wC2");
            log.severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-12-x.237088/");

        } catch (Exception ex) {
            HeadsPlus.get().getLogger().warning("An error has occurred! We tried creating a debug report, but that " +
                    "didn't work... task: " + name + "; stacktraces:");
            e.printStackTrace();
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
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
                            HPPlayer pl = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
                            if (pl != null) {
                                report = new DebugFileCreator().createPlayerReport(pl);
                                sender.sendMessage(ChatColor.GREEN + "Report name: " + report);
                            } else {
                                hpc.sendMessage("commands.profile.no-data", sender);
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
                    case "fix":
                    case "mask":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (args.length > 2) {
                                int slot = player.getInventory().getHeldItemSlot();
                                // lmao who needs getItemInMainHand
                                ItemStack item = player.getInventory().getItem(slot);
                                if (item == null) return false;
                                item = item.clone();
                                if (subcommand.equals("fix") && SellableHeadsManager.get().isRegistered(args[2])) {
                                    PersistenceManager.get().setSellable(item, true);
                                    PersistenceManager.get().setSellType(item, args[2]);
                                    double price;
                                    double headsPrice = SellableHeadsManager.get().getPrice(args[2]);
                                    if (headsPrice != 0.0) {
                                        price = headsPrice;
                                    } else {
                                        price = ConfigCrafting.get().getPrice(args[2]);
                                    }
                                    PersistenceManager.get().setSellPrice(item, price);
                                    sender.sendMessage(ChatColor.GREEN + "Applying ID " + args[2] + " to held item (sellable)");
                                } else if (subcommand.equals("mask") && MaskManager.get().isMaskRegistered(args[2])) {
                                    PersistenceManager.get().setMaskType(item, args[2]);
                                    sender.sendMessage(ChatColor.GREEN + "Applying ID " + args[2] + " to held item (mask)");
                                } else {
                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                }
                                final ItemStack finalItem = item;
                                Bukkit.getScheduler().runTask(HeadsPlus.get(),
                                        () -> player.getInventory().setItem(slot, finalItem));
                            } else {
                                hpc.sendMessage("commands.errors.invalid-args", sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.not-a-player", sender);
                        }
                        break;
                    case "verbose":
                        DebugVerbose.fire(sender, args);
                        break;
                }
            } else {
                hpc.sendMessage("commands.errors.no-perm", sender);
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (sender.hasPermission("headsplus.commands.debug")) {
            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], Arrays.asList("dump", "head", "player", "clearim", "item",
                        "delete", "save", "transfer", "fix", "verbose"), results);
            } else if (args.length > 2) {
                switch (args[1].toLowerCase()) {
                    case "fix":
                        StringUtil.copyPartialMatches(args[2], SellableHeadsManager.get().getKeys(), results);
                        break;
                    case "mask":
                        StringUtil.copyPartialMatches(args[2], MaskManager.get().getMaskKeys(), results);
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
