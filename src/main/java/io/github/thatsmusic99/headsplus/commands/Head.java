package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HeadsPlusAPI;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@CommandInfo(
        commandname = "head",
        permission = "headsplus.head",
        subcommand = "Head",
        maincommand = false,
        usage = "/head <IGN> [Player]"
)
public class Head implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final HeadsPlus hp = HeadsPlus.get();
    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    private final List<String> selectors = Arrays.asList("@a", "@p", "@s", "@r");

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
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        PaperUtil.get().setProfile((SkullMeta) skull.getItemMeta(), n).thenAccept(meta -> {
            meta.setDisplayName(ConfigMobs.get().getPlayerDisplayName(n));
            skull.setItemMeta(meta);
            p.getInventory().addItem(skull);
        });
    }

	private void giveH(String[] args, CommandSender sender, Player p) {
        Player p2 = sender instanceof Player ? (Player) sender : null;
	   /* SelectorList blacklist = hp.getConfiguration().getHeadsBlacklist();
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
            hpc.sendMessage("commands.head.full-inv", p2);
            return;
        }
        if (wlOn) {
            if (blacklistOn) {
                if (wl.contains(head)) {
                    if (!bl.contains(head)) {
                        giveHead(p, args[0]);
                    } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                        giveHead(p, args[0]);
                    } else {
                        hpc.sendMessage("commands.head.blacklist-head", p2);
                    }
                } else if (sender.hasPermission("headsplus.bypass.whitelist")) {
                    if (!bl.contains(head)) {
                        giveHead(p, args[0]);
                    } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                        giveHead(p, args[0]);
                    } else {
                        hpc.sendMessage("commands.head.blacklist-head", p2);
                    }
                } else {
                    hpc.sendMessage("commands.head.whitelist-head", p2);
                }
            } else {
                if (wl.contains(head)) {
                    giveHead(p, args[0]);
                } else if (sender.hasPermission("headsplus.bypass.whitelist")){
                    giveHead(p, args[0]);
                } else {
                    hpc.sendMessage("commands.head.whitelist-head", p2);
                }
            }
        } else {
            if (blacklistOn) {
                if (!bl.contains(head)) {
                    giveHead(p, args[0]);
                } else if (sender.hasPermission("headsplus.bypass.blacklist")){
                    giveHead(p, args[0]);
                } else {
                    hpc.sendMessage("commands.head.blacklist-head", p2);
                }
            } else {
                giveHead(p, args[0]);
            }
        }
	    */
        giveHead(p, args[0]);
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.head", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
	    try {
	        if (sender.hasPermission("headsplus.head")) {
	            if (args.length > 1) {
	                if (sender.hasPermission("headsplus.head.others")) {
                        if (sender instanceof BlockCommandSender && startsWithSelector(args[0]) && startsWithSelector(args[1])) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:execute as " + args[1] + " run head " + args[0] + " " + args[1]);
                        } else if (Bukkit.getPlayer(args[1]) != null) {
                            if (CachedValues.PLAYER_NAME.matcher(args[0]).matches()) {
                                String[] s = new String[2];
                                s[0] = args[0];
                                s[1] = args[1];
                                giveH(s, sender, Bukkit.getPlayer(args[1]));
                                return true;
                            } else {
                                hpc.sendMessage("commands.head.invalid-args", sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.player-offline", sender);
                        }
	                    return true;
	                } else {
	                    hpc.sendMessage("commands.errors.no-perm", sender);
	                }
	            } else if (args.length > 0) {
	                if (sender instanceof Player) {
	                    Player p = (Player) sender;
	                    if (CachedValues.PLAYER_NAME.matcher(args[0]).matches()) {
	                        giveH(args, sender, p);
	                        return true;
	                    } else if (args[0].startsWith("http")) {
                            String texture = new String(Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", args[0]).getBytes()));
                            p.getInventory().addItem(new HeadsPlusAPI().createSkull(texture, ":)"));
                            return true;
	                    }
	                } else {
	                    hpc.sendMessage("commands.errors.not-a-player", sender);
	                }
	            } else {
	                hpc.sendMessage("commands.errors.invalid-args", sender);
	            }
	        } else {
	            hpc.sendMessage("commands.errors.no-perm", sender);
	        }
        } catch (Exception e) {
	        DebugPrint.createReport(e, "Command (head)", true, sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (sender.hasPermission("headsplus.head")) {
            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], IHeadsPlusCommand.getPlayers(sender), results);
            } else if (args.length == 2 && sender.hasPermission("headsplus.head.others")) {
                StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(sender), results);
            }
        }

        return results;
    }
}
	
