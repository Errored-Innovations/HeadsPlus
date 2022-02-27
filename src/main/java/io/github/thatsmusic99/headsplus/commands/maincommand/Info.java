package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigTextMenus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "info",
        permission = "headsplus.maincommand.info",
        maincommand = true,
        usage = "/hp info",
        descriptionPath = "descriptions.hp.info"
)
public class Info implements IHeadsPlusCommand {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label,
							 @NotNull String[] args) {
        sender.sendMessage(ConfigTextMenus.InfoTranslator.translate(sender));
        return true;
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
