package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

@CommandInfo(
		commandname = "blacklistwl",
		permission = "headsplus.maincommand.blacklistw.list",
		subcommand = "Blacklistwl",
		maincommand = true,
		usage = "/hp blacklistwl [Page No.]"
)
public class BlacklistwList implements IHeadsPlusCommand {

    // M
    private final HeadsPlus hp = HeadsPlus.getInstance();
	private final HeadsPlusMessagesConfig hpc = hp.getMessagesConfig();

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descBlacklistwList();
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
		    List<String> bl = hp.getConfiguration().getBlacklist().list;
		    int page;
		    if (args.length == 1) {
		        page = 1;
            } else {
		        page = Integer.parseInt(args[1]);
            }
            if (bl.size() == 0) {
                sender.sendMessage(hpc.getString("empty-blw"));
                return true;
            }
            sender.sendMessage(HeadsPlusConfigTextMenu.BlacklistTranslator.translate("blacklist", "world", bl, page));

        } catch (Exception e) {
		    new DebugPrint(e, "Subcommand (blacklistwl)", true, sender);
        }

		return false;
	}
}
