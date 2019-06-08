package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "whitelistadd",
        permission = "headsplus.maincommand.whitelist.add",
        subcommand = "Whitelistadd",
        maincommand = true,
        usage = "/hp whitelistadd <Username>"
)
public class WhitelistAdd implements IHeadsPlusCommand {

    private final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistAdd();
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
            List<String> whitelist = config.getHeadsWhitelist().list;
            String aHead = args[1].toLowerCase();
            if (whitelist.contains(aHead)) {
                sender.sendMessage(hpc.getString("head-a-add"));
            } else {
                whitelist.add(aHead);
				config.getConfig().set("whitelist.default.list", whitelist);
                config.save();
                sender.sendMessage(hpc.getString("head-added-wl").replaceAll("\\{name}", args[1]));
            }
        } catch (Exception e) {
            new DebugPrint(e, "Subcommand (whitelistadd)", true, sender);
        }
        return false;
    }
}
