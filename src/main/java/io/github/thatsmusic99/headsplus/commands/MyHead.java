package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig.SelectorList;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "myhead",
        permission = "headsplus.myhead",
        subcommand = "Myhead",
        maincommand = false,
        usage = "/myhead"
)
public class MyHead implements CommandExecutor, IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String l, String[] args) {
        try {
            if (sender.hasPermission("headsplus.myhead")) {


                if (sender instanceof BlockCommandSender) {
                    if (args.length > 0) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null && target.isOnline()) {
                            Bukkit.dispatchCommand(target, "minecraft:execute as " + args[1] + "run myhead");
                        } else {
                            sender.sendMessage(hpc.getString("commands.errors.player-offline", null));
                        }
                        return false;
                    } else {
                        return false;
                    }
                } else if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
                    return false;
                }
                Player p = (Player) sender;
                SelectorList blacklist = HeadsPlus.getInstance().getConfiguration().getHeadsBlacklist();
                SelectorList whitelist = HeadsPlus.getInstance().getConfiguration().getHeadsWhitelist();
                HeadsPlus.getInstance().saveConfig();
                List<String> bl = new ArrayList<>();
                for (String str : blacklist.list) {
                    bl.add(str.toLowerCase());
                }
                List<String> wl = new ArrayList<>();
                for (String str : whitelist.list) {
                    wl.add(str.toLowerCase());
                }

                boolean blacklistOn = blacklist.enabled;
                boolean wlOn = whitelist.enabled;
                String head = sender.getName().toLowerCase();
                if (p.getInventory().firstEmpty() == -1) {
                    sender.sendMessage(hpc.getString("commands.head.full-inv", p));
                    return true;
                }
                if (wlOn) {
                    if (blacklistOn) {
                        if (wl.contains(head)) {
                            if (!bl.contains(head)) {
                                giveHead(p, sender.getName());
                                return true;
                            } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                                giveHead(p, sender.getName());
                                return true;
                            } else {
                                sender.sendMessage(hpc.getString("commands.head.blacklist-head", p));
                                return true;
                            }
                        } else if (sender.hasPermission("headsplus.bypass.whitelist")) {
                            if (!bl.contains(head)) {
                                giveHead(p, sender.getName());
                                return true;
                            } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                                giveHead(p, sender.getName());
                                return true;
                            } else {
                                sender.sendMessage(hpc.getString("commands.head.blacklist-head", p));
                                return true;
                            }
                        } else {
                            sender.sendMessage(hpc.getString("commands.head.whitelist-head", p));
                            return true;
                        }
                    } else {
                        if (wl.contains(head)) {
                            giveHead(p, sender.getName());
                            return true;
                        } else if (sender.hasPermission("headsplus.bypass.whitelist")){
                            giveHead(p, sender.getName());
                            return true;
                        } else {
                            sender.sendMessage(hpc.getString("commands.head.whitelist-head", p));
                            return true;
                        }
                    }
                } else {
                    if (blacklistOn) {
                        if (!bl.contains(head)) {
                            giveHead(p, sender.getName());
                            return true;
                        } else if (sender.hasPermission("headsplus.bypass.blacklist")){
                            giveHead(p, sender.getName());
                            return true;
                        } else {
                            sender.sendMessage(hpc.getString("commands.head.blacklist-head", p));
                            return true;
                        }
                    } else {
                        giveHead(p, sender.getName());
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (myhead)", true, sender);
        }

        return false;
    }
    private static void giveHead(Player p, String n) {
        NMSManager nms = HeadsPlus.getInstance().getNMS();
        ItemStack skull = nms.getSkullMaterial(1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta = nms.setSkullOwner(n, meta);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HeadsPlus.getInstance().getHeadsConfig().getConfig().getString("player.display-name").replaceAll("\\{player}", n)));
        skull.setItemMeta(meta);
        p.getInventory().addItem(skull);
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.myhead", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
