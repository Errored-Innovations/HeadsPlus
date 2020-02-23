package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "addhead",
        permission = "headsplus.addhead",
        subcommand = "addhead",
        maincommand = false,
        usage = "/addhead <player>"
)
public class AddHead implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        getDebug().startTimings(sender, "addhead");
        if(args.length > 0) {
            if (args[0].matches("^[A-Za-z0-9_]+$")) {
                if (args[0].length() > 2) {
                    if (args[0].length() < 17) {
                        HeadsPlus hp = HeadsPlus.getInstance();
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                        String uuid = p.getUniqueId().toString();
                        if (!hp.getServer().getOnlineMode()) {
                            hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting to grab UUID...");
                            uuid = hp.getHeadsXConfig().grabUUID(p.getName(), 3, null);
                        }
                        if(HeadsPlus.getInstance().getHeadsXConfig().grabProfile(uuid, sender, true)) {
                            sender.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.addhead.head-adding", sender instanceof Player ? (Player) sender : null)
                                    .replace("{player}", p.getName())
                                    .replace("{header}", HeadsPlus.getInstance().getMenus().getConfig().getString("profile.header")));
                        }
                        getDebug().stopTimings(sender, "addhead");
                        return true;
                    } else {
                        sender.sendMessage(hpc.getString("commands.head.head-too-long", sender));
                    }
                } else {
                    sender.sendMessage(hpc.getString("commands.head.head-too-short", sender));
                }
            } else {
                sender.sendMessage(hpc.getString("commands.head.alpha-names", sender));
            }
        } else {
            sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
        }
        getDebug().stopTimings(sender, "addhead");
        return true;
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.addhead", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], IHeadsPlusCommand.getPlayers(), results);
        }
        return results;
    }

}
