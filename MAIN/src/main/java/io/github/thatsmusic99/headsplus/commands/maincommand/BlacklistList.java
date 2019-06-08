package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "blacklistl",
        permission = "headsplus.maincommand.blacklist.list",
        subcommand = "Blacklistl",
        maincommand = true,
        usage = "/hp blacklistl [Page No.]"
)
public class BlacklistList implements IHeadsPlusCommand {

    // L
    private HeadsPlus hp = HeadsPlus.getInstance();
	private final HeadsPlusMessagesConfig hpc = hp.getMessagesConfig();
	private HeadsPlusMainConfig config = hp.getConfiguration();

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descBlacklistList();
	}

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 1) {
            if (args[1].matches("^[0-9]+$")) {
                h.put(true, "");
            } else {
                h.put(false, hpc.getString("invalid-input-int"));
            }
        } else {
            h.put(true, "");
        }

        return h;
    }

	@Override
	public boolean fire(String[] args, CommandSender sender) {
	    try {
	        int page;
            if (args.length == 1) {
                page = 1;
            } else {
                page = Integer.parseInt(args[1]);
            }

            List<String> bl = config.getHeadsBlacklist().list;
            if (bl.size() == 0) {
                sender.sendMessage(hpc.getString("empty-bl"));
                return true;
            }
            sender.sendMessage(HeadsPlusConfigTextMenu.BlacklistTranslator.translate("blacklist", "default", bl, page));

        } catch (Exception e) {
	        new DebugPrint(e, "Subcommand (blacklistl)", true, sender);
        }
        return true;
	}
}
