package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        commandname = "addhead",
        permission = "headsplus.addhead",
        subcommand = "addhead",
        maincommand = false,
        usage = "/addhead <player>"
)
public class AddHead implements CommandExecutor, IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
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
        } else {
            sender.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-args", sender instanceof Player ? (Player) sender : null));
        }
        return true;
    }

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        Player p = sender instanceof Player ? (Player) sender : null;
        if (args.length > 0) {
            // todo? allow adding actual textures (and category, encoding) via this command
            if (args[0].matches("^[A-Za-z0-9_]+$")) {
                if (args[0].length() > 2) {
                    if (args[0].length() < 17) {
                        return "";
                    } else {
                        return hpc.getString("commands.head.head-too-long", p);
                    }
                } else {
                    return hpc.getString("commands.head.head-too-short", p);
                }
            } else {
                return hpc.getString("commands.head.alpha-names", p);
            }
        } else {
            return hpc.getString("commands.errors.invalid-args", p);
        }
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.addhead", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
    
}
