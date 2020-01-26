package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig.SelectorList;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "head",
        permission = "headsplus.head",
        subcommand = "Head",
        maincommand = false,
        usage = "/head <IGN> [Player]"
)
public class Head implements CommandExecutor, IHeadsPlusCommand {

    private final HeadsPlus hp = HeadsPlus.getInstance();
    private final HeadsPlusMessagesManager hpc = hp.getMessagesConfig();
    private final HashMap<String, Boolean> tests = new HashMap<>();

    private List<String> selectors = Arrays.asList("@a", "@p", "@s", "@r");

    private boolean startsWithSelector(String arg) {
        for(String selector : selectors) {
            if(arg.startsWith(selector)) return true;
        }
        return false;
    }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return fire(args, sender);
    }

	private void giveHead(Player p, String n) {
		ItemStack skull = hp.getNMS().getSkullMaterial(1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta = hp.getNMS().setSkullOwner(n, meta);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', hp.getHeadsConfig().getConfig().getString("player.display-name").replaceAll("\\{player}", n)));
        skull.setItemMeta(meta);
        Location playerLoc = (p).getLocation();
        double playerLocY = playerLoc.getY() + 1;
        playerLoc.setY(playerLocY);
        World world = (p).getWorld();
        world.dropItem(playerLoc, skull).setPickupDelay(0);
	}

	private void giveH(String[] args, CommandSender sender, Player p) {
        Player p2 = sender instanceof Player ? (Player) sender : null;
	    SelectorList blacklist = hp.getConfiguration().getHeadsBlacklist();
        SelectorList whitelist = hp.getConfiguration().getHeadsWhitelist();
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
        String head = args[0].toLowerCase();
        if (p.getInventory().firstEmpty() == -1) {
            sender.sendMessage(hpc.getString("commands.head.full-inv", p2));
            return;
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
                        giveHead(p, args[0]);
                    } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                        giveHead(p, args[0]);
                    } else {
                        sender.sendMessage(hpc.getString("commands.head.blacklist-head", p2));
                    }
                } else if (sender.hasPermission("headsplus.bypass.whitelist")) {
                    if (!bl.contains(head)) {
                        giveHead(p, args[0]);
                    } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                        giveHead(p, args[0]);
                    } else {
                        sender.sendMessage(hpc.getString("commands.head.blacklist-head", p2));
                    }
                } else {
                    sender.sendMessage(hpc.getString("commands.head.whitelist-head", p2));
                }
            } else {
                if (wl.contains(head)) {
                    giveHead(p, args[0]);
                } else if (sender.hasPermission("headsplus.bypass.whitelist")){
                    giveHead(p, args[0]);
                } else {
                    sender.sendMessage(hpc.getString("commands.head.whitelist-head", p2));
                }
            }
        } else {
            if (blacklistOn) {
                if (!bl.contains(head)) {
                    giveHead(p, args[0]);
                } else if (sender.hasPermission("headsplus.bypass.blacklist")){
                    giveHead(p, args[0]);
                } else {
                    sender.sendMessage(hpc.getString("commands.head.blacklist-head", p2));
                }
            } else {
                giveHead(p, args[0]);
            }
        }
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.head", sender);
    }

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        if (args.length > 0) {
            if ((args[0].matches("^[A-Za-z0-9_]+$"))) {
                if (args[0].length() < 17) {
                    if (args[0].length() > 2) {
                       return "";
                    } else {
                        return hpc.getString("commands.head.head-too-short", sender);
                    }
                } else {
                    return hpc.getString("commands.head.head-too-long", sender);
                }
            } else {
                return hpc.getString("commands.head.alpha-names", sender);
            }
        } else {
            return hpc.getString("commands.head.invalid-args", sender);
        }
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
	    tests.clear();
	    try {
	        tests.put("No permission", !sender.hasPermission("headsplus.head"));
	        if (sender.hasPermission("headsplus.head")) {
	            tests.put("More than 1 arg", args.length >= 2);
	            if (args.length > 1) {
	                tests.put("No Other permission", !sender.hasPermission("headsplus.head.others"));
	                if (sender.hasPermission("headsplus.head.others")) {

                        tests.put("Player Found", hp.getNMS().getPlayer(args[1]) != null);
                        if (sender instanceof BlockCommandSender && startsWithSelector(args[0]) && startsWithSelector(args[1])) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:execute as " + args[1] + " run head " + args[0] + " " + args[1]);
                        } else if (hp.getNMS().getPlayer(args[1]) != null) {

                            boolean b = args[0].matches("^[A-Za-z0-9_]+$") && (2 < args[0].length()) && (args[0].length() < 17);
                            tests.put("Valid name", b);
                            if (b) {
                                String[] s = new String[2];
                                s[0] = args[0];
                                s[1] = args[1];
                                giveH(s, sender, hp.getNMS().getPlayer(args[1]));
                                printDebugResults(tests, true);
                                return true;
                            } else if (!args[0].matches("^[A-Za-z0-9_]+$")) {
                                sender.sendMessage(hpc.getString("commands.head.alpha-names", sender));
                            } else if (args[0].length() < 3) {
                                sender.sendMessage(hpc.getString("commands.head.head-too-short", sender));
                            } else {
                                sender.sendMessage(hpc.getString("commands.head.head-too-long", sender));
                            }
                        } else {
                            sender.sendMessage(hpc.getString("commands.errors.player-offline", sender));
                        }
	                    printDebugResults(tests, false);
	                    return true;
	                } else {
	                    sender.sendMessage(hpc.getString("commands.errors.no-perm", sender));
	                }
	            } else if (args.length > 0) {
	                tests.put("Instance of Player", sender instanceof Player);
	                if (sender instanceof Player) {
	                    Player p = (Player) sender;
	                    boolean b = args[0].matches("^[A-Za-z0-9_]+$") && (2 < args[0].length()) && (args[0].length() < 17);
	                    tests.put("Valid name", b);
	                    if (b) {
	                        giveH(args, sender, p);
	                        printDebugResults(tests, true);
	                        return true;
	                    }
	                } else {
	                    sender.sendMessage(hpc.getString("commands.errors.not-a-player", sender));
	                }
	            } else {
	                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
	            }
	        } else {
	            sender.sendMessage(hpc.getString("commands.errors.no-perm", sender));
	        }
        } catch (Exception e) {
	        DebugPrint.createReport(e, "Command (head)", true, sender);
        }
        printDebugResults(tests, false);
        return false;
    }
}
	
