package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigTextMenus;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "help",
        permission = "headsplus.maincommand",
        maincommand = true,
        usage = "/hp <help|Page No.> [Page No.]",
        descriptionPath = "descriptions.hp.help"
)
public class HelpMenu implements IHeadsPlusCommand {

    private void helpNoArgs(CommandSender sender) {
        ConfigTextMenus.HelpMenuTranslator.translateHelpMenu(sender, 1);
    }

    private void helpNo(CommandSender sender, String str) {
        ConfigTextMenus.HelpMenuTranslator.translateHelpMenu(sender, Integer.parseInt(str));
    }

    private void helpCmd(CommandSender cs, String cmdName) {
        if (!cs.hasPermission("headsplus.maincommand")) return;

        IHeadsPlusCommand pe = null;
        for (IHeadsPlusCommand key : HeadsPlus.get().getCommands().values()) {
            if (key.getClass().getAnnotation(CommandInfo.class).commandname().equalsIgnoreCase(cmdName)) {
                pe = key;
                break;
            }
        }

        if (pe != null) {
            cs.sendMessage(ConfigTextMenus.HelpMenuTranslator.translateCommandHelp(pe, cs));
        } else {
            helpNoArgs(cs);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
        if (args.length == 0) {
            helpNoArgs(sender);
        } else if (args.length == 1) {
            if (CachedValues.MATCH_PAGE.matcher(args[0]).matches()) {
                helpNo(sender, args[0]);
            } else if (args[0].equalsIgnoreCase("help")) {
                helpNoArgs(sender);
            } else {
                helpNoArgs(sender);
            }
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                if (CachedValues.MATCH_PAGE.matcher(args[1]).matches()) {
                    helpNo(sender, args[1]);
                } else {
                    helpCmd(sender, args[1]);
                }
            } else {
                helpNoArgs(sender);
            }
        }
        return true;
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        if (args.length == 2) {
            List<String> commands = new ArrayList<>();
            for (IHeadsPlusCommand key : HeadsPlus.get().getCommands().values()) {
                CommandInfo command = key.getClass().getAnnotation(CommandInfo.class);
                if (!sender.hasPermission(command.permission())) continue;
                if (!command.maincommand()) continue;
                commands.add(command.commandname());
            }
            List<String> results = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], commands, results);
            return results;
        }
        return new ArrayList<>();
    }
}
