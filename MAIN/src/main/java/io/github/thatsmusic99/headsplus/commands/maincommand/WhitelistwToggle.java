package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

@CommandInfo(
        commandname = "whitelistw",
        permission = "headsplus.maincommand.whitelistw.toggle",
        subcommand = "Whitelistwl",
        maincommand = true,
        usage = "/hp whitelistwl [On|Off]"
)
public class WhitelistwToggle implements IHeadsPlusCommand{

    private final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistwToggle();
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
            ConfigurationSection whitelist = config.getConfig().getConfigurationSection("whitelist.world");
            if (args.length == 1) {
                if (whitelist.getBoolean("enabled")) {
                    whitelist.set("enabled", false);
                    config.getWhitelist().enabled = false;
                    config.save();
                    sender.sendMessage(hpc.getString("wlw-off"));
                } else if (!whitelist.getBoolean("enabled")) {
                    whitelist.set("enabled", true);
                    config.getWhitelist().enabled = true;
                    config.save();
                    sender.sendMessage(hpc.getString("wlw-on"));
                }
            } else {
                String str = args[1];
                if (str.equalsIgnoreCase("on")) {
                    if (!whitelist.getBoolean("enabled")) {
                        whitelist.set("enabled", true);
                        config.getWhitelist().enabled = true;
                        config.save();
                        sender.sendMessage(hpc.getString("wlw-on"));
                    } else {
                        sender.sendMessage(hpc.getString("wlw-a-on"));
                    }
                } else if (str.equalsIgnoreCase("off")) {
                    if (whitelist.getBoolean("enabled")) {
                        whitelist.set("enabled", false);
                        config.getWhitelist().enabled = false;
                        config.save();
                        sender.sendMessage(hpc.getString("wlw-off"));
                    } else {
                        sender.sendMessage(hpc.getString("wlw-a-off"));
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + "/headsplus whitelistw [On|Off]");
                }
            }
        } catch (Exception e) {
            new DebugPrint(e, "Subcommand (whitelistw)", true, sender);
        }

        return false;
    }
}
