package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "whitelistwdel",
        permission = "headsplus.maincommand.whitelistw.delete",
        subcommand = "Whitelistwdel",
        maincommand = true,
        usage = "/hp whitelistwdel <World Name>"
)
public class WhitelistwDelete implements IHeadsPlusCommand {
    private final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistwDelete();
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 1) {
            if (args[1].matches("^[A-Za-z0-9_]+$")) {
                h.put(true, "");
            } else {
                h.put(false, hpc.getString("alpha-names"));
            }
        } else {
            h.put(false, hpc.getString("invalid-args"));
        }

        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            if (args.length > 1) {
                if (args[1].matches("^[A-Za-z0-9_]+$")) {
                    List<String> whitelist = config.getWhitelist().list;
                    String rHead = args[1].toLowerCase();
                    if (whitelist.contains(rHead)) {
                        whitelist.remove(rHead);
                        config.getConfig().set("whitelist.world.list", whitelist);
                        config.save();
                        sender.sendMessage(hpc.getString("world-removed-wl").replaceAll("\\{name}", args[1]));
                    } else {
                        sender.sendMessage(hpc.getString("world-a-removed-wl"));
                    }
                } else {
                    sender.sendMessage(hpc.getString("alpha-names"));
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + getClass().getAnnotation(CommandInfo.class).usage());
            }
        } catch (Exception e) {
            new DebugPrint(e, "Subcommand (whitelistwdel)", true, sender);
        }

        return false;
    }
}
