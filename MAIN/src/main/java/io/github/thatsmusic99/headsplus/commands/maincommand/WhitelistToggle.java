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

@CommandInfo(
        commandname = "whitelist",
        permission = "headsplus.maincommand.whitelist.toggle",
        subcommand = "Whitelist",
        maincommand = true,
        usage = "/hp whitelist [On|Off]"
)
public class WhitelistToggle implements IHeadsPlusCommand {

    private final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistToggle();
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        h.put(true, "");
        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            if (args.length == 1) {
                if (config.getHeadsWhitelist().enabled) {
                    config.getConfig().set("whitelist.default.enabled", config.getHeadsWhitelist().enabled = false);
                    config.save();
                    sender.sendMessage(hpc.getString("wl-off"));
                } else {
                    config.getConfig().set("whitelist.default.enabled", config.getHeadsWhitelist().enabled = true);
                    config.save();
                    sender.sendMessage(hpc.getString("wl-on"));
                }
            } else {
                String str = args[1];
                if (str.equalsIgnoreCase("on")) {
                    if (!config.getHeadsWhitelist().enabled) {
                        config.getConfig().set("whitelist.default.enabled", config.getHeadsWhitelist().enabled = true);
                        config.save();
                        sender.sendMessage(hpc.getString("wl-on"));
                    } else {
                        sender.sendMessage(hpc.getString("wl-a-on"));
                    }
                } else if (str.equalsIgnoreCase("off")) {
                    if (config.getHeadsWhitelist().enabled) {
                        config.getConfig().set("whitelist.default.enabled", config.getHeadsWhitelist().enabled = false);
                        config.save();
                        sender.sendMessage(hpc.getString("wl-off"));
                    } else {
                        sender.sendMessage(hpc.getString("wl-a-off"));
                    }
                } else if (!(str.equalsIgnoreCase("on"))) {
                    sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + getClass().getAnnotation(CommandInfo.class).usage());
                }
            }
        } catch (Exception e) {
            new DebugPrint(e, "Subcommand (whitelist)", true, sender);
        }

        return false;
    }
}
