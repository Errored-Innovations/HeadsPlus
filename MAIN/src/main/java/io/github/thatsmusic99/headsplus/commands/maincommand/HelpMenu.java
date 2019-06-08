package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

@CommandInfo(
        commandname = "help",
        permission = "headsplus.maincommand",
        subcommand = "Help",
        maincommand = true,
        usage = "/hp <help|Page No.> [Page No.]"
)
public class HelpMenu implements IHeadsPlusCommand {

    // I

	private void helpNoArgs(CommandSender sender) {
        HeadsPlusConfigTextMenu.HelpMenuTranslator.translateHelpMenu(sender, 1);
	}
	private void helpNo(CommandSender sender, String str) {
        HeadsPlusConfigTextMenu.HelpMenuTranslator.translateHelpMenu(sender, Integer.parseInt(str));
	}

	private void helpCmd(CommandSender cs, String cmdName) {
        if (cs.hasPermission("headsplus.maincommand")) {
            IHeadsPlusCommand pe = null;
            for (IHeadsPlusCommand key : HeadsPlus.getInstance().getCommands()) {
                if (key.getClass().getAnnotation(CommandInfo.class).commandname().equalsIgnoreCase(cmdName)) {
                    pe = key;
                    break;
                }
            }
            if (pe != null) {
                cs.sendMessage(HeadsPlusConfigTextMenu.HelpMenuTranslator.translateCommandHelp(pe, cs));
            } else {
                helpNoArgs(cs);
            }
        }
    }

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descHelpMenu();
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
            if (args.length == 0) {
                helpNoArgs(sender);
            } else if (args.length == 1) {
                if (args[0].matches("^[0-9]+$")) {
                    helpNo(sender, args[0]);
                } else if (args[0].equalsIgnoreCase("help")) {
                    helpNoArgs(sender);
                } else {
                    helpNoArgs(sender);
                }
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    if (args[1].matches("^[0-9]+$")) {
                        helpNo(sender, args[1]);
                    } else {
                        helpCmd(sender, args[1]);
                    }
                } else {
                    helpNoArgs(sender);
                }
            }
        } catch (Exception e) {
	        new DebugPrint(e, "Subcommand (help)", true, sender);
        }

        return true;
	}
}
