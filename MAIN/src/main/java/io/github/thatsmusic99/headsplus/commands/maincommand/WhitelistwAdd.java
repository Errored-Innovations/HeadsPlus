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
        commandname = "whitelistwadd",
        permission = "headsplus.maincommand.whitelistw.add",
        subcommand = "Whitelistwadd",
        maincommand = true,
        usage = "/hp whitelistwadd <World Name>"
)
public class WhitelistwAdd implements IHeadsPlusCommand {

    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistwAdd();
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
                        HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
                        List<String> blacklist = config.getWhitelist().list;
                        String aWorld = args[1].toLowerCase();
                        if (blacklist.contains(aWorld)) {
                            sender.sendMessage(hpc.getString("world-a-add"));
                        } else {
                            blacklist.add(aWorld);
                            config.getConfig().set("whitelist.world.list", blacklist);
                            config.save();
                            sender.sendMessage(hpc.getString("world-added-wl").replaceAll("\\{name}", args[1].replaceAll("\\{world}", args[1])));
                        }
                } else {
                    sender.sendMessage(hpc.getString("alpha-names"));
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + getClass().getAnnotation(CommandInfo.class).usage());
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (whitelistwadd)", true, sender);
        }
        return false;
    }
}

