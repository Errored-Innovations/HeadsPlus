package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig.SelectorList;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "myhead",
        permission = "headsplus.myhead",
        subcommand = "Myhead",
        maincommand = false,
        usage = "/myhead"
)
public class MyHead implements CommandExecutor, IHeadsPlusCommand {

    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();
    private final HashMap<String, Boolean> tests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String l, String[] args) {
        try {
            if (sender.hasPermission("headsplus.myhead")) {


                if (sender instanceof BlockCommandSender) {
                    if (args.length > 0) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null && target.isOnline()) {
                            Bukkit.dispatchCommand(target, "minecraft:execute as " + args[1] + "run myhead");
                            return false;
                        } else {
                            sender.sendMessage(hpc.getString("player-offline"));
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
                    return false;
                }
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
                if (((Player) sender).getInventory().firstEmpty() == -1) {
                    sender.sendMessage(hpc.getString("full-inv"));
                    return true;
                }
                tests.put("Whitelist enabled", wlOn);
                tests.put("Blacklist enabled", blacklistOn);
                tests.put("Whitelist contains head", wl.contains(head));
                tests.put("Blacklist contains head", bl.contains(head));
                tests.put("Can bypass blacklist", sender.hasPermission("headsplus.bypass.blacklist"));
                tests.put("Can bypass whitelist", sender.hasPermission("headsplus.bypass.whitelist"));
                if (wlOn) {
                    if (blacklistOn) {
                        if (wl.contains(head)) {
                            if (!bl.contains(head)) {
                                giveHead((Player) sender, sender.getName());
                                return true;
                            } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                                giveHead((Player) sender, sender.getName());
                                return true;
                            } else {
                                sender.sendMessage(hpc.getString("blacklist-head"));
                                return true;
                            }
                        } else if (sender.hasPermission("headsplus.bypass.whitelist")) {
                            if (!bl.contains(head)) {
                                giveHead((Player) sender, sender.getName());
                                return true;
                            } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                                giveHead((Player) sender, sender.getName());
                                return true;
                            } else {
                                sender.sendMessage(hpc.getString("blacklist-head"));
                                return true;
                            }
                        } else {
                            sender.sendMessage(hpc.getString("whitelist-head"));
                            return true;
                        }
                    } else {
                        if (wl.contains(head)) {
                            giveHead((Player) sender, sender.getName());
                            return true;
                        } else if (sender.hasPermission("headsplus.bypass.whitelist")){
                            giveHead((Player) sender, sender.getName());
                            return true;
                        } else {
                            sender.sendMessage(hpc.getString("whitelist-head"));
                            return true;
                        }
                    }
                } else {
                    if (blacklistOn) {
                        if (!bl.contains(head)) {
                            giveHead((Player) sender, sender.getName());
                            return true;
                        } else if (sender.hasPermission("headsplus.bypass.blacklist")){
                            giveHead((Player) sender, sender.getName());
                            return true;
                        } else {
                            sender.sendMessage(hpc.getString("blacklist-head"));
                            return true;
                        }
                    } else {
                        giveHead((Player) sender, sender.getName());
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            new DebugPrint(e, "Command (myhead)", true, sender);
        }
        printDebugResults(tests, true);

        return false;
    }
    private static void giveHead(Player p, String n) {
        NMSManager nms = HeadsPlus.getInstance().getNMS();
        ItemStack skull = nms.getSkullMaterial(1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta = nms.setSkullOwner(n, meta);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', HeadsPlus.getInstance().getHeadsConfig().getConfig().getString("player.display-name").replaceAll("\\{player}", n)));
        skull.setItemMeta(meta);
        Location playerLoc = (p).getLocation();
        double playerLocY = playerLoc.getY() + 1;
        playerLoc.setY(playerLocY);
        World world = (p).getWorld();
        world.dropItem(playerLoc, skull).setPickupDelay(0);
    }

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descMyHead();
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        h.put(true, "");
        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
}
