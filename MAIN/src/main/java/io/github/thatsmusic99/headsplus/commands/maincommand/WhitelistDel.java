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
        commandname = "whitelistdel",
        permission = "headsplus.maincommand.whitelist.delete",
        subcommand = "Whitelistdel",
        maincommand = true,
        usage = "/hp whitelistdel <Username>"
)
public class WhitelistDel implements IHeadsPlusCommand {

    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistDelete();
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
            HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
            List<String> whitelist = config.getHeadsWhitelist().list;
            String rHead = args[1].toLowerCase();
            if (whitelist.contains(rHead)) {
                whitelist.remove(rHead);
				config.getConfig().set("whitelist.default.list", whitelist);
                config.save();
                sender.sendMessage(hpc.getString("head-removed-wl").replaceAll("\\{name}", args[1]));
            } else {
                sender.sendMessage(hpc.getString("head-a-removed-wl"));
            }
        } catch (Exception e) {
            new DebugPrint(e, "Subcommand (whitelistdel)", true, sender);

        }

        return true;
    }
}
