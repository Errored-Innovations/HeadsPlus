package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

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
                            hpc.sendMessage("commands.errors.player-offline", sender);
                        }
                    }
                    return false;
                } else if (!(sender instanceof Player)) {
                    hpc.sendMessage("commands.errors.not-a-player", sender);
                    return false;
                }
                Player p = (Player) sender;
                /*SelectorList blacklist = HeadsPlus.get().getConfiguration().getHeadsBlacklist();
                SelectorList whitelist = HeadsPlus.get().getConfiguration().getHeadsWhitelist();
                HeadsPlus.get().saveConfig();
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
                    hpc.sendMessage("commands.head.full-inv", p);
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
                                hpc.sendMessage("commands.head.blacklist-head", sender);
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
                                hpc.sendMessage("commands.head.blacklist-head", sender);
                                return true;
                            }
                        } else {
                            hpc.sendMessage("commands.head.whitelist-head", sender);
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
                            hpc.sendMessage("commands.head.whitelist-head", sender);
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
                            hpc.sendMessage("commands.head.blacklist-head", sender);
                            return true;
                        }
                    } else {
                        giveHead(p, sender.getName());
                        return true;
                    }
                } */
                giveHead(p, sender.getName());
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (myhead)", true, sender);
        }

        return false;
    }

    private void giveHead(Player p, String n) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        PaperUtil.get().setProfile((SkullMeta) skull.getItemMeta(), n).thenAccept(meta -> {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigMobs.get().getPlayerDisplayName(n)));
            skull.setItemMeta(meta);
            p.getInventory().addItem(skull);
        });
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlusMessagesManager.get().getString("descriptions.myhead", sender);
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
