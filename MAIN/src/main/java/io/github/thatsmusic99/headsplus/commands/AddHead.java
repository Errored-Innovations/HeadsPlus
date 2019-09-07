package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@CommandInfo(
        commandname = "addhead",
        permission = "headsplus.addhead",
        subcommand = "addhead",
        maincommand = false,
        usage = "/addhead <player>"
)
public class AddHead implements CommandExecutor, IHeadsPlusCommand {

    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

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
                sender.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("head-adding")
                        .replace("{player}", p.getName())
                        .replace("{header}", HeadsPlus.getInstance().getMenus().getConfig().getString("profile.header")));
            }
        } else {
            sender.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("invalid-args"));
        }
        return true;
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 0) {
            // todo? allow adding actual textures (and category, encoding) via this command
            if (args[0].matches("^[A-Za-z0-9_]+$")) {
                if (args[0].length() > 2) {
                    if (args[0].length() < 17) {
                        h.put(true, "");
                    } else {
                        h.put(false,  hpc.getString("head-too-long"));
                    }
                } else {
                    h.put(false, hpc.getString("too-short-head"));
                }
            } else {
                h.put(false, hpc.getString("alpha-names"));
            }
        } else {
            h.put(false, hpc.getString("invalid-args"));
        }
        return h;
    }

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descAddHead();
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
    
}
