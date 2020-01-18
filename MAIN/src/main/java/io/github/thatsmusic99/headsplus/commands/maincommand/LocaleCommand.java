package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import org.bukkit.command.CommandSender;

@CommandInfo(
        commandname = "locale",
        permission = "headsplus.maincommand.locale",
        subcommand = "Locale",
        maincommand = true,
        usage = "/hp locale <Locale> [Player]"
)
public class LocaleCommand implements IHeadsPlusCommand {
    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        if (args.length > 1) {

        }
        return null;
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.locale", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
}
